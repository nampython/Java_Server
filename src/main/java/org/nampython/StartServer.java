package org.nampython;


import com.cyecize.ioc.MagicInjector;
import com.cyecize.ioc.config.MagicConfiguration;
import com.cyecize.ioc.services.DependencyContainer;
import org.nampython.config.ConfigCenter;
import org.nampython.config.ConfigValue;
import org.nampython.core.InitLoadingRequest;
import org.nampython.core.Server;
import org.nampython.core.ServerImplement;
import org.nampython.creation.BeanCenter;
import org.nampython.support.IocCenter;
import org.nampython.support.Logger;
import org.nampython.type.ServerComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StartServer {

    public static void main(String[] args) {
        startServer(8080, StartServer.class);
    }

    public static void startServer(Integer port, Class<?> serverInitializationClass) {
        startServer(port, new HashMap<>(), serverInitializationClass);

    }

    public static void startServer(Integer port, Map<String, Object> config, Class<?> mainClass) {
        startServer(port, config, mainClass, null);
    }

    private static void startServer(Integer port, Map<String, Object> config, Class<?> serverInitializationClass, Runnable onServerLoadedEvent) {
        Logger loggingService = null;
        try {
            MagicConfiguration magicConfiguration = new MagicConfiguration()
                    .scanning()
                    .addCustomServiceAnnotation(ServerComponent.class)
                    .and()
                    .build();
            BeanCenter.port = 8080;
            BeanCenter.mainClass = StartServer.class;
            BeanCenter.configs = new HashMap<>();

            /**
             * Initi.. ApplicationContext to ...
             */
            final DependencyContainer dependencyContainer = MagicInjector.run(StartServer.class, magicConfiguration);
            IocCenter.setServerDependencyContainer(dependencyContainer);
            IocCenter.setRequestHandlersDependencyContainer(dependencyContainer);
            dependencyContainer.getService(InitLoadingRequest.class).loadRequestHandlers(new ArrayList<>(), null, null);
            loggingService = dependencyContainer.getService(Logger.class);

            final Server server = new ServerImplement(
                    dependencyContainer.getService(InitLoadingRequest.class),
                    dependencyContainer.getService(ConfigCenter.class).getConfigValue(ConfigValue.SERVER_PORT, int.class)
            );
            if (onServerLoadedEvent != null) {
                onServerLoadedEvent.run();
            }
            server.run();
        } catch (Exception ex) {
            if (loggingService != null) {
                loggingService.printStackTrace(ex);
            } else {
                ex.printStackTrace();
            }
            System.exit(1);
        }
    }
}
