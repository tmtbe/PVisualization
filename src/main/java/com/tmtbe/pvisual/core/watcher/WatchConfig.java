package com.tmtbe.pvisual.core.watcher;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WatchConfig {
    private boolean canCreateTrace;
    private String serviceName;
}
