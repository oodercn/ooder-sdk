package net.ooder.scene.llm.command;

import java.io.Serializable;

/**
 * 安全信息
 * 
 * <p>集成现有安全机制，支持双用户模型。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class SecurityInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userToken;
    private String llmToken;
    private String sessionId;
    private String securityLevel;
    private String signature;
    
    private String userId;
    private String llmUserId;

    public SecurityInfo() {}
    
    public SecurityInfo(String userToken, String sessionId) {
        this.userToken = userToken;
        this.sessionId = sessionId;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getUserToken() { return userToken; }
    public void setUserToken(String userToken) { this.userToken = userToken; }
    
    public String getLlmToken() { return llmToken; }
    public void setLlmToken(String llmToken) { this.llmToken = llmToken; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getLlmUserId() { return llmUserId; }
    public void setLlmUserId(String llmUserId) { this.llmUserId = llmUserId; }
    
    public static class Builder {
        private SecurityInfo info = new SecurityInfo();
        
        public Builder userToken(String userToken) {
            info.setUserToken(userToken);
            return this;
        }
        
        public Builder llmToken(String llmToken) {
            info.setLlmToken(llmToken);
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            info.setSessionId(sessionId);
            return this;
        }
        
        public Builder securityLevel(String securityLevel) {
            info.setSecurityLevel(securityLevel);
            return this;
        }
        
        public Builder signature(String signature) {
            info.setSignature(signature);
            return this;
        }
        
        public Builder userId(String userId) {
            info.setUserId(userId);
            return this;
        }
        
        public Builder llmUserId(String llmUserId) {
            info.setLlmUserId(llmUserId);
            return this;
        }
        
        public SecurityInfo build() {
            return info;
        }
    }
}
