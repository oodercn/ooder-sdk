package net.ooder.scene.skill.notification;

import java.util.Map;

/**
 * 通知请求
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class NotificationRequest {
    
    private NotificationType type;
    private String title;
    private String content;
    private String recipientId;
    private String recipientName;
    private String senderId;
    private String senderName;
    private String sceneGroupId;
    private String sceneGroupName;
    private NotificationService.PushChannel channel;
    private String linkUrl;
    private String priority;
    private Map<String, Object> data;
    
    public NotificationRequest() {}
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
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
    
    public NotificationService.PushChannel getChannel() {
        return channel;
    }
    
    public void setChannel(NotificationService.PushChannel channel) {
        this.channel = channel;
    }
    
    public String getLinkUrl() {
        return linkUrl;
    }
    
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final NotificationRequest request = new NotificationRequest();
        
        public Builder type(NotificationType type) {
            request.setType(type);
            return this;
        }
        
        public Builder title(String title) {
            request.setTitle(title);
            return this;
        }
        
        public Builder content(String content) {
            request.setContent(content);
            return this;
        }
        
        public Builder recipientId(String recipientId) {
            request.setRecipientId(recipientId);
            return this;
        }
        
        public Builder recipientName(String recipientName) {
            request.setRecipientName(recipientName);
            return this;
        }
        
        public Builder senderId(String senderId) {
            request.setSenderId(senderId);
            return this;
        }
        
        public Builder senderName(String senderName) {
            request.setSenderName(senderName);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            request.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder sceneGroupName(String sceneGroupName) {
            request.setSceneGroupName(sceneGroupName);
            return this;
        }
        
        public Builder channel(NotificationService.PushChannel channel) {
            request.setChannel(channel);
            return this;
        }
        
        public Builder linkUrl(String linkUrl) {
            request.setLinkUrl(linkUrl);
            return this;
        }
        
        public Builder priority(String priority) {
            request.setPriority(priority);
            return this;
        }
        
        public Builder data(Map<String, Object> data) {
            request.setData(data);
            return this;
        }
        
        public NotificationRequest build() {
            return request;
        }
    }
}
