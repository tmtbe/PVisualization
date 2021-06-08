package com.tmtbe.pvisual.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.tmtbe.pvisual.core.watcher.PWatch;
import org.springframework.util.ConcurrentReferenceHashMap;

public class ForkJoinTaskInitWatch extends PWatch {
    static final ConcurrentReferenceHashMap<Object, Object> captureMap
            = new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    @Override
    protected void checking() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.util.concurrent.ForkJoinTask";
    }

    @Override
    public String getWatchMethodName() {
        return "<init>";
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        if (advice.getTarget() instanceof TtlEnhanced) {
            return;
        }
        captureMap.put(advice.getTarget(), TransmittableThreadLocal.Transmitter.capture());
    }
}
