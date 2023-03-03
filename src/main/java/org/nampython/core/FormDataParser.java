package org.nampython.core;

import org.nampython.base.HttpRequest;

import java.io.InputStream;

/**
 * Service for reading request's body, parsing it using the multipart/form-data format
 * and populating {@link HttpRequest}
 */
public interface FormDataParser {
    /**
     * @param inputStream - request's input stream, read to the point where the body starts.
     * @param request     - current request.
     */
    void parseBodyParams(InputStream inputStream, HttpRequest request) throws CannotParseRequestException;
}
