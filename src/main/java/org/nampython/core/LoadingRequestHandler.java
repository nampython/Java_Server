package org.nampython.core;


import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import com.cyecize.ioc.models.ServiceDetails;
import org.nampython.support.IocCenter;


import java.io.File;
import java.net.URL;
import java.util.*;

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
        Collection<ServiceDetails> implementations = IocCenter.getServerDependencyContainer().getImplementations(RequestHandler.class);
        List<RequestHandler> requestHandlerInstances = new ArrayList<>();
        for (ServiceDetails implementation : implementations) {
            RequestHandler requestHandlerInstance = (RequestHandler) implementation.getInstance();
            requestHandlerInstances.add(requestHandlerInstance);
        }
        requestHandlerInstances.sort(Comparator.comparingInt(RequestHandler::order));
        for (RequestHandler requestHandlerInstance : requestHandlerInstances) {
            requestHandlerInstance.init();
        }
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
