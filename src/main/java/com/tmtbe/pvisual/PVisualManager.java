package com.tmtbe.pvisual;

import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.tmtbe.pvisual.core.watcher.PVisualWatcherManager;
import com.tmtbe.pvisual.universal.DemoWatch;
import com.tmtbe.pvisual.universal.HttpServletWatch;
import com.tmtbe.pvisual.universal.PreparedStatementWatch;
import com.tmtbe.pvisual.universal.SqlWatch;

public class PVisualManager extends PVisualWatcherManager {
    public PVisualManager(ModuleEventWatcher moduleEventWatcher) {
        super(moduleEventWatcher);
    }

    @Override
    protected void prepare() {
        add(new HttpServletWatch());
        add(new SqlWatch());
        add(new PreparedStatementWatch());
        add(new DemoWatch());
    }
}
