package net.ooder.scene.websocket.auth;

/**
 * 连接信息
 *
 * <p>表示一个活跃的WebSocket连接</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ConnectionInfo {
    
    private String sessionId;
    private String userId;
    private String sceneGroupId;
    private String clientIp;
    private String userAgent;
    private long connectedAt;
    private long lastActivityAt;
    private ConnectionState state;
    
    public ConnectionInfo() {
        this.connectedAt = System.currentTimeMillis();
        this.lastActivityAt = this.connectedAt;
        this.state = ConnectionState.CONNECTED;
    }
    
    public ConnectionInfo(String sessionId, String userId, String sceneGroupId) {
        this();
        this.sessionId = sessionId;
        this.userId = userId;
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public long getConnectedAt() {
        return connectedAt;
    }
    
    public void setConnectedAt(long connectedAt) {
        this.connectedAt = connectedAt;
    }
    
    public long getLastActivityAt() {
        return lastActivityAt;
    }
    
    public void setLastActivityAt(long lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
    
    public ConnectionState getState() {
        return state;
    }
    
    public void setState(ConnectionState state) {
        this.state = state;
    }
    
    public void touch() {
        this.lastActivityAt = System.currentTimeMillis();
    }
    
    public long getConnectionDuration() {
        return System.currentTimeMillis() - connectedAt;
    }
    
    public long getIdleTime() {
        return System.currentTimeMillis() - lastActivityAt;
    }
    
    public boolean isIdle(long idleTimeoutMs) {
        return getIdleTime() > idleTimeoutMs;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final ConnectionInfo info = new ConnectionInfo();
        
        public Builder sessionId(String sessionId) {
            info.setSessionId(sessionId);
            return this;
        }
        
        public Builder userId(String userId) {
            info.setUserId(userId);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            info.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder clientIp(String clientIp) {
            info.setClientIp(clientIp);
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            info.setUserAgent(userAgent);
            return this;
        }
        
        public Builder connectedAt(long connectedAt) {
            info.setConnectedAt(connectedAt);
            return this;
        }
        
        public Builder state(ConnectionState state) {
            info.setState(state);
            return this;
        }
        
        public ConnectionInfo build() {
            return info;
        }
    }
    
    @Override
    public String toString() {
        return "ConnectionInfo{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", state=" + state +
                ", connectionDuration=" + getConnectionDuration() + "ms" +
                '}';
    }
}
