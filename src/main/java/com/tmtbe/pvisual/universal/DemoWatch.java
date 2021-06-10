package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.tmtbe.pvisual.core.watcher.PWatch;

public class DemoWatch extends PWatch {
    @Override
    protected void checking() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "com.example.demo.*";
    }

    @Override
    public String getWatchMethodName() {
        return "*";
    }

    @Override
    public EventWatchBuilder.PatternType getPatternType() {
        return EventWatchBuilder.PatternType.WILDCARD;
    }
}
