package com.tmtbe.pvisual.core.trace;

import lombok.Data;

@Data
public class TraceConfig {
    private String localServiceName;
    private String zipkinEndPoint;

    public void print() {
        System.out.println("zipkinEndPoint:" + zipkinEndPoint);
        System.out.println("localServiceName:" + localServiceName);
    }
}
