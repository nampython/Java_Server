package org.nampython.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler implements ConfigCenter {

    protected Map<String, Object> configParameters;


    public ConfigHandler() {
        this.init();
    }

    /**
     *
     */
    private void init() {
//        try {
//            this.loadRequestHandlerConfig();
        this.initDefaultConfigParams();
//            this.initConfigParams();
//            this.applyEnvironmentVariables();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     *
     */
    private void initDefaultConfigParams() {
        this.configParameters = new HashMap<>();
        this.configParameters.put(ConfigValue.SERVER_PORT.name(), CorePool.EMPTY_PORT);
        this.configParameters.put(ConfigValue.MAX_REQUEST_SIZE.name(), Integer.MAX_VALUE);
        this.configParameters.put(ConfigValue.RESOURCE_HANDLER_ORDER.name(), 1);
        this.configParameters.put(ConfigValue.DISPATCHER_ORDER.name(), 2);
        this.configParameters.put(ConfigValue.FALLBACK_HANDLER_ORDER.name(), Integer.MAX_VALUE);
        this.configParameters.put(ConfigValue.REQUEST_PROCESSOR_ORDER.name(), Integer.MIN_VALUE);
        this.configParameters.put(ConfigValue.PRINT_EXCEPTIONS.name(), true);

    }

    private void initConfigParams() {

    }

    /**
     * @param configKey
     * @param type
     * @param <T>
     * @return
     */
    @Override
    public <T> T getConfigValue(Enum<? extends ConfigValue> configKey, Class<T> type) {
        return this.getConfigValue(configKey.name(), type);
    }


    /**
     * @param paramName
     * @param type
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getConfigValue(String paramName, Class<T> type) {
        return (T) this.configParameters.get(paramName);
    }

    /**
     * @param configKey
     * @param configValue
     */
    @Override
    public void addConfigParam(Enum<? extends ConfigValue> configKey, Object configValue) {
        this.addConfigParam(configKey.name(), configValue);
    }

    /**
     * @param name
     * @param value
     */
    @Override
    public void addConfigParam(String name, Object value) {
        this.configParameters.put(name, value);
    }
}
