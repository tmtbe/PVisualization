package com.tmtbe.pvisual.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper;
import com.tmtbe.pvisual.core.watcher.PWatch;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinWorkerThreadFactoryWatch extends PWatch {
    @Override
    protected void onCheck() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.util.concurrent.ForkJoinPool";
    }

    @Override
    public String getWatchMethodName() {
        return "<init>";
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
