package net.ooder.scene.todo;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 待办DTO
 * 
 * <p>待办数据传输对象，包含待办的完整信息。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class TodoDTO {
    
    private String id;
    private TodoType type;
    private String title;
    private String description;
    private TodoStatus status;
    private String priority;
    
    private String sceneGroupId;
    private String sceneGroupName;
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private String toUserName;
    
    private String role;
    private String installId;
    private String capabilityId;
    private String actionType;
    
    private Long deadline;
    private Long createTime;
    private Long completedTime;
    private String completedBy;
    
    private String errorMessage;
    
    private Map<String, Object> extra = new HashMap<>();
    
    public TodoDTO() {}
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public TodoType getType() {
        return type;
    }
    
    public void setType(TodoType type) {
        this.type = type;
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
    
    public TodoStatus getStatus() {
        return status;
    }
    
    public void setStatus(TodoStatus status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
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
    
    public String getInstallId() {
        return installId;
    }
    
    public void setInstallId(String installId) {
        this.installId = installId;
    }
    
    public String getCapabilityId() {
        return capabilityId;
    }
    
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public Long getDeadline() {
        return deadline;
    }
    
    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Long getCompletedTime() {
        return completedTime;
    }
    
    public void setCompletedTime(Long completedTime) {
        this.completedTime = completedTime;
    }
    
    public String getCompletedBy() {
        return completedBy;
    }
    
    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra != null ? extra : new HashMap<>();
    }
    
    public void addExtra(String key, Object value) {
        this.extra.put(key, value);
    }
    
    public boolean isExpired() {
        if (deadline == null) {
            return false;
        }
        return System.currentTimeMillis() > deadline;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final TodoDTO dto = new TodoDTO();
        
        public Builder id(String id) {
            dto.setId(id);
            return this;
        }
        
        public Builder type(TodoType type) {
            dto.setType(type);
            return this;
        }
        
        public Builder title(String title) {
            dto.setTitle(title);
            return this;
        }
        
        public Builder description(String description) {
            dto.setDescription(description);
            return this;
        }
        
        public Builder status(TodoStatus status) {
            dto.setStatus(status);
            return this;
        }
        
        public Builder priority(String priority) {
            dto.setPriority(priority);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            dto.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder sceneGroupName(String sceneGroupName) {
            dto.setSceneGroupName(sceneGroupName);
            return this;
        }
        
        public Builder fromUserId(String fromUserId) {
            dto.setFromUserId(fromUserId);
            return this;
        }
        
        public Builder fromUserName(String fromUserName) {
            dto.setFromUserName(fromUserName);
            return this;
        }
        
        public Builder toUserId(String toUserId) {
            dto.setToUserId(toUserId);
            return this;
        }
        
        public Builder toUserName(String toUserName) {
            dto.setToUserName(toUserName);
            return this;
        }
        
        public Builder role(String role) {
            dto.setRole(role);
            return this;
        }
        
        public Builder installId(String installId) {
            dto.setInstallId(installId);
            return this;
        }
        
        public Builder capabilityId(String capabilityId) {
            dto.setCapabilityId(capabilityId);
            return this;
        }
        
        public Builder actionType(String actionType) {
            dto.setActionType(actionType);
            return this;
        }
        
        public Builder deadline(Long deadline) {
            dto.setDeadline(deadline);
            return this;
        }
        
        public Builder createTime(Long createTime) {
            dto.setCreateTime(createTime);
            return this;
        }
        
        public Builder extra(String key, Object value) {
            dto.addExtra(key, value);
            return this;
        }
        
        public TodoDTO build() {
            if (dto.getId() == null) {
                dto.setId(generateId());
            }
            if (dto.getCreateTime() == null) {
                dto.setCreateTime(System.currentTimeMillis());
            }
            if (dto.getStatus() == null) {
                dto.setStatus(TodoStatus.PENDING);
            }
            return dto;
        }
        
        private String generateId() {
            return "todo-" + System.currentTimeMillis() + "-" + 
                   Integer.toHexString((int)(Math.random() * 0xFFFF));
        }
    }
    
    @Override
    public String toString() {
        return "TodoDTO{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", toUserId='" + toUserId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                '}';
    }
}
