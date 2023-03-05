package org.nampython.core;

import com.cyecize.ioc.annotations.Service;
import org.nampython.base.api.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class FormDataParserDefault implements FormDataParser {
    public static final String RAW_BODY_PARAM_NAME = "rawBodyText";
    /**
     * @param inputStream - request's input stream, read to the point where the body starts.
     * @param request     - current request.
     */
    @Override
    public void parseBodyParams(InputStream inputStream, HttpRequest request) throws CannotParseRequestException {

    }

    /**
     *
     * @param inputStream
     * @param request
     * @return
     * @throws IOException
     */
    private String readBody(InputStream inputStream, HttpRequest request) throws IOException {
        final int contentLength = request.getContentLength();

        final byte[] bytes = inputStream.readNBytes(contentLength);

        final String body = new String(bytes, StandardCharsets.UTF_8);
//        if (this.showRequestLog) {
//            this.loggingService.info(body);
//        }

        return body;
    }


    /**
     *
     * @param requestBody
     * @param request
     */
    private void setBodyParameters(String requestBody, HttpRequest request) {
        if (requestBody == null || requestBody.isEmpty() || requestBody.trim().isEmpty()) {
            return;
        }
        request.addBodyParameter(RAW_BODY_PARAM_NAME, requestBody);
        final String[] bodyParamPairs = requestBody.split("&");
        for (String bodyParamPair : bodyParamPairs) {
            final String[] tokens = bodyParamPair.split("=");
            final String paramKey = decode(tokens[0]);
            final String value = tokens.length > 1 ? decode(tokens[1]) : null;

            request.addBodyParameter(paramKey, value);
        }
    }

    /**
     *
     * @param str
     * @return
     */
    private static String decode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }
}
