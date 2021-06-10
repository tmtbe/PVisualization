package com.tmtbe.pvisual.core;

import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class DynamicWatch extends PWatch {
    private final String watchString;

    public DynamicWatch(String watchString) throws PTraceException {
        this.watchString = watchString;
    }

    @Override
    protected WatchConfig createWatchConfig() {
        String watchMethodName = null;
        String watchClassName = null;
        if (watchString != null) {
            String[] split = watchString.split(":");
            if (split.length == 2) {
                watchClassName = split[0];
                watchMethodName = split[1];
            }
        }
        return WatchConfig.builder()
                .className(watchClassName)
                .behaviorName(watchMethodName)
                .patternType(EventWatchBuilder.PatternType.WILDCARD)
                .build();
    }

    @Override
    protected void checking() throws Throwable {

    }
}
