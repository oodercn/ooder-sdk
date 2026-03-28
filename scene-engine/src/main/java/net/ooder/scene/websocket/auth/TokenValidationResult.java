package net.ooder.scene.websocket.auth;

/**
 * Token验证结果
 *
 * <p>封装WebSocket Token验证的结果</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class TokenValidationResult {
    
    private boolean valid;
    private String userId;
    private String sceneGroupId;
    private String errorMessage;
    private WebSocketToken token;
    
    public TokenValidationResult() {
    }
    
    public TokenValidationResult(boolean valid) {
        this.valid = valid;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public WebSocketToken getToken() {
        return token;
    }
    
    public void setToken(WebSocketToken token) {
        this.token = token;
    }
    
    public static TokenValidationResult success(WebSocketToken token) {
        TokenValidationResult result = new TokenValidationResult(true);
        result.setToken(token);
        result.setUserId(token.getUserId());
        result.setSceneGroupId(token.getSceneGroupId());
        return result;
    }
    
    public static TokenValidationResult failure(String errorMessage) {
        TokenValidationResult result = new TokenValidationResult(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public static TokenValidationResult expired() {
        return failure("Token has expired");
    }
    
    public static TokenValidationResult invalid() {
        return failure("Invalid token");
    }
    
    public static TokenValidationResult revoked() {
        return failure("Token has been revoked");
    }
    
    public static TokenValidationResult notFound() {
        return failure("Token not found");
    }
    
    @Override
    public String toString() {
        return "TokenValidationResult{" +
                "valid=" + valid +
                ", userId='" + userId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
