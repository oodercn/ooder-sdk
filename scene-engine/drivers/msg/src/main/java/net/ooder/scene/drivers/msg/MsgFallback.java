package net.ooder.scene.drivers.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MsgFallback implements MsgManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MsgFallback.class);
    
    private String storagePath;
    private Map<String, FallbackMessage> messageMap = new HashMap<String, FallbackMessage>();
    private Map<String, FallbackTopic> topicMap = new HashMap<String, FallbackTopic>();
    private Map<String, Set<String>> userMessagesMap = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> topicSubscribersMap = new HashMap<String, Set<String>>();
    
    public MsgFallback() {
        this.storagePath = "data/msg";
    }
    
    public MsgFallback(String storagePath) {
        this.storagePath = storagePath;
    }
    
    public void initialize() {
        logger.info("Initializing MSG Fallback with storage: {}", storagePath);
        
        FallbackTopic defaultTopic = new FallbackTopic();
        defaultTopic.setTopicId("topic-default");
        defaultTopic.setName("Default Topic");
        defaultTopic.setOwnerId("system");
        defaultTopic.setCreateTime(System.currentTimeMillis());
        topicMap.put(defaultTopic.getTopicId(), defaultTopic);
        
        logger.info("MSG Fallback initialized with default topic");
    }
    
    public void shutdown() {
        logger.info("MSG Fallback shutdown completed");
    }
    
    @Override
    public void init(Object configCode) {
        initialize();
    }
    
    @Override
    public Message sendMessage(String senderId, String receiverId, String title, String content) throws MsgException {
        FallbackMessage msg = new FallbackMessage();
        msg.setMessageId("msg-" + System.currentTimeMillis());
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setType(MessageType.P2P);
        msg.setPriority(MessagePriority.NORMAL);
        msg.setStatus(MessageStatus.SENT);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setSentTime(System.currentTimeMillis());
        
        messageMap.put(msg.getMessageId(), msg);
        
        if (!userMessagesMap.containsKey(receiverId)) {
            userMessagesMap.put(receiverId, new HashSet<String>());
        }
        userMessagesMap.get(receiverId).add(msg.getMessageId());
        
        logger.info("Message sent: {} -> {}", senderId, receiverId);
        return msg;
    }
    
    @Override
    public Message sendTopicMessage(String senderId, String topicId, String title, String content) throws MsgException {
        FallbackTopic topic = topicMap.get(topicId);
        if (topic == null) {
            throw new MsgException("Topic not found: " + topicId);
        }
        
        FallbackMessage msg = new FallbackMessage();
        msg.setMessageId("msg-" + System.currentTimeMillis());
        msg.setSenderId(senderId);
        msg.setTopic(topicId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setType(MessageType.TOPIC);
        msg.setPriority(MessagePriority.NORMAL);
        msg.setStatus(MessageStatus.SENT);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setSentTime(System.currentTimeMillis());
        
        messageMap.put(msg.getMessageId(), msg);
        
        Set<String> subscribers = topicSubscribersMap.get(topicId);
        if (subscribers != null) {
            for (String subscriberId : subscribers) {
                if (!userMessagesMap.containsKey(subscriberId)) {
                    userMessagesMap.put(subscriberId, new HashSet<String>());
                }
                userMessagesMap.get(subscriberId).add(msg.getMessageId());
            }
        }
        
        logger.info("Topic message sent to topic: {}", topicId);
        return msg;
    }
    
    @Override
    public Message sendBroadcast(String senderId, String title, String content) throws MsgException {
        FallbackMessage msg = new FallbackMessage();
        msg.setMessageId("msg-" + System.currentTimeMillis());
        msg.setSenderId(senderId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setType(MessageType.BROADCAST);
        msg.setPriority(MessagePriority.HIGH);
        msg.setStatus(MessageStatus.SENT);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setSentTime(System.currentTimeMillis());
        
        messageMap.put(msg.getMessageId(), msg);
        
        for (String userId : userMessagesMap.keySet()) {
            userMessagesMap.get(userId).add(msg.getMessageId());
        }
        
        logger.info("Broadcast message sent");
        return msg;
    }
    
    @Override
    public Message getMessage(String messageId) throws MsgException {
        return messageMap.get(messageId);
    }
    
    @Override
    public List<Message> getMessages(String userId, int limit) throws MsgException {
        List<Message> result = new ArrayList<Message>();
        Set<String> msgIds = userMessagesMap.get(userId);
        if (msgIds != null) {
            int count = 0;
            for (String msgId : msgIds) {
                if (count >= limit) break;
                Message msg = messageMap.get(msgId);
                if (msg != null) {
                    result.add(msg);
                    count++;
                }
            }
        }
        return result;
    }
    
    @Override
    public List<Message> getUnreadMessages(String userId) throws MsgException {
        List<Message> result = new ArrayList<Message>();
        Set<String> msgIds = userMessagesMap.get(userId);
        if (msgIds != null) {
            for (String msgId : msgIds) {
                FallbackMessage msg = messageMap.get(msgId);
                if (msg != null && !msg.isRead()) {
                    result.add(msg);
                }
            }
        }
        return result;
    }
    
    @Override
    public int getUnreadCount(String userId) throws MsgException {
        int count = 0;
        Set<String> msgIds = userMessagesMap.get(userId);
        if (msgIds != null) {
            for (String msgId : msgIds) {
                FallbackMessage msg = messageMap.get(msgId);
                if (msg != null && !msg.isRead()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    @Override
    public boolean markAsRead(String messageId) throws MsgException {
        FallbackMessage msg = messageMap.get(messageId);
        if (msg == null) {
            return false;
        }
        msg.setStatus(MessageStatus.READ);
        msg.setReadTime(System.currentTimeMillis());
        return true;
    }
    
    @Override
    public boolean markAllAsRead(String userId) throws MsgException {
        Set<String> msgIds = userMessagesMap.get(userId);
        if (msgIds == null) {
            return false;
        }
        
        for (String msgId : msgIds) {
            FallbackMessage msg = messageMap.get(msgId);
            if (msg != null && !msg.isRead()) {
                msg.setStatus(MessageStatus.READ);
                msg.setReadTime(System.currentTimeMillis());
            }
        }
        return true;
    }
    
    @Override
    public boolean deleteMessage(String messageId) throws MsgException {
        FallbackMessage msg = messageMap.remove(messageId);
        if (msg == null) {
            return false;
        }
        
        for (Set<String> msgIds : userMessagesMap.values()) {
            msgIds.remove(messageId);
        }
        
        return true;
    }
    
    @Override
    public Topic createTopic(String name, String ownerId, String description) throws MsgException {
        FallbackTopic topic = new FallbackTopic();
        topic.setTopicId("topic-" + System.currentTimeMillis());
        topic.setName(name);
        topic.setOwnerId(ownerId);
        topic.setDescription(description);
        topic.setCreateTime(System.currentTimeMillis());
        
        topicMap.put(topic.getTopicId(), topic);
        topicSubscribersMap.put(topic.getTopicId(), new HashSet<String>());
        
        logger.info("Topic created: {}", name);
        return topic;
    }
    
    @Override
    public Topic getTopic(String topicId) throws MsgException {
        return topicMap.get(topicId);
    }
    
    @Override
    public List<Topic> getTopics() throws MsgException {
        return new ArrayList<Topic>(topicMap.values());
    }
    
    @Override
    public boolean subscribeTopic(String topicId, String userId) throws MsgException {
        if (!topicMap.containsKey(topicId)) {
            throw new MsgException("Topic not found: " + topicId);
        }
        
        Set<String> subscribers = topicSubscribersMap.get(topicId);
        if (subscribers == null) {
            subscribers = new HashSet<String>();
            topicSubscribersMap.put(topicId, subscribers);
        }
        
        return subscribers.add(userId);
    }
    
    @Override
    public boolean unsubscribeTopic(String topicId, String userId) throws MsgException {
        Set<String> subscribers = topicSubscribersMap.get(topicId);
        if (subscribers == null) {
            return false;
        }
        return subscribers.remove(userId);
    }
    
    @Override
    public List<String> getTopicSubscribers(String topicId) throws MsgException {
        Set<String> subscribers = topicSubscribersMap.get(topicId);
        return subscribers != null ? new ArrayList<String>(subscribers) : new ArrayList<String>();
    }
    
    @Override
    public boolean isSupportPush() { return true; }
    
    @Override
    public boolean isSupportP2P() { return true; }
    
    @Override
    public boolean isSupportTopic() { return true; }
    
    @Override
    public boolean isSupportBroadcast() { return true; }
    
    @Override
    public void reloadAll() {
        messageMap.clear();
        topicMap.clear();
        userMessagesMap.clear();
        topicSubscribersMap.clear();
        initialize();
    }
    
    static class FallbackMessage implements Message {
        private String messageId;
        private String topic;
        private String senderId;
        private String receiverId;
        private String title;
        private String content;
        private MessageType type;
        private MessagePriority priority;
        private MessageStatus status;
        private Long createTime;
        private Long sentTime;
        private Long deliveredTime;
        private Long readTime;
        private Map<String, Object> metadata = new HashMap<String, Object>();
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public MessageType getType() { return type; }
        public void setType(MessageType type) { this.type = type; }
        public MessagePriority getPriority() { return priority; }
        public void setPriority(MessagePriority priority) { this.priority = priority; }
        public MessageStatus getStatus() { return status; }
        public void setStatus(MessageStatus status) { this.status = status; }
        public Long getCreateTime() { return createTime; }
        public void setCreateTime(Long createTime) { this.createTime = createTime; }
        public Long getSentTime() { return sentTime; }
        public void setSentTime(Long sentTime) { this.sentTime = sentTime; }
        public Long getDeliveredTime() { return deliveredTime; }
        public void setDeliveredTime(Long deliveredTime) { this.deliveredTime = deliveredTime; }
        public Long getReadTime() { return readTime; }
        public void setReadTime(Long readTime) { this.readTime = readTime; }
        public Map<String, Object> getMetadata() { return metadata; }
        public boolean isRead() { return readTime != null; }
        public boolean isDelivered() { return deliveredTime != null; }
    }
    
    static class FallbackTopic implements Topic {
        private String topicId;
        private String name;
        private String description;
        private String ownerId;
        private Long createTime;
        private Map<String, Object> metadata = new HashMap<String, Object>();
        
        public String getTopicId() { return topicId; }
        public void setTopicId(String topicId) { this.topicId = topicId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public Long getCreateTime() { return createTime; }
        public void setCreateTime(Long createTime) { this.createTime = createTime; }
        public List<String> getSubscriberIds() { return new ArrayList<String>(); }
        public int getSubscriberCount() { return 0; }
        public boolean isSubscribed(String userId) { return false; }
        public Map<String, Object> getMetadata() { return metadata; }
    }
}
