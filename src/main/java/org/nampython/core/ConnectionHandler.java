package org.nampython.core;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *
 */
public class ConnectionHandler implements Runnable {
    private final Socket socketClient;
    private final List<RequestHandler> requestHandlers;
    private final List<RequestDestroyHandler> requestDestroyHandlers;

    public ConnectionHandler(Socket socketClient, List<RequestHandler> requestHandlers, List<RequestDestroyHandler> requestDestroyHandlers) {
        this.socketClient = socketClient;
        this.requestHandlers = requestHandlers;
        this.requestDestroyHandlers = requestDestroyHandlers;
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            this.handlerRequest();
            socketClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @throws IOException
     */
    private void handlerRequest() throws IOException {
        RequestHandlerShareData sharedData = new RequestHandlerShareData();
        for (RequestHandler requestHandler : this.requestHandlers) {
            boolean isRequestHandler = requestHandler.handleRequest(this.socketClient.getInputStream(), this.socketClient.getOutputStream(), sharedData);
            if (isRequestHandler) {
                break;
            }
        }
        this.handlerDestroy(sharedData);
    }

    /**
     *
     * @param shareData
     */
    private void handlerDestroy(RequestHandlerShareData shareData) {
        for (RequestDestroyHandler requestDestroyHandler : this.requestDestroyHandlers) {
            requestDestroyHandler.destroy(shareData);
        }
    }
}
