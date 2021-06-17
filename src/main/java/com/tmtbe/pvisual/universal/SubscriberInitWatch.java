package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.trace.PTracer;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;
import org.springframework.util.ConcurrentReferenceHashMap;

public class SubscriberInitWatch extends PWatch {
    public static ConcurrentReferenceHashMap<Object, PTracer> parentCache = new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

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
