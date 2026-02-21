package net.ooder.server.httpproxy;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSUtil;
import net.ooder.config.UserBean;
import net.ooder.server.httpproxy.core.ChainableProperties;
import net.ooder.server.httpproxy.core.ProxyHost;
import net.ooder.server.httpproxy.core.Server;
import net.ooder.server.httpproxy.nioproxy.ProxyServer;
import net.ooder.web.APIConfigFactory;
import net.ooder.web.RequestMethodBean;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class ServerProxyFactory {

    public static ServerProxyFactory serverFactory;

    public static Map<String, ProxyHost> serverProxyMap = new HashMap();

    public static Map<String, Server> serverMap = new HashMap();

    public static final String THREAD_LOCK = "Thread Lock";

    public static final String AdmainHost = "itjds.net";

    public static final String protPropertes = "http.port";

    public static final String[] OtherUrlfilter = new String[]{".*googleapis.com//?.*", ".*cloudflare.com//?.*", ".*beacons.*?.*\\.gvt.*?\\.com//.*", ".*click.com//?.*", ".*beacons.*?.*\\.gvt.?\\.com//.*", ".*gvt1.com//?.*"};

    public static final String[] CDNUrlfilter = new String[]{".*alicdn.com//?.*"};


    public static final String[] RADUrlfilter = new String[]{".*/(public|custom|RAD|plugins|ood|root|debug|jds|vfs)//?.*", ".*?/.*\\.(dyn|cls)$"};


    public static final String[] localUrlfilte = new String[]{".*/favicon.ico"};

    public static final String[] CustomUrlfilte = new String[]{"/(custom|thumbnail)//?.*", ".*?/.*\\.view$"};

    public Server adminServer;

    public Server customServer;

    public Server webServer;

    public ProxyServer proxyServer;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ServerProxyFactory.class);

    public static ServerProxyFactory getInstance() {
        if (serverFactory == null) {
            synchronized (THREAD_LOCK) {
                if (serverFactory == null) {
                    serverFactory = new ServerProxyFactory();
                }
            }
        }
        return serverFactory;
    }

    void loadProject() {

    }

    public void clear() {

        serverProxyMap.clear();
        serverMap.clear();
    }


    ServerProxyFactory() {
        start();
    }

    public Server getWebServer() {
        return webServer;
    }

    public void setWebServer(Server webServer) {
        this.webServer = webServer;
    }

    void start() {

        this.startAdminServer();
        this.startProxyServer();
        this.startCustomServer();
        this.startWebServer();
        String defalutAppUrl = System.getProperty("appUrl");
        String proxyUrl = System.getProperty("proxyUrl");
        String indexPage = System.getProperty("indexPage");
        String proxyPort = System.getProperty("proxyPort");
        if (proxyPort == null || proxyPort.equals("")) {
            proxyPort = UserBean.getInstance().getEsdServerPort();
        }

        String defalutProjectName = System.getProperty("projectName");

        if (defalutAppUrl != null && defalutProjectName != null) {
            try {
                if (defalutAppUrl.indexOf("\"") > -1) {
                    defalutAppUrl = defalutAppUrl.substring(1, defalutAppUrl.length() - 1);
                }
                if (defalutAppUrl.indexOf("$") > -1) {
                    String[] defalutAppUrls = StringUtility.split(defalutAppUrl, "$");
                    for (String url : defalutAppUrls) {
                        if (!url.startsWith("http")) {
                            url = "http://" + url;
                        }
                        createProxy(new URL(url), defalutProjectName, indexPage, proxyUrl, null);
                    }
                } else if (defalutAppUrl.indexOf("|") > -1) {
                    String[] defalutAppUrls = StringUtility.split(defalutAppUrl, "|");
                    for (String url : defalutAppUrls) {
                        if (!url.startsWith("http")) {
                            url = "http://" + url;
                        }
                        createProxy(new URL(url), defalutProjectName, indexPage, proxyUrl, proxyPort);
                    }
                } else {
                    if (!defalutAppUrl.startsWith("http")) {
                        defalutAppUrl = "http://" + defalutAppUrl;
                    }
                    createProxy(new URL(defalutAppUrl), defalutProjectName, indexPage, proxyUrl, proxyPort);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    void startProxyServer() {
        try {
            Integer proxyPort = UserBean.getInstance().getProxyPort();
            this.proxyServer = new ProxyServer(proxyPort);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    proxyServer.start();
                }
            }).start();


        } catch (IOReactorException e) {
            e.printStackTrace();
        }
    }


    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public void setProxyServer(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }


    public HttpHost getProxyHost(HttpRequest request) {

        String urlStr = request.getRequestLine().getUri();


        if (urlStr.indexOf("google") > -1) {
            return new HttpHost(urlStr);
        }

        String protocol = request.getProtocolVersion().getProtocol().toLowerCase();
        String host = request.getFirstHeader("host").getValue();

        if (!urlStr.toLowerCase().startsWith(protocol)) {
            if (urlStr.startsWith(host)) {
                urlStr = protocol + "://" + urlStr;
            } else {
                urlStr = protocol + "://" + host + urlStr;
            }
        }

        if (urlStr.endsWith(":443")) {
            urlStr = urlStr.substring(0, urlStr.length() - ":443".length());
        }

        HttpHost target = null;
        try {
            try {
                URL sourceUrl = new URL(urlStr);
                String proxyUrl = getProxyUrl(sourceUrl);
                URL url = new URL(proxyUrl);
                if (sourceUrl != null && url == null) {
                    target = new HttpHost(sourceUrl.getHost(), sourceUrl.getPort(), sourceUrl.getProtocol());
                } else {
                    target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                    request.setHeader("Host", url.getHost());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return target;
    }

    String getProxyUrl(URL url) {
        String adminUrl = "http://" + getLocalIp() + ":" + adminServer.getProperty(protPropertes);
        String customUrl = "http://" + getLocalIp() + ":" + customServer.getProperty(protPropertes);
        for (String otherUrl : CustomUrlfilte) {
            Pattern rule = Pattern.compile(otherUrl);
            boolean ruleMatches = rule.matcher(url.getPath()).matches();
            if (ruleMatches) {
                return customUrl;
            }
        }


        if (url.getHost().equals(AdmainHost)) {
            return adminUrl;
        }

        for (String otherUrl : OtherUrlfilter) {
            Pattern rule = Pattern.compile(otherUrl);
            boolean ruleMatches = rule.matcher(url.toString()).matches();
            if (ruleMatches) {
                return url.toString();
            }
        }


        for (String otherUrl : CDNUrlfilter) {
            Pattern rule = Pattern.compile(otherUrl);
            boolean ruleMatches = rule.matcher(url.toString()).matches();
            if (ruleMatches) {
                return adminUrl;
            }
        }
        ProxyHost proxy = getProxy(url);
        if (proxy == null) {
            try {
                proxy = this.createProxy(url, null, null, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String localProxy = url.getProtocol() + "://" + proxy.getLocalIp() + ":" + proxy.getPort();
        for (String otherUrl : localUrlfilte) {
            Pattern rule = Pattern.compile(otherUrl);
            boolean ruleMatches = rule.matcher(url.getPath()).matches();
            if (ruleMatches) {
                return localProxy;
            }
        }

        if (proxy.getProjectName() != null) {
            Pattern pattern = Pattern.compile(".*/(" + proxy.getProjectName() + ")//?.*");
            if (pattern != null) {
                if (pattern.matcher(url.getPath()).matches()) {
                    return localProxy;
                }
            }
        }


        for (String otherUrl : RADUrlfilter) {
            Pattern rule = Pattern.compile(otherUrl);
            boolean ruleMatches = rule.matcher(url.getPath()).matches();
            if (ruleMatches) {
                return localProxy;
            }
        }

        RequestMethodBean methodBean = APIConfigFactory.getInstance().findMethodBean(url.getPath());
        if (methodBean != null) {
            return localProxy;
        }
        String proxyUrl = proxy.getProxyUrl();
        if (!proxyUrl.toLowerCase().startsWith("http")) {
            proxyUrl = "http://" + proxyUrl;
        }


        logger.info("源地址：" + url.getPath() + "转向地址：" + proxyUrl);
        return proxyUrl;
    }

    void startCustomServer() {

        if (customServer == null) {
            Properties props = new Properties();
            try {
                File engineConfigFile = new File(JDSUtil.getJdsRealPath(), "customserver.properties");
                props.load(new FileInputStream(engineConfigFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            props.setProperty(protPropertes, this.getLocalPort());
            ChainableProperties config = new ChainableProperties(props);
            customServer = new Server(config);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        customServer.start();
                        synchronized (customServer) {
                            customServer.wait();
                        }
                    } catch (Exception e) {
                        customServer.getConfig().setProperty(protPropertes, getLocalPort());
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    void startAdminServer() {
        if (adminServer == null) {
            Properties props = new Properties();
            try {
                File engineConfigFile = new File(JDSUtil.getJdsRealPath(), "consoleserver.properties");
                props.load(new FileInputStream(engineConfigFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            props.setProperty(protPropertes, getLocalPort());
            ChainableProperties config = new ChainableProperties(props);
            adminServer = new Server(config);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        adminServer.start();
                        synchronized (adminServer) {
                            adminServer.wait();
                        }
                    } catch (Exception e) {
                        adminServer.getConfig().setProperty(protPropertes, getLocalPort());
                        e.printStackTrace();

                    }
                }
            }).start();
        }
    }

    void startWebServer() {
        if (webServer == null) {
            Properties props = new Properties();
            try {
                File engineConfigFile = new File(JDSUtil.getJdsRealPath(), "server.properties");
                props.load(new FileInputStream(engineConfigFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String webServerPort = UserBean.getInstance().getWebServerPort();
            webServerPort = checkPort(webServerPort);

            props.setProperty(protPropertes, webServerPort);
            ChainableProperties config = new ChainableProperties(props);
            webServer = new Server(config);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        webServer.start();
                        synchronized (webServer) {
                            webServer.wait();
                        }
                    } catch (Exception e) {
                        webServer.getConfig().setProperty(protPropertes, getLocalPort());

                        e.printStackTrace();

                    }
                }
            }).start();
        }
    }

    void startDebugServer() {
        if (adminServer == null) {
            Properties props = new Properties();
            try {
                File engineConfigFile = new File(JDSUtil.getJdsRealPath(), "consoleserver.properties");
                props.load(new FileInputStream(engineConfigFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            props.setProperty(protPropertes, getLocalPort());
            ChainableProperties config = new ChainableProperties(props);
            adminServer = new Server(config);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        adminServer.start();
                        synchronized (adminServer) {
                            adminServer.wait();
                        }
                    } catch (Exception e) {
                        adminServer.getConfig().setProperty(protPropertes, getLocalPort());
                        e.printStackTrace();

                    }
                }
            }).start();
        }
    }


    public void shutdown() {
        this.customServer.shutdown();
        this.customServer = null;
        this.adminServer.shutdown();
        this.adminServer = null;
        this.webServer.shutdown();
        this.webServer = null;
        this.proxyServer.stop();
        this.proxyServer = null;
        this.clear();

        this.start();
    }


    public List<ProxyHost> getProxyList() {
        Set<String> keySet = serverProxyMap.keySet();
        List<ProxyHost> proxyHostLis = new ArrayList<ProxyHost>();
        for (String hostName : keySet) {
            ProxyHost proxyHost = serverProxyMap.get(hostName);
            if (proxyHost != null) {
                proxyHostLis.add(proxyHost);
            }
        }

        return proxyHostLis;
    }

    public ProxyHost getProxy(URL url) {
        ProxyHost host = serverProxyMap.get(url.getHost());
        if (host != null) {
            Server server = serverMap.get(host.getHost());
            if (server == null || !server.isStarted()) {
                host = this.createProxy(host);
                serverProxyMap.put(url.getHost(), host);
            }
        }
        return host;
    }

    private String checkPort(String localPort) {
        if (localPort == null || localPort.equals("")) {
            localPort = getLocalPort();
        } else {
            ServerSocket s = null;
            try {
                s = new ServerSocket(Integer.valueOf(localPort));
            } catch (IOException e) {
                //  e.printStackTrace();
            }

            if (s == null) {
                localPort = getLocalPort();
            } else {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localPort;
    }

    public ProxyHost createProxy(ProxyHost hostProxy) {
        Server server = serverMap.get(hostProxy.getHost());
        ProxyHost proxyHost = null;
        if (server == null || !server.isStarted()) {
            try {
                String localPort = hostProxy.getPort();
                localPort = checkPort(localPort);
                hostProxy.setPort(localPort);
                hostProxy.setLocalIp(getLocalIp());
                File engineConfigFile = new File(JDSUtil.getJdsRealPath(), "server.properties");
                if (engineConfigFile.exists()) {
                    hostProxy.setPropertiesFile(engineConfigFile.getAbsolutePath());
                }
                Server proxyServer = new Server(hostProxy);
                CountDownLatch countDownLatch = new CountDownLatch(1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            proxyServer.start();
                            countDownLatch.countDown();
                            synchronized (proxyServer) {
                                proxyServer.wait();
                            }

                        } catch (Exception e) {
                            proxyServer.shutdown();
                            String port = getLocalPort();
                            proxyServer.getConfig().setProperty(protPropertes, port);
                            proxyServer.getProxyHost().setPort(port);
                            try {
                                proxyServer.start();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            countDownLatch.countDown();
                        }
                    }
                }).start();
                countDownLatch.await();
                serverMap.put(proxyServer.getProxyHost().getHost(), proxyServer);
                serverProxyMap.put(proxyServer.getProxyHost().getHost(), proxyServer.getProxyHost());
                server = proxyServer;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        proxyHost = server.getProxyHost();
        return proxyHost;
    }

    public ProxyHost createProxy(URL url, String projectName, String indexPage, String proxyHost, String localPort) throws IOException {

        if (projectName == null) {
            projectName = System.getProperty("projectName");
        }

        if (proxyHost == null) {
            proxyHost = System.getProperty("proxyUrl");
        }
        if (indexPage == null) {
            indexPage = System.getProperty("indexPage");
        }

        ProxyHost host = this.getProxy(url);
        if (host == null) {
            localPort = this.getLocalPort();
            String localIP = getLocalIp();
            File engineConfigFile = new File(JDSUtil.getJdsRealPath(), "server.properties");
            logger.info("开始创建新代理服务器 url ：" + url.toString() + "projectName：" + projectName + "proxyHost:" + proxyHost + " indexPage:" + indexPage);
            host = new ProxyHost(url, projectName, engineConfigFile.getPath(), localIP, localPort, indexPage, proxyHost);
            host = createProxy(host);
        } else {
            localPort = checkPort(host.getPort());
            host.setLocalIp(this.getLocalIp());
            host.setPort(localPort);
            if (projectName != null) {
                host.setProjectName(projectName);
            }
            if (indexPage != null) {
                host.setIndexPage(indexPage);
            }
            logger.info("开始创建新代理服务器 url ：" + host.toString() + "projectName：" + projectName + "proxyHost:" + proxyHost + " indexPage:" + indexPage);

            host = createProxy(host);
        }
        return host;
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

    public static boolean isLocalPortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress Address = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(Address, port);  //建立一个Socket连接
            flag = true;
        } catch (IOException e) {

        }
        return flag;
    }

    public static String getLocalPort() {
        ServerSocket s = null;
        String port = "8083";
        try {
            s = new ServerSocket(0);
            port = s.getLocalPort() + "";
            s.close();
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return port;
    }

    public Server getAdminServer() {
        return adminServer;
    }

    public void setAdminServer(Server adminServer) {
        this.adminServer = adminServer;
    }

    public Server getCustomServer() {
        return customServer;
    }

    public void setCustomServer(Server customServer) {
        this.customServer = customServer;
    }
}