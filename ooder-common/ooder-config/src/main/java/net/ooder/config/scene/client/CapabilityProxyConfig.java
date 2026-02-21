package net.ooder.config.scene.client;

import net.ooder.config.scene.enums.DataSourceLevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapabilityProxyConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String capabilityId;
    private String capabilityName;
    private String interfaceClass;
    
    private String endpoint;
    private int timeout = 30000;
    private int retryCount = 3;
    
    private boolean cacheEnabled = true;
    private long cacheExpireTime = 60000L;
    
    private boolean offlineSupported = false;
    private String fallbackStrategy = "error";
    
    private List<String> requiredDataSources = new ArrayList<String>();
    
    public CapabilityProxyConfig() {
    }
    
    public CapabilityProxyConfig(String capabilityId, String capabilityName) {
        this.capabilityId = capabilityId;
        this.capabilityName = capabilityName;
    }
    
    public static CapabilityProxyConfigBuilder builder() {
        return new CapabilityProxyConfigBuilder();
    }
    
    public String getCapabilityId() {
        return capabilityId;
    }
    
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }
    
    public String getCapabilityName() {
        return capabilityName;
    }
    
    public void setCapabilityName(String capabilityName) {
        this.capabilityName = capabilityName;
    }
    
    public String getInterfaceClass() {
        return interfaceClass;
    }
    
    public void setInterfaceClass(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
    
    public long getCacheExpireTime() {
        return cacheExpireTime;
    }
    
    public void setCacheExpireTime(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
    }
    
    public boolean isOfflineSupported() {
        return offlineSupported;
    }
    
    public void setOfflineSupported(boolean offlineSupported) {
        this.offlineSupported = offlineSupported;
    }
    
    public String getFallbackStrategy() {
        return fallbackStrategy;
    }
    
    public void setFallbackStrategy(String fallbackStrategy) {
        this.fallbackStrategy = fallbackStrategy;
    }
    
    public List<String> getRequiredDataSources() {
        return requiredDataSources;
    }
    
    public void setRequiredDataSources(List<String> requiredDataSources) {
        this.requiredDataSources = requiredDataSources != null ? 
            requiredDataSources : new ArrayList<String>();
    }
    
    public void addRequiredDataSource(String dataSourceId) {
        if (dataSourceId != null && !requiredDataSources.contains(dataSourceId)) {
            requiredDataSources.add(dataSourceId);
        }
    }
    
    public static class CapabilityProxyConfigBuilder {
        private CapabilityProxyConfig config = new CapabilityProxyConfig();
        
        public CapabilityProxyConfigBuilder capabilityId(String capabilityId) {
            config.capabilityId = capabilityId;
            return this;
        }
        
        public CapabilityProxyConfigBuilder capabilityName(String capabilityName) {
            config.capabilityName = capabilityName;
            return this;
        }
        
        public CapabilityProxyConfigBuilder interfaceClass(String interfaceClass) {
            config.interfaceClass = interfaceClass;
            return this;
        }
        
        public CapabilityProxyConfigBuilder endpoint(String endpoint) {
            config.endpoint = endpoint;
            return this;
        }
        
        public CapabilityProxyConfigBuilder timeout(int timeout) {
            config.timeout = timeout;
            return this;
        }
        
        public CapabilityProxyConfigBuilder retryCount(int retryCount) {
            config.retryCount = retryCount;
            return this;
        }
        
        public CapabilityProxyConfigBuilder cacheEnabled(boolean cacheEnabled) {
            config.cacheEnabled = cacheEnabled;
            return this;
        }
        
        public CapabilityProxyConfigBuilder cacheExpireTime(long cacheExpireTime) {
            config.cacheExpireTime = cacheExpireTime;
            return this;
        }
        
        public CapabilityProxyConfigBuilder offlineSupported(boolean offlineSupported) {
            config.offlineSupported = offlineSupported;
            return this;
        }
        
        public CapabilityProxyConfigBuilder fallbackStrategy(String fallbackStrategy) {
            config.fallbackStrategy = fallbackStrategy;
            return this;
        }
        
        public CapabilityProxyConfigBuilder requiredDataSource(String dataSourceId) {
            config.addRequiredDataSource(dataSourceId);
            return this;
        }
        
        public CapabilityProxyConfig build() {
            return config;
        }
    }
}
