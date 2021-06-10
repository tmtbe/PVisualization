package com.tmtbe.pvisual.core.watcher;

import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;

@Data
@Builder
public class WatchConfig {
    private boolean canCreateTrace;
    private String serviceName;
    private String className;
    private String behaviorName;
    private EventWatchBuilder.PatternType patternType;
    private Consumer<EventWatchBuilder.IBuildingForClass> buildingForClass;
    private Consumer<EventWatchBuilder.IBuildingForBehavior> buildingForBehavior;
    private Consumer<EventWatchBuilder.IBuildingForWatching> buildingForWatching;

    public String getName() {
        return className + ":" + behaviorName;
    }
}
