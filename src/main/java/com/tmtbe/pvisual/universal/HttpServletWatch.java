package com.tmtbe.pvisual.universal;

import brave.Span;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HttpServletWatch extends PWatch {
    protected Method getMethod;

    public HttpServletWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .canCreateTrace(true)
                .serviceName("Servlet")
                .className("javax.servlet.http.HttpServlet")
                .behaviorName("service")
                .patternType(EventWatchBuilder.PatternType.REGEX)
                .buildingForBehavior((t) -> t.withAccess(Modifier.PROTECTED)
                        .withParameterTypes("javax.servlet.http.HttpServletRequest", "javax.servlet.http.HttpServletResponse"))
                .buildingForClass((t) -> {
                })
                .build();
    }

    @Override
    protected void checking(ClassLoader classLoader) throws Throwable {
        Class<?> httpServletRequestClass = getBusinessClass("javax.servlet.http.HttpServletRequest", classLoader);
        getMethod = httpServletRequestClass.getDeclaredMethod("getMethod");
    }


    @Override
    protected void before(Advice advice) throws Throwable {
        startSpan(advice, span -> {
            span.kind(Span.Kind.SERVER);
            span.name((String) getMethod.invoke(advice.getParameterArray()[0]));
            addStackTrace(span);
        });
    }
}
