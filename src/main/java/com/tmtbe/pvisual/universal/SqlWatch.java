package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class SqlWatch extends PWatch {

    public SqlWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .canCreateTrace(true)
                .serviceName("DB")
                .className("java.sql.Connection")
                .behaviorName("prepareStatement")
                .patternType(EventWatchBuilder.PatternType.REGEX)
                .build();
    }

    protected void checking(ClassLoader classLoader) throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        startSpan(advice, span -> {
            span.name(advice.getTarget().getClass().getSimpleName() + ":" + advice.getBehavior().getName());
            if (advice.getParameterArray().length > 0) {
                span.tag("sql", advice.getParameterArray()[0].toString());
            }
        });
    }
}
