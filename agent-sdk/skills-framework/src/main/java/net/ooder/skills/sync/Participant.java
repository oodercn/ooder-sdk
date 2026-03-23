package net.ooder.skills.sync;

import java.util.UUID;

public class Participant {
    
    private String participantId;
    private String userId;
    private String displayName;
    private Type type;
    private Role role;
    private String status;
    private long joinTime;
    private long lastActiveTime;
    
    public enum Type {
        USER("user", "Human user"),
        AGENT("agent", "AI Agent"),
        SUPER_AGENT("super_agent", "Super Agent");
        
        private final String code;
        private final String description;
        
        Type(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
        
        public static Type fromCode(String code) {
            for (Type type : values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown participant type: " + code);
        }
    }
    
    public enum Role {
        OWNER("owner", "Scene group owner with full control"),
        MANAGER("manager", "Manager with administrative privileges"),
        COORDINATOR("coordinator", "Coordinator for collaboration"),
        EMPLOYEE("employee", "Regular participant"),
        OBSERVER("observer", "Read-only observer"),
        LLM_ASSISTANT("llm_assistant", "LLM-based assistant");
        
        private final String code;
        private final String description;
        
        Role(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
        
        public static Role fromCode(String code) {
            for (Role role : values()) {
                if (role.code.equalsIgnoreCase(code)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown participant role: " + code);
        }
    }
    
    public Participant() {
        this.participantId = UUID.randomUUID().toString();
        this.joinTime = System.currentTimeMillis();
        this.lastActiveTime = this.joinTime;
        this.status = "active";
    }
    
    public Participant(String participantId, String userId, String displayName, Type type) {
        this();
        this.participantId = participantId;
        this.userId = userId;
        this.displayName = displayName;
        this.type = type;
    }
    
    public String getParticipantId() { return participantId; }
    public void setParticipantId(String participantId) { this.participantId = participantId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getJoinTime() { return joinTime; }
    public void setJoinTime(long joinTime) { this.joinTime = joinTime; }
    
    public long getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    
    public void activate() {
        this.status = "active";
        this.lastActiveTime = System.currentTimeMillis();
    }
    
    public void suspend() {
        this.status = "suspended";
    }
    
    public boolean isActive() {
        return "active".equals(status);
    }
    
    public boolean isManager() {
        return role == Role.OWNER || role == Role.MANAGER;
    }

    private String sessionToken;
    private AgentCapabilities capabilities;
    private long sessionExpireTime;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public AgentCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(AgentCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public long getSessionExpireTime() {
        return sessionExpireTime;
    }

    public void setSessionExpireTime(long sessionExpireTime) {
        this.sessionExpireTime = sessionExpireTime;
    }

    public boolean isSessionExpired() {
        return sessionExpireTime > 0 && System.currentTimeMillis() > sessionExpireTime;
    }

    public boolean hasValidSession() {
        return sessionToken != null && !sessionToken.isEmpty() && !isSessionExpired();
    }

    public void refreshSession(long newExpireTime) {
        this.lastActiveTime = System.currentTimeMillis();
        this.sessionExpireTime = newExpireTime;
    }
}
