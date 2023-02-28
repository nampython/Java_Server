package org.nampython.config;

import java.util.Map;

public class AdditionalConfig extends ConfigHandler{
    public AdditionalConfig(Map<String, Object> configs) {
        super();
        super.configParameters.putAll(configs);
    }
}
