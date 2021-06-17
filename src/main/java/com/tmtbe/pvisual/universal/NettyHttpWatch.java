package com.tmtbe.pvisual.universal;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.tmtbe.pvisual.core.support.PTraceException;
import com.tmtbe.pvisual.core.watcher.PWatch;
import com.tmtbe.pvisual.core.watcher.WatchConfig;
import reactor.netty.Connection;
import reactor.netty.ConnectionObserver;
import reactor.netty.http.server.HttpServerState;

public class NettyHttpWatch extends PWatch {
    public NettyHttpWatch() throws PTraceException {
    }

    @Override
    protected WatchConfig createWatchConfig() {
        return WatchConfig.builder()
                .serviceName("netty")
                .className("reactor.netty.ConnectionObserver")
                .behaviorName("onStateChange")
                .canCreateTrace(true)
                .build();
    }

    @Override
    protected void checking(ClassLoader classLoader) throws Throwable {

    }

    @Override
    protected void before(Advice advice) throws Throwable {
        Connection connection = (Connection) advice.getParameterArray()[0];
        ConnectionObserver.State newState = (ConnectionObserver.State) advice.getParameterArray()[1];
        if (newState == HttpServerState.REQUEST_RECEIVED) {
            startSpanWithAsync(advice, connection, null);
        } else if (newState == HttpServerState.DISCONNECTING) {
            finishSpanWithAsync(advice, connection, null);
        }
    }

    @Override
    protected void after(Advice advice) throws Throwable {

    }
}
