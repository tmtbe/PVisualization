package com.tmtbe.pvisual.core.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

import java.util.Collection;
import java.util.concurrent.Callable;

public class ExecutorServiceWatch extends PWatch {
    public ExecutorServiceWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("java.util.concurrent.ExecutorService")
                .behaviorName("submit|invokeAll|invokeAny")
                .build();
    }

    protected void checking(ClassLoader classLoader) throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Object param = advice.getParameterArray()[0];
        if (advice.getBehavior().getParameterTypes()[0].equals(Runnable.class)) {
            advice.changeParameter(0, TtlRunnable.get((Runnable) param, false, true));
        } else if (advice.getBehavior().getParameterTypes()[0].equals(Callable.class)) {
            advice.changeParameter(0, TtlCallable.get((Callable<?>) param, false, true));
        } else if (advice.getBehavior().getParameterTypes()[0].equals(Collection.class)) {
            advice.changeParameter(0, TtlCallable.gets((Collection) param, false, true));
        }
    }
}
