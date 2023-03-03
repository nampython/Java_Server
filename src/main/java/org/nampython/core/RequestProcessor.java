package org.nampython.core;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import org.nampython.base.HttpCookie;
import org.nampython.base.HttpRequest;
import org.nampython.base.HttpResponse;
import org.nampython.config.ConfigCenter;
import org.nampython.config.ConfigValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class RequestProcessor implements RequestHandler {
    public static final String CONTENT_LENGTH;
    public static final String CACHE_CONTROL_HEADER_NAME;
    public static final String COOKIE_HEADER_NAME;
    public static final String TEXT_PLAIN;
    public static final String MULTIPART_FORM_DATA;
    public static final String RAW_BODY_PARAM_NAME;
    private static final String REQUEST_TOO_BIG_MSG = "Request too big.";
    static {
        CONTENT_LENGTH = "Content-Length";
        CACHE_CONTROL_HEADER_NAME = "Cache-Control";
        COOKIE_HEADER_NAME = "Cookie";
        TEXT_PLAIN = "text/plain";
        MULTIPART_FORM_DATA = "multipart/form-data";
        RAW_BODY_PARAM_NAME = "rawBodyText";
    }

    private final Map<FormDataParserProvider, FormDataParser> instanceProviderMap = new HashMap<>();
    private final List<FormDataParser> formDataParsers;
    private final ConfigCenter configCenter;
    private final ErrorHandling errorHandling;
    private final int maxRequestSize;

    @Autowired
    public RequestProcessor(List<FormDataParser> formDataParsers, ConfigCenter configCenter, ErrorHandling errorHandling) {
        this.formDataParsers = formDataParsers;
        this.configCenter = configCenter;
        this.maxRequestSize = configCenter.getConfigValue(ConfigValue.MAX_REQUEST_SIZE, int.class);
        this.errorHandling = errorHandling;
    }

    /**
     *
     */
    @Override
    public void init() {
        //NOTHING HERE
    }

    /**
     * @param inputStream
     * @param outputStream
     * @param sharedData
     * @return
     * @throws IOException
     */
    @Override
    public boolean handleRequest(InputStream inputStream, OutputStream outputStream, RequestHandlerShareData sharedData) throws IOException {
        try {
            final HttpRequest httpRequest = this.parseHttpRequest(inputStream);
            final HttpResponse httpResponse = new HttpResponse();
            sharedData.addObject(RequestHandlerShareData.HTTP_REQUEST, httpRequest);
            sharedData.addObject(RequestHandlerShareData.HTTP_RESPONSE, httpResponse);
        } catch (RequestTooBigException ex) {
            this.disposeInputStream(ex.getContentLength(), inputStream);
            return this.errorHandling.handleRequestTooBig(outputStream, ex, new HttpResponse());
        } catch (Exception e) {
            return this.errorHandling.handleException(outputStream, e, new HttpResponse(), HttpResponse.HttpStatus.BAD_REQUEST);
        }
        return false;
    }

    /**
     * The purpose of this method is to read the input stream before closing it
     * otherwise the TCP connection will not be closed properly.
     */
    private void disposeInputStream(int length, InputStream inputStream) throws IOException {
        byte[] buffer = new byte[0];
        int leftToRead = length;
        int bytesRead = Math.min(2048, inputStream.available());

        while (leftToRead > 0) {
            buffer = inputStream.readNBytes(bytesRead);
            leftToRead -= bytesRead;
            bytesRead = Math.min(2048, inputStream.available());
        }

        buffer = null;
    }

    /**
     * @param inputStream
     * @return
     */
    private HttpRequest parseHttpRequest(InputStream inputStream) {

        try {
            final HttpRequest httpRequest = new HttpRequest();
            final List<String> requestMetadata = this.parseMetadataLines(inputStream, false);
            this.handlerMethodAndURL(requestMetadata.get(0), httpRequest);
            this.handlerParamQuery(requestMetadata.get(0), httpRequest);
            this.handlerHeader(requestMetadata, httpRequest);
            this.handlerCookies(httpRequest);
            this.handlerContentLength(inputStream, httpRequest);
            if (httpRequest.getContentLength() > this.maxRequestSize) {
                throw new RequestTooBigException(REQUEST_TOO_BIG_MSG, httpRequest.getContentLength());
            } else {
                final String contentType = httpRequest.getContentType();
                final FormDataParserProvider formDataParserProvider = FormDataParserProvider.findByContentType(contentType);
                this.getParser(formDataParserProvider).parseBodyParams(inputStream, httpRequest);
            }
            this.trimRequestPath(httpRequest);
            return httpRequest;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param request
     */
    private void trimRequestPath(HttpRequest request) {
        request.setRequestURL(
                request.getRequestURL().replaceAll("\\.{2,}\\/?", "")
        );
    }

    /**
     * @param inputStream
     * @param requestMetadata
     */
    private void handlerContentLength(InputStream inputStream, HttpRequest request) throws IOException {
        if (request.getHeaders().get(CONTENT_LENGTH) != null) {
            request.setContentLength(Integer.parseInt(request.getHeaders().get(CONTENT_LENGTH)));
        } else {
            request.setContentLength(inputStream.available());
        }
    }

    /**
     * @param httpRequest
     */
    private void handlerCookies(HttpRequest httpRequest) {
        if (httpRequest.getHeaders().get(COOKIE_HEADER_NAME) == null) {
            return;
        }
        String[] allCookies = httpRequest.getHeaders().get(COOKIE_HEADER_NAME).split(";\\s");
        for (String cookieStr : allCookies) {
            final String[] cookieKeyValuePair = cookieStr.split("=");
            final String keyName = decode(cookieKeyValuePair[0]);
            final String value = cookieKeyValuePair.length > 1 ? decode(cookieKeyValuePair[1]) : null;
            httpRequest.getCookies().put(keyName, new HttpCookie(keyName, value));
        }
    }

    /**
     * @param requestMetadata
     * @param httpRequest
     */
    private void handlerHeader(List<String> requestMetadata, HttpRequest httpRequest) {
        for (int i = 1; i < requestMetadata.size(); i++) {
            final String[] headerKeyValuePair = requestMetadata.get(i).split(":\\s+");
            final String key = headerKeyValuePair[0];
            final String value = headerKeyValuePair[1];
            httpRequest.addHeader(key, value);
        }
    }

    /**
     * @param requestFirstLine
     * @param httpRequest
     */
    private void handlerMethodAndURL(String requestFirstLine, HttpRequest httpRequest) {
        httpRequest.setMethod(requestFirstLine.split("\\s")[0]);
        httpRequest.setRequestURL(URLDecoder.decode(
                requestFirstLine.split("[\\s\\?]")[1],
                StandardCharsets.UTF_8
        ));
    }

    /**
     * @param requestFirstLine
     * @param httpRequest
     */
    private void handlerParamQuery(String requestFirstLine, HttpRequest httpRequest) {
        String fullRequestURL = requestFirstLine.split("\\s")[1];
        String[] urlQueryParamPair = fullRequestURL.split("\\?");

        if (urlQueryParamPair.length > 2) {
            final String[] queryParamPairs = urlQueryParamPair[1].split("&");
            final Map<String, String> queryParameters = httpRequest.getQueryParameters();
            for (String paramPair : queryParamPairs) {
                final String[] queryParamPair = paramPair.split("=");
                final String keyName = decode(queryParamPair[0]);
                final String value = queryParamPair.length > 1 ? decode(queryParamPair[1]) : null;
                queryParameters.put(keyName, value);
            }
        }
    }


    /**
     * @param provider
     * @return
     */
    private FormDataParser getParser(FormDataParserProvider provider) {
        if (this.instanceProviderMap.containsKey(provider)) {
            return this.instanceProviderMap.get(provider);
        }

        final FormDataParser formDataParser = this.formDataParsers.stream()
                .filter(parser -> provider.getParserType().isAssignableFrom(parser.getClass()))
                .findFirst()
                .orElseThrow(() -> new CannotParseRequestException(String.format(
                        "Could not find %s form data parser", provider
                )));
        this.instanceProviderMap.put(provider, formDataParser);
        return formDataParser;
//        FormDataParser formDataParser = null;
//        if (this.instanceProviderMap.containsKey(provider)) {
//            return this.instanceProviderMap.get(provider);
//        } else {
//            for (FormDataParser dataParser : this.formDataParsers) {
//                if (provider.getParserType().isAssignableFrom(dataParser.getClass())) {
//                    formDataParser = dataParser;
//                    break;
//                } else {
//                    throw new CannotParseRequestException(String.format(
//                            "Could not find %s form data parser", provider
//                    ));
//                }
//            }
//        }
    }

    /**
     *
     * @param str
     * @return
     */
    private static String decode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }


    /**
     * @param inputStream
     * @param allowNewLineWithoutReturn
     * @return
     * @throws CannotParseRequestException
     */
    private List<String> parseMetadataLines(InputStream inputStream, boolean allowNewLineWithoutReturn)
            throws CannotParseRequestException {
        try {
            final List<String> metadataLines = new ArrayList<>();

            StringBuilder metadataBuilder = new StringBuilder();
            boolean wasNewLine = true;
            int lineNumber = 1;
            int readBytesCount = 0;
            int b;

            while ((b = inputStream.read()) >= 0) {
                readBytesCount++;
                if (b == '\r') {
                    // expect new-line
                    int next = inputStream.read();
                    if (next < 0 || next == '\n') {
                        lineNumber++;
                        if (wasNewLine) {
                            break;
                        }
                        metadataLines.add(metadataBuilder.toString());
                        if (next < 0) break;
                        metadataBuilder = new StringBuilder();
                        wasNewLine = true;
                    } else {
                        inputStream.close();
                        throw new CannotParseRequestException(
                                String.format("Illegal character after return on line %d.", lineNumber)
                        );
                    }
                } else if (b == '\n') {
                    if (!allowNewLineWithoutReturn) {
                        throw new CannotParseRequestException(
                                String.format("Illegal new-line character without preceding return on line %d.", lineNumber)
                        );
                    }

                    // unexpected, but let's accept new-line without returns
                    lineNumber++;
                    if (wasNewLine) {
                        break;
                    }
                    metadataLines.add(metadataBuilder.toString());
                    metadataBuilder = new StringBuilder();
                    wasNewLine = true;
                } else {
                    metadataBuilder.append((char) b);
                    wasNewLine = false;
                }
            }

            if (metadataBuilder.length() > 0) {
                metadataLines.add(metadataBuilder.toString());
            }

            if (readBytesCount < 2) {
                throw new CannotParseRequestException("Request is empty");
            }

//            if (this.showRequestLog) {
//                this.loggingService.info(String.join("\n", metadataLines));
//            }

            return metadataLines;
        } catch (IOException ex) {
            throw new CannotParseRequestException(ex.getMessage(), ex);
        }
    }


    /**
     * @return
     */
    @Override
    public int order() {
        return configCenter.getConfigValue(ConfigValue.REQUEST_PROCESSOR_ORDER, int.class);
    }

    /**
     *
     */
    enum FormDataParserProvider {
        DEFAULT(TEXT_PLAIN, FormDataParserDefault.class),
        MULTIPART(MULTIPART_FORM_DATA, FormDataParserDefault.class);
        private final String contentType;
        private final Class<? extends FormDataParser> parserType;

        FormDataParserProvider(String contentType, Class<? extends FormDataParser> parserType) {
            this.contentType = contentType;
            this.parserType = parserType;
        }

        /**
         * @param contentType
         * @return
         */
        public static FormDataParserProvider findByContentType(String contentType) {
            if (contentType != null) {
                for (FormDataParserProvider value : values()) {
                    if (value.contentType.startsWith(contentType)) {
                        return value;
                    }
                }
            }
            return DEFAULT;
        }

        /**
         * @return
         */
        public Class<? extends FormDataParser> getParserType() {
            return parserType;
        }
    }
}
