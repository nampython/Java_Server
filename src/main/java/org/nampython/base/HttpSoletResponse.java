package org.nampython.base;

import java.io.OutputStream;
import java.util.Map;

public class HttpSoletResponse extends  HttpResponse{

    private final HttpResponse response;

    private final SoletOutputStream soletOutputStream;

    public HttpSoletResponse(HttpResponse response,
                             OutputStream clientOutputStream) {
        this.response = response;
        this.soletOutputStream = new SoletOutputStream(clientOutputStream, this);
    }


    public void sendRedirect(String location) {
        this.response.setStatusCode(HttpResponse.HttpStatus.SEE_OTHER);
        this.response.setContent(location);
        this.response.addHeader("Location", location);
    }

    public SoletOutputStream getOutputStream() {
        return this.soletOutputStream;
    }

    public void setStatusCode(HttpResponse.HttpStatus statusCode) {
        this.response.setStatusCode(statusCode);
    }


    public void setContent(String content) {
        this.response.setContent(content);
    }


    public void setContent(byte[] content) {
        this.response.setContent(content);
    }


    public void addHeader(String header, String value) {
        this.response.addHeader(header, value);
    }


    public void addCookie(String name, String value) {
        this.response.addCookie(name, value);
    }


    public void addCookie(HttpCookie cookie) {
        this.response.addCookie(cookie);
    }


    public String getResponse() {
        return this.response.getResponse();
    }


    public HttpResponse.HttpStatus getStatusCode() {
        return this.response.getStatusCode();
    }


    public byte[] getContent() {
        return this.response.getContent();
    }


    public byte[] getBytes() {
        return this.response.getBytes();
    }


    public Map<String, String> getHeaders() {
        return this.response.getHeaders();
    }
}
