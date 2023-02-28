package org.nampython.core;

import org.nampython.support.RequestDestroyHandler;
import org.nampython.support.RequestHandler;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface InitLoadingRequest {
    void loadRequestHandlers(List<String> requestHandlerFileNames, Map<File, URL> libURLs, Map<File, URL> apiURLs);
    List<RequestHandler> getRequestHandlers();
    List<RequestDestroyHandler> getRequestDestroyHandlers();
}