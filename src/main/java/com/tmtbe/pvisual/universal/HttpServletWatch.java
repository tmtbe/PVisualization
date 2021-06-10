package com.tmtbe.pvisual.universal;

import brave.Span;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HttpServletWatch extends PWatch {
    protected Method getMethod;

    @Override
    protected WatchConfig getWatchConfig() {
        return WatchConfig.builder().canCreateTrace(true).serviceName("Servlet").build();
    }

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
        // 默认会includeSubClasses，导致会有2个Trace这里重写下。
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        startSpan(advice, span -> {
            span.kind(Span.Kind.SERVER);
            //span.name((String) getMethod.invoke(advice.getParameterArray()[0]));
            addStackTrace(span);
        });
    }
}
