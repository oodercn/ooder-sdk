package net.ooder.skill.common.spi.message;

import java.util.List;
import java.util.Map;

public class Message {
    
    private String messageId;
    private String fromUserId;
    private List<String> toUserIds;
    private String title;
    private String content;
    private MessageType type;
    private Map<String, Object> data;
    private long timestamp;
    
    public enum MessageType {
        TEXT, CARD, LINK, ACTION
    }
    
    public Message() {}
    
    public Message(String fromUserId, List<String> toUserIds, String title, String content) {
        this.fromUserId = fromUserId;
        this.toUserIds = toUserIds;
        this.title = title;
        this.content = content;
        this.type = MessageType.TEXT;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }
    public List<String> getToUserIds() { return toUserIds; }
    public void setToUserIds(List<String> toUserIds) { this.toUserIds = toUserIds; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
