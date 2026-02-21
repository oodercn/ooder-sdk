package net.ooder.config.scene;

import java.io.Serializable;

public class CapabilityConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String capabilityName;
    private String interfaceId;
    private String endpoint;
    private String systemCode;
    
    private Integer timeout = 30000;
    private Integer retryCount = 3;
    private boolean enabled = true;
    
    public CapabilityConfig() {
    }
    
    public CapabilityConfig(String capabilityName, String interfaceId, String endpoint) {
        this.capabilityName = capabilityName;
        this.interfaceId = interfaceId;
        this.endpoint = endpoint;
    }
    
    public static CapabilityConfigBuilder builder() {
        return new CapabilityConfigBuilder();
    }
    
    public String getCapabilityName() {
        return capabilityName;
    }
    
    public void setCapabilityName(String capabilityName) {
        this.capabilityName = capabilityName;
    }
    
    public String getInterfaceId() {
        return interfaceId;
    }
    
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getSystemCode() {
        return systemCode;
    }
    
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }
    
    public Integer getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "CapabilityConfig{" +
                "capabilityName='" + capabilityName + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", timeout=" + timeout +
                ", enabled=" + enabled +
                '}';
    }
    
    public static class CapabilityConfigBuilder {
        private CapabilityConfig config = new CapabilityConfig();
        
        public CapabilityConfigBuilder capabilityName(String capabilityName) {
            config.capabilityName = capabilityName;
            return this;
        }
        
        public CapabilityConfigBuilder interfaceId(String interfaceId) {
            config.interfaceId = interfaceId;
            return this;
        }
        
        public CapabilityConfigBuilder endpoint(String endpoint) {
            config.endpoint = endpoint;
            return this;
        }
        
        public CapabilityConfigBuilder systemCode(String systemCode) {
            config.systemCode = systemCode;
            return this;
        }
        
        public CapabilityConfigBuilder timeout(Integer timeout) {
            config.timeout = timeout;
            return this;
        }
        
        public CapabilityConfigBuilder retryCount(Integer retryCount) {
            config.retryCount = retryCount;
            return this;
        }
        
        public CapabilityConfigBuilder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }
        
        public CapabilityConfig build() {
            return config;
        }
    }
}
