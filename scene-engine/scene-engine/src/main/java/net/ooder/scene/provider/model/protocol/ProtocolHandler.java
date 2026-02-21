package net.ooder.scene.provider.model.protocol;

import java.util.Map;

public class ProtocolHandler {
    
    private String handlerType;
    private String description;
    private String endpoint;
    private String version;
    private boolean enabled;
    private Map<String, Object> config;
    
    public String getHandlerType() {
        return handlerType;
    }
    
    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
