package org.nampython.core;


/**
 * All implementations of this interface will be called after each request.
 */
public interface RequestDestroyHandler {

    void destroy(RequestHandlerShareData sharedData);
}
