package net.ooder.scene.event.session;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class SessionEvent extends SceneEvent {
    
    private final String sessionId;
    private final String userId;
    private final String reason;
    
    private SessionEvent(Object source, SceneEventType eventType, String sessionId, String userId) {
        super(source, eventType);
        this.sessionId = sessionId;
        this.userId = userId;
        this.reason = null;
    }
    
    private SessionEvent(Object source, SceneEventType eventType, String sessionId, String userId, String reason) {
        super(source, eventType);
        this.sessionId = sessionId;
        this.userId = userId;
        this.reason = reason;
    }
    
    public static SessionEvent created(Object source, String sessionId, String userId) {
        return new SessionEvent(source, SceneEventType.SESSION_CREATED, sessionId, userId);
    }
    
    public static SessionEvent destroyed(Object source, String sessionId, String userId) {
        return new SessionEvent(source, SceneEventType.SESSION_DESTROYED, sessionId, userId);
    }
    
    public static SessionEvent destroyed(Object source, String sessionId, String userId, String reason) {
        return new SessionEvent(source, SceneEventType.SESSION_DESTROYED, sessionId, userId, reason);
    }
    
    public static SessionEvent expired(Object source, String sessionId, String userId) {
        return new SessionEvent(source, SceneEventType.SESSION_EXPIRED, sessionId, userId);
    }
    
    public static SessionEvent refreshed(Object source, String sessionId, String userId) {
        return new SessionEvent(source, SceneEventType.SESSION_REFRESHED, sessionId, userId);
    }
    
    public static SessionEvent touched(Object source, String sessionId, String userId) {
        return new SessionEvent(source, SceneEventType.SESSION_TOUCHED, sessionId, userId);
    }
    
    public static SessionEvent validationFailed(Object source, String sessionId, String reason) {
        return new SessionEvent(source, SceneEventType.SESSION_VALIDATION_FAILED, sessionId, null, reason);
    }
    
    public static SessionEvent userSessionsCleared(Object source, String userId) {
        return new SessionEvent(source, SceneEventType.USER_SESSIONS_CLEARED, null, userId);
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "SessionEvent{" +
                "eventType=" + getEventType() +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", reason='" + reason + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
