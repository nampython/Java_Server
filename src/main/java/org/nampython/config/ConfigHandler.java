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
        try {
//            this.loadRequestHandlerConfig();
            this.initDefaultConfigParams();
//            this.initConfigParams();
//            this.applyEnvironmentVariables();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    private void initDefaultConfigParams() {
        this.configParameters = new HashMap<>();
        this.configParameters.put(ConfigValue.SERVER_PORT.name(), CorePool.EMPTY_PORT);

    }

    private void initConfigParams() {

    }
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getConfigParam(String paramName, Class<T> type) {
        return (T) this.configParameters.get(paramName);
    }

    @Override
    public <T> T getConfigParam(ConfigValue paramName, Class<T> type) {
        return this.getConfigParam(paramName.name(), type);
    }

    @Override
    public Object getConfigParam(ConfigValue paramName) {
        return null;
    }
}
