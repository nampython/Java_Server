package org.nampython.core;


import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import com.cyecize.ioc.models.ServiceDetails;
import org.nampython.base.BaseHttp;
import org.nampython.base.Controller;
import org.nampython.support.IocCenter;


import java.io.File;
import java.net.URL;
import java.util.*;

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
        this.handlerRequestHandlers();
        this.handlerRequestDestroyHandlers();
    }

    /**
     *
     */
    private void handlerRequestDestroyHandlers() {
        for (ServiceDetails implementation : IocCenter.getServerDependencyContainer().getImplementations(RequestDestroyHandler.class)) {
            this.destroyHandlers.add((RequestDestroyHandler) implementation.getInstance());
        }
    }

    /**
     * //TODO
     */
    private void handlerRequestHandlers() {
        Collection<ServiceDetails> implementationOfRequestHandlers =  IocCenter.getServerDependencyContainer().getImplementations(RequestHandler.class);
        Iterator<ServiceDetails> serviceDetailsIterator = implementationOfRequestHandlers.iterator();

        while (serviceDetailsIterator.hasNext()) {
            ServiceDetails next = serviceDetailsIterator.next();
            RequestHandler instance = (RequestHandler) next.getInstance();
            this.requestHandlers.add(instance);
        }
        requestHandlers.sort(Comparator.comparingInt(RequestHandler::order));
        this.callInitRequestHandler(requestHandlers);
    }

    /**
     * We need to call the init method of each implement {@link RequestHandler}. But the specified init method is just called
     * in the {@link org.nampython.core.center.Dispatcher} and {@link org.nampython.core.center.ResourceHandler}
     * At {@link org.nampython.core.center.Dispatcher} we will find all class that having {@link Controller} annotation and
     * extends {@link BaseHttp}. Besides, We need also to decide the application's name.
     * //TODO: Research the init method in the ResourceHandler class
     * @param requestHandlers List of the requestHandlers
     */
    private void callInitRequestHandler(List<RequestHandler> requestHandlers) {
        Iterator<RequestHandler> requestHandlerIterator = this.requestHandlers.iterator();
        while (requestHandlerIterator.hasNext()) {
            RequestHandler requestHandler = requestHandlerIterator.next();
            requestHandler.init();
        }
    }


    /**
     * //TODO
     * @return
     */
    @Override
    public List<RequestHandler> getRequestHandlers() {
        return this.requestHandlers;
    }

    /**
     * //TODO
     * @return
     */
    @Override
    public List<RequestDestroyHandler> getRequestDestroyHandlers() {
        return this.destroyHandlers;
    }
}
