package org.nampython.core;

import com.cyecize.ioc.annotations.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
@Service
public class ResourceHandler implements RequestHandler {

    @Override
    public void init() {
        System.out.println("Calling init of ResourceHandler");
    }

    @Override
    public boolean handleRequest(InputStream inputStream, OutputStream responseStream, RequestHandlerShareData sharedData) throws IOException {
        return false;
    }

    @Override
    public int order() {
        return 0;
    }
}
