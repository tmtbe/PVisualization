package com.tmtbe.pvisual.core.trace;

import com.tmtbe.pvisual.core.support.PTraceException;

public enum TracingLevel {
    PERFORMANCE(0), NORMAL(1), INFO(2), MORE(3);
    private final int level;

    TracingLevel(int level) {
        this.level = level;
    }

    public static TracingLevel nameOf(int level) throws PTraceException {
        switch (level) {
            case 0:
                return PERFORMANCE;
            case 1:
                return NORMAL;
            case 2:
                return INFO;
            case 3:
                return MORE;
        }
        throw new PTraceException("not support tracing level");
    }
}
