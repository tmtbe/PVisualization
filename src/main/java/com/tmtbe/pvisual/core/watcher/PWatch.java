package com.tmtbe.pvisual.core.watcher;

import brave.Span;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.support.ExConsumer;
import com.tmtbe.pvisual.core.trace.PTracer;
import com.tmtbe.pvisual.core.trace.TracingLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.function.Consumer;

@Slf4j
public abstract class PWatch implements RemoveHandle {
    protected TracingLevel tracingLevel;
    protected PVisualWatcherManager pVisualWatcherManager;
    @Getter
    protected PAdviceListener adviceListener = new PAdviceListener(this);


    /**
     * 没有异常则认为Check状态是通过的
     *
     * @throws Throwable Throwable
     */
    protected abstract void checking() throws Throwable;

    public abstract String getWatchClassName();

    public abstract String getWatchMethodName();

    public void buildingForClass(EventWatchBuilder.IBuildingForClass iBuildingForClass) {
        iBuildingForClass.includeSubClasses().includeBootstrap();
    }

    public void buildingForBehavior(EventWatchBuilder.IBuildingForBehavior iBuildingForBehavior) {

    }

    public void buildingForWatching(EventWatchBuilder.IBuildingForWatching iBuildingForWatching) {

    }

    public String getName() {
        return getWatchClassName() + ":" + getWatchMethodName();
    }

    protected Context getContext(Advice advice) {
        return advice.attachment();
    }

    protected void startSpan(Advice advice, Consumer<Span> handler) {
        Context context = new Context();
        advice.attach(context);
        PTracer parent = PTracer.getParent();
        //保存当前PTracer
        context.setPTracer(parent);
        if (parent != null) {
            Span span = PTracer.startSpan(parent.getTracer(), parent.getSpan().context());
            context.setSpan(span);
            addStackTrace(span);
            //用Span设置新的PTracer
            PTracer.setParent(new PTracer(parent.getTracer(), span));
            if (handler != null) {
                handler.accept(span);
            }
        }
    }

    @SneakyThrows
    protected void startSpan(Advice advice, Span span, ExConsumer<Span> handler) {
        Context context = new Context();
        advice.attach(context);
        PTracer parent = PTracer.getParent();
        //保存当前PTracer
        context.setPTracer(parent);
        context.setSpan(span);
        addStackTrace(span);
        //用Span设置新的PTracer
        PTracer.setParent(new PTracer(PTracer.getTracing(pVisualWatcherManager).tracer(), span));
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
            if (handler != null) {
                handler.accept(span);
            }
            PTracer.finishSpan(span);
        }
        //恢复之前保存的PTracer
        PTracer.setParent(context.getPTracer());
    }

    protected void before(Advice advice) throws Throwable {
        startSpan(advice, span -> {
            span.name(advice.getTarget().getClass().getSimpleName() + ":" + advice.getBehavior().getName());
        });
    }

    protected void after(Advice advice) throws Throwable {
        finishSpan(advice, null);
    }

    public EventWatchBuilder.PatternType getPatternType() {
        return EventWatchBuilder.PatternType.REGEX;
    }

    protected Class<?> getBClass(String className) throws ClassNotFoundException {
        return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
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

    public void setPVisualWatcherManager(PVisualWatcherManager pVisualWatcherManager) {
        this.pVisualWatcherManager = pVisualWatcherManager;
        this.tracingLevel = pVisualWatcherManager.getTraceConfig().getTracingLevel();
    }
}
