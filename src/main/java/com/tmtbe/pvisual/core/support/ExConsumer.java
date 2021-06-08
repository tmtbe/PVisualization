package com.tmtbe.pvisual.core.support;

@FunctionalInterface
public interface ExConsumer<T> {
    void accept(T var1) throws Throwable;
}