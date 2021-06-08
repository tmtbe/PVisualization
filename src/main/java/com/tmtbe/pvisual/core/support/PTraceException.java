package com.tmtbe.pvisual.core.support;

public class PTraceException extends Exception {
    public PTraceException() {
    }

    public PTraceException(String message) {
        super(message);
    }

    public PTraceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PTraceException(Throwable cause) {
        super(cause);
    }

    protected PTraceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
