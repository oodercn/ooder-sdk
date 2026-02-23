package net.ooder.sdk.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MsgClientConfig {
    
    private String clientId;
    private String brokerUrl;
    private String username;
    private String password;
    private int reconnectInterval = 5000;
    private int maxReconnectAttempts = 10;
    private boolean cleanSession = true;
    private int keepAliveInterval = 60;
    private int connectionTimeout = 30000;
    private Map<String, Object> extensions = new ConcurrentHashMap<>();
    
    public MsgClientConfig() {}
    
    public MsgClientConfig(String clientId, String brokerUrl) {
        this.clientId = clientId;
        this.brokerUrl = brokerUrl;
    }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getBrokerUrl() { return brokerUrl; }
    public void setBrokerUrl(String brokerUrl) { this.brokerUrl = brokerUrl; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getReconnectInterval() { return reconnectInterval; }
    public void setReconnectInterval(int reconnectInterval) { this.reconnectInterval = reconnectInterval; }
    
    public int getMaxReconnectAttempts() { return maxReconnectAttempts; }
    public void setMaxReconnectAttempts(int maxReconnectAttempts) { this.maxReconnectAttempts = maxReconnectAttempts; }
    
    public boolean isCleanSession() { return cleanSession; }
    public void setCleanSession(boolean cleanSession) { this.cleanSession = cleanSession; }
    
    public int getKeepAliveInterval() { return keepAliveInterval; }
    public void setKeepAliveInterval(int keepAliveInterval) { this.keepAliveInterval = keepAliveInterval; }
    
    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
    
    public Map<String, Object> getExtensions() { return extensions; }
    public void setExtensions(Map<String, Object> extensions) { 
        this.extensions = extensions != null ? extensions : new ConcurrentHashMap<>(); 
    }
    
    public MsgClientConfig clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
    
    public MsgClientConfig brokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
        return this;
    }
}
