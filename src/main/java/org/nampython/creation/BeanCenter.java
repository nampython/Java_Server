package org.nampython.creation;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Bean;
import com.cyecize.ioc.annotations.PostConstruct;
import org.nampython.config.*;
import org.nampython.support.JarFileUnzip;
import org.nampython.support.JarFileUnzipImplement;
import org.nampython.type.ServerComponent;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;


@ServerComponent
public class BeanCenter {
    private final static String EXTENSION_JAR = ".jar";
    public static Integer port;
    public static Map<String, Object> configs;
    public static Class<?> mainClass;
    private final JarFileUnzip jarFileUnzip;

    @Autowired
    public BeanCenter(JarFileUnzip jarFileUnzip) {
        this.jarFileUnzip = jarFileUnzip;
    }

    @PostConstruct
    public void init() {
        this.initConfigs();
    }

    /**
     *
     */
    private void initConfigs() {
//        configs.put(JavacheConfigValue.APP_COMPILE_OUTPUT_DIR_NAME.name(), "");

    }

    /**
     *
     */
    @Bean
    public ConfigCenter configHandler() {
        final ConfigCenter configCenter = new AdditionalConfig(configs);
        int port;
        if (configCenter.getConfigValue(ConfigValue.SERVER_PORT, int.class) == CorePool.EMPTY_PORT) {
            if (BeanCenter.port != null) {
                port = BeanCenter.port;
            } else {
                port = CorePool.DEFAULT_SERVER_PORT;
            }
            configCenter.addConfigParam(ConfigValue.SERVER_PORT, port);
        }
        configCenter.addConfigParam(ConfigValue.SERVER_WORKING_DIRECTORY, this.getWorkingDirectory());
        return configCenter;
    }

    /**
     *
     * @return
     */
    private String getWorkingDirectory() {
        String workingDir;
        try {
            final URI uri = mainClass.getProtectionDomain().getCodeSource().getLocation().toURI();
            workingDir = Path.of(uri).toString();
            if (workingDir.endsWith(EXTENSION_JAR)) {
                File file = new File(workingDir);
                this.jarFileUnzip.unzipJar(file, false, workingDir.replace(".jar", ""));
                workingDir = workingDir.replace(".jar", "");
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(String.format("Working Directory: %s", workingDir));
        return null;
    }
}
