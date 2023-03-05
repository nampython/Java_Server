package org.nampython.core;


import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import com.cyecize.ioc.models.ServiceDetails;
import org.nampython.support.IocCenter;


import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class LoadingRequestHandler implements InitLoadingRequest {
    private final LinkedList<RequestHandler> requestHandlers;
    private final List<RequestDestroyHandler> destroyHandlers;

    @Autowired
    public LoadingRequestHandler() {
        this.requestHandlers = new LinkedList<>();
        this.destroyHandlers = new ArrayList<>();
    }

    /**
     * @param requestHandlerFileNames
     * @param libURLs
     * @param apiURLs
     */
    @Override
    public void loadRequestHandlers(List<String> requestHandlerFileNames, Map<File, URL> libURLs, Map<File, URL> apiURLs) {
        this.requestHandlers.addAll(IocCenter.getServerDependencyContainer().getImplementations(RequestHandler.class)
                .stream()
                .map(sd -> (RequestHandler) sd.getInstance())
                .sorted(Comparator.comparingInt(RequestHandler::order))
                .peek(RequestHandler::init)
                .collect(Collectors.toList()));
        this.destroyHandlers.addAll(IocCenter.getServerDependencyContainer().getImplementations(RequestDestroyHandler.class)
                .stream()
                .map(sd -> (RequestDestroyHandler) sd.getInstance())
                .collect(Collectors.toList())
        );
    }

    @Override
    public List<RequestHandler> getRequestHandlers() {
        return this.requestHandlers;
    }

    @Override
    public List<RequestDestroyHandler> getRequestDestroyHandlers() {
        return this.destroyHandlers;
    }
}
