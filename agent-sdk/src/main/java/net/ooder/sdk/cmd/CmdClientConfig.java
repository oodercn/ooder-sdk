package net.ooder.sdk.cmd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CmdClientConfig {
    
    private String clientId;
    private String endpoint;
    private String username;
    private String password;
    private int defaultTimeout = 30000;
    private int maxRetryAttempts = 3;
    private int retryInterval = 1000;
    private Map<String, Object> extensions = new ConcurrentHashMap<>();
    
    public CmdClientConfig() {}
    
    public CmdClientConfig(String clientId, String endpoint) {
        this.clientId = clientId;
        this.endpoint = endpoint;
    }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getDefaultTimeout() { return defaultTimeout; }
    public void setDefaultTimeout(int defaultTimeout) { this.defaultTimeout = defaultTimeout; }
    
    public int getMaxRetryAttempts() { return maxRetryAttempts; }
    public void setMaxRetryAttempts(int maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }
    
    public int getRetryInterval() { return retryInterval; }
    public void setRetryInterval(int retryInterval) { this.retryInterval = retryInterval; }
    
    public Map<String, Object> getExtensions() { return extensions; }
    public void setExtensions(Map<String, Object> extensions) { 
        this.extensions = extensions != null ? extensions : new ConcurrentHashMap<>(); 
    }
    
    public CmdClientConfig clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
    
    public CmdClientConfig endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }
}
