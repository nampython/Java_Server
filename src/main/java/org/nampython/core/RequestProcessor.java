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
public class RequestProcessor implements RequestHandler {

    private final ConfigCenter configCenter;

    @Autowired
    public RequestProcessor(ConfigCenter configCenter) {
        this.configCenter = configCenter;
    }

    @Override
    public void init() {
        System.out.println("Calling init of RequestProcessor");
    }

    @Override
    public boolean handleRequest(InputStream inputStream, OutputStream responseStream, RequestHandlerShareData sharedData) throws IOException {
        System.out.println("Calling handleRequest method of " + RequestProcessor.class.getSimpleName());
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public int order() {
        return configCenter.getConfigValue(ConfigValue.REQUEST_PROCESSOR_ORDER, int.class);
    }
}
