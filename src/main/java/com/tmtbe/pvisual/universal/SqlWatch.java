package com.tmtbe.pvisual.universal;

import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;

public class SqlWatch extends PWatch {

    @Override
    protected WatchConfig getWatchConfig() {
        return WatchConfig.builder().serviceName("DB").build();
    }

    @Override
    protected void checking() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "java.sql.Connection";
    }

    @Override
    public String getWatchMethodName() {
        return "prepareStatement";
    }
}
