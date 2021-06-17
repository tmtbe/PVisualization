package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.trace.PTracer;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

import java.util.concurrent.ConcurrentHashMap;

public class SubscriberInitWatch extends PWatch {
    public static ConcurrentHashMap<Object, PTracer> parentCache = new ConcurrentHashMap<>();

    public SubscriberInitWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("org.reactivestreams.Subscriber")
                .behaviorName("<init>")
                .build();
    }

    @Override
    protected void checking(ClassLoader classLoader) throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        if (PTracer.getParent() != null) {
            parentCache.put(advice.getTarget(), PTracer.getParent());
        }
    }
}
