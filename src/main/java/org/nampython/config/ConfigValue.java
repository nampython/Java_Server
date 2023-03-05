package org.nampython.config;

/**
 *
 */
public enum ConfigValue {
    /**
     * Specify the folder name in which the logs will be located, defaults to logs.
     */
    LOGS_DIR_NAME,
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
    PRINT_EXCEPTIONS,    /**
     * Specify the name of the main web app, defaults to ROOT.
     */
    MAIN_APP_JAR_NAME,
    /**
     * The context directory of Javache.
     */
    WORKING_DIRECTORY,
    /**
     * Specify the folder name in which the permanent web app assets will be located, defaults to assets.
     */
    ASSETS_DIR_NAME,
    WEB_APPS_DIR_NAME,
    /**
     * Specify the folder name within a web app JAR in which the compile output will be located, defaults to classes.
     */
    APP_COMPILE_OUTPUT_DIR_NAME,
    /**
     * Specify the folder name withing a web app JAR within the compile output dir in which
     * web resources such as html, css, js will be located, defaults to webapp.
     */
    APP_RESOURCES_DIR_NAME,
    /**
     * Specify an expression for the caching type for each resource media type.
     * Format - media/type1, media/type2 @ header-value & media/type3 @ header-value
     */
    RESOURCE_CACHING_EXPRESSION,
    /**
     * Setting to false will result in resources not being cached (no Cache-Control header being sent)/
     */
    ENABLE_RESOURCE_CACHING,
    /**
     * Specify the folder name withing a web app JAR in which the app's libraries are located, defaults to lib.
     */
    APPLICATION_DEPENDENCIES_FOLDER_NAME,

    /**
     * Setting to false will result in solets not accepting requests that might be resource requests (css, js, html).
     */
    BROCCOLINA_TRACK_RESOURCES,
}
