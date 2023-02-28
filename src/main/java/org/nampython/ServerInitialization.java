package org.nampython;


import com.cyecize.ioc.MagicInjector;
import com.cyecize.ioc.config.MagicConfiguration;
import com.cyecize.ioc.services.DependencyContainer;
import org.nampython.type.ServerComponent;

public class ServerInitialization {
    public static void main(String[] args) {
        MagicConfiguration magicConfiguration = new MagicConfiguration()
                .scanning()
                .addCustomServiceAnnotation(ServerComponent.class)
                .and()
                .build();


        /**
         * Initi.. ApplicationContext to ...
         */
        final DependencyContainer dependencyContainer = MagicInjector.run(ServerInitialization.class, magicConfiguration);


    }
}
