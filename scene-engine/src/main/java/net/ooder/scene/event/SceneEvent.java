package net.ooder.scene.event;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class SceneEvent extends ApplicationEvent {
    
    private final SceneEventType eventType;
    private final String traceId;
    private final Map<String, Object> metadata;
    
    public SceneEvent(Object source, SceneEventType eventType) {
        super(source);
        this.eventType = eventType;
        this.traceId = generateTraceId();
        this.metadata = new HashMap<>();
    }
    
    public SceneEventType getEventType() {
        return eventType;
    }
    
    public String getTraceId() {
        return traceId;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public SceneEvent addMetadata(String key, Object value) {
        metadata.put(key, value);
        return this;
    }
    
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    @Override
    public String toString() {
        return "SceneEvent{" +
                "eventType=" + eventType +
                ", timestamp=" + getTimestamp() +
                ", traceId='" + traceId + '\'' +
                ", source=" + source.getClass().getSimpleName() +
                '}';
    }
}
