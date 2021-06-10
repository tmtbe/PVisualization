package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class PreparedStatementWatch extends PWatch {

    public PreparedStatementWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .canCreateTrace(true)
                .serviceName("DB")
                .className("java.sql.PreparedStatement")
                .behaviorName("execute*")
                .patternType(EventWatchBuilder.PatternType.WILDCARD)
                .build();
    }

    protected void checking(ClassLoader classLoader) throws Throwable {

    }
}
