package org.nampython.config;

/**
 *
 */
public enum ConfigValue {
    /**
     * Specify the max request size in bytes.
     */
    MAX_REQUEST_SIZE,
    /**
     * Specify the server port, defaults to 8000
     */
    SERVER_PORT,
    /**
     * The context directory of Server.
     */
    SERVER_WORKING_DIRECTORY,
    /**
     *
     */
    RESOURCE_HANDLER_ORDER,
    /**
     *
     */
    DISPATCHER_ORDER,
    /**
     *
     */
    REQUEST_PROCESSOR_ORDER,
    /**
     *
     */
    FALLBACK_HANDLER_ORDER,
    /**
     * Setting to false will result in javache not logging unhandled exceptions.
     */
    PRINT_EXCEPTIONS,
}
