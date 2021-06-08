package com.tmtbe.pvisual;

import com.alibaba.jvm.sandbox.api.http.printer.Printer;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.tmtbe.pvisual.core.watcher.PVisualWatcherManager;
import com.tmtbe.pvisual.thread.*;

public class PVisualManager extends PVisualWatcherManager {
    public PVisualManager(ModuleEventWatcher moduleEventWatcher, Printer printer) {
        super(moduleEventWatcher, printer);
    }

    public void enhance() {
        add(new ExecutorServiceWatch());
        add(new ExecutorWatch());
        add(new ForkJoinTaskWatch());
        add(new ForkJoinTaskInitWatch());
        add(new ForkJoinWorkerThreadFactoryWatch());
        add(new ScheduledExecutorServiceWatch());
    }
}
