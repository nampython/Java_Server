package org.nampython.base;

import org.nampython.core.DispatcherConfig;

public interface HttpSolet {

    void init(DispatcherConfig soletConfig);

    void service(HttpSoletRequest request, HttpSoletResponse response) throws Exception;

    boolean isInitialized();

    boolean hasIntercepted();

    DispatcherConfig getSoletConfig();
}
