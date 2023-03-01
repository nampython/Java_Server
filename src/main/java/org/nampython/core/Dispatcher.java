package org.nampython.core;


import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import org.nampython.config.ConfigCenter;
import org.nampython.config.ConfigValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class Dispatcher implements RequestHandler {
    private final ConfigCenter configCenter;

    @Autowired
    public Dispatcher(ConfigCenter configCenter) {
        this.configCenter = configCenter;
    }

    @Override
    public void init() {
        System.out.println("Calling init of Dispatcher");
    }

    @Override
    public boolean handleRequest(InputStream inputStream, OutputStream responseStream, RequestHandlerShareData sharedData) throws IOException {
        System.out.println("Calling handleRequest method of " + Dispatcher.class.getSimpleName());
        return false;
    }

    @Override
    public int order() {
        return this.configCenter.getConfigValue(ConfigValue.DISPATCHER_ORDER.name(), int.class);
    }
}
