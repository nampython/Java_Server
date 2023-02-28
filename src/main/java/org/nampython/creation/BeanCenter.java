package org.nampython.creation;

import com.cyecize.ioc.annotations.Bean;
import com.cyecize.ioc.annotations.PostConstruct;
import org.nampython.config.*;
import org.nampython.type.ServerComponent;

import java.util.Map;


@ServerComponent
public class BeanCenter {
    public static Integer port;
    public static Map<String, Object> configs;
    public static Class<?> mainClass;

    @PostConstruct
    public void init() {
        this.initConfigs();
    }

    /**
     *
     */
    private void initConfigs() {
    }


    /**
     *
     */
    @Bean
    public ConfigHandler configHandler() {
        final ConfigCenter configHandler = new AdditionalConfig(configs);
        if (configHandler.getConfigValue(ConfigValue.SERVER_PORT) == CorePool.EMPTY_PORT) {

        }
    }
}
