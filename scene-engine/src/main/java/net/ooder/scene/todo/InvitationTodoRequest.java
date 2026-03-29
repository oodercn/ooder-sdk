package net.ooder.scene.todo;

import java.util.HashMap;
import java.util.Map;

/**
 * 邀请待办请求
 * 
 * <p>用于创建协作邀请类型的待办。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class InvitationTodoRequest {
    
    private String sceneGroupId;
    private String sceneGroupName;
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private String toUserName;
    private String role;
    private String title;
    private String description;
    private Long deadline;
    private String priority;
    private Map<String, Object> extra;
    
    public InvitationTodoRequest() {}
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getSceneGroupName() {
        return sceneGroupName;
    }
    
    public void setSceneGroupName(String sceneGroupName) {
        this.sceneGroupName = sceneGroupName;
    }
    
    public String getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    public String getFromUserName() {
        return fromUserName;
    }
    
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }
    
    public String getToUserId() {
        return toUserId;
    }
    
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
    
    public String getToUserName() {
        return toUserName;
    }
    
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getDeadline() {
        return deadline;
    }
    
    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
    
    public TodoDTO toTodoDTO() {
        TodoDTO dto = TodoDTO.builder()
                .type(TodoType.INVITATION)
                .sceneGroupId(sceneGroupId)
                .sceneGroupName(sceneGroupName)
                .fromUserId(fromUserId)
                .fromUserName(fromUserName)
                .toUserId(toUserId)
                .toUserName(toUserName)
                .role(role)
                .title(title != null ? title : "邀请加入场景组")
                .description(description)
                .deadline(deadline)
                .priority(priority)
                .build();
        
        if (extra != null) {
            extra.forEach(dto::addExtra);
        }
        
        return dto;
    }
}
