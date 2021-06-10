package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class PreparedStatementWatch extends PWatch {
    @Override
    protected WatchConfig getWatchConfig() {
        return WatchConfig.builder().serviceName("DB").build();
    }

    @Override
    protected void checking() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.sql.PreparedStatement";
    }

    @Override
    public String getWatchMethodName() {
        return "execute*";
    }

    @Override
    public EventWatchBuilder.PatternType getPatternType() {
        return EventWatchBuilder.PatternType.WILDCARD;
    }
}
