package net.ooder.config.scene.client;

import net.ooder.config.scene.enums.DataSourceLevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientSceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private String sceneName;
    private String sceneVersion;
    
    private ClientMode mode = ClientMode.ONLINE;
    private String serverEndpoint;
    private long configRefreshInterval = 300000L;
    
    private Map<String, CapabilityProxyConfig> capabilities = new HashMap<String, CapabilityProxyConfig>();
    private Map<String, DataSourceProxyConfig> dataSources = new HashMap<String, DataSourceProxyConfig>();
    
    private CacheConfig cache = new CacheConfig();
    private OfflineConfig offline = new OfflineConfig();
    private SecurityConfig security = new SecurityConfig();
    
    public enum ClientMode {
        ONLINE,
        OFFLINE,
        HYBRID
    }
    
    public ClientSceneConfig() {
    }
    
    public static ClientSceneConfigBuilder builder() {
        return new ClientSceneConfigBuilder();
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getSceneName() {
        return sceneName;
    }
    
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
    
    public String getSceneVersion() {
        return sceneVersion;
    }
    
    public void setSceneVersion(String sceneVersion) {
        this.sceneVersion = sceneVersion;
    }
    
    public ClientMode getMode() {
        return mode;
    }
    
    public void setMode(ClientMode mode) {
        this.mode = mode;
    }
    
    public String getServerEndpoint() {
        return serverEndpoint;
    }
    
    public void setServerEndpoint(String serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }
    
    public long getConfigRefreshInterval() {
        return configRefreshInterval;
    }
    
    public void setConfigRefreshInterval(long configRefreshInterval) {
        this.configRefreshInterval = configRefreshInterval;
    }
    
    public Map<String, CapabilityProxyConfig> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, CapabilityProxyConfig> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new HashMap<String, CapabilityProxyConfig>();
    }
    
    public CapabilityProxyConfig getCapability(String capabilityId) {
        return capabilities.get(capabilityId);
    }
    
    public void addCapability(CapabilityProxyConfig capability) {
        if (capability != null && capability.getCapabilityId() != null) {
            this.capabilities.put(capability.getCapabilityId(), capability);
        }
    }
    
    public Map<String, DataSourceProxyConfig> getDataSources() {
        return dataSources;
    }
    
    public void setDataSources(Map<String, DataSourceProxyConfig> dataSources) {
        this.dataSources = dataSources != null ? dataSources : new HashMap<String, DataSourceProxyConfig>();
    }
    
    public DataSourceProxyConfig getDataSource(String dataSourceId) {
        return dataSources.get(dataSourceId);
    }
    
    public void addDataSource(DataSourceProxyConfig dataSource) {
        if (dataSource != null && dataSource.getDataSourceId() != null) {
            this.dataSources.put(dataSource.getDataSourceId(), dataSource);
        }
    }
    
    public CacheConfig getCache() {
        return cache;
    }
    
    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }
    
    public OfflineConfig getOffline() {
        return offline;
    }
    
    public void setOffline(OfflineConfig offline) {
        this.offline = offline;
    }
    
    public SecurityConfig getSecurity() {
        return security;
    }
    
    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }
    
    public boolean isOnline() {
        return mode == ClientMode.ONLINE;
    }
    
    public boolean isOffline() {
        return mode == ClientMode.OFFLINE;
    }
    
    public boolean isHybrid() {
        return mode == ClientMode.HYBRID;
    }
    
    public static class ClientSceneConfigBuilder {
        private ClientSceneConfig config = new ClientSceneConfig();
        
        public ClientSceneConfigBuilder sceneId(String sceneId) {
            config.sceneId = sceneId;
            return this;
        }
        
        public ClientSceneConfigBuilder sceneName(String sceneName) {
            config.sceneName = sceneName;
            return this;
        }
        
        public ClientSceneConfigBuilder sceneVersion(String sceneVersion) {
            config.sceneVersion = sceneVersion;
            return this;
        }
        
        public ClientSceneConfigBuilder mode(ClientMode mode) {
            config.mode = mode;
            return this;
        }
        
        public ClientSceneConfigBuilder serverEndpoint(String serverEndpoint) {
            config.serverEndpoint = serverEndpoint;
            return this;
        }
        
        public ClientSceneConfigBuilder configRefreshInterval(long interval) {
            config.configRefreshInterval = interval;
            return this;
        }
        
        public ClientSceneConfigBuilder capability(CapabilityProxyConfig capability) {
            config.addCapability(capability);
            return this;
        }
        
        public ClientSceneConfigBuilder dataSource(DataSourceProxyConfig dataSource) {
            config.addDataSource(dataSource);
            return this;
        }
        
        public ClientSceneConfigBuilder cache(CacheConfig cache) {
            config.cache = cache;
            return this;
        }
        
        public ClientSceneConfigBuilder offline(OfflineConfig offline) {
            config.offline = offline;
            return this;
        }
        
        public ClientSceneConfigBuilder security(SecurityConfig security) {
            config.security = security;
            return this;
        }
        
        public ClientSceneConfig build() {
            return config;
        }
    }
}
