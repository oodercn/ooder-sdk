package net.ooder.scene.websocket.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * WebSocket Token
 *
 * <p>用于WebSocket连接认证的Token，绑定用户和场景组</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class WebSocketToken {
    
    private String tokenId;
    private String token;
    private String userId;
    private String sceneGroupId;
    
    private long createdAt;
    private long expireAt;
    
    private Map<String, Object> claims;
    
    public WebSocketToken() {
        this.tokenId = UUID.randomUUID().toString().replace("-", "");
        this.createdAt = System.currentTimeMillis();
        this.claims = new HashMap<>();
    }
    
    public WebSocketToken(String userId, String sceneGroupId, long expireSeconds) {
        this();
        this.userId = userId;
        this.sceneGroupId = sceneGroupId;
        this.expireAt = createdAt + (expireSeconds * 1000);
    }
    
    public String getTokenId() {
        return tokenId;
    }
    
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
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
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getExpireAt() {
        return expireAt;
    }
    
    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
    
    public Map<String, Object> getClaims() {
        return claims;
    }
    
    public void setClaims(Map<String, Object> claims) {
        this.claims = claims != null ? claims : new HashMap<>();
    }
    
    public void addClaim(String key, Object value) {
        this.claims.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String key) {
        return (T) this.claims.get(key);
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expireAt;
    }
    
    public long getRemainingTime() {
        long remaining = expireAt - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final WebSocketToken wsToken = new WebSocketToken();
        
        public Builder tokenId(String tokenId) {
            wsToken.setTokenId(tokenId);
            return this;
        }
        
        public Builder token(String token) {
            wsToken.setToken(token);
            return this;
        }
        
        public Builder userId(String userId) {
            wsToken.setUserId(userId);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            wsToken.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder createdAt(long createdAt) {
            wsToken.setCreatedAt(createdAt);
            return this;
        }
        
        public Builder expireAt(long expireAt) {
            wsToken.setExpireAt(expireAt);
            return this;
        }
        
        public Builder expireInSeconds(long seconds) {
            wsToken.setExpireAt(System.currentTimeMillis() + (seconds * 1000));
            return this;
        }
        
        public Builder claims(Map<String, Object> claims) {
            wsToken.setClaims(claims);
            return this;
        }
        
        public Builder claim(String key, Object value) {
            wsToken.addClaim(key, value);
            return this;
        }
        
        public WebSocketToken build() {
            return wsToken;
        }
    }
    
    @Override
    public String toString() {
        return "WebSocketToken{" +
                "tokenId='" + tokenId + '\'' +
                ", userId='" + userId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", expired=" + isExpired() +
                '}';
    }
}
