package org.nampython.base;

import java.util.List;
import java.util.Map;

public class HttpSoletRequest extends HttpRequest{
    private final HttpRequest request;
    private String contextPath;

    public HttpSoletRequest(HttpRequest request) {
        this.request = request;
        this.setContextPath("");
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public String getRelativeRequestURL() {
        return this.request.getRequestURL().replaceFirst(this.contextPath, "");
    }

    @Override
    public void setMethod(String method) {
        this.request.setMethod(method);
    }

    @Override
    public void setRequestURL(String requestUrl) {
        this.request.setRequestURL(requestUrl);
    }

    @Override
    public void setContentLength(int contentLength) {
        this.request.setContentLength(contentLength);
    }

    public void setSession(HttpSession session) {
        this.request.setSession(session);
    }

    @Override
    public void addHeader(String header, String value) {
        this.request.addHeader(header, value);
    }

    @Override
    public void addBodyParameter(String parameter, String value) {
        this.request.addBodyParameter(parameter, value);
    }

    public void addMultipartFile(MultipartFile multipartFile) {
        this.request.addMultipartFile(multipartFile);
    }

    public boolean isResource() {
        return this.request.isResource();
    }

    @Override
    public int getContentLength() {
        return this.request.getContentLength();
    }

    @Override
    public String getMethod() {
        return this.request.getMethod();
    }

    @Override
    public String getRequestURL() {
        return this.request.getRequestURL();
    }

    public String getHost() {
        return this.request.getHost();
    }

    public String getRequestURI() {
        return this.request.getRequestURI();
    }

    @Override
    public String getContentType() {
        return this.request.getContentType();
    }

    public String getQueryParam(String paramName) {
        return this.request.getQueryParam(paramName);
    }

    public String getBodyParam(String paramName) {
        return this.request.getBodyParam(paramName);
    }

    public String get(String paramName) {
        return this.request.get(paramName);
    }

    @Override
    public String getHeader(String headerName) {
        return this.request.getHeader(headerName);
    }

    public HttpSession getSession() {
        return this.request.getSession();
    }

    public HttpCookie getCookie(String cookieName) {
        return this.request.getCookies().get(cookieName);
    }

    public List<MultipartFile> getMultipartFiles() {
        return this.request.getMultipartFiles();
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.request.getHeaders();
    }

    @Override
    public Map<String, String> getQueryParameters() {
        return this.request.getQueryParameters();
    }

    @Override
    public Map<String, String> getBodyParameters() {
        return this.request.getBodyParameters();
    }

    @Override
    public Map<String, List<String>> getBodyParametersAsList() {
        return this.request.getBodyParametersAsList();
    }

    @Override
    public Map<String, HttpCookie> getCookies() {
        return this.request.getCookies();
    }
}
