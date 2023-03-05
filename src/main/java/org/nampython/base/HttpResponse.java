package org.nampython.base;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {
    private static final String SERVER_HTTP_VERSION;
    private static final String LINE_SEPARATOR;
    private static final String CONTENT_TYPE;

    static {
        SERVER_HTTP_VERSION = "HTTP/1.1";
        LINE_SEPARATOR = "\r\n";
        CONTENT_TYPE = "Content-Type";
    }
    private HttpStatus statusCode;
    private byte[] content;
    private final Map<String, String> headers;
    private final Map<String, HttpCookie> cookies;

    public HttpResponse() {
        this.setContent(new byte[0]);
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
    }

    public String getResponse() {
        return this.getHeaderString() + new String(this.getContent(), StandardCharsets.UTF_8);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * @param content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content.getBytes(StandardCharsets.UTF_8);
    }
    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public byte[] getContent() {
        return content;
    }

    public Map<String, HttpCookie> getCookies() {
        return cookies;
    }

    /**
     * Appends all headers to form a valid HTTP header section.
     *
     * @return headers.
     */
    private String getHeaderString() {
        final StringBuilder result = new StringBuilder()
                .append(HttpStatus.getResponseLine(
                        Objects.requireNonNullElse(this.getStatusCode(), HttpStatus.OK).getStatusCode())
                )
                .append(LINE_SEPARATOR);

        this.headers.put(CONTENT_TYPE, this.resolveCharset(this.headers.getOrDefault(CONTENT_TYPE, "text/html")));

        for (Map.Entry<String, String> header : this.getHeaders().entrySet()) {
            result.append(header.getKey()).append(": ").append(header.getValue()).append(LINE_SEPARATOR);
        }

        if (!this.cookies.isEmpty()) {
            for (HttpCookie cookie : this.cookies.values()) {
                result.append("Set-Cookie: ").append(cookie.toRFCString()).append(LINE_SEPARATOR);
            }
        }

        result.append(LINE_SEPARATOR);
        return result.toString();
    }

    private String resolveCharset(String contentType) {
        if (contentType == null || contentType.contains("charset")) {
            return contentType;
        } else {
            return contentType + "; charset=utf8";
        }
    }

    public void addHeader(String header, String value) {
        this.headers.put(header, value);
    }

    public byte[] getBytes() {
        final byte[] headers = this.getHeaderString().getBytes();
        final byte[] result = new byte[headers.length + this.getContent().length];

        System.arraycopy(headers, 0, result, 0, headers.length);
        System.arraycopy(this.getContent(), 0, result, headers.length, this.getContent().length);

        return result;
    }

    public void addCookie(HttpCookie cookie) {
        this.cookies.put(cookie.getName(), cookie);
    }

    public void addCookie(String name, String value) {
        this.cookies.put(name, new HttpCookie(name, value));
    }

    public enum HttpStatus {
        OK(200, "OK"),

        CREATED(201, "Created"),

        ACCEPTED(202, "Accepted"),

        NO_CONTENT(204, "No Content"),

        MOVED_PERMANENTLY(301, "Moved Permanently"),

        FOUND(302, "Found"),
        SEE_OTHER(303, "See Other"),

        BAD_REQUEST(400, "Bad Request"),

        UNAUTHORIZED(401, "Unauthorized"),

        FORBIDDEN(403, "Forbidden"),

        NOT_FOUND(404, "Not Found"),

        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

        NOT_ACCEPTABLE(406, "Not Acceptable"),

        PAYLOAD_TOO_LARGE(413, "Payload Too Large"),

        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

        NOT_IMPLEMENTED(501, "Not Implemented");
        private final int statusCode;
        private final String statusPhrase;
        HttpStatus(int statusCode, String statusPhrase) {
            this.statusCode = statusCode;
            this.statusPhrase = statusPhrase;
        }
        public int getStatusCode() {
            return this.statusCode;
        }

        public String getStatusPhrase() {
            return this.statusPhrase;
        }

        /**
         * @return Http status in Http format.
         */
        public static String getResponseLine(int statusCode) {
            final HttpStatus httpStatus = Arrays.stream(values())
                    .filter(sc -> sc.statusCode == statusCode)
                    .findFirst()
                    .orElse(INTERNAL_SERVER_ERROR);
            return SERVER_HTTP_VERSION
                    + " "
                    + httpStatus.getStatusCode()
                    + " "
                    + httpStatus.getStatusPhrase();
        }
    }
}
