package com.tmtbe.pvisual.core.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class ForkJoinTaskWatch extends PWatch {
    public ForkJoinTaskWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("java.util.concurrent.ForkJoinTask")
                .behaviorName("doExec")
                .build();
    }

    @Override
    protected void checking() throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        if (advice.getTarget() instanceof TtlEnhanced) {
            return;
        }
        Object capture = ForkJoinTaskInitWatch.captureMap.get(advice.getTarget());
        Object backup = TransmittableThreadLocal.Transmitter.replay(capture);
        advice.attach(backup);
    }

    @Override
    protected void after(Advice advice) throws Throwable {
        if (advice.getTarget() instanceof TtlEnhanced) {
            return;
        }
        Object backup = advice.attachment();
        TransmittableThreadLocal.Transmitter.restore(backup);
    }
}
