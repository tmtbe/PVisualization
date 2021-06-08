package com.tmtbe.pvisual.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.tmtbe.pvisual.core.watcher.PWatch;

import java.util.concurrent.Callable;

public class ScheduledExecutorServiceWatch extends PWatch {
    @Override
    protected void onCheck() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.util.concurrent.ScheduledExecutorService";
    }

    @Override
    public String getWatchMethodName() {
        return "schedule|scheduleAtFixedRate|scheduleWithFixedDelay";
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Object param = advice.getParameterArray()[0];
        if (param instanceof Runnable) {
            advice.getParameterArray()[0] = TtlRunnable.get((Runnable) param, false, true);
        } else if (param instanceof Callable) {
            advice.getParameterArray()[0] = TtlCallable.get((Callable<?>) param, false, true);
        }
    }
}
