package net.ooder.sdk.core.driver;

import java.util.Map;

/**
 * 驱动上下文
 * 从 scene-engine 迁移到 agent-sdk
 */
public class DriverContext {
    
    private String driverId;
    private String version;
    private Map<String, Object> properties;
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }
    
    public void setProperty(String key, Object value) {
        if (properties != null) {
            properties.put(key, value);
        }
    }
}
