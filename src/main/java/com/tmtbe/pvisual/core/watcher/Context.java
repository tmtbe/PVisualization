package com.tmtbe.pvisual.core.watcher;

import brave.Span;
import com.tmtbe.pvisual.core.trace.PTracer;
import lombok.Data;

import java.util.HashMap;

@Data
public class Context {
    private Span span;
    private PTracer pTracer;
    private HashMap<String, Object> hashMap = new HashMap<>();

    public void put(String key, Object value) {
        hashMap.put(key, value);
    }

    public <T> T get(String key) {
        return (T) hashMap.get(key);
    }
}
