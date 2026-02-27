package net.ooder.scene.event.security;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class LogoutEvent extends SceneEvent {
    
    private final String userId;
    private final String username;
    private final String sessionId;
    private final String reason;
    
    public LogoutEvent(Object source, String userId, String username, String sessionId) {
        super(source, SceneEventType.LOGOUT);
        this.userId = userId;
        this.username = username;
        this.sessionId = sessionId;
        this.reason = null;
    }
    
    public LogoutEvent(Object source, String userId, String username, String sessionId, String reason) {
        super(source, SceneEventType.LOGOUT);
        this.userId = userId;
        this.username = username;
        this.sessionId = sessionId;
        this.reason = reason;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "LogoutEvent{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", reason='" + reason + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
