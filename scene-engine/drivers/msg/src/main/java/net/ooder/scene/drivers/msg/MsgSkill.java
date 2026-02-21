package net.ooder.scene.drivers.msg;

import java.util.List;
import java.util.Map;

public interface MsgSkill {

    String getSkillId();
    
    String getSkillName();
    
    String getSkillVersion();
    
    List<String> getCapabilities();
    
    void initialize(MsgCapabilities capabilities);
    
    void initialize(MsgCapabilities capabilities, MsgManager remoteMsgManager);
    
    Message sendMessage(String senderId, String receiverId, String title, String content);
    
    Message sendTopicMessage(String senderId, String topicId, String title, String content);
    
    Message sendBroadcast(String senderId, String title, String content);
    
    Message getMessage(String messageId);
    
    List<Message> getMessages(String userId, int limit);
    
    List<Message> getUnreadMessages(String userId);
    
    int getUnreadCount(String userId);
    
    boolean markAsRead(String messageId);
    
    boolean markAllAsRead(String userId);
    
    boolean deleteMessage(String messageId);
    
    Topic createTopic(String name, String ownerId, String description);
    
    Topic getTopic(String topicId);
    
    List<Topic> getTopics();
    
    boolean subscribeTopic(String topicId, String userId);
    
    boolean unsubscribeTopic(String topicId, String userId);
    
    MsgCapabilities getMsgCapabilities();
    
    boolean isSupport(String capability);
    
    Object invoke(String capability, Map<String, Object> params);
}
