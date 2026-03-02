package net.ooder.scene.skill.audit;

import java.util.Map;

/**
 * 审计日志条目
 *
 * @author ooder
 * @since 2.3
 */
public class AuditEntry {
    
    /** 日志ID */
    private String logId;
    
    /** 用户ID */
    private String userId;
    
    /** 用户角色 */
    private String role;
    
    /** 操作类型 */
    private String operation;
    
    /** 资源类型 */
    private String resourceType;
    
    /** 资源ID */
    private String resourceId;
    
    /** 操作结果 */
    private boolean success;
    
    /** 操作详情 */
    private String details;
    
    /** 客户端IP */
    private String clientIp;
    
    /** 场景ID */
    private String sceneId;
    
    /** 时间戳 */
    private long timestamp;
    
    /** 执行时长（毫秒） */
    private long duration;
    
    /** 扩展属性 */
    private Map<String, Object> attributes;
    
    public AuditEntry() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
