package com.tmtbe.pvisual.core.thread;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;
import com.tmtbe.pvisual.core.watcher.WatchData;
import org.springframework.util.ConcurrentReferenceHashMap;

public class ForkJoinTaskInitWatch extends PWatch {
    static final ConcurrentReferenceHashMap<Object, Object> captureMap
            = new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public ForkJoinTaskInitWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .className("java.util.concurrent.ForkJoinTask")
                .behaviorName("<init>")
                .build();
    }

    protected void checking(ClassLoader classLoader) throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        if (advice.getTarget() instanceof TtlEnhanced) {
            return;
        }
        captureMap.put(advice.getTarget(), TransmittableThreadLocal.Transmitter.capture());
    }

    @Override
    public void onRemove(WatchData watchData) {
        captureMap.clear();
    }
}
