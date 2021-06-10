package com.tmtbe.pvisual.core.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinWorkerThreadFactoryWatch extends PWatch {
    public ForkJoinWorkerThreadFactoryWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("java.util.concurrent.ForkJoinPool")
                .behaviorName("<init>")
                .build();
    }

    @Override
    protected void checking() throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        for (int i = 0; i < advice.getParameterArray().length; i++) {
            if (advice.getParameterArray()[i] instanceof ForkJoinPool.ForkJoinWorkerThreadFactory) {
                advice.changeParameter(i, TtlForkJoinPoolHelper.getDisableInheritableForkJoinWorkerThreadFactory(
                        (ForkJoinPool.ForkJoinWorkerThreadFactory) advice.getParameterArray()[i]));
            }
        }
    }
}
