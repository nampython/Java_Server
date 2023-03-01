package org.nampython.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 *
 */
public class ServerImplement implements Server {
    private static final int SOCKET_TIMEOUT_MILLISECONDS;
    private static final String LISTENING_MESSAGE_FORMAT;

    static {
        SOCKET_TIMEOUT_MILLISECONDS = 60000;
        LISTENING_MESSAGE_FORMAT = "http://localhost:%d";
    }

    private final InitLoadingRequest initLoadingRequest;
    private final int port;

    public ServerImplement(InitLoadingRequest initLoadingRequest, int port) {
        this.initLoadingRequest = initLoadingRequest;
        this.port = port;
    }

    /**
     * @throws IOException
     */
    @Override
    public void run() throws IOException {
        final ServerSocket serverSocket = new ServerSocket(this.port);
        serverSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);
        System.out.println(String.format(LISTENING_MESSAGE_FORMAT, this.port));

        while (true) {
            final Socket client = serverSocket.accept();
            client.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);
            final Thread thread = new Thread(
                    () -> {
                        final List<RequestHandler> requestHandlers = this.initLoadingRequest.getRequestHandlers();
                        final List<RequestDestroyHandler> requestDestroyHandlers = this.initLoadingRequest.getRequestDestroyHandlers();
                        final RequestHandlerShareData requestHandlerShareData = new RequestHandlerShareData();

                        for (RequestHandler requestHandler : requestHandlers) {
                            try {
                                boolean requestHandled = requestHandler.handleRequest(client.getInputStream(), client.getOutputStream(), requestHandlerShareData);
                                if (requestHandled) {
                                    break;
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        for (RequestDestroyHandler requestDestroyHandler : requestDestroyHandlers) {
                            requestDestroyHandler.destroy(requestHandlerShareData);
                        }
                        try {
                            client.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            thread.start();
        }
    }

}
