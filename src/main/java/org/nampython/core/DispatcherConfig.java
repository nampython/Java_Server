package org.nampython.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DispatcherConfig {
    private final Map<String, Object> attributes;

    public DispatcherConfig() {
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String name, Object attribute) {
        this.attributes.put(name, attribute);
    }


    public void setIfMissing(String name, Object attribute) {
        this.attributes.putIfAbsent(name, attribute);
    }


    public void deleteAttribute(String name) {
        this.attributes.remove(name);
    }


    public boolean hasAttribute(String name) {
        return this.attributes.containsKey(name);
    }


    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }


    public Map<String, Object> getAllAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }
}
