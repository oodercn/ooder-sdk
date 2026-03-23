package net.ooder.scene.failover;

import java.time.Instant;

public class FailoverEvent {

    private String eventId;
    private FailoverEventType type;
    private String agentId;
    private String sceneGroupId;
    private String taskId;
    private String targetAgentId;
    private String message;
    private Instant timestamp;
    private boolean handled;

    public FailoverEvent() {
        this.eventId = java.util.UUID.randomUUID().toString().replace("-", "");
        this.timestamp = Instant.now();
        this.handled = false;
    }

    public FailoverEvent(FailoverEventType type, String agentId) {
        this();
        this.type = type;
        this.agentId = agentId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public FailoverEventType getType() {
        return type;
    }

    public void setType(FailoverEventType type) {
        this.type = type;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTargetAgentId() {
        return targetAgentId;
    }

    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    @Override
    public String toString() {
        return "FailoverEvent{" +
                "eventId='" + eventId + '\'' +
                ", type=" + type +
                ", agentId='" + agentId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", handled=" + handled +
                '}';
    }
}
