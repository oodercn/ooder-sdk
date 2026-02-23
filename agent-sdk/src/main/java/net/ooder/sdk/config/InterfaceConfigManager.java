package net.ooder.sdk.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InterfaceConfigManager {
    
    void registerConfig(String interfaceId, InterfaceConfig config);
    
    void unregisterConfig(String interfaceId);
    
    Optional<InterfaceConfig> getConfig(String interfaceId);
    
    Map<String, InterfaceConfig> getAllConfigs();
    
    void updateConfig(String interfaceId, Map<String, Object> updates);
    
    void setProperty(String interfaceId, String key, Object value);
    
    Optional<Object> getProperty(String interfaceId, String key);
    
    <T> Optional<T> getProperty(String interfaceId, String key, Class<T> type);
    
    <T> T getProperty(String interfaceId, String key, T defaultValue);
    
    void removeProperty(String interfaceId, String key);
    
    boolean hasConfig(String interfaceId);
    
    List<String> getConfiguredInterfaces();
    
    void loadConfigs(String configPath);
    
    void saveConfigs(String configPath);
    
    void addConfigListener(InterfaceConfigListener listener);
    
    void removeConfigListener(InterfaceConfigListener listener);
    
    void reset(String interfaceId);
    
    void resetAll();
    
    class InterfaceConfig {
        private String interfaceId;
        private String preferredImplementation;
        private List<String> fallbackChain;
        private Map<String, Object> properties;
        private boolean enabled;
        private int timeout;
        private int retryCount;
        private long lastUpdateTime;
        
        public InterfaceConfig() {
            this.enabled = true;
            this.timeout = 30000;
            this.retryCount = 3;
            this.properties = new java.util.concurrent.ConcurrentHashMap<>();
            this.fallbackChain = new java.util.ArrayList<>();
        }
        
        public InterfaceConfig(String interfaceId) {
            this();
            this.interfaceId = interfaceId;
        }
        
        public String getInterfaceId() { return interfaceId; }
        public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
        
        public String getPreferredImplementation() { return preferredImplementation; }
        public void setPreferredImplementation(String preferredImplementation) { this.preferredImplementation = preferredImplementation; }
        
        public List<String> getFallbackChain() { return fallbackChain; }
        public void setFallbackChain(List<String> fallbackChain) { this.fallbackChain = fallbackChain; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
        
        public long getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
        
        public Object getProperty(String key) { return properties.get(key); }
        
        public void setProperty(String key, Object value) { 
            properties.put(key, value); 
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public void addFallback(String skillId) {
            if (!fallbackChain.contains(skillId)) {
                fallbackChain.add(skillId);
            }
        }
    }
}
