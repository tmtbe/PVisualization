package com.tmtbe.pvisual.springboot;

import com.tmtbe.pvisual.core.watcher.PWatch;

public class DemoWatch extends PWatch {
    @Override
    protected void checking() throws Throwable {

    }

    @Override
    public String getWatchClassName() {
        return "com.example.demo.TestController";
    }

    @Override
    public String getWatchMethodName() {
        return "check";
    }
}
