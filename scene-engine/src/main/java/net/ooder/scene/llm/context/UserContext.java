package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户上下文
 * 
 * <p>封装用户相关信息，支持双用户模型（用户 + LLM-USER）。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String userName;
    private String domainId;
    private String token;
    private String sessionId;
    private List<String> roles;
    private List<String> permissions;
    
    private boolean isLlmUser;
    private String llmUserId;
    private String llmToken;
    
    private Map<String, Object> attributes;

    public UserContext() {
        this.roles = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.attributes = new HashMap<>();
    }
    
    public static UserContext of(String userId, String userName) {
        UserContext ctx = new UserContext();
        ctx.setUserId(userId);
        ctx.setUserName(userName);
        return ctx;
    }
    
    public static UserContext ofLlmUser(String llmUserId, String parentUserId) {
        UserContext ctx = new UserContext();
        ctx.setLlmUser(true);
        ctx.setLlmUserId(llmUserId);
        ctx.setUserId(parentUserId);
        return ctx;
    }
    
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
    
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
    
    public void addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }
    
    public void addPermission(String permission) {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getDomainId() { return domainId; }
    public void setDomainId(String domainId) { this.domainId = domainId; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    
    public boolean isLlmUser() { return isLlmUser; }
    public void setLlmUser(boolean llmUser) { isLlmUser = llmUser; }
    
    public String getLlmUserId() { return llmUserId; }
    public void setLlmUserId(String llmUserId) { this.llmUserId = llmUserId; }
    
    public String getLlmToken() { return llmToken; }
    public void setLlmToken(String llmToken) { this.llmToken = llmToken; }
    
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    
    public static class Builder {
        private UserContext context = new UserContext();
        
        public Builder userId(String userId) {
            context.setUserId(userId);
            return this;
        }
        
        public Builder userName(String userName) {
            context.setUserName(userName);
            return this;
        }
        
        public Builder domainId(String domainId) {
            context.setDomainId(domainId);
            return this;
        }
        
        public Builder token(String token) {
            context.setToken(token);
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            context.setSessionId(sessionId);
            return this;
        }
        
        public Builder roles(List<String> roles) {
            context.setRoles(roles);
            return this;
        }
        
        public Builder permissions(List<String> permissions) {
            context.setPermissions(permissions);
            return this;
        }
        
        public Builder llmUser(boolean isLlmUser) {
            context.setLlmUser(isLlmUser);
            return this;
        }
        
        public Builder llmUserId(String llmUserId) {
            context.setLlmUserId(llmUserId);
            return this;
        }
        
        public Builder llmToken(String llmToken) {
            context.setLlmToken(llmToken);
            return this;
        }
        
        public UserContext build() {
            return context;
        }
    }
}
