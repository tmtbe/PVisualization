package com.tmtbe.pvisual.core.watcher;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.tmtbe.pvisual.core.support.ExRunnable;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PAdviceListener extends AdviceListener {
    private final PWatch pWatch;
    protected Boolean isCheckSuccess = null;

    public PAdviceListener(PWatch pWatch) {
        this.pWatch = pWatch;
    }

    /**
     * 检查是否满足条件，满足会执行runnable
     *
     * @param runnable runnable
     */
    @SneakyThrows
    public void check(@NonNull ExRunnable runnable) {
        if (isCheckSuccess == null) {
            try {
                pWatch.checking();
                isCheckSuccess = true;
            } catch (Throwable e) {
                isCheckSuccess = false;
                log.warn(pWatch.getName() + " is check failed");
            }
        }
        if (isCheckSuccess) {
            runnable.run();
        }
    }

    /**
     * 尝试进行Check
     */
    public void tryCheck() {
        try {
            pWatch.checking();
            isCheckSuccess = true;
        } catch (Throwable e) {
        }
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        check(() -> pWatch.before(advice));
    }

    @Override
    protected void after(Advice advice) throws Throwable {
        check(() -> pWatch.after(advice));
    }
}
