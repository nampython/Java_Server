package org.nampython.base;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HttpSession {
    private final Map<String, Object> sessionAttributes;
    private final String sessionId;
    private boolean isSessionValid;

    public HttpSession() {
        this.isSessionValid = true;
        this.sessionId = UUID.randomUUID().toString();
        this.sessionAttributes = new HashMap<>();
    }


    public void invalidate() {
        this.isSessionValid = false;
        this.sessionAttributes.clear();
    }


    public void addAttribute(String name, Object attribute) {
        this.sessionAttributes.put(name, attribute);
    }


    public boolean isValid() {
        return this.isSessionValid;
    }


    public String getId() {
        return this.sessionId;
    }


    public Object getAttribute(String key) {
        return this.sessionAttributes.get(key);
    }


    public Map<String, Object> getAttributes() {
        return this.sessionAttributes;
    }
}
