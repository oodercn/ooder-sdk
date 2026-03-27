package net.ooder.scene.session.unified;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一会话接口
 *
 * <p>表示系统中的统一会话，支持多种会话类型。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class UnifiedSession {
    
    private String sessionId;
    private SessionType type;
    private String ownerId;
    private String sceneGroupId;
    private OnlineStatus status;
    private long createdAt;
    private long lastActiveAt;
    private long expireAt;
    private Map<String, Object> metadata;
    
    private boolean isVirtual;
    private boolean requireHeartbeat;
    
    public UnifiedSession() {
        this.metadata = new ConcurrentHashMap<>();
        this.status = OnlineStatus.ONLINE;
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = this.createdAt;
    }
    
    public UnifiedSession(String sessionId, SessionType type, String ownerId) {
        this();
        this.sessionId = sessionId;
        this.type = type;
        this.ownerId = ownerId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public SessionType getType() {
        return type;
    }
    
    public void setType(SessionType type) {
        this.type = type;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public OnlineStatus getStatus() {
        return status;
    }
    
    public void setStatus(OnlineStatus status) {
        this.status = status;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getLastActiveAt() {
        return lastActiveAt;
    }
    
    public void setLastActiveAt(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
    
    public long getExpireAt() {
        return expireAt;
    }
    
    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key) {
        return (T) this.metadata.get(key);
    }
    
    public boolean isVirtual() {
        return isVirtual;
    }
    
    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }
    
    public boolean isRequireHeartbeat() {
        return requireHeartbeat;
    }
    
    public void setRequireHeartbeat(boolean requireHeartbeat) {
        this.requireHeartbeat = requireHeartbeat;
    }
    
    public boolean isExpired() {
        return expireAt > 0 && System.currentTimeMillis() > expireAt;
    }
    
    public boolean isValid() {
        return !isExpired() && status != OnlineStatus.OFFLINE;
    }
    
    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }
    
    public void refresh(long ttlMs) {
        this.lastActiveAt = System.currentTimeMillis();
        this.expireAt = this.lastActiveAt + ttlMs;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final UnifiedSession session = new UnifiedSession();
        
        public Builder sessionId(String sessionId) {
            session.setSessionId(sessionId);
            return this;
        }
        
        public Builder type(SessionType type) {
            session.setType(type);
            return this;
        }
        
        public Builder ownerId(String ownerId) {
            session.setOwnerId(ownerId);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            session.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder status(OnlineStatus status) {
            session.setStatus(status);
            return this;
        }
        
        public Builder expireAt(long expireAt) {
            session.setExpireAt(expireAt);
            return this;
        }
        
        public Builder ttl(long ttlMs) {
            session.setExpireAt(System.currentTimeMillis() + ttlMs);
            return this;
        }
        
        public Builder virtual(boolean isVirtual) {
            session.setVirtual(isVirtual);
            return this;
        }
        
        public Builder requireHeartbeat(boolean requireHeartbeat) {
            session.setRequireHeartbeat(requireHeartbeat);
            return this;
        }
        
        public Builder metadata(String key, Object value) {
            session.setMetadata(key, value);
            return this;
        }
        
        public UnifiedSession build() {
            return session;
        }
    }
    
    @Override
    public String toString() {
        return "UnifiedSession{" +
                "sessionId='" + sessionId + '\'' +
                ", type=" + type +
                ", ownerId='" + ownerId + '\'' +
                ", status=" + status +
                ", isVirtual=" + isVirtual +
                '}';
    }
}
