package org.nampython.core;

import org.nampython.support.RequestHandlerSharedData;

/**
 * All implementations of this interface will be called after each request.
 */
public interface RequestDestroyHandler {

    void destroy(RequestHandlerSharedData sharedData);
}
