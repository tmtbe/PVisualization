package com.tmtbe.pvisual.core;

import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import org.apache.commons.lang3.StringUtils;

public class DynamicWatch extends PWatch {
    private final String watchClassName;
    private final String watchMethodName;

    public DynamicWatch(String str) {
        if (str == null) {
            watchMethodName = null;
            watchClassName = null;
        } else {
            String[] split = str.split(":");
            if (split.length == 2) {
                watchClassName = split[0];
                watchMethodName = split[1];
            } else {
                watchMethodName = null;
                watchClassName = null;
            }
        }
    }

    @Override
    protected void checking() throws Throwable {
        if (StringUtils.isAnyEmpty(watchClassName, watchMethodName)) {
            throw new PTraceException("Incorrect format");
        }
    }

    @Override
    public String getWatchClassName() {
        return watchClassName;
    }

    @Override
    public String getWatchMethodName() {
        return watchMethodName;
    }

    @Override
    public EventWatchBuilder.PatternType getPatternType() {
        return EventWatchBuilder.PatternType.WILDCARD;
    }
}
