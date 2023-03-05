package org.nampython.base;


import org.nampython.base.api.HttpRequest;

public interface HttpSoletRequest extends HttpRequest {

    void setContextPath(String contextPath);

    String getContextPath();

    String getRelativeRequestURL();
}
