package org.nampython.core;

import com.cyecize.ioc.annotations.Service;
import org.nampython.base.api.BaseHttpRequest;
import org.nampython.base.api.MultipartFile;


import java.io.IOException;

/**
 * Request handler called always after every request.
 * The purpose is to clear or dispose any left out resource to avoid memory leaks.
 */
@Service
public class ToyoteRequestDestroyHandler implements RequestDestroyHandler {

    @Override
    public void destroy(RequestHandlerShareData sharedData) {
        final BaseHttpRequest request = sharedData.getObject(RequestHandlerShareData.HTTP_REQUEST, BaseHttpRequest.class);
        if (request == null || request.getMultipartFiles() == null) {
            return;
        }

        for (MultipartFile multipartFile : request.getMultipartFiles()) {
            try {
                multipartFile.getInputStream().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
