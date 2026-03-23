package net.ooder.sdk.api.agent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import net.ooder.skills.sync.AgentCapabilities;

public class AgentSession {

    private String sessionId;
    private String agentId;
    private String agentName;
    private String sessionToken;
    private AgentCapabilities capabilities;
    private SessionState state;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant lastActiveAt;
    private Map<String, Object> context;

    public enum SessionState {
        CREATED,
        ACTIVE,
        EXPIRED,
        INVALIDATED,
        SUSPENDED
    }

    public AgentSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.state = SessionState.CREATED;
        this.createdAt = Instant.now();
        this.lastActiveAt = this.createdAt;
    }

    public AgentSession(String agentId, String sessionToken) {
        this();
        this.agentId = agentId;
        this.sessionToken = sessionToken;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public AgentCapabilities getCapabilities() { return capabilities; }
    public void setCapabilities(AgentCapabilities capabilities) { this.capabilities = capabilities; }

    public SessionState getState() { return state; }
    public void setState(SessionState state) { this.state = state; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(Instant lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }

    public boolean isValid() {
        return state == SessionState.ACTIVE && 
               (expiresAt == null || Instant.now().isBefore(expiresAt));
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public void activate() {
        this.state = SessionState.ACTIVE;
        this.lastActiveAt = Instant.now();
    }

    public void invalidate() {
        this.state = SessionState.INVALIDATED;
    }

    public void suspend() {
        this.state = SessionState.SUSPENDED;
    }

    public void refresh() {
        this.lastActiveAt = Instant.now();
    }
}
