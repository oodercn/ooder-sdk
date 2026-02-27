package net.ooder.scene.event.security;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class TokenEvent extends SceneEvent {
    
    private final String tokenId;
    private final String userId;
    private final String tokenType;
    private final boolean success;
    private final String errorMessage;
    
    private TokenEvent(Object source, SceneEventType eventType, String tokenId, String userId) {
        super(source, eventType);
        this.tokenId = tokenId;
        this.userId = userId;
        this.tokenType = null;
        this.success = true;
        this.errorMessage = null;
    }
    
    private TokenEvent(Object source, SceneEventType eventType, String tokenId, String userId, 
                       boolean success, String errorMessage) {
        super(source, eventType);
        this.tokenId = tokenId;
        this.userId = userId;
        this.tokenType = null;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public static TokenEvent generated(Object source, String tokenId, String userId) {
        return new TokenEvent(source, SceneEventType.TOKEN_GENERATED, tokenId, userId);
    }
    
    public static TokenEvent revoked(Object source, String tokenId, String userId) {
        return new TokenEvent(source, SceneEventType.TOKEN_REVOKED, tokenId, userId);
    }
    
    public static TokenEvent expired(Object source, String tokenId, String userId) {
        return new TokenEvent(source, SceneEventType.TOKEN_EXPIRED, tokenId, userId);
    }
    
    public static TokenEvent validationFailed(Object source, String tokenId, String errorMessage) {
        return new TokenEvent(source, SceneEventType.TOKEN_VALIDATION_FAILED, tokenId, null, false, errorMessage);
    }
    
    public String getTokenId() {
        return tokenId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    public String toString() {
        return "TokenEvent{" +
                "eventType=" + getEventType() +
                ", tokenId='" + tokenId + '\'' +
                ", userId='" + userId + '\'' +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
