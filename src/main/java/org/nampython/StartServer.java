package org.nampython;


import com.cyecize.ioc.MagicInjector;
import com.cyecize.ioc.config.MagicConfiguration;
import com.cyecize.ioc.services.DependencyContainer;
import org.nampython.creation.BeanCenter;
import org.nampython.type.ServerComponent;

import java.util.HashMap;

public class StartServer {
    public static void main(String[] args) {
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
        int debug = 0;
//        ServerInitialization.startServer(8080, StartServer.class);
    }
}
