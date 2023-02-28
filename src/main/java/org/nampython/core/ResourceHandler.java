package org.nampython.core;

import com.cyecize.ioc.annotations.Service;
import org.nampython.support.RequestHandlerSharedData;

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

    }

    @Override
    public boolean handleRequest(InputStream inputStream, OutputStream responseStream, RequestHandlerSharedData sharedData) throws IOException {
        return false;
    }

    @Override
    public int order() {
        return 0;
    }
}
