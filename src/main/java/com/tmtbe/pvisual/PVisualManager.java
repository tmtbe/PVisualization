package com.tmtbe.pvisual;

import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PVisualWatcherManager;
import com.tmtbe.pvisual.universal.*;

public class PVisualManager extends PVisualWatcherManager {
    public PVisualManager(ModuleEventWatcher moduleEventWatcher) {
        super(moduleEventWatcher);
    }

    @Override
    protected void prepare() throws PTraceException {
        add(new HttpServletWatch());
        add(new NettyHttpWatch());
        add(new SubscriberOnNextWatch());
        add(new SubscriberInitWatch());
        add(new SqlWatch());
        add(new PreparedStatementWatch());
        add(new DemoWatch());
    }
}
