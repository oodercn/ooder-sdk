package net.ooder.config.scene.server;

import net.ooder.config.scene.enums.DataSourceLevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSourceConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String dataSourceId;
    private String dataSourceName;
    private String dataSourceType;
    
    private String category;
    private DataSourceLevel maxLevel = DataSourceLevel.READ_WRITE;
    
    private Map<String, Object> connectionConfig = new HashMap<String, Object>();
    private PoolConfig poolConfig = new PoolConfig();
    
    private boolean enabled = true;
    private boolean readonly = false;
    
    private List<String> capabilities = new ArrayList<String>();
    private List<String> allowedSkills = new ArrayList<String>();
    
    private HealthCheckConfig healthCheck = new HealthCheckConfig();
    private FailoverConfig failover = new FailoverConfig();
    
    public DataSourceConfig() {
    }
    
    public DataSourceConfig(String dataSourceId, String dataSourceType) {
        this.dataSourceId = dataSourceId;
        this.dataSourceType = dataSourceType;
    }
    
    public static DataSourceConfigBuilder builder() {
        return new DataSourceConfigBuilder();
    }
    
    public String getDataSourceId() {
        return dataSourceId;
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceName() {
        return dataSourceName;
    }
    
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
    
    public String getDataSourceType() {
        return dataSourceType;
    }
    
    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public DataSourceLevel getMaxLevel() {
        return maxLevel;
    }
    
    public void setMaxLevel(DataSourceLevel maxLevel) {
        this.maxLevel = maxLevel;
    }
    
    public Map<String, Object> getConnectionConfig() {
        return connectionConfig;
    }
    
    public void setConnectionConfig(Map<String, Object> connectionConfig) {
        this.connectionConfig = connectionConfig != null ? 
            connectionConfig : new HashMap<String, Object>();
    }
    
    public Object get(String key) {
        return connectionConfig.get(key);
    }
    
    public String getString(String key) {
        Object value = connectionConfig.get(key);
        return value != null ? value.toString() : null;
    }
    
    public PoolConfig getPoolConfig() {
        return poolConfig;
    }
    
    public void setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isReadonly() {
        return readonly;
    }
    
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
    
    public List<String> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new ArrayList<String>();
    }
    
    public void addCapability(String capabilityId) {
        if (capabilityId != null && !capabilities.contains(capabilityId)) {
            capabilities.add(capabilityId);
        }
    }
    
    public List<String> getAllowedSkills() {
        return allowedSkills;
    }
    
    public void setAllowedSkills(List<String> allowedSkills) {
        this.allowedSkills = allowedSkills != null ? allowedSkills : new ArrayList<String>();
    }
    
    public void addAllowedSkill(String skillId) {
        if (skillId != null && !allowedSkills.contains(skillId)) {
            allowedSkills.add(skillId);
        }
    }
    
    public HealthCheckConfig getHealthCheck() {
        return healthCheck;
    }
    
    public void setHealthCheck(HealthCheckConfig healthCheck) {
        this.healthCheck = healthCheck;
    }
    
    public FailoverConfig getFailover() {
        return failover;
    }
    
    public void setFailover(FailoverConfig failover) {
        this.failover = failover;
    }
    
    public boolean isCore() {
        return "core".equals(category);
    }
    
    public boolean isApplication() {
        return "application".equals(category);
    }
    
    public boolean isCache() {
        return "cache".equals(category);
    }
    
    public static class DataSourceConfigBuilder {
        private DataSourceConfig config = new DataSourceConfig();
        
        public DataSourceConfigBuilder dataSourceId(String dataSourceId) {
            config.dataSourceId = dataSourceId;
            return this;
        }
        
        public DataSourceConfigBuilder dataSourceName(String dataSourceName) {
            config.dataSourceName = dataSourceName;
            return this;
        }
        
        public DataSourceConfigBuilder dataSourceType(String dataSourceType) {
            config.dataSourceType = dataSourceType;
            return this;
        }
        
        public DataSourceConfigBuilder category(String category) {
            config.category = category;
            return this;
        }
        
        public DataSourceConfigBuilder maxLevel(DataSourceLevel maxLevel) {
            config.maxLevel = maxLevel;
            return this;
        }
        
        public DataSourceConfigBuilder connectionConfig(Map<String, Object> connectionConfig) {
            config.setConnectionConfig(connectionConfig);
            return this;
        }
        
        public DataSourceConfigBuilder connection(String key, Object value) {
            config.connectionConfig.put(key, value);
            return this;
        }
        
        public DataSourceConfigBuilder poolConfig(PoolConfig poolConfig) {
            config.poolConfig = poolConfig;
            return this;
        }
        
        public DataSourceConfigBuilder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }
        
        public DataSourceConfigBuilder readonly(boolean readonly) {
            config.readonly = readonly;
            return this;
        }
        
        public DataSourceConfigBuilder capability(String capabilityId) {
            config.addCapability(capabilityId);
            return this;
        }
        
        public DataSourceConfigBuilder allowedSkill(String skillId) {
            config.addAllowedSkill(skillId);
            return this;
        }
        
        public DataSourceConfigBuilder healthCheck(HealthCheckConfig healthCheck) {
            config.healthCheck = healthCheck;
            return this;
        }
        
        public DataSourceConfigBuilder failover(FailoverConfig failover) {
            config.failover = failover;
            return this;
        }
        
        public DataSourceConfig build() {
            return config;
        }
    }
}
