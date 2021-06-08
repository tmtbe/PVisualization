package com.tmtbe.pvisual.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TtlRunnable;
import com.tmtbe.pvisual.core.watcher.PWatch;

public class ExecutorWatch extends PWatch {
    @Override
    protected void onCheck() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.util.concurrent.Executor";
    }

    @Override
    public String getWatchMethodName() {
        return "execute";
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Runnable runnable = (Runnable) advice.getParameterArray()[0];
        advice.changeParameter(0, TtlRunnable.get(runnable, false, true));
    }
}
