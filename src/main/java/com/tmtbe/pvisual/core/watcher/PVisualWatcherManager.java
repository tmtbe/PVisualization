package com.tmtbe.pvisual.core.watcher;

import com.alibaba.jvm.sandbox.api.http.printer.ConcurrentLinkedQueuePrinter;
import com.alibaba.jvm.sandbox.api.http.printer.Printer;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.support.ProgressPrinter;
import com.tmtbe.pvisual.core.thread.*;
import com.tmtbe.pvisual.core.trace.TraceConfig;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.HashMap;

public abstract class PVisualWatcherManager {
    private boolean isEnhance = false;
    private final ModuleEventWatcher moduleEventWatcher;
    private final HashMap<String, WatchData> watchDataMap = new HashMap<>();
    @Getter
    private TraceConfig traceConfig;
    private Printer printer;

    public PVisualWatcherManager(ModuleEventWatcher moduleEventWatcher) {
        this.moduleEventWatcher = moduleEventWatcher;
    }

    protected void prepare0() {
        add(new ExecutorServiceWatch());
        add(new ExecutorWatch());
        add(new ForkJoinTaskWatch());
        add(new ForkJoinTaskInitWatch());
        add(new ForkJoinWorkerThreadFactoryWatch());
        add(new ScheduledExecutorServiceWatch());
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public WatchData add(PWatch pWatch) {
        if (watchDataMap.containsKey(pWatch.getName())) {
            return null;
        }
        pWatch.setPVisualWatcherManager(this);
        WatchData watchData = new WatchData();
        watchData.setName(pWatch.getName());
        watchData.setRunnable(() -> {
            EventWatchBuilder.IBuildingForClass iBuildingForClass = new EventWatchBuilder(moduleEventWatcher, pWatch.getPatternType())
                    .onClass(pWatch.getWatchClassName());
            pWatch.buildingForClass(iBuildingForClass);
            EventWatchBuilder.IBuildingForBehavior iBuildingForBehavior = iBuildingForClass.onBehavior(pWatch.getWatchMethodName());
            pWatch.buildingForBehavior(iBuildingForBehavior);
            EventWatchBuilder.IBuildingForWatching iBuildingForWatching = iBuildingForBehavior.onWatching();
            pWatch.buildingForWatching(iBuildingForWatching);
            if (printer != null) {
                iBuildingForWatching.withProgress(new ProgressPrinter(printer, pWatch.getName()));
            }
            watchData.setWatchId(iBuildingForWatching.onWatch(pWatch.adviceListener).getWatchId());
        });
        watchData.setRemoveHandle(pWatch);
        watchDataMap.put(watchData.getName(), watchData);
        return watchData;
    }

    public void dynamicAdd(PWatch pWatch) throws Throwable {
        pWatch.checking();
        if (this.traceConfig == null) {
            throw new PTraceException("TraceConfig is not exist");
        }
        if (!this.isEnhance) {
            throw new PTraceException("Not enhance");
        }
        WatchData watchData = add(pWatch);
        watchData.run();
    }

    protected abstract void prepare();

    public void enhance(final PrintWriter writer, TraceConfig traceConfig) {
        this.traceConfig = traceConfig;
        traceConfig.print();
        if (writer != null) {
            setPrinter(new ConcurrentLinkedQueuePrinter(writer));
        }
        prepare0();
        prepare();
        watchDataMap.values().forEach(WatchData::run);
        this.isEnhance = true;
    }

    public void remove(String watcherName, final PrintWriter writer) {
        if (writer != null) {
            setPrinter(new ConcurrentLinkedQueuePrinter(writer));
        }
        WatchData watchData = watchDataMap.remove(watcherName);
        watchData.delete(moduleEventWatcher, printer);
    }

    public void unEnhance(final PrintWriter writer) {
        if (writer != null) {
            setPrinter(new ConcurrentLinkedQueuePrinter(writer));
        }
        watchDataMap.forEach((watchName, watchData) -> watchData.delete(moduleEventWatcher, printer));
        watchDataMap.clear();
        isEnhance = false;
    }
}
