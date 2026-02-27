package net.ooder.scene.event.security;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class LoginEvent extends SceneEvent {
    
    private final String username;
    private final String userId;
    private final String ipAddress;
    private final boolean success;
    private final String failureReason;
    
    private LoginEvent(Object source, String username, String userId, String ipAddress, 
                       boolean success, String failureReason) {
        super(source, success ? SceneEventType.LOGIN_SUCCESS : SceneEventType.LOGIN_FAILED);
        this.username = username;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.success = success;
        this.failureReason = failureReason;
    }
    
    public static LoginEvent success(Object source, String username, String userId) {
        return new LoginEvent(source, username, userId, null, true, null);
    }
    
    public static LoginEvent success(Object source, String username, String userId, String ipAddress) {
        return new LoginEvent(source, username, userId, ipAddress, true, null);
    }
    
    public static LoginEvent failed(Object source, String username, String reason) {
        return new LoginEvent(source, username, null, null, false, reason);
    }
    
    public static LoginEvent failed(Object source, String username, String ipAddress, String reason) {
        return new LoginEvent(source, username, null, ipAddress, false, reason);
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    @Override
    public String toString() {
        return "LoginEvent{" +
                "eventType=" + getEventType() +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", success=" + success +
                ", failureReason='" + failureReason + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
