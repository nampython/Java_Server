package org.nampython.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String requestURL;
    private HttpSession session;
    private int contentLength;
    private final List<MultipartFile> multipartFiles;
    private final Map<String, String> headers;
    private final Map<String, String> queryParameters;
    private final Map<String, String> bodyParameters;
    private final Map<String, List<String>> bodyParametersAsList;
    private final Map<String, HttpCookie> cookies;

    public HttpRequest() {
        this.headers = new HashMap<>();
        this.queryParameters = new HashMap<>();
        this.bodyParameters = new HashMap<>();
        this.bodyParametersAsList = new HashMap<>();
        this.cookies = new HashMap<>();
        this.multipartFiles = new ArrayList<>();
    }

    public List<MultipartFile> getMultipartFiles() {
        return multipartFiles;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public HttpSession getSession() {
        return this.session;
    }
    public String getBodyParam(String paramName) {
        return this.bodyParameters.get(paramName);
    }
    public boolean isResource() {
        return this.getRequestURL().contains(".");
    }
    public String getHost() {
        return this.getHeaders().getOrDefault("Host", "");
    }
    public void addMultipartFile(MultipartFile multipartFile) {
        this.multipartFiles.add(multipartFile);
    }
    public String get(String paramName) {
        String param = this.getQueryParam(paramName);

        if (param == null) {
            param = this.getBodyParam(paramName);
        }

        return param;
    }

    public String getQueryParam(String paramName) {
        return this.queryParameters.get(paramName);
    }
    public String getRequestURI() {
        return this.getHost() + this.getRequestURL();
    }

    public String getContentType() {
        return this.getHeader("Content-Type");
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public void addBodyParameter(String parameter, String value) {
        this.bodyParameters.put(parameter, value);
        if (!this.bodyParametersAsList.containsKey(parameter)) {
            this.bodyParametersAsList.put(parameter, new ArrayList<>());
        }
        this.bodyParametersAsList.get(parameter).add(value);
    }


    public Map<String, HttpCookie> getCookies() {
        return cookies;
    }

    /**
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public Map<String, String> getBodyParameters() {
        return bodyParameters;
    }

    public Map<String, List<String>> getBodyParametersAsList() {
        return bodyParametersAsList;
    }
}
