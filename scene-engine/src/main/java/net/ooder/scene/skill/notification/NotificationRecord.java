package net.ooder.scene.skill.notification;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知记录
 * 
 * <p>存储的通知记录实体。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class NotificationRecord {
    
    private String id;
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
    private boolean read;
    private Long createTime;
    private Long readTime;
    private String linkUrl;
    private String priority;
    private Map<String, Object> data;
    
    public NotificationRecord() {}
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Long getReadTime() {
        return readTime;
    }
    
    public void setReadTime(Long readTime) {
        this.readTime = readTime;
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
    
    public void addData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final NotificationRecord record = new NotificationRecord();
        
        public Builder id(String id) {
            record.setId(id);
            return this;
        }
        
        public Builder type(NotificationType type) {
            record.setType(type);
            return this;
        }
        
        public Builder title(String title) {
            record.setTitle(title);
            return this;
        }
        
        public Builder content(String content) {
            record.setContent(content);
            return this;
        }
        
        public Builder recipientId(String recipientId) {
            record.setRecipientId(recipientId);
            return this;
        }
        
        public Builder recipientName(String recipientName) {
            record.setRecipientName(recipientName);
            return this;
        }
        
        public Builder senderId(String senderId) {
            record.setSenderId(senderId);
            return this;
        }
        
        public Builder senderName(String senderName) {
            record.setSenderName(senderName);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            record.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder sceneGroupName(String sceneGroupName) {
            record.setSceneGroupName(sceneGroupName);
            return this;
        }
        
        public Builder channel(NotificationService.PushChannel channel) {
            record.setChannel(channel);
            return this;
        }
        
        public Builder linkUrl(String linkUrl) {
            record.setLinkUrl(linkUrl);
            return this;
        }
        
        public Builder priority(String priority) {
            record.setPriority(priority);
            return this;
        }
        
        public Builder data(String key, Object value) {
            record.addData(key, value);
            return this;
        }
        
        public NotificationRecord build() {
            if (record.getId() == null) {
                record.setId("notif-" + System.currentTimeMillis() + "-" + 
                           Integer.toHexString((int)(Math.random() * 0xFFFF)));
            }
            if (record.getCreateTime() == null) {
                record.setCreateTime(System.currentTimeMillis());
            }
            if (record.getChannel() == null) {
                record.setChannel(NotificationService.PushChannel.IN_APP);
            }
            return record;
        }
    }
}
