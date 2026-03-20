package net.ooder.scene.group;

import java.time.Instant;

/**
 * 场景组事件
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneGroupEvent {
    
    private final String eventId;
    private final String sceneGroupId;
    private final Type type;
    private final String relatedId;
    private final String description;
    private final long timestamp;
    private String userId;
    private String agentId;
    
    public enum Type {
        CREATED,
        DESTROYED,
        ACTIVATED,
        SUSPENDED,
        ARCHIVED,
        RESTORED,
        PARTICIPANT_JOINED,
        PARTICIPANT_LEFT,
        PARTICIPANT_ROLE_CHANGED,
        CAPABILITY_BOUND,
        CAPABILITY_UNBOUND,
        KNOWLEDGE_BOUND,
        KNOWLEDGE_UNBOUND,
        STATUS_CHANGED,
        CONFIG_CHANGED,
        SNAPSHOT_CREATED,
        SNAPSHOT_RESTORED
    }
    
    public SceneGroupEvent(String sceneGroupId, Type type, String relatedId, String description) {
        this.eventId = generateEventId();
        this.sceneGroupId = sceneGroupId;
        this.type = type;
        this.relatedId = relatedId;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }
    
    private String generateEventId() {
        return "evt-" + System.currentTimeMillis() + "-" + Integer.toHexString((int)(Math.random() * 0xFFFF));
    }
    
    public String getEventId() { return eventId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public Type getType() { return type; }
    public String getRelatedId() { return relatedId; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public Instant getInstant() {
        return Instant.ofEpochMilli(timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("SceneGroupEvent{eventId=%s, type=%s, sceneGroupId=%s, description=%s}",
            eventId, type, sceneGroupId, description);
    }
}
