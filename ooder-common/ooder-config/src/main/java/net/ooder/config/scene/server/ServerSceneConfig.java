package net.ooder.config.scene.server;

import net.ooder.config.scene.enums.DataSourceLevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private String sceneName;
    private String sceneVersion;
    
    private SceneStatus status = SceneStatus.DRAFT;
    private SceneEnvironment environment = SceneEnvironment.DEVELOPMENT;
    
    private Map<String, CapabilityConfig> capabilities = new HashMap<String, CapabilityConfig>();
    private Map<String, DataSourceConfig> dataSources = new HashMap<String, DataSourceConfig>();
    
    private List<String> allowedClients = new ArrayList<String>();
    private SecurityPolicy securityPolicy = new SecurityPolicy();
    
    private SyncConfig sync = new SyncConfig();
    private MonitoringConfig monitoring = new MonitoringConfig();
    
    public enum SceneStatus {
        DRAFT,
        PUBLISHED,
        DEPLOYED,
        ACTIVE,
        DEPRECATED,
        ARCHIVED
    }
    
    public enum SceneEnvironment {
        DEVELOPMENT,
        TESTING,
        STAGING,
        PRODUCTION
    }
    
    public ServerSceneConfig() {
    }
    
    public static ServerSceneConfigBuilder builder() {
        return new ServerSceneConfigBuilder();
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
    
    public SceneStatus getStatus() {
        return status;
    }
    
    public void setStatus(SceneStatus status) {
        this.status = status;
    }
    
    public SceneEnvironment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(SceneEnvironment environment) {
        this.environment = environment;
    }
    
    public Map<String, CapabilityConfig> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, CapabilityConfig> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new HashMap<String, CapabilityConfig>();
    }
    
    public CapabilityConfig getCapability(String capabilityId) {
        return capabilities.get(capabilityId);
    }
    
    public void addCapability(CapabilityConfig capability) {
        if (capability != null && capability.getCapabilityId() != null) {
            this.capabilities.put(capability.getCapabilityId(), capability);
        }
    }
    
    public Map<String, DataSourceConfig> getDataSources() {
        return dataSources;
    }
    
    public void setDataSources(Map<String, DataSourceConfig> dataSources) {
        this.dataSources = dataSources != null ? dataSources : new HashMap<String, DataSourceConfig>();
    }
    
    public DataSourceConfig getDataSource(String dataSourceId) {
        return dataSources.get(dataSourceId);
    }
    
    public void addDataSource(DataSourceConfig dataSource) {
        if (dataSource != null && dataSource.getDataSourceId() != null) {
            this.dataSources.put(dataSource.getDataSourceId(), dataSource);
        }
    }
    
    public List<String> getAllowedClients() {
        return allowedClients;
    }
    
    public void setAllowedClients(List<String> allowedClients) {
        this.allowedClients = allowedClients != null ? allowedClients : new ArrayList<String>();
    }
    
    public void addAllowedClient(String clientId) {
        if (clientId != null && !allowedClients.contains(clientId)) {
            allowedClients.add(clientId);
        }
    }
    
    public SecurityPolicy getSecurityPolicy() {
        return securityPolicy;
    }
    
    public void setSecurityPolicy(SecurityPolicy securityPolicy) {
        this.securityPolicy = securityPolicy;
    }
    
    public SyncConfig getSync() {
        return sync;
    }
    
    public void setSync(SyncConfig sync) {
        this.sync = sync;
    }
    
    public MonitoringConfig getMonitoring() {
        return monitoring;
    }
    
    public void setMonitoring(MonitoringConfig monitoring) {
        this.monitoring = monitoring;
    }
    
    public boolean isActive() {
        return status == SceneStatus.ACTIVE;
    }
    
    public boolean isProduction() {
        return environment == SceneEnvironment.PRODUCTION;
    }
    
    public static class ServerSceneConfigBuilder {
        private ServerSceneConfig config = new ServerSceneConfig();
        
        public ServerSceneConfigBuilder sceneId(String sceneId) {
            config.sceneId = sceneId;
            return this;
        }
        
        public ServerSceneConfigBuilder sceneName(String sceneName) {
            config.sceneName = sceneName;
            return this;
        }
        
        public ServerSceneConfigBuilder sceneVersion(String sceneVersion) {
            config.sceneVersion = sceneVersion;
            return this;
        }
        
        public ServerSceneConfigBuilder status(SceneStatus status) {
            config.status = status;
            return this;
        }
        
        public ServerSceneConfigBuilder environment(SceneEnvironment environment) {
            config.environment = environment;
            return this;
        }
        
        public ServerSceneConfigBuilder capability(CapabilityConfig capability) {
            config.addCapability(capability);
            return this;
        }
        
        public ServerSceneConfigBuilder dataSource(DataSourceConfig dataSource) {
            config.addDataSource(dataSource);
            return this;
        }
        
        public ServerSceneConfigBuilder allowedClient(String clientId) {
            config.addAllowedClient(clientId);
            return this;
        }
        
        public ServerSceneConfigBuilder securityPolicy(SecurityPolicy securityPolicy) {
            config.securityPolicy = securityPolicy;
            return this;
        }
        
        public ServerSceneConfigBuilder sync(SyncConfig sync) {
            config.sync = sync;
            return this;
        }
        
        public ServerSceneConfigBuilder monitoring(MonitoringConfig monitoring) {
            config.monitoring = monitoring;
            return this;
        }
        
        public ServerSceneConfig build() {
            return config;
        }
    }
}
