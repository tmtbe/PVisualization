package com.tmtbe.pvisual.core.trace;

import lombok.Getter;
import lombok.Setter;

public enum TraceConfig {
    INSTANCE;
    @Getter
    @Setter
    private String localServiceName;
    @Getter
    @Setter
    private String zipkinEndPoint;

    public void print() {
        System.out.println("zipkinEndPoint:" + zipkinEndPoint);
        System.out.println("localServiceName:" + localServiceName);
    }
}
