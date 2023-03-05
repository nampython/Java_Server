package org.nampython.core.center;

import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import org.nampython.base.api.HttpRequest;
import org.nampython.base.api.HttpResponse;
import org.nampython.base.api.HttpStatus;
import org.nampython.config.ConfigCenter;
import org.nampython.config.ConfigValue;
import org.nampython.core.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Service
public class ResourceHandler implements RequestHandler {
    private final ConfigCenter configCenter;
    private final ResourceLocation resourceLocation;
    private final TikaBase tikaBase;
    private Map<String, String> mediaTypeCacheMap;


    @Autowired
    public ResourceHandler(ConfigCenter configCenter, ResourceLocation resourceLocation, TikaBase tikaBase) {
        this.configCenter = configCenter;
        this.resourceLocation = resourceLocation;
        this.tikaBase = tikaBase;
    }

    @Override
    public void init() {
        this.mediaTypeCacheMap = CachingExpressingParser.parseExpression(this.configCenter.getConfigValue(ConfigValue.RESOURCE_CACHING_EXPRESSION));
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
        final HttpRequest httpRequest = sharedData.getObject(RequestHandlerShareData.HTTP_REQUEST, HttpRequest.class);
        final HttpResponse httpResponse = sharedData.getObject(RequestHandlerShareData.HTTP_RESPONSE, HttpResponse.class);

        try {
            final File resource = this.resourceLocation.locateResource(httpRequest.getRequestURL());
            try (final FileInputStream fileInputStream = new FileInputStream(resource)) {
                this.handleResourceFoundResponse(httpRequest, httpResponse, resource, fileInputStream.available());
                outputStream.write(httpResponse.getBytes());
                this.transferStream(fileInputStream, outputStream);
            }
            return true;
        } catch (ResourceNotFoundException ignored) {
        }
        return false;
    }


    /**
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    private void transferStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        final byte[] buffer = new byte[2048];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }


    /**
     * @return
     */
    @Override
    public int order() {
        return this.configCenter.getConfigValue(ConfigValue.RESOURCE_HANDLER_ORDER.name(), int.class);
    }

    /**
     * Populates {@link HttpResponse} with found resource.
     * Adds necessary headers that are required in order to transfer a resource using the HTTP protocol.
     */
    private void handleResourceFoundResponse(HttpRequest request, HttpResponse response, File resourceFile, long fileSize) throws IOException {
        final String mediaType = this.tikaBase.detect(resourceFile);
        response.setStatusCode(HttpStatus.OK);
        response.addHeader("Content-Type", mediaType);
        response.addHeader("Content-Length", fileSize + "");
        response.addHeader("Content-Disposition", "inline");
        this.addCachingHeader(request, response, mediaType);
    }

    /**
     * Adds caching header to the given response.
     * <p>
     * If caching is not enabled or the caching header is already present, do nothing.
     * <p>
     * Uses Cache-Control header to set up caching options.
     * Cache-Control header value from the request is prioritized if present,
     * otherwise value from the config for that specific media type will be used (if present)
     *
     * @param request       - current request.
     * @param response      - current response.
     * @param fileMediaType - current file media type.
     */
    public void addCachingHeader(HttpRequest request, HttpResponse response, String fileMediaType) {
        if (!this.isCachingEnabled() || this.hasCacheHeader(response)) {
            return;
        }
        String responseCacheControl = request.getHeader(RequestProcessor.CACHE_CONTROL_HEADER_NAME);
        if (responseCacheControl == null && this.mediaTypeCacheMap.containsKey(fileMediaType)) {
            responseCacheControl = this.mediaTypeCacheMap.get(fileMediaType);
        }
        if (responseCacheControl != null) {
            response.addHeader(RequestProcessor.CACHE_CONTROL_HEADER_NAME, responseCacheControl);
        }
    }

    /**
     * @return
     */
    private boolean isCachingEnabled() {
        return this.configCenter.getConfigValue(ConfigValue.ENABLE_RESOURCE_CACHING, boolean.class);
    }

    /**
     * @param response
     * @return
     */
    private boolean hasCacheHeader(HttpResponse response) {
        return response.getHeaders().containsKey(RequestProcessor.CACHE_CONTROL_HEADER_NAME);
    }


    /**
     *
     */
    static class CachingExpressingParser {
        public static Map<String, String> parseExpression(String expressionString) {
            final Map<String, String> mediaTypeCacheMap = new HashMap<>();
            final String[] expressions = expressionString.split("\\s*&\\s*");
            try {
                for (String expression : expressions) {
                    final String[] tokens = expression.split("\\s*@\\s*");
                    final String headerValue = tokens[1].trim();
                    final String[] mediaTypes = tokens[0].split(",\\s*");

                    for (String mediaType : mediaTypes) {
                        mediaTypeCacheMap.put(mediaType.trim(), headerValue);
                    }
                }
            } catch (Exception ex) {
                throw new CannotParseExpressionException(
                        String.format("Cannot parse caching expression '%s', check the syntax.", expressionString),
                        ex
                );
            }
            return mediaTypeCacheMap;
        }
    }
}
