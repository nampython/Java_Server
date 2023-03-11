package org.nampython.base;


import org.nampython.base.api.HttpResponse;

public interface HttpSoletResponse extends HttpResponse {
    void sendRedirect(String location);

}
