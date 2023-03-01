package org.nampython.core;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import org.nampython.config.ConfigCenter;
import org.nampython.config.ConfigValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
@Service
public class ResourceHandler implements RequestHandler {
    private final ConfigCenter configCenter;

    @Autowired
    public ResourceHandler(ConfigCenter configCenter) {
        this.configCenter = configCenter;
    }


    @Override
    public void init() {
        System.out.println("Calling init of ResourceHandler");
    }

    @Override
    public boolean handleRequest(InputStream inputStream, OutputStream responseStream, RequestHandlerShareData sharedData) throws IOException {
        System.out.println("Calling handleRequest method of " + ResourceHandler.class.getSimpleName());
        return false;
    }

    @Override
    public int order() {
        return this.configCenter.getConfigValue(ConfigValue.RESOURCE_HANDLER_ORDER.name(), int.class);
    }
}
