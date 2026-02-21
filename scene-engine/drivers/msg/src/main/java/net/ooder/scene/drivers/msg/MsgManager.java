package net.ooder.scene.drivers.msg;

import java.util.List;
import java.util.Map;

public interface MsgManager {
    
    void init(Object configCode);
    
    Message sendMessage(String senderId, String receiverId, String title, String content) throws MsgException;
    
    Message sendTopicMessage(String senderId, String topicId, String title, String content) throws MsgException;
    
    Message sendBroadcast(String senderId, String title, String content) throws MsgException;
    
    Message getMessage(String messageId) throws MsgException;
    
    List<Message> getMessages(String userId, int limit) throws MsgException;
    
    List<Message> getUnreadMessages(String userId) throws MsgException;
    
    int getUnreadCount(String userId) throws MsgException;
    
    boolean markAsRead(String messageId) throws MsgException;
    
    boolean markAllAsRead(String userId) throws MsgException;
    
    boolean deleteMessage(String messageId) throws MsgException;
    
    Topic createTopic(String name, String ownerId, String description) throws MsgException;
    
    Topic getTopic(String topicId) throws MsgException;
    
    List<Topic> getTopics() throws MsgException;
    
    boolean subscribeTopic(String topicId, String userId) throws MsgException;
    
    boolean unsubscribeTopic(String topicId, String userId) throws MsgException;
    
    List<String> getTopicSubscribers(String topicId) throws MsgException;
    
    boolean isSupportPush();
    
    boolean isSupportP2P();
    
    boolean isSupportTopic();
    
    boolean isSupportBroadcast();
    
    void reloadAll();
}
