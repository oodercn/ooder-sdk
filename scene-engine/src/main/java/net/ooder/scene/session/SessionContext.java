package net.ooder.scene.session;

import java.io.Serializable;
import java.util.Map;

public class SessionContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String userId;
    private String userName;
    private String domainId;
    private String token;
    private long createdAt;
    private long expiresAt;
    private long lastActiveAt;
    private String clientIp;
    private String userAgent;
    private Map<String, Object> attributes;
    private boolean admin;

    public SessionContext() {
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = this.createdAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }

    public Object getAttribute(String key) {
        return attributes != null ? attributes.get(key) : null;
    }

    public void setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new java.util.HashMap<String, Object>();
        }
        attributes.put(key, value);
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDomainId() { return domainId; }
    public void setDomainId(String domainId) { this.domainId = domainId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
    public long getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(long lastActiveAt) { this.lastActiveAt = lastActiveAt; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public void setAdmin(boolean admin) { this.admin = admin; }
}
