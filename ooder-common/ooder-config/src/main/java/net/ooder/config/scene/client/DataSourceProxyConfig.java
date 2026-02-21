package net.ooder.config.scene.client;

import net.ooder.config.scene.enums.DataSourceLevel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataSourceProxyConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String dataSourceId;
    private String dataSourceType;
    
    private String category;
    private DataSourceLevel level = DataSourceLevel.READONLY;
    
    private boolean cacheEnabled = true;
    private boolean offlineSupported = false;
    
    private Map<String, Object> connectionConfig = new HashMap<String, Object>();
    
    public DataSourceProxyConfig() {
    }
    
    public DataSourceProxyConfig(String dataSourceId, String dataSourceType) {
        this.dataSourceId = dataSourceId;
        this.dataSourceType = dataSourceType;
    }
    
    public static DataSourceProxyConfigBuilder builder() {
        return new DataSourceProxyConfigBuilder();
    }
    
    public String getDataSourceId() {
        return dataSourceId;
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
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
    
    public DataSourceLevel getLevel() {
        return level;
    }
    
    public void setLevel(DataSourceLevel level) {
        this.level = level;
    }
    
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
    
    public boolean isOfflineSupported() {
        return offlineSupported;
    }
    
    public void setOfflineSupported(boolean offlineSupported) {
        this.offlineSupported = offlineSupported;
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
    
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }
    
    public static class DataSourceProxyConfigBuilder {
        private DataSourceProxyConfig config = new DataSourceProxyConfig();
        
        public DataSourceProxyConfigBuilder dataSourceId(String dataSourceId) {
            config.dataSourceId = dataSourceId;
            return this;
        }
        
        public DataSourceProxyConfigBuilder dataSourceType(String dataSourceType) {
            config.dataSourceType = dataSourceType;
            return this;
        }
        
        public DataSourceProxyConfigBuilder category(String category) {
            config.category = category;
            return this;
        }
        
        public DataSourceProxyConfigBuilder level(DataSourceLevel level) {
            config.level = level;
            return this;
        }
        
        public DataSourceProxyConfigBuilder cacheEnabled(boolean cacheEnabled) {
            config.cacheEnabled = cacheEnabled;
            return this;
        }
        
        public DataSourceProxyConfigBuilder offlineSupported(boolean offlineSupported) {
            config.offlineSupported = offlineSupported;
            return this;
        }
        
        public DataSourceProxyConfigBuilder connectionConfig(Map<String, Object> connectionConfig) {
            config.setConnectionConfig(connectionConfig);
            return this;
        }
        
        public DataSourceProxyConfigBuilder connection(String key, Object value) {
            config.connectionConfig.put(key, value);
            return this;
        }
        
        public DataSourceProxyConfig build() {
            return config;
        }
    }
}
