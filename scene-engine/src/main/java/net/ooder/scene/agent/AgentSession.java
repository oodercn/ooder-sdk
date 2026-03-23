package net.ooder.scene.agent;

import java.util.HashMap;
import java.util.Map;

public class AgentSession {

    private String agentId;
    private String sessionToken;
    private AgentStatus status;
    private long loginTime;
    private long lastHeartbeat;
    private long expireTime;
    private Map<String, Object> attributes;

    public AgentSession() {
        this.attributes = new HashMap<>();
        this.status = AgentStatus.ONLINE;
    }

    public AgentSession(String agentId, String sessionToken) {
        this();
        this.agentId = agentId;
        this.sessionToken = sessionToken;
        this.loginTime = System.currentTimeMillis();
        this.lastHeartbeat = this.loginTime;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) this.attributes.get(key);
    }

    public boolean isValid() {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }
        if (expireTime > 0 && System.currentTimeMillis() > expireTime) {
            return false;
        }
        return status != AgentStatus.OFFLINE;
    }

    public boolean isExpired() {
        return expireTime > 0 && System.currentTimeMillis() > expireTime;
    }

    public void touch() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "AgentSession{" +
                "agentId='" + agentId + '\'' +
                ", status=" + status +
                ", loginTime=" + loginTime +
                ", lastHeartbeat=" + lastHeartbeat +
                ", expireTime=" + expireTime +
                ", valid=" + isValid() +
                '}';
    }
}
