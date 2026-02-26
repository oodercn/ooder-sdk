package net.ooder.scene.event.user;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class UserEvent extends SceneEvent {
    
    private final String userId;
    private final String username;
    private final String operatorId;
    private final String reason;
    private final boolean enabled;
    
    private UserEvent(Object source, SceneEventType eventType, String userId, String username) {
        super(source, eventType);
        this.userId = userId;
        this.username = username;
        this.operatorId = null;
        this.reason = null;
        this.enabled = true;
    }
    
    private UserEvent(Object source, SceneEventType eventType, String userId, String username,
                      String operatorId, String reason, boolean enabled) {
        super(source, eventType);
        this.userId = userId;
        this.username = username;
        this.operatorId = operatorId;
        this.reason = reason;
        this.enabled = enabled;
    }
    
    public static UserEvent created(Object source, String userId, String username, String operatorId) {
        return new UserEvent(source, SceneEventType.USER_CREATED, userId, username, operatorId, null, true);
    }
    
    public static UserEvent updated(Object source, String userId, String username, String operatorId) {
        return new UserEvent(source, SceneEventType.USER_UPDATED, userId, username, operatorId, null, true);
    }
    
    public static UserEvent deleted(Object source, String userId, String username, String operatorId) {
        return new UserEvent(source, SceneEventType.USER_DELETED, userId, username, operatorId, null, true);
    }
    
    public static UserEvent enabled(Object source, String userId, String username, String operatorId) {
        return new UserEvent(source, SceneEventType.USER_ENABLED, userId, username, operatorId, null, true);
    }
    
    public static UserEvent disabled(Object source, String userId, String username, String operatorId, String reason) {
        return new UserEvent(source, SceneEventType.USER_DISABLED, userId, username, operatorId, reason, false);
    }
    
    public static UserEvent permissionsChanged(Object source, String userId, String operatorId) {
        return new UserEvent(source, SceneEventType.USER_PERMISSIONS_CHANGED, userId, null, operatorId, null, true);
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getOperatorId() {
        return operatorId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public String toString() {
        return "UserEvent{" +
                "eventType=" + getEventType() +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", enabled=" + enabled +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
