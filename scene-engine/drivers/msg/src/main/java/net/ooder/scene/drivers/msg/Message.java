package net.ooder.scene.drivers.msg;

import java.util.Map;

public interface Message {

    String getMessageId();
    
    String getTopic();
    
    String getSenderId();
    
    String getReceiverId();
    
    String getTitle();
    
    String getContent();
    
    MessageType getType();
    
    MessagePriority getPriority();
    
    MessageStatus getStatus();
    
    Long getCreateTime();
    
    Long getSentTime();
    
    Long getDeliveredTime();
    
    Long getReadTime();
    
    Map<String, Object> getMetadata();
    
    boolean isRead();
    
    boolean isDelivered();
}
