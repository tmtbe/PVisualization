package com.tmtbe.pvisual;

import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.tmtbe.pvisual.core.watcher.PVisualWatcherManager;
import com.tmtbe.pvisual.springboot.DemoWatch;
import com.tmtbe.pvisual.springboot.HttpServletWatch;

public class PVisualManager extends PVisualWatcherManager {
    public PVisualManager(ModuleEventWatcher moduleEventWatcher) {
        super(moduleEventWatcher);
    }

    @Override
    protected void prepare() {
        add(new HttpServletWatch());
        add(new DemoWatch());
    }
}
