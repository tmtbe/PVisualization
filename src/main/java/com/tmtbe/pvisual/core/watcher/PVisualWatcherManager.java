package com.tmtbe.pvisual.core.watcher;

import com.alibaba.jvm.sandbox.api.http.printer.Printer;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.tmtbe.pvisual.core.support.ProgressPrinter;
import lombok.AllArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
public class PVisualWatcherManager {
    private final ModuleEventWatcher moduleEventWatcher;
    private final HashMap<String, Integer> iWatchHashMap = new HashMap<>();
    private Printer printer;

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public boolean add(PWatch pWatch) {
        if (iWatchHashMap.containsKey(pWatch.getName())) {
            return false;
        }
        pWatch.check(() -> {
        });
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
        int watchId = iBuildingForWatching.onWatch(pWatch).getWatchId();
        iWatchHashMap.put(pWatch.getName(), watchId);
        return true;
    }

    public void remove(String watcherName) {
        Integer watchId = iWatchHashMap.get(watcherName);
        if (watchId != null) {
            if (printer != null) {
                moduleEventWatcher.delete(watchId, new ProgressPrinter(printer, watcherName));
            } else {
                moduleEventWatcher.delete(watchId);
            }
        }
    }

    public void removeAll() {
        iWatchHashMap.forEach((watchName, watchId) -> {
            if (printer != null) {
                moduleEventWatcher.delete(watchId, new ProgressPrinter(printer, watchName));
            } else {
                moduleEventWatcher.delete(watchId);
            }
        });
        iWatchHashMap.clear();
    }
}
