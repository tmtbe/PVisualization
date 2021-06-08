package com.tmtbe.pvisual.core.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.tmtbe.pvisual.core.support.PTraceException;
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
    private static final ConcurrentHashMap<Tracer, LinkedList<Span>> tracerSpanMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Tracer> tracerMap = new ConcurrentHashMap<>();
    private static final TransmittableThreadLocal<PTracer> parentThreadLocal = new TransmittableThreadLocal<>();
    private static final ConcurrentHashMap<Long, HashMap<Object, Span>> spanTagMap = new ConcurrentHashMap<>();
    @Getter
    private final Tracer tracer;
    @Getter
    private final Span span;

    public static PTracer getParent() {
        return parentThreadLocal.get();
    }

    public static void setParent(PTracer parent) {
        parentThreadLocal.set(parent);
    }

    @SneakyThrows
    public static Tracing getTracing() {
        String localServiceName = TraceConfig.INSTANCE.getLocalServiceName();
        String zipkinEndPoint = TraceConfig.INSTANCE.getZipkinEndPoint();
        if (StringUtils.isAnyEmpty(localServiceName, zipkinEndPoint)) {
            throw new PTraceException("Trace Config is not setting");
        }
        return tracingMap.compute(localServiceName + zipkinEndPoint, (k, v) -> {
            if (v == null) {
                OkHttpSender sender = OkHttpSender.newBuilder().endpoint(zipkinEndPoint).build();
                AsyncReporter<zipkin2.Span> reporter = AsyncReporter.builder(sender).build();
                v = Tracing.newBuilder().localServiceName(localServiceName)
                        .traceId128Bit(false)
                        .addSpanHandler(ZipkinSpanHandler.newBuilder(reporter).build())
                        .build();
            }
            return v;
        });
    }

    public static Span startTracerSpan() {
        Tracer tracer = getTracing().tracer();
        Span span = tracer.newTrace();
        span.start();
        tracerMap.putIfAbsent(span.context().traceId(), tracer);
        parentThreadLocal.set(new PTracer(tracer, span));
        return span;
    }

    public static Tracer getTracer(long traceId) {
        return tracerMap.get(traceId);
    }

    public static void finishTracer(long traceId) {
        Tracer tracer = getTracer(traceId);
        if (tracer == null) return;
        tracerSpanMap.compute(tracer, (k, v) -> {
            if (v != null) {
                LinkedList<Span> spans = tracerSpanMap.get(tracer);
                spans.forEach(Span::finish);
            }
            return null;
        });
        tracerMap.remove(traceId);
        spanTagMap.remove(traceId);
    }

    public static Span startSpan(@NonNull Tracer tracer, @NonNull TraceContext traceContext) {
        Span span = tracer.newChild(traceContext);
        tracerSpanMap.compute(tracer, (k, v) -> {
            if (v == null) {
                v = new LinkedList<>();
            }
            v.add(span);
            return v;
        });
        span.start();
        return span;
    }

    public static Span startSpan(long traceId, @NonNull TraceContext traceContext) {
        Tracer tracer = getTracer(traceId);
        if (tracer == null) return null;
        return startSpan(tracer, traceContext);
    }

    public static void finishSpan(@NonNull Span span) {
        span.finish();
    }

    public static void setSpanWithTag(@NonNull Span span, @NonNull Object tag) {
        spanTagMap.compute(span.context().traceId(), (k, v) -> {
            if (v == null) {
                v = new HashMap<>();
            }
            v.put(tag, span);
            return v;
        });
    }

    public static Span getSpanWithTag(long traceId, @NonNull Object tag) {
        HashMap<Object, Span> tagSpanHashMap = spanTagMap.get(traceId);
        return tagSpanHashMap.get(tag);
    }
}
