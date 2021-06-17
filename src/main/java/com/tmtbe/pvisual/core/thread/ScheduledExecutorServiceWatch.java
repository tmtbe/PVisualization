package com.tmtbe.pvisual.core.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

import java.util.concurrent.Callable;

public class ScheduledExecutorServiceWatch extends PWatch {

    public ScheduledExecutorServiceWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("java.util.concurrent.ScheduledExecutorService")
                .behaviorName("schedule|scheduleAtFixedRate|scheduleWithFixedDelay")
                .build();
    }

    protected void checking(ClassLoader classLoader) throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        if (advice.getParameterArray().length != 3) return;
        Object param = advice.getParameterArray()[0];

        if (advice.getBehavior().getParameterTypes()[0].equals(Runnable.class)) {
            advice.changeParameter(0, TtlRunnable.get((Runnable) param, false, true));
        } else if (advice.getBehavior().getParameterTypes()[0].equals(Callable.class)) {
            advice.changeParameter(0, TtlCallable.get((Callable<?>) param, false, true));
        }
    }
}
