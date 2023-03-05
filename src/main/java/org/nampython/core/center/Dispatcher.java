package org.nampython.core.center;


import com.cyecize.ioc.annotations.Autowired;
import com.cyecize.ioc.annotations.Service;
import org.nampython.base.*;
import org.nampython.base.api.HttpRequest;
import org.nampython.base.api.HttpResponse;
import org.nampython.base.api.HttpStatus;
import org.nampython.config.ConfigCenter;
import org.nampython.config.ConfigValue;
import org.nampython.core.DispatcherConfig;
import org.nampython.core.RequestHandler;
import org.nampython.core.RequestHandlerShareData;
import org.nampython.core.SessionManagement;
import org.nampython.support.IocCenter;
import org.nampython.support.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nampython.core.SessionManagement.CONTENT_LENGTH_HEADER;

/**
 *
 */
@Service
public class Dispatcher implements RequestHandler {
    private static final String MISSING_SOLET_ANNOTATION_FORMAT = "Missing solet annotation for class named %s.";

    public static final String CONFIG_ASSETS_DIR;
    public static final String CFG_WORKING_DIR;
    public static final String CONFIG_APP_NAME_PREFIX;
    public static final String CONFIG_SESSION_STORAGE_KEY;
    public static final String CONFIG_SERVER_CONFIG_SERVICE_KEY;
    public static final String CONFIG_DEPENDENCY_CONTAINER_KEY;
    public static final String CONFIG_LOGGER;

    static {
        CONFIG_ASSETS_DIR = "cfg.assets.dir";
        CFG_WORKING_DIR = "cfg.working.dir";
        CONFIG_APP_NAME_PREFIX = "cfg.app.name.prefix";
        CONFIG_SESSION_STORAGE_KEY = "cfg.session.storage";
        CONFIG_SERVER_CONFIG_SERVICE_KEY = "cfg.javache.config";
        CONFIG_DEPENDENCY_CONTAINER_KEY = "cfg.javache.dependency.container";
        CONFIG_LOGGER = "cfg.logger";
    }

    private final String assetsDir;
    private boolean isRootDir;
    private final String webappsDir;

    private final ConfigCenter configCenter;
    private final SessionManagement sessionManagement;
    private final Map<String, List<Class<HttpSolet>>> soletClasses;
    private final Map<String, HttpSolet> solets;
    private DispatcherConfig dispatcherConfig;
    private final boolean trackResources;

    //SoletCandidateFinder
    private final String rootAppName;
    private Map<String, HttpSolet> soletMap;
    private List<String> applicationNames;


    @Autowired
    public Dispatcher(ConfigCenter configCenter, SessionManagement sessionManagement) {
        this.soletClasses = new HashMap<>();
        this.configCenter = configCenter;
        this.sessionManagement = sessionManagement;
        this.assetsDir = this.getAssetsDir();
        this.webappsDir = this.getWebappsDir();

        //ApplicationScanning
        this.isRootDir = true;
        this.rootAppName = configCenter.getConfigValue(ConfigValue.MAIN_APP_JAR_NAME);
        this.soletClasses.put(this.rootAppName, new ArrayList<>());
        this.isRootDir = true;
        this.solets = new HashMap<>();
        this.loadLibraries();
        this.trackResources = configCenter.getConfigValue(ConfigValue.BROCCOLINA_TRACK_RESOURCES, boolean.class);


    }

    @Override
    public void init() {
        try {
            this.soletMap = this.loadApplications(this.createDispatcherConfig());
            this.applicationNames = this.getApplicationNames();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean handleRequest(InputStream inputStream, OutputStream outputStream, RequestHandlerShareData sharedData) throws IOException {
        final HttpSoletRequest request = new HttpSoletRequestImpl(
                sharedData.getObject(RequestHandlerShareData.HTTP_REQUEST, HttpRequest.class));
        final HttpSoletResponse response = new HttpSoletResponseImpl(
                sharedData.getObject(RequestHandlerShareData.HTTP_RESPONSE, HttpResponse.class), outputStream);

        if (request.isResource() && !this.trackResources) {
            return false;
        }
        this.sessionManagement.initSessionIfExistent(request);
        final HttpSolet solet = this.findSoletCandidate(request);
        if (solet == null || !this.runSolet(solet, request, response)) {
            return false;
        }
        if (response.getStatusCode() == null) {
            response.setStatusCode(HttpStatus.OK);
        }
        if (!response.getHeaders().containsKey(CONTENT_LENGTH_HEADER)
                && response.getContent() != null
                && response.getContent().length > 0) {
            response.addHeader(CONTENT_LENGTH_HEADER, response.getContent().length + "");
        }
        this.sessionManagement.sendSessionIfExistent(request, response);
        this.sessionManagement.clearInvalidSessions();
        response.getOutputStream().write();
        return false;
    }

    private boolean runSolet(HttpSolet solet, HttpSoletRequest request, HttpSoletResponse response) {
        try {
            solet.service(request, response);
            return solet.hasIntercepted();
        } catch (Exception ex) {
//            this.loggingService.printStackTrace(ex);
        }

        return true;
    }


    public HttpSolet findSoletCandidate(HttpSoletRequest request) {
        request.setContextPath(this.resolveCurrentRequestAppName(request));

        final String requestUrl = request.getRequestURL();
        final Pattern applicationRouteMatchPattern = Pattern
                .compile(Pattern.quote(request.getContextPath() + "\\/[a-zA-Z0-9]+\\/"));

        final Matcher applicationRouteMatcher = applicationRouteMatchPattern.matcher(requestUrl);

        if (this.soletMap.containsKey(requestUrl)) {
            return this.soletMap.get(requestUrl);
        }

        if (applicationRouteMatcher.find()) {
            String applicationRoute = applicationRouteMatcher.group(0) + "*";
            if (this.soletMap.containsKey(applicationRoute)) {
                return this.soletMap.get(applicationRoute);
            }
        }

        if (this.soletMap.containsKey(request.getContextPath() + "/*")) {
            return this.soletMap.get(request.getContextPath() + "/*");
        }

        return null;
    }

    private String resolveCurrentRequestAppName(HttpSoletRequest request) {
        for (String applicationName : this.applicationNames) {
            if (request.getRequestURL().startsWith(applicationName) && !applicationName.equals(this.rootAppName)) {
                return applicationName;
            }
        }

        return "";
    }

    @Override
    public int order() {
        return this.configCenter.getConfigValue(ConfigValue.DISPATCHER_ORDER.name(), int.class);
    }

    /**
     * @param dispatcherConfig
     * @return
     * @throws IOException
     */
    public Map<String, HttpSolet> loadApplications(DispatcherConfig dispatcherConfig) throws ClassNotFoundException, RuntimeException {
        try {
            this.dispatcherConfig = dispatcherConfig;
            final Map<String, List<Class<HttpSolet>>> soletClasses = this.findSoletClasses();
            for (Map.Entry<String, List<Class<HttpSolet>>> entry : soletClasses.entrySet()) {
                final String applicationName = entry.getKey();
                this.makeAppAssetDir(PathUtil.appendPath(this.assetsDir, applicationName));
                for (Class<HttpSolet> soletClass : entry.getValue()) {
                    this.loadSolet(soletClass, applicationName);
                }
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
        return this.solets;
    }

    public List<String> getApplicationNames() {
        return Collections.singletonList(this.rootAppName);
    }

    /**
     * Creates an instance of the solet.
     * If the application name is different than the javache specified main jar name (ROOT.jar by default),
     * add the appName to the route.
     * Put the solet in a solet map with a key being the soletRoute.
     */
    private void loadSolet(Class<HttpSolet> soletClass, String applicationName)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final HttpSolet soletInstance = soletClass.getDeclaredConstructor().newInstance();
        final WebSolet soletAnnotation = this.getSoletAnnotation(soletInstance.getClass());

        if (soletAnnotation == null) {
            throw new IllegalArgumentException(String.format(MISSING_SOLET_ANNOTATION_FORMAT, soletClass.getName()));
        }

        String soletRoute = soletAnnotation.value();
        if (!applicationName.equals(this.rootAppName)) {
            soletRoute = "/" + applicationName + soletRoute;
        }

        final SoletConfig soletConfigCopy = this.copySoletConfig();
        soletConfigCopy.setAttribute(
                CONFIG_ASSETS_DIR,
                PathUtil.appendPath(this.assetsDir, applicationName)
        );

        soletConfigCopy.setAttribute(CFG_WORKING_DIR, this.getSoletWorkingDir(applicationName));

//        soletConfigCopy.setAttribute(CONFIG_LOGGER, new SoletLoggerImpl(
//                this.loggingService,
//                applicationName
//        ));

        if (!applicationName.equals("") && !applicationName.equals(this.rootAppName)) {
            soletConfigCopy.setAttribute(CONFIG_APP_NAME_PREFIX, "/" + applicationName);
        }

        if (!soletInstance.isInitialized()) {
            soletInstance.init(soletConfigCopy);
        }

        this.solets.put(soletRoute, soletInstance);
    }

    private String getSoletWorkingDir(String appName) {
        final String appDir = PathUtil.appendPath(this.webappsDir, appName);
        final String appWorkingDir = PathUtil.appendPath(
                appDir,
                this.configCenter.getConfigValue(ConfigValue.APP_COMPILE_OUTPUT_DIR_NAME)
        );

        return PathUtil.appendPath(appWorkingDir, File.separator);
    }

    /**
     * Create SoletConfig instance and add objects.
     * This Solet Config will be used for initializing every solet.
     */
    private SoletConfig copySoletConfig() {
        final SoletConfig soletConfig = new SoletConfigImpl();
        this.dispatcherConfig.getAllAttributes().forEach(soletConfig::setAttribute);
        return soletConfig;
    }

    /**
     * Recursive method for getting {@link WebSolet} annotation from a given class.
     * Recursion is required since only parent class could have {@link WebSolet} annotation
     * and not the child.
     */
    private WebSolet getSoletAnnotation(Class<?> soletClass) {
        final WebSolet solet = soletClass.getAnnotation(WebSolet.class);
        if (solet == null && soletClass.getSuperclass() != null) {
            return this.getSoletAnnotation(soletClass.getSuperclass());
        }
        return solet;
    }


    /**
     * Creates asset directory for the current app in javache's assets directory.
     */
    private void makeAppAssetDir(String dir) {
        final File file = new File(dir);

        if (!file.exists()) {
            file.mkdir();
        }
    }

    private String getAssetsDir() {
        return PathUtil.appendPath(
                this.configCenter.getConfigValue(ConfigValue.JAVACHE_WORKING_DIRECTORY),
                this.configCenter.getConfigValue(ConfigValue.ASSETS_DIR_NAME)
        );
    }

    private String getWebappsDir() {
        return PathUtil.appendPath(
                this.configCenter.getConfigValue(ConfigValue.JAVACHE_WORKING_DIRECTORY),
                this.configCenter.getConfigValue(ConfigValue.WEB_APPS_DIR_NAME)
        );
    }


    private Map<String, List<Class<HttpSolet>>> findSoletClasses() throws ClassNotFoundException {
        File file = new File((String) this.configCenter.getConfigValue(ConfigValue.JAVACHE_WORKING_DIRECTORY));
        String packageName = "";
        this.loadClass(file, packageName);
        return this.soletClasses;
    }

    /**
     * Recursive method for loading classes, starts with empty packageName.
     * If the file is directory, iterate all files inside and call loadClass with the current file name
     * appended to the packageName.
     * <p>
     * If the file is file and the file name ends with .class, load it and check if the class
     * is assignable from {@link HttpSolet}. If it is, add it to the map of solet classes.
     */
    private void loadClass(File currentFile, String packageName) throws ClassNotFoundException {
        if (currentFile.isDirectory()) {
            //If the folder is the root dir, do not append package name since the name is outside the java packages.
            boolean appendPackage = !this.isRootDir;
            //Since the root dir is reached only once, set it to false.
            this.isRootDir = false;

            for (File childFile : currentFile.listFiles()) {
                if (appendPackage) {
                    this.loadClass(childFile, (packageName + currentFile.getName() + "."));
                } else {
                    this.loadClass(childFile, (packageName));
                }
            }
        } else {
            if (!currentFile.getName().endsWith(".class")) {
                return;
            }

            final String className = packageName + currentFile
                    .getName()
                    .replace(".class", "")
                    .replace("/", ".");


            final Class currentClassFile = Class
                    .forName(className, true, Thread.currentThread().getContextClassLoader());

            if (HttpSolet.class.isAssignableFrom(currentClassFile)) {
                System.out.println(currentClassFile.getName());
                this.soletClasses.get(this.rootAppName).add(currentClassFile);
            }
        }
    }

    /**
     * Checks if there is folder that matches the folder name in the config file (lib by default)
     * Iterates all elements and adds the .jar files to the system's classpath.
     */
    private void loadLibraries() {
        String workingDir = this.configCenter.getConfigValue(ConfigValue.JAVACHE_WORKING_DIRECTORY);
        if (!workingDir.endsWith("/") && !workingDir.endsWith("\\")) {
            workingDir += "/";
        }

        final File libFolder = new File(workingDir + this.configCenter
                .getConfigValue(ConfigValue.APPLICATION_DEPENDENCIES_FOLDER_NAME));

        if (!libFolder.exists()) {
            return;
        }

        for (File file : libFolder.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                //TODO: add libs
//                try {
//                    ReflectionUtils.addJarFileToClassPath(file.getCanonicalPath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    /**
     * Create SoletConfig instance and add objects.
     * This Solet Config will be used for initializing every solet.
     */
    private DispatcherConfig createDispatcherConfig() {
        final DispatcherConfig soletConfig = new DispatcherConfig();
        soletConfig.setAttribute(
                Dispatcher.CONFIG_SESSION_STORAGE_KEY,
                this.sessionManagement.getSessionStorage()
        );

        soletConfig.setAttribute(
                Dispatcher.CONFIG_SERVER_CONFIG_SERVICE_KEY,
                this.configCenter
        );

        soletConfig.setAttribute(
                Dispatcher.CONFIG_DEPENDENCY_CONTAINER_KEY,
                IocCenter.getRequestHandlersDependencyContainer()
        );

        return soletConfig;
    }
}
