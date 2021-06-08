package com.tmtbe.pvisual.core.watcher;

import brave.Span;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.trace.PTracer;
import lombok.NonNull;

import java.util.function.Consumer;

public abstract class PWatch extends AdviceListener {
    protected boolean isCheckSuccess = false;

    /**
     * 检查是否满足条件，满足会执行runnable
     *
     * @param runnable runnable
     */
    public void check(@NonNull Runnable runnable) {
        if (isCheckSuccess) {
            runnable.run();
            return;
        }
        try {
            onCheck();
            isCheckSuccess = true;
        } catch (Throwable e) {
            isCheckSuccess = false;
        }
        if (isCheckSuccess) {
            runnable.run();
        }
    }

    /**
     * 没有异常则认为Check状态是通过的
     *
     * @throws Throwable Throwable
     */
    protected abstract void onCheck() throws Throwable;

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
            //用Span设置新的PTracer
            PTracer.setParent(new PTracer(parent.getTracer(), span));
            if (handler != null) {
                handler.accept(span);
            }
        }
    }

    protected void finishSpan(Advice advice, Consumer<Span> handler) {
        Context context = getContext(advice);
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

    @Override
    protected void before(Advice advice) throws Throwable {
        check(() -> startSpan(advice, span -> {
            span.kind(Span.Kind.CLIENT);
            span.name(getName());
        }));
    }

    @Override
    protected void after(Advice advice) throws Throwable {
        check(() -> finishSpan(advice, null));
    }

    public EventWatchBuilder.PatternType getPatternType() {
        return EventWatchBuilder.PatternType.REGEX;
    }
}
