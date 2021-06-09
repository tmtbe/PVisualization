package com.tmtbe.pvisual.core.trace;

import lombok.Data;

@Data
public class TraceConfig {
    private String localServiceName;
    private String zipkinEndPoint;
    private TracingLevel tracingLevel;

    public void print() {
        System.out.println("ZipkinEndPoint: " + zipkinEndPoint);
        System.out.println("LocalServiceName: " + localServiceName);
        System.out.println("TracingLevel: " + tracingLevel.name());
    }
}
