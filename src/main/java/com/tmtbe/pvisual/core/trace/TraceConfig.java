package com.tmtbe.pvisual.core.trace;

import com.alibaba.jvm.sandbox.api.http.printer.Printer;
import lombok.Data;

@Data
public class TraceConfig {
    private String localServiceName;
    private String zipkinEndPoint;
    private TracingLevel tracingLevel;

    public void print(Printer printer) {
        printer.println("ZipkinEndPoint: " + zipkinEndPoint);
        printer.println("LocalServiceName: " + localServiceName);
        printer.println("TracingLevel: " + tracingLevel.name());
    }
}
