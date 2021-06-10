package com.tmtbe.pvisual.core.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TtlRunnable;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class ExecutorWatch extends PWatch {
    public ExecutorWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("java.util.concurrent.Executor")
                .behaviorName("execute")
                .build();
    }

    @Override
    protected void checking() throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Runnable runnable = (Runnable) advice.getParameterArray()[0];
        advice.changeParameter(0, TtlRunnable.get(runnable, false, true));
    }
}
