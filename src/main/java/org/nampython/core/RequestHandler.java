package org.nampython.core;

import org.nampython.support.RequestHandlerSharedData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public interface RequestHandler {
    void init();
    boolean handleRequest(InputStream inputStream, OutputStream responseStream, RequestHandlerSharedData sharedData) throws IOException;
    int order();
}
