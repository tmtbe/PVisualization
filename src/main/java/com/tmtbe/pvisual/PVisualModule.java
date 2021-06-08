package com.tmtbe.pvisual;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleLifecycle;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.http.printer.ConcurrentLinkedQueuePrinter;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleManager;
import com.tmtbe.pvisual.core.support.ParamSupported;
import com.tmtbe.pvisual.core.trace.TraceConfig;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;

@MetaInfServices(Module.class)
@Information(id = "PVisual", version = "0.1.0", author = "jincheng.zhang")
public class PVisualModule extends ParamSupported implements Module, ModuleLifecycle {
    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Resource
    private ModuleManager moduleManager;

    private PVisualManager pVisualManager;

    @Override
    public void onLoad() throws Throwable {
        pVisualManager = new PVisualManager(moduleEventWatcher, null);
    }

    @Override
    public void onUnload() throws Throwable {

    }

    @Override
    public void onActive() throws Throwable {
        System.out.println("PVisual Active");
        TraceConfig.INSTANCE.setZipkinEndPoint("http://localhost:9411/api/v2/spans");
        TraceConfig.INSTANCE.setLocalServiceName("Demo");
        TraceConfig.INSTANCE.print();
        pVisualManager.enhance();
    }

    @Override
    public void onFrozen() throws Throwable {

    }

    @Override
    public void loadCompleted() {

    }

    @Command("start")
    public void start(final Map<String, String> param, final PrintWriter writer) {
        String endpoint = "http://{0}:{1}/api/v2/spans";
        String host = param.getOrDefault("host", "localhost");
        String port = param.getOrDefault("port", "9411");
        TraceConfig.INSTANCE.setZipkinEndPoint(MessageFormat.format(endpoint, host, port));
        TraceConfig.INSTANCE.setLocalServiceName(param.getOrDefault("name", "Demo"));
        TraceConfig.INSTANCE.print();
        pVisualManager.setPrinter(new ConcurrentLinkedQueuePrinter(writer));
        pVisualManager.enhance();
    }

    @Command("end")
    public void end(final Map<String, String> param, final PrintWriter writer) {
        pVisualManager.setPrinter(new ConcurrentLinkedQueuePrinter(writer));
        pVisualManager.removeAll();
    }
}
