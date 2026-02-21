package net.ooder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 用户/系统信息配置属性类
 * 使用 @ConfigurationProperties 实现类型安全的配置管理
 */
@Component
@ConfigurationProperties(prefix = "user")
public class UserProperties {
    private String username;
    private String userpassword;
    private String password;
    private String personid;
    private String systemCode;
    private String configName;
    private String index;
    private String serverUrl;
    private String loginUrl = "/api/sys/syslogin";
    private String clitentLoginUrl = "/api/sys/clientLogin";
    private String panelDisplayName;
    private Integer filePort;
    private boolean autoLogin;
    private boolean savePassword;
    private String proxyHost = "http://127.0.0.1";
    private Integer proxyPort = 8081;
    private String esdServerPort = "8091";
    private String webServerPort = "8081";
    private boolean offLine = true;
    private Integer msgport = 8088;
    private String udpUrl;
    private String title;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getClitentLoginUrl() {
        return clitentLoginUrl;
    }

    public void setClitentLoginUrl(String clitentLoginUrl) {
        this.clitentLoginUrl = clitentLoginUrl;
    }

    public String getPanelDisplayName() {
        return panelDisplayName;
    }

    public void setPanelDisplayName(String panelDisplayName) {
        this.panelDisplayName = panelDisplayName;
    }

    public Integer getFilePort() {
        return filePort;
    }

    public void setFilePort(Integer filePort) {
        this.filePort = filePort;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public boolean isSavePassword() {
        return savePassword;
    }

    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getEsdServerPort() {
        return esdServerPort;
    }

    public void setEsdServerPort(String esdServerPort) {
        this.esdServerPort = esdServerPort;
    }

    public String getWebServerPort() {
        return webServerPort;
    }

    public void setWebServerPort(String webServerPort) {
        this.webServerPort = webServerPort;
    }

    public boolean isOffLine() {
        return offLine;
    }

    public void setOffLine(boolean offLine) {
        this.offLine = offLine;
    }

    public Integer getMsgport() {
        return msgport;
    }

    public void setMsgport(Integer msgport) {
        this.msgport = msgport;
    }

    public String getUdpUrl() {
        return udpUrl;
    }

    public void setUdpUrl(String udpUrl) {
        this.udpUrl = udpUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}