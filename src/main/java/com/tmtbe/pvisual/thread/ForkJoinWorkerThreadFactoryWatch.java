package com.tmtbe.pvisual.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.tmtbe.pvisual.core.watcher.PWatch;

public class ForkJoinWorkerThreadFactoryWatch extends PWatch {
    @Override
    protected void onCheck() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.util.concurrent.ForkJoinPool$ForkJoinWorkerThreadFactory";
    }

    @Override
    public String getWatchMethodName() {
        return "newThread";
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Object backup = TransmittableThreadLocal.Transmitter.clear();
        advice.attach(backup);
    }

    @Override
    protected void after(Advice advice) throws Throwable {
        Object backup = advice.attachment();
        TransmittableThreadLocal.Transmitter.restore(backup);
    }
}
