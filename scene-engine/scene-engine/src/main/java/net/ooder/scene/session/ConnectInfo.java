package net.ooder.scene.session;

/**
 * 连接信息
 */
public class ConnectInfo {
    private String connectionId;
    private String sessionId;
    private String userId;
    private String protocol;
    private String endpoint;
    private String clientIp;
    private String userAgent;
    private long connectedAt;
    private long lastActiveAt;
    private String status;

    public ConnectInfo() {}

    public String getConnectionId() { return connectionId; }
    public void setConnectionId(String connectionId) { this.connectionId = connectionId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public long getConnectedAt() { return connectedAt; }
    public void setConnectedAt(long connectedAt) { this.connectedAt = connectedAt; }
    public long getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(long lastActiveAt) { this.lastActiveAt = lastActiveAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isConnected() {
        return "CONNECTED".equals(status);
    }
}
