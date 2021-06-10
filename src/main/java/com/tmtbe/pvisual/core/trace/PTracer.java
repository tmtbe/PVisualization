package com.tmtbe.pvisual.core.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PVisualWatcherManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.brave.ZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class PTracer {
    private static final ConcurrentHashMap<String, Tracing> tracingMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, LinkedList<Span>> traceIdSpanMap = new ConcurrentHashMap<>();
    private static final TransmittableThreadLocal<PTracer> parentThreadLocal = new TransmittableThreadLocal<>();
    private static final ConcurrentHashMap<Long, HashMap<Object, Span>> traceIdTagSpanMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Object, Long> tagTraceIdMap = new ConcurrentHashMap<>();
    @Getter
    private final Span span;

    public static PTracer getParent() {
        return parentThreadLocal.get();
    }

    public static void setParent(PTracer parent) {
        parentThreadLocal.set(parent);
    }

    @SneakyThrows
    private static Tracing getTracing(PVisualWatcherManager pVisualWatcherManager, String serviceName) {
        String localServiceName = pVisualWatcherManager.getTraceConfig().getLocalServiceName();
        String zipkinEndPoint = pVisualWatcherManager.getTraceConfig().getZipkinEndPoint();
        if (StringUtils.isAnyEmpty(localServiceName, zipkinEndPoint)) {
            throw new PTraceException("Trace Config is not setting");
        }
        if (serviceName == null) {
            serviceName = localServiceName;
        }
        String finalServiceName = serviceName;
        return tracingMap.compute(finalServiceName + zipkinEndPoint, (k, v) -> {
            if (v == null) {
                OkHttpSender sender = OkHttpSender.newBuilder().endpoint(zipkinEndPoint).build();
                AsyncReporter<zipkin2.Span> reporter = AsyncReporter.builder(sender).build();
                v = Tracing.newBuilder().localServiceName(finalServiceName)
                        .traceId128Bit(false)
                        .addSpanHandler(ZipkinSpanHandler.newBuilder(reporter).build())
                        .build();
            }
            return v;
        });
    }

    public static Span startTracerSpan(PVisualWatcherManager pVisualWatcherManager, String serviceName) {
        Tracer tracer = getTracing(pVisualWatcherManager, serviceName).tracer();
        Span span = tracer.newTrace();
        span.start();
        parentThreadLocal.set(new PTracer(span));
        return span;
    }

    public static void finishTracer(long traceId) {
        traceIdSpanMap.compute(traceId, (k, v) -> {
            if (v != null) {
                LinkedList<Span> spans = traceIdSpanMap.get(traceId);
                spans.forEach(Span::finish);
            }
            return null;
        });
        HashMap<Object, Span> tagSpan = traceIdTagSpanMap.remove(traceId);
        if (tagSpan != null) {
            tagSpan.keySet().forEach(tagTraceIdMap::remove);
        }
        parentThreadLocal.set(null);
    }

    public static Span startSpan(@NonNull PVisualWatcherManager pVisualWatcherManager, String serviceName, @NonNull TraceContext traceContext) {
        Tracer tracer = getTracing(pVisualWatcherManager, serviceName).tracer();
        Span span = tracer.newChild(traceContext);
        traceIdSpanMap.compute(traceContext.traceId(), (k, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }
            v.add(span);
            return v;
        });
        span.start();
        return span;
    }

    public static void finishSpan(@NonNull Span span) {
        span.finish();
    }

    public static void setSpanWithTag(@NonNull Span span, @NonNull Object tag) {
        traceIdTagSpanMap.compute(span.context().traceId(), (k, v) -> {
            if (v == null) {
                v = new HashMap<>();
            }
            v.put(tag, span);
            return v;
        });
        tagTraceIdMap.put(tag, span.context().traceId());
    }

    public static Span getAndRemoveSpanWithTag(@NonNull Object tag) {
        Long traceId = tagTraceIdMap.remove(tag);
        if (traceId == null) return null;
        HashMap<Object, Span> tagSpanHashMap = traceIdTagSpanMap.get(traceId);
        return tagSpanHashMap.remove(tag);
    }

    public static void clear() {
        traceIdSpanMap.clear();
        tracingMap.clear();
        parentThreadLocal.set(null);
        traceIdTagSpanMap.clear();
        tagTraceIdMap.clear();
    }
}
