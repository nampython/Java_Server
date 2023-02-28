package org.nampython.config;

public interface ConfigCenter {
    <T> T getConfigParam(ConfigValue paramName, Class<T> type);
    Object getConfigParam(ConfigValue paramName);
    <T> T getConfigParam(String paramName, Class<T> type);

}
