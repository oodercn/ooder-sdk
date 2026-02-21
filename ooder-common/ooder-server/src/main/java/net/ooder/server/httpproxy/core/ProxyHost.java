package net.ooder.server.httpproxy.core;

import org.apache.http.HttpHost;

import java.net.URL;

public class ProxyHost {

    String proxyId;
    String host;
    String propertiesFile;
    String localIp;
    String port;
    String loginScript;
    String expression;
    String projectName;
    String sessionId;
    String indexPage;
    String proxyUrl;
    String pttern;


    public ProxyHost(URL hostUrl, String projectName, String propertiesFile, String ip, String port, String indexPage, String proxyUrl) {
        HttpHost target = new HttpHost(hostUrl.getHost(), hostUrl.getPort(), hostUrl.getProtocol());
        this.proxyId = UUID.createUUID().toString();
        this.host = hostUrl.getHost();
        if (proxyUrl != null) {
            this.proxyUrl = proxyUrl;
        } else {
            this.proxyUrl = target.toString();
        }

        this.projectName = projectName;
        this.propertiesFile = propertiesFile;
        this.localIp = ip;
        this.port = port;
        this.indexPage = indexPage;

    }

    public ProxyHost fill(ProxyHost proxyHost) {
        this.host = proxyHost.getHost();
        if (proxyHost.getLocalIp() != null && !proxyHost.getLocalIp().equals("")) {
            this.localIp = proxyHost.getLocalIp();
        }
        if (proxyHost.getPort() != null && !proxyHost.getPort().equals("")) {
            this.port = proxyHost.getPort();
        }
        this.loginScript = proxyHost.getLoginScript();
        this.expression = proxyHost.getExpression();
        this.pttern = proxyHost.getPttern();
        this.sessionId = proxyHost.getSessionId();
        this.proxyUrl = proxyHost.getProxyUrl();

        return this;

    }


    public ProxyHost() {

    }


    public String getIndexPage() {
        return indexPage;
    }

    public void setIndexPage(String indexPage) {
        this.indexPage = indexPage;
    }

    public String getPttern() {
        return pttern;
    }

    public void setPttern(String pttern) {
        this.pttern = pttern;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }


    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProxyId() {
        return proxyId;
    }

    public void setProxyId(String proxyId) {
        this.proxyId = proxyId;
    }


    public String getLoginScript() {
        return loginScript;
    }

    public void setLoginScript(String loginScript) {
        this.loginScript = loginScript;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProxyUrl() {
        if (proxyUrl == null) {
            proxyUrl = "http://" + getHost() + ":" + port;
        }
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
