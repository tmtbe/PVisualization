package com.tmtbe.pvisual;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleLifecycle;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleManager;
import com.tmtbe.pvisual.core.DynamicWatch;
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
        pVisualManager = new PVisualManager(moduleEventWatcher);
    }

    @Override
    public void onUnload() throws Throwable {

    }

    @Override
    public void onActive() throws Throwable {
        System.out.println("PVisual Active");
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
        TraceConfig traceConfig = new TraceConfig();
        traceConfig.setZipkinEndPoint(MessageFormat.format(endpoint, host, port));
        traceConfig.setLocalServiceName(param.getOrDefault("name", "Demo"));
        pVisualManager.enhance(writer, traceConfig);
    }

    @Command("end")
    public void end(final Map<String, String> param, final PrintWriter writer) {
        pVisualManager.unEnhance(writer);
    }

    @Command("end")
    public void add(final Map<String, String> param, final PrintWriter writer) {
        String str = param.get("d");
        DynamicWatch dynamicWatch = new DynamicWatch(str);
        try {
            pVisualManager.dynamicAdd(dynamicWatch);
        } catch (Throwable e) {
            writer.println(e.getMessage());
            writer.flush();
        }
    }
}
