package net.ooder.config.scene.server;

import net.ooder.config.scene.enums.DataSourceLevel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapabilityConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String capabilityId;
    private String capabilityName;
    private String capabilityType;
    
    private String skillId;
    private String skillVersion;
    
    private List<String> requiredDataSources = new ArrayList<String>();
    private DataSourceLevel requiredLevel = DataSourceLevel.READONLY;
    
    private int timeout = 30000;
    private int maxConcurrent = 100;
    private int retryCount = 3;
    
    private boolean enabled = true;
    private boolean requiresAuth = true;
    
    private List<String> allowedRoles = new ArrayList<String>();
    private List<String> allowedClients = new ArrayList<String>();
    
    private RateLimitConfig rateLimit = new RateLimitConfig();
    private CircuitBreakerConfig circuitBreaker = new CircuitBreakerConfig();
    
    public CapabilityConfig() {
    }
    
    public CapabilityConfig(String capabilityId, String capabilityName) {
        this.capabilityId = capabilityId;
        this.capabilityName = capabilityName;
    }
    
    public static CapabilityConfigBuilder builder() {
        return new CapabilityConfigBuilder();
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
    
    public String getCapabilityType() {
        return capabilityType;
    }
    
    public void setCapabilityType(String capabilityType) {
        this.capabilityType = capabilityType;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getSkillVersion() {
        return skillVersion;
    }
    
    public void setSkillVersion(String skillVersion) {
        this.skillVersion = skillVersion;
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
    
    public DataSourceLevel getRequiredLevel() {
        return requiredLevel;
    }
    
    public void setRequiredLevel(DataSourceLevel requiredLevel) {
        this.requiredLevel = requiredLevel;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public int getMaxConcurrent() {
        return maxConcurrent;
    }
    
    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isRequiresAuth() {
        return requiresAuth;
    }
    
    public void setRequiresAuth(boolean requiresAuth) {
        this.requiresAuth = requiresAuth;
    }
    
    public List<String> getAllowedRoles() {
        return allowedRoles;
    }
    
    public void setAllowedRoles(List<String> allowedRoles) {
        this.allowedRoles = allowedRoles != null ? allowedRoles : new ArrayList<String>();
    }
    
    public void addAllowedRole(String role) {
        if (role != null && !allowedRoles.contains(role)) {
            allowedRoles.add(role);
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
    
    public RateLimitConfig getRateLimit() {
        return rateLimit;
    }
    
    public void setRateLimit(RateLimitConfig rateLimit) {
        this.rateLimit = rateLimit;
    }
    
    public CircuitBreakerConfig getCircuitBreaker() {
        return circuitBreaker;
    }
    
    public void setCircuitBreaker(CircuitBreakerConfig circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }
    
    public boolean isQueryType() {
        return "query".equals(capabilityType);
    }
    
    public boolean isAdminType() {
        return "admin".equals(capabilityType);
    }
    
    public static class CapabilityConfigBuilder {
        private CapabilityConfig config = new CapabilityConfig();
        
        public CapabilityConfigBuilder capabilityId(String capabilityId) {
            config.capabilityId = capabilityId;
            return this;
        }
        
        public CapabilityConfigBuilder capabilityName(String capabilityName) {
            config.capabilityName = capabilityName;
            return this;
        }
        
        public CapabilityConfigBuilder capabilityType(String capabilityType) {
            config.capabilityType = capabilityType;
            return this;
        }
        
        public CapabilityConfigBuilder skillId(String skillId) {
            config.skillId = skillId;
            return this;
        }
        
        public CapabilityConfigBuilder skillVersion(String skillVersion) {
            config.skillVersion = skillVersion;
            return this;
        }
        
        public CapabilityConfigBuilder requiredDataSource(String dataSourceId) {
            config.addRequiredDataSource(dataSourceId);
            return this;
        }
        
        public CapabilityConfigBuilder requiredLevel(DataSourceLevel level) {
            config.requiredLevel = level;
            return this;
        }
        
        public CapabilityConfigBuilder timeout(int timeout) {
            config.timeout = timeout;
            return this;
        }
        
        public CapabilityConfigBuilder maxConcurrent(int maxConcurrent) {
            config.maxConcurrent = maxConcurrent;
            return this;
        }
        
        public CapabilityConfigBuilder retryCount(int retryCount) {
            config.retryCount = retryCount;
            return this;
        }
        
        public CapabilityConfigBuilder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }
        
        public CapabilityConfigBuilder requiresAuth(boolean requiresAuth) {
            config.requiresAuth = requiresAuth;
            return this;
        }
        
        public CapabilityConfigBuilder allowedRole(String role) {
            config.addAllowedRole(role);
            return this;
        }
        
        public CapabilityConfigBuilder allowedClient(String clientId) {
            config.addAllowedClient(clientId);
            return this;
        }
        
        public CapabilityConfigBuilder rateLimit(RateLimitConfig rateLimit) {
            config.rateLimit = rateLimit;
            return this;
        }
        
        public CapabilityConfigBuilder circuitBreaker(CircuitBreakerConfig circuitBreaker) {
            config.circuitBreaker = circuitBreaker;
            return this;
        }
        
        public CapabilityConfig build() {
            return config;
        }
    }
}
