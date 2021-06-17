package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.trace.PTracer;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class SubscriberOnNextWatch extends PWatch {
    public SubscriberOnNextWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("org.reactivestreams.Subscriber")
                .behaviorName("onNext")
                .build();
    }

    @Override
    protected void checking(ClassLoader classLoader) throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        PTracer pTracer = SubscriberInitWatch.parentCache.get(advice.getTarget());
        PTracer.setParent(pTracer);
    }
}
