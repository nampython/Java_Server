package org.nampython.config;

/**
 *
 */
public class CorePool {
    /**
     *
     */
    public static final int DEFAULT_SERVER_PORT = 8000;
    /**
     *
     */
    public static final int EMPTY_PORT = -1;
    /**
     *
     */
    public static final String DEFAULT_CACHING_EXPRESSION = "image/png, image/gif, image/jpeg @ max-age=120 " +
            "& text/css @ max-age=84600, public " +
            "& application/javascript @ max-age=7200";
}
