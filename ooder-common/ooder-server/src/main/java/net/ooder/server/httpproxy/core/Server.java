package net.ooder.server.httpproxy.core;


import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSUtil;
import net.ooder.server.httpproxy.config.Handle;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.LogManager;


public class Server implements Runnable {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, Server.class);
    private ProxyHost proxyHost;


    Properties config = new ChainableProperties();
    HashMap endpoints = new HashMap();
    HashMap<String, Map> session = new HashMap<String, Map>();
    Handler handler = null;
    ResponseListener responseListener = null;
    ThreadPool threadPool;
    public boolean started = false;


    private static final String CLAZZ = ".class";


    public Server(String filename) throws IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(filename));
            config.load(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        setDefaultProperties(config);
    }

    public Server() {
        config = new ChainableProperties();
        setDefaultProperties(config);
        try {
            setupLogging(config);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public Server(ProxyHost host) {
        this.proxyHost = host;
        Properties props = new Properties();
        try {
            File engineConfigFile = new File(JDSUtil.getJdsRealPath(), "server.properties");
            props.load(new FileInputStream(engineConfigFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        props.setProperty("http.port", host.getPort());
        ChainableProperties config = new ChainableProperties(props);
        setDefaultProperties(config);
        this.config = config;
        try {
            setupLogging(config);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public Server(Properties config) {
        setDefaultProperties(config);
        try {
            setupLogging(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.config = config;

    }


    public Server(String[] args) throws IOException {
        config = new ChainableProperties();
        processArguments(args, config);
    }


    public void addEndPoint(String name, EndPoint endpoint) {
        endpoints.put(name, endpoint);
    }

    public void putProperty(Object key, Object value) {
        config.put(key, value);
    }

    public String getProperty(String key) {
        return config.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        String value = config.getProperty(key, defaultValue);
        return value;
    }

    public ProxyHost getProxyHost() {
        if (proxyHost == null) {
            proxyHost = new ProxyHost();
            proxyHost.setLocalIp(this.getLocalIp());
            proxyHost.setPort(this.getProperty("http.port"));
        }
        return proxyHost;
    }

    public void setProxyHost(ProxyHost proxyHost) {
        this.proxyHost = proxyHost;
    }

    public boolean hasProperty(String key) {
        return config.containsKey(key);
    }


    public Object get(Object key) {
        return config.get(key);
    }

    public Properties getConfig() {
        return config;
    }

    public Object getRegisteredComponent(Class clazz) {
        return config.get(clazz);
    }

    public void registerComponent(Object object) {
        config.put(object.getClass(), object);
    }


    public void start() throws IOException {
        if (!started) {
            // Runtime.getRuntime().addShutdownHook(new Thread(this, "JdsShutdown"));
            log.info("################################################################################");
            log.info("--------JDSConsoleServer Start---------port: [" + this.config.get("http.port") + "]-----------");
            log.info("#################################################################################");
            log.info("--------JDSConsoleServer InitEndPoint-----------");
            initializeThreads();
            initializeHandler();
            if (handler == null) {
                return;
            }

            constructEndPoints();

            for (Iterator i = endpoints.values().iterator(); i.hasNext(); ) {
                EndPoint currentEndPoint = (EndPoint) i.next();
                try {
                    currentEndPoint.start();
                } catch (IOException e) {
                    this.shutdown();
                    throw e;
                }
            }
            log.info("#################################################################################");
            started = true;
        }
    }

    private void initializeThreads() {
        try {
            threadPool = new ThreadPool(Integer.parseInt(config.getProperty("threadpool.size", "150")));
        } catch (NumberFormatException e) {
            log.warn("threadpool.size was not a number using default of 150");
            threadPool = new ThreadPool(150);
        }
    }

    protected void initializeHandler() {
        if (handler == null) {
            handler = (Handler) constructJdsObject(getProperty("handler"));
        }
        handler.initialize(getProperty("handler"), this);
    }

    public Object constructJdsObject(Handle handle) {
        Object theObject = null;
        String objectClassname = handle.getClassname();
        try {
            if (objectClassname == null)
                throw new ClassNotFoundException(objectClassname + CLAZZ + " configuration property not found.");
            Class handlerClass = Class.forName(objectClassname);
            Constructor[] constructors = handlerClass.getConstructors();
            Class[] paramClass = constructors[0].getParameterTypes();
            Object[] params = new Object[paramClass.length];
            for (int i = 0; i < paramClass.length; i++) {
                if (paramClass[i].equals(Server.class)) {
                    params[i] = this;
                } else if (paramClass[i].equals(Handle.class)) {
                    params[i] = handle;
                } else {
                    params[i] = getRegisteredComponent(paramClass[i]);
                }
            }
            theObject = constructors[0].newInstance(params);
            log.info("JDSConsoleServer object constructed. object=" + handle.getHandleid() + " class=" + objectClassname);
        } catch (IllegalAccessException e) {
            log.error("Could not access constructor.  Make sure it has the constructor is public.  Service not started.  class=" + objectClassname, e);
        } catch (InstantiationException e) {
            log.error("Could not instantiate object.  Service not started.  class=" + objectClassname, e);
        } catch (ClassNotFoundException e) {
            log.error("Could not find class.  Service not started.  class=" + objectClassname, e);
        } catch (InvocationTargetException e) {
            log.error("Could not instantiate object because constructor threw an exception.  Service not started.  class=" + objectClassname, e);
            log.error("Cause:", e.getTargetException());
        }
        return theObject;
    }


    public Object constructJdsObject(String objectName) {
        Object theObject = null;
        String objectClassname = getProperty(objectName + CLAZZ);
        try {
            if (objectClassname == null)
                throw new ClassNotFoundException(objectName + CLAZZ + " configuration property not found.");
            Class handlerClass = Class.forName(objectClassname);
            Constructor[] constructors = handlerClass.getConstructors();
            Class[] paramClass = constructors[0].getParameterTypes();
            Object[] params = new Object[paramClass.length];
            for (int i = 0; i < paramClass.length; i++) {
                if (paramClass[i].equals(Server.class)) {
                    params[i] = this;
                } else if (paramClass[i].equals(String.class)) {
                    params[i] = objectName;
                } else {
                    params[i] = getRegisteredComponent(paramClass[i]);
                }
            }
            theObject = constructors[0].newInstance(params);
            log.info("Jds object constructed. object=" + objectName + " class=" + objectClassname);
        } catch (IllegalAccessException e) {
            log.error("Could not access constructor.  Make sure it has the constructor is public.  Service not started.  class=" + objectClassname, e);
        } catch (InstantiationException e) {
            log.error("Could not instantiate object.  Service not started.  class=" + objectClassname, e);
        } catch (ClassNotFoundException e) {
            log.error("Could not find class.  Service not started.  class=" + objectClassname, e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error("Could not instantiate object because constructor threw an exception.  Service not started.  class=" + objectClassname, e);
            log.error("Cause:", e.getTargetException());
        }
        return theObject;
    }

    private void constructEndPoints() {
        String val = getProperty("endpoints");
        if (val != null) {
            StringTokenizer tokenizer = new StringTokenizer(val);
            while (tokenizer.hasMoreTokens()) {
                String endPointName = tokenizer.nextToken();
                try {
                    EndPoint endPoint = (EndPoint) constructJdsObject(endPointName);
                    endPoint.initialize(endPointName, this);
                    addEndPoint(endPointName, endPoint);
                } catch (IOException e) {
                    log.error(endPointName + " was not initialized properly.", e);
                }
            }
        } else {
            log.error("No endpoints defined.");
        }
    }


    public void run() {
        shutdown();
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * This method will shutdown the Handler, and call {@link EndPoint#shutdown} on each EndPoint.
     */
    public void shutdown() {

        log.info("Starting shutdown.");
        try {
            threadPool.shutdown();
            if (handler != null) {
                log.info("Shutting down handlers.");
                handler.shutdown(this);
            }
            Collection values = endpoints.values();
            if (values != null) {
                for (Iterator i = values.iterator(); i.hasNext(); ) {
                    EndPoint currentEndPoint = (EndPoint) i.next();
                    log.info("Shutting down endpoint " + currentEndPoint.getName());
                    currentEndPoint.shutdown(this);
                }
            }
        } finally {
            log.info("Shutdown complete.");
        }
        started = false;

    }

    public boolean isStarted() {
        return started;
    }


    public boolean post(Request request, Response response) throws IOException {

        return handler.handle(request, response);
    }

    public void post(Runnable runnable) {
        threadPool.execute(runnable);
    }


    public ResponseListener getResponseListeners() {
        return responseListener;
    }

    public void setResponseListener(ResponseListener listener) {
        this.responseListener = listener;
    }


    public static int main(String[] args, InputStream in, PrintStream out) throws IOException {
        Server server = new Server(args);
        try {
            server.start();
            System.out.println("Server started.  Press <Ctrl-C> to stop.");
            synchronized (server) {
                server.wait();
            }
        } catch (InterruptedException e) {
            log.error("Server Interupted.");
        }
        return 0;
    }

    ;

    public static void main(String[] args) throws IOException {
        Server server = new Server(args);
        try {
            server.start();
            System.out.println("Server started.  Press <Ctrl-C> to stop.");
            synchronized (server) {
                server.wait();
            }
        } catch (InterruptedException e) {
            log.error("Server Interupted.");
        }

    }

    protected void processArguments(String[] args, Properties props) throws IOException {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equalsIgnoreCase("-config")) {
                if (i + 1 < args.length) {
                    loadConfiguration(args[i + 1], props);
                } else {
                    throw new IOException("-config parameter must be followed by a config file.");
                }
            } else if (args[i].startsWith("-")) {
                props.setProperty(args[i].substring(1), args[i + 1]);
            }
        }
        setDefaultProperties(props);
        setupLogging(props);
    }

    private void setupLogging(Properties props) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        props.store(stream, "");
        ByteArrayInputStream bais = new ByteArrayInputStream(stream.toByteArray());
        LogManager.getLogManager().readConfiguration(bais);
    }

    private static void setDefaultProperties(Properties props) {
        setDefaultProperty(props, "mime.html", "text/html");
        setDefaultProperty(props, "mime.zip", "application/x-zip-compressed");
        setDefaultProperty(props, "mime.gif", "image/gif");
        setDefaultProperty(props, "mime.jpeg", "image/jpeg");
        setDefaultProperty(props, "mime.jpg", "image/jpeg");
        setDefaultProperty(props, "mime.png", "image/png");
        setDefaultProperty(props, "mime.css", "text/css");
        setDefaultProperty(props, "http.port", "8082");
        setDefaultProperty(props, "mime.js", "application/x-javascript");
        setDefaultProperty(props, "mime.action", "text/html");

        // sets a default endpoint for http
        setDefaultProperty(props, "endpoints", "http");
        setDefaultProperty(props, "http.class", "net.ooder.server.httpproxy.core.ServerSocketEndPoint");

        setDefaultProperty(props, "handler", "chain");
        setDefaultProperty(props, "chain.class", "net.ooder.server.httpproxy.handler.DefaultChainHandler");
        setDefaultProperty(props, "chain.chain", "root,action");

        // these are properties read by the ResourceHandler named 'root'
        setDefaultProperty(props, "root.class", "net.ooder.server.httpproxy.handler.ResourceHandler");
        setDefaultProperty(props, "root.urlPrefix", "/");
        setDefaultProperty(props, "root.resourceMount", "/");

//      these are properties read by the ResourceHandler named 'root'
        setDefaultProperty(props, "action.class", "net.ooder.server.httpproxy.handler.UrlProxyHandler");
        setDefaultProperty(props, "action.urlPrefix", "/");
        setDefaultProperty(props, "action.rule", ".*\\.(action|do|ajax|jsp)(|\\?.*)");
        setDefaultProperty(props, "action.subst", ".action");

    }

    private static void setDefaultProperty(Properties props, String key, String value) {
        if (props.getProperty(key) == null) {
            props.setProperty(key, value);
        }
    }

    protected void loadConfiguration(String config, Properties props) throws IOException {
        InputStream is = openInputStream(config);
        props.load(is);
        is.close();
    }

    private InputStream openInputStream(String config) throws FileNotFoundException {
        InputStream is;
        try {
            is = new FileInputStream(config);
        } catch (FileNotFoundException e) {
            is = Server.class.getResourceAsStream("/" + config);
            if (is == null) throw e;
        }
        return is;
    }

    private String getLocalIp() {
        String localIp = "127.0.0.1";
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return localIp;
    }

    public HashMap<String, Map> getSession() {
        return session;
    }

    public void setSession(HashMap session) {
        this.session = session;
    }
}
