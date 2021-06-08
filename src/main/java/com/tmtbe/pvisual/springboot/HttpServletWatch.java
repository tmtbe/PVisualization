package com.tmtbe.pvisual.springboot;

import brave.Span;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.trace.PTracer;
import com.tmtbe.pvisual.core.watcher.PWatch;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HttpServletWatch extends PWatch {
    protected Method getMethod;

    @Override
    protected void checking() throws Throwable {
        Class<?> httpServletRequestClass = getBClass("javax.servlet.http.HttpServletRequest");
        getMethod = httpServletRequestClass.getDeclaredMethod("getMethod");
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
        iBuildingForBehavior.withAccess(Modifier.PROTECTED);
    }

    @Override
    public void buildingForClass(EventWatchBuilder.IBuildingForClass iBuildingForClass) {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Span traceSpan = PTracer.startTracerSpan();
        startSpan(advice, traceSpan, span -> {
            span.kind(Span.Kind.SERVER);
            span.name((String) getMethod.invoke(advice.getParameterArray()[0]));
            span.tag("getModifiers", advice.getBehavior().getModifiers() + "");
            addStackTrace(span);
        });
    }

    @Override
    protected void after(Advice advice) throws Throwable {
        finishSpan(advice, null);
    }
}
