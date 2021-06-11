package com.tmtbe.pvisual.core.watcher;

import brave.Span;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.support.ExConsumer;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.trace.PTracer;
import com.tmtbe.pvisual.core.trace.TracingLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

@Slf4j
public abstract class PWatch implements RemoveHandle {
    @Getter
    protected WatchConfig watchConfig;
    protected TracingLevel tracingLevel;
    protected PVisualWatcherManager pVisualWatcherManager;
    @Getter
    protected PAdviceListener adviceListener = new PAdviceListener(this);
    protected boolean isTraceRoot = false;

    public PWatch() throws PTraceException {
        watchConfig = createWatchConfig();
        if (StringUtils.isAnyEmpty(watchConfig.getClassName(), watchConfig.getBehaviorName())) {
            throw new PTraceException("watch class name & watch behavior name can not empty!");
        }
        if (watchConfig.getPatternType() == null) {
            watchConfig.setPatternType(EventWatchBuilder.PatternType.REGEX);
        }
        if (watchConfig.getBuildingForWatching() == null) {
            watchConfig.setBuildingForWatching((t) -> {
            });
        }
        if (watchConfig.getBuildingForBehavior() == null) {
            watchConfig.setBuildingForBehavior((t) -> {
            });
        }
        if (watchConfig.getBuildingForClass() == null) {
            watchConfig.setBuildingForClass((t) -> t.includeSubClasses().includeBootstrap());
        }
    }

    protected abstract WatchConfig createWatchConfig();

    /**
     * 没有异常则认为Check状态是通过的
     *
     * @throws Throwable Throwable
     */
    protected abstract void checking(ClassLoader classLoader) throws Throwable;


    public String getName() {
        return watchConfig.getName();
    }

    protected Context getContext(Advice advice) {
        return advice.attachment();
    }

    @SneakyThrows
    protected void startSpan(Advice advice, ExConsumer<Span> handler) {
        PTracer parent = PTracer.getParent();
        if (parent != null) {
            Span span = PTracer.startSpan(pVisualWatcherManager, watchConfig.getServiceName(), parent.getSpan().context());
            setSpanName(advice, span);
            startSpan(advice, span, handler);
        } else {
            if (watchConfig.isCanCreateTrace()) {
                Span traceSpan = PTracer.startTracerSpan(pVisualWatcherManager, watchConfig.getServiceName());
                setSpanName(advice, traceSpan);
                startSpan(advice, traceSpan, handler);
                isTraceRoot = true;
            }
        }
    }

    protected void setSpanName(Advice advice, Span span) {
        if (advice.getTarget() != null) {
            span.name(advice.getTarget().getClass().getSimpleName() + ":" + advice.getBehavior().getName());
        } else {
            span.name(advice.getBehavior().getName());
        }
    }

    @SneakyThrows
    private void startSpan(Advice advice, Span span, ExConsumer<Span> handler) {
        Context context = new Context();
        advice.attach(context);
        PTracer parent = PTracer.getParent();
        //保存当前PTracer
        context.setPTracer(parent);
        context.setSpan(span);
        addStackTrace(span);
        addAdviceBeforeInfo(span, advice);
        //用Span设置新的PTracer
        PTracer.setParent(new PTracer(span));
        if (handler != null) {
            handler.accept(span);
        }
    }

    @SneakyThrows
    protected void finishSpan(Advice advice, ExConsumer<Span> handler) {
        Context context = getContext(advice);
        if (context == null) return;
        Span span = context.getSpan();
        if (span != null) {
            addAdviceAfterInfo(span, advice);
            if (handler != null) {
                handler.accept(span);
            }
            PTracer.finishSpan(span);
            if (isTraceRoot) {
                PTracer.finishTracer(span.context().traceId());
            }
        }
        //恢复之前保存的PTracer
        PTracer.setParent(context.getPTracer());
    }

    protected void before(Advice advice) throws Throwable {
        startSpan(advice, null);
    }

    protected void after(Advice advice) throws Throwable {
        finishSpan(advice, null);
    }


    protected Class<?> getBusinessClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(className, true, classLoader);
    }

    protected void addStackTrace(Span span) {
        if (tracingLevel.getLevel() > TracingLevel.NORMAL.getLevel()) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            ArrayList<String> stack = new ArrayList<>();
            int index = 0;
            for (int i = stackTrace.length - 1; i >= 0; i--) {
                if (stackTrace[i].getClassName().startsWith("java.com.alibaba.jvm.sandbox")) break;
                stack.add("[" + index + "] " + stackTrace[i].getClassName() + "::" + stackTrace[i].getMethodName() + "  " + stackTrace[i].getLineNumber());
                index++;
            }
            span.tag("stackTrace", StringUtils.join(stack, "\n"));
        }
    }

    private void addAdviceBeforeInfo(Span span, Advice advice) {

    }

    private void addAdviceAfterInfo(Span span, Advice advice) {
        if (tracingLevel.getLevel() > TracingLevel.PERFORMANCE.getLevel()) {
            if (advice.isThrows()) {
                span.error(advice.getThrowable());
                if (advice.getThrowable().getCause() != null) {
                    span.tag("error-message", advice.getThrowable().getCause().getMessage());
                } else {
                    span.tag("error-message", advice.getThrowable().getMessage());
                }
                if (tracingLevel.getLevel() > TracingLevel.NORMAL.getLevel()) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    advice.getThrowable().printStackTrace(pw);
                    if (advice.getThrowable().getCause() != null) {
                        pw.println("Cause by: " + advice.getThrowable().getCause().getMessage());
                        advice.getThrowable().getCause().printStackTrace(pw);
                    }
                    span.tag("error-stackTrace", sw.toString());
                }
            }
        }
    }

    public void setPVisualWatcherManager(PVisualWatcherManager pVisualWatcherManager) {
        this.pVisualWatcherManager = pVisualWatcherManager;
        this.tracingLevel = pVisualWatcherManager.getTraceConfig().getTracingLevel();
        if (StringUtils.isEmpty(watchConfig.getServiceName())) {
            watchConfig.setServiceName(pVisualWatcherManager.getTraceConfig().getLocalServiceName());
        }
    }
}
