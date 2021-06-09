package com.tmtbe.pvisual.core.watcher;

import com.alibaba.jvm.sandbox.api.http.printer.Printer;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.tmtbe.pvisual.core.support.ProgressPrinter;
import lombok.Data;

@Data
public class WatchData {
    private String name;
    private Runnable runnable;
    private Integer watchId;
    private RemoveHandle removeHandle;

    public void run() {
        this.runnable.run();
    }

    public boolean isRunning() {
        return watchId != null;
    }

    public void delete(ModuleEventWatcher moduleEventWatcher, Printer printer) {
        removeHandle.onRemove(this);
        if (isRunning()) {
            if (printer != null) {
                moduleEventWatcher.delete(getWatchId(), new ProgressPrinter(printer, name));
            } else {
                moduleEventWatcher.delete(getWatchId());
            }
        }
    }
}
