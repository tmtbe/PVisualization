package com.tmtbe.pvisual.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.tmtbe.pvisual.core.watcher.PWatch;
import org.springframework.util.ConcurrentReferenceHashMap;

public class ForkJoinTaskWatch extends PWatch {
    private static final ConcurrentReferenceHashMap<Object, Object> captureMap
            = new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    @Override
    protected void onCheck() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.util.concurrent.ForkJoinTask";
    }

    @Override
    public String getWatchMethodName() {
        return "doExec";
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        if (advice.getTarget() instanceof TtlEnhanced) {
            return;
        }
        Object capture = captureMap.compute(advice.getTarget(), (k, v) -> {
            if (v == null) {
                v = TransmittableThreadLocal.Transmitter.capture();
            }
            return v;
        });
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
