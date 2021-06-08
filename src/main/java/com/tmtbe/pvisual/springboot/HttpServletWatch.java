package com.tmtbe.pvisual.springboot;

import brave.Span;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.trace.PTracer;
import com.tmtbe.pvisual.core.watcher.PWatch;

public class HttpServletWatch extends PWatch {
    @Override
    protected void onCheck() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "javax.servlet.http.HttpServlet";
    }

    @Override
    public String getWatchMethodName() {
        return "service";
    }

    @Override
    public void buildingForBehavior(EventWatchBuilder.IBuildingForBehavior iBuildingForBehavior) {
        iBuildingForBehavior.withParameterTypes("javax.servlet.http.HttpServletRequest", "javax.servlet.http.HttpServletResponse");
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Span traceSpan = PTracer.startTracerSpan();
        check(() -> startSpan(advice, traceSpan, span -> {
            span.kind(Span.Kind.CLIENT);
            span.name(getName());
        }));
    }

    @Override
    protected void after(Advice advice) throws Throwable {
        check(() -> finishSpan(advice, null));
    }
}
