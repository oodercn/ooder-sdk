package net.ooder.config.scene;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JdsSceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private String configName;
    
    private Integer adminPort = 9090;
    private String adminKey = "jds-admin";
    private boolean singleLogin = true;
    
    private boolean sessionEnabled = true;
    private long sessionExpireTime = 30 * 60 * 1000L;
    private long sessionCheckInterval = 5 * 60 * 1000L;
    
    private Integer cacheMaxSize = 10 * 1024 * 1024;
    private long cacheExpireTime = 24 * 60 * 60 * 1000L;
    
    private boolean clusterEnabled = false;
    private Integer udpPort = 8087;
    
    private Map<String, CapabilityConfig> capabilities = new HashMap<String, CapabilityConfig>();
    
    public JdsSceneConfig() {
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    
    public Integer getAdminPort() {
        return adminPort;
    }
    
    public void setAdminPort(Integer adminPort) {
        this.adminPort = adminPort;
    }
    
    public String getAdminKey() {
        return adminKey;
    }
    
    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }
    
    public boolean isSingleLogin() {
        return singleLogin;
    }
    
    public void setSingleLogin(boolean singleLogin) {
        this.singleLogin = singleLogin;
    }
    
    public boolean isSessionEnabled() {
        return sessionEnabled;
    }
    
    public void setSessionEnabled(boolean sessionEnabled) {
        this.sessionEnabled = sessionEnabled;
    }
    
    public long getSessionExpireTime() {
        return sessionExpireTime;
    }
    
    public void setSessionExpireTime(long sessionExpireTime) {
        this.sessionExpireTime = sessionExpireTime;
    }
    
    public long getSessionCheckInterval() {
        return sessionCheckInterval;
    }
    
    public void setSessionCheckInterval(long sessionCheckInterval) {
        this.sessionCheckInterval = sessionCheckInterval;
    }
    
    public Integer getCacheMaxSize() {
        return cacheMaxSize;
    }
    
    public void setCacheMaxSize(Integer cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }
    
    public long getCacheExpireTime() {
        return cacheExpireTime;
    }
    
    public void setCacheExpireTime(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
    }
    
    public boolean isClusterEnabled() {
        return clusterEnabled;
    }
    
    public void setClusterEnabled(boolean clusterEnabled) {
        this.clusterEnabled = clusterEnabled;
    }
    
    public Integer getUdpPort() {
        return udpPort;
    }
    
    public void setUdpPort(Integer udpPort) {
        this.udpPort = udpPort;
    }
    
    public Map<String, CapabilityConfig> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, CapabilityConfig> capabilities) {
        this.capabilities = capabilities;
    }
    
    @Override
    public String toString() {
        return "JdsSceneConfig{" +
                "sceneId='" + sceneId + '\'' +
                ", configName='" + configName + '\'' +
                ", sessionEnabled=" + sessionEnabled +
                ", sessionExpireTime=" + sessionExpireTime +
                '}';
    }
}
