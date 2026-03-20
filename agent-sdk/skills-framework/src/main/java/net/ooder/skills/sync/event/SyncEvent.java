package net.ooder.skills.sync.event;

import java.util.UUID;

public class SyncEvent {
    
    private final String eventId;
    private final String sceneGroupId;
    private final Type type;
    private final Object data;
    private final long timestamp;
    
    public enum Type {
        COLLABORATOR_ADDED,
        COLLABORATOR_REMOVED,
        COLLABORATOR_ROLE_CHANGED,
        SKILL_ADDED,
        SKILL_REMOVED,
        SKILL_CONFIG_UPDATED,
        COLLABORATION_STARTED,
        COLLABORATION_ENDED,
        CAPABILITY_BOUND,
        CAPABILITY_UNBOUND,
        KNOWLEDGE_BASE_BOUND,
        KNOWLEDGE_BASE_UNBOUND,
        SCENE_GROUP_ACTIVATED,
        SCENE_GROUP_DEACTIVATED,
        FAILOVER_COMPLETED,
        FAILOVER_STARTED,
        MEMBER_STATUS_CHANGED,
        SYNC_SUCCESS,
        SYNC_FAILURE,
        STATE_SYNCED
    }
    
    public SyncEvent(String sceneGroupId, Type type, Object data) {
        this.eventId = UUID.randomUUID().toString();
        this.sceneGroupId = sceneGroupId;
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getEventId() { return eventId; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    
    public Type getType() { return type; }
    
    public Object getData() { return data; }
    
    public long getTimestamp() { return timestamp; }
    
    @SuppressWarnings("unchecked")
    public <T> T getDataAs(Class<T> clazz) {
        if (data != null && clazz.isInstance(data)) {
            return (T) data;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "SyncEvent{" +
            "eventId='" + eventId + '\'' +
            ", sceneGroupId='" + sceneGroupId + '\'' +
            ", type=" + type +
            ", timestamp=" + timestamp +
            '}';
    }
}
