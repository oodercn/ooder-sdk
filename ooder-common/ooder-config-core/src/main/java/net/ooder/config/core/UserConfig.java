package net.ooder.config.core;

import java.io.Serializable;

public class UserConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String serverUrl;
    private String systemCode;
    private String configName;
    private String username;
    private String password;
    private String personId;
    private String index;
    private String proxyHost = "http://127.0.0.1";
    private Integer proxyPort = 8081;
    private String loginUrl = "/api/sys/syslogin";
    private boolean autoLogin;
    private boolean savePassword;
    private boolean offline = true;
    private Integer msgPort = 8088;
    private String udpUrl;
    
    public UserConfig() {
    }
    
    public String getServerUrl() {
        return serverUrl;
    }
    
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPersonId() {
        return personId;
    }
    
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    
    public String getIndex() {
        return index;
    }
    
    public void setIndex(String index) {
        this.index = index;
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
    
    public String getLoginUrl() {
        return loginUrl;
    }
    
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
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
    
    public boolean isOffline() {
        return offline;
    }
    
    public void setOffline(boolean offline) {
        this.offline = offline;
    }
    
    public Integer getMsgPort() {
        return msgPort;
    }
    
    public void setMsgPort(Integer msgPort) {
        this.msgPort = msgPort;
    }
    
    public String getUdpUrl() {
        return udpUrl;
    }
    
    public void setUdpUrl(String udpUrl) {
        this.udpUrl = udpUrl;
    }
    
    public String getValue(String key) {
        switch (key) {
            case "serverUrl":
                return serverUrl;
            case "systemCode":
                return systemCode;
            case "configName":
                return configName;
            case "username":
                return username;
            case "password":
            case "userpassword":
                return password;
            case "personId":
            case "personid":
                return personId;
            case "index":
                return index;
            case "proxyHost":
                return proxyHost;
            case "proxyPort":
                return proxyPort != null ? String.valueOf(proxyPort) : null;
            case "loginUrl":
                return loginUrl;
            case "autoLogin":
                return String.valueOf(autoLogin);
            case "savePassword":
                return String.valueOf(savePassword);
            case "offline":
            case "offLine":
            case "OffLine":
                return String.valueOf(offline);
            case "msgPort":
            case "msgport":
                return msgPort != null ? String.valueOf(msgPort) : null;
            case "udpUrl":
                return udpUrl;
            default:
                return null;
        }
    }
}
