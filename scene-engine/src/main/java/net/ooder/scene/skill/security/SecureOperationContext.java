package net.ooder.scene.skill.security;

import java.util.Map;

/**
 * 安全操作上下文
 * 封装操作的安全信息，用于审计和用户校验
 *
 * @author ooder
 * @since 2.3
 */
public class SecureOperationContext {
    
    /** 操作用户ID */
    private String userId;
    
    /** 用户角色 */
    private String role;
    
    /** 场景ID */
    private String sceneId;
    
    /** 操作类型 */
    private String operationType;
    
    /** 资源标识 */
    private String resourceId;
    
    /** 操作时间戳 */
    private long timestamp;
    
    /** 扩展属性 */
    private Map<String, Object> attributes;
    
    public SecureOperationContext() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static SecureOperationContext create(String userId, String operationType, String resourceId) {
        SecureOperationContext ctx = new SecureOperationContext();
        ctx.setUserId(userId);
        ctx.setOperationType(operationType);
        ctx.setResourceId(resourceId);
        return ctx;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
