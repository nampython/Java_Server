package org.nampython.core;

import com.cyecize.ioc.annotations.Service;
import org.nampython.base.api.HttpRequest;

import java.io.InputStream;

//TODO
@Service
public class FormDataParserMultipart implements FormDataParser{
    /**
     * @param inputStream - request's input stream, read to the point where the body starts.
     * @param request     - current request.
     */
    @Override
    public void parseBodyParams(InputStream inputStream, HttpRequest request) throws CannotParseRequestException {

    }
}
