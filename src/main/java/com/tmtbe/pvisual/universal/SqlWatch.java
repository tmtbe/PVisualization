package com.tmtbe.pvisual.universal;

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

    @Override
    protected void checking() throws Throwable {

    }
}
