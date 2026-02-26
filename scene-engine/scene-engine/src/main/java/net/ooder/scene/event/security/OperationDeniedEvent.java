package net.ooder.scene.event.security;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class OperationDeniedEvent extends SceneEvent {
    
    private final String userId;
    private final String operation;
    private final String resource;
    private final String reason;
    
    public OperationDeniedEvent(Object source, String userId, String operation, String resource, String reason) {
        super(source, SceneEventType.OPERATION_DENIED);
        this.userId = userId;
        this.operation = operation;
        this.resource = resource;
        this.reason = reason;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getResource() {
        return resource;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "OperationDeniedEvent{" +
                "userId='" + userId + '\'' +
                ", operation='" + operation + '\'' +
                ", resource='" + resource + '\'' +
                ", reason='" + reason + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
