package org.nampython.config;

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
        this.configParameters.put(ConfigValue.SERVER_PORT.name(), ConstantsPool.EMPTY_PORT);
        this.configParameters.put(ConfigValue.MAX_REQUEST_SIZE.name(), Integer.MAX_VALUE);
        this.configParameters.put(ConfigValue.RESOURCE_HANDLER_ORDER.name(), 1);
        this.configParameters.put(ConfigValue.DISPATCHER_ORDER.name(), 2);
        this.configParameters.put(ConfigValue.FALLBACK_HANDLER_ORDER.name(), Integer.MAX_VALUE);
        this.configParameters.put(ConfigValue.REQUEST_PROCESSOR_ORDER.name(), Integer.MIN_VALUE);
        this.configParameters.put(ConfigValue.PRINT_EXCEPTIONS.name(), true);
        this.configParameters.put(ConfigValue.MAIN_APP_JAR_NAME.name(), "ROOT");
        this.configParameters.put(ConfigValue.WORKING_DIRECTORY.name(), ConstantsPool.WORKING_DIRECTORY);
        this.configParameters.put(ConfigValue.ASSETS_DIR_NAME.name(), "assets/");
        this.configParameters.put(ConfigValue.WEB_APPS_DIR_NAME.name(), "webapps/");
        this.configParameters.put(ConfigValue.APP_COMPILE_OUTPUT_DIR_NAME.name(), "classes");
        this.configParameters.put(ConfigValue.APP_RESOURCES_DIR_NAME.name(), "webapp");
        this.configParameters.put(ConfigValue.RESOURCE_CACHING_EXPRESSION.name(), ConstantsPool.DEFAULT_CACHING_EXPRESSION);
        this.configParameters.put(ConfigValue.ENABLE_RESOURCE_CACHING.name(), true);
        this.configParameters.put(ConfigValue.APPLICATION_DEPENDENCIES_FOLDER_NAME.name(), "lib");
        this.configParameters.put(ConfigValue.BROCCOLINA_TRACK_RESOURCES.name(), true);
        this.configParameters.put(ConfigValue.LOGS_DIR_NAME.name(), "logs/");

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

    @SuppressWarnings("unchecked")

    @Override
    public <T> T getConfigValue(Enum<? extends ConfigValue> paramName) {
        return (T) this.configParameters.get(paramName.name());
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

    @Override
    public String getConfigParamString(Enum<? extends ConfigValue> paramName) {
        return this.getConfigParamString(paramName.name());
    }

    @Override
    public String getConfigParamString(String paramName) {
        final Object configParam = this.getConfigValue(paramName, Object.class);
        if (configParam != null) {
            return configParam.toString();
        }
        return null;
    }
}
