package net.ooder.scene.drivers.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MsgSkillImpl implements MsgSkill {

    private static final Logger logger = LoggerFactory.getLogger(MsgSkillImpl.class);

    private static final String SKILL_ID = "skill-msg";
    private static final String SKILL_NAME = "Message Skill";
    private static final String SKILL_VERSION = "0.7.3";

    private MsgCapabilities capabilities;
    private MsgManager remoteMsgManager;
    private MsgFallback fallback;
    
    private boolean initialized = false;

    public MsgSkillImpl() {
        this.capabilities = MsgCapabilities.forLocal();
        this.fallback = new MsgFallback();
    }

    @Override
    public void initialize(MsgCapabilities capabilities) {
        this.capabilities = capabilities;
        this.fallback = new MsgFallback();
        this.fallback.initialize();
        this.initialized = true;
        
        logger.info("MsgSkill initialized with provider: {}", capabilities.getProviderType());
        if (capabilities.requiresFallback()) {
            logger.info("Fallback required for: {}", capabilities.getUnsupportedCapabilities());
        }
    }

    @Override
    public void initialize(MsgCapabilities capabilities, MsgManager remoteMsgManager) {
        this.capabilities = capabilities;
        this.remoteMsgManager = remoteMsgManager;
        this.fallback = new MsgFallback();
        this.fallback.initialize();
        this.initialized = true;
        
        logger.info("MsgSkill initialized with remote provider: {}", capabilities.getProviderType());
    }

    @Override
    public String getSkillId() {
        return SKILL_ID;
    }

    @Override
    public String getSkillName() {
        return SKILL_NAME;
    }

    @Override
    public String getSkillVersion() {
        return SKILL_VERSION;
    }

    @Override
    public List<String> getCapabilities() {
        List<String> caps = new ArrayList<String>();
        caps.add("msg.send");
        caps.add("msg.receive");
        caps.add("msg.read");
        caps.add("msg.delete");
        caps.add("topic.create");
        caps.add("topic.subscribe");
        caps.add("topic.unsubscribe");
        caps.add("broadcast.send");
        return caps;
    }

    @Override
    public Message sendMessage(String senderId, String receiverId, String title, String content) {
        try {
            if (capabilities.isSupportP2P() && remoteMsgManager != null) {
                return remoteMsgManager.sendMessage(senderId, receiverId, title, content);
            }
            return fallback.sendMessage(senderId, receiverId, title, content);
        } catch (MsgException e) {
            logger.warn("Failed to send message: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Message sendTopicMessage(String senderId, String topicId, String title, String content) {
        try {
            if (capabilities.isSupportTopic() && remoteMsgManager != null) {
                return remoteMsgManager.sendTopicMessage(senderId, topicId, title, content);
            }
            return fallback.sendTopicMessage(senderId, topicId, title, content);
        } catch (MsgException e) {
            logger.warn("Failed to send topic message: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Message sendBroadcast(String senderId, String title, String content) {
        if (!capabilities.isSupportBroadcast()) {
            logger.warn("Broadcast not supported");
            return null;
        }
        
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.sendBroadcast(senderId, title, content);
            }
            return fallback.sendBroadcast(senderId, title, content);
        } catch (MsgException e) {
            logger.warn("Failed to send broadcast: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Message getMessage(String messageId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.getMessage(messageId);
            }
            return fallback.getMessage(messageId);
        } catch (MsgException e) {
            logger.warn("Failed to get message: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Message> getMessages(String userId, int limit) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.getMessages(userId, limit);
            }
            return fallback.getMessages(userId, limit);
        } catch (MsgException e) {
            logger.warn("Failed to get messages: {}", e.getMessage());
            return new ArrayList<Message>();
        }
    }

    @Override
    public List<Message> getUnreadMessages(String userId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.getUnreadMessages(userId);
            }
            return fallback.getUnreadMessages(userId);
        } catch (MsgException e) {
            logger.warn("Failed to get unread messages: {}", e.getMessage());
            return new ArrayList<Message>();
        }
    }

    @Override
    public int getUnreadCount(String userId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.getUnreadCount(userId);
            }
            return fallback.getUnreadCount(userId);
        } catch (MsgException e) {
            logger.warn("Failed to get unread count: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean markAsRead(String messageId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.markAsRead(messageId);
            }
            return fallback.markAsRead(messageId);
        } catch (MsgException e) {
            logger.warn("Failed to mark as read: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean markAllAsRead(String userId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.markAllAsRead(userId);
            }
            return fallback.markAllAsRead(userId);
        } catch (MsgException e) {
            logger.warn("Failed to mark all as read: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteMessage(String messageId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.deleteMessage(messageId);
            }
            return fallback.deleteMessage(messageId);
        } catch (MsgException e) {
            logger.warn("Failed to delete message: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Topic createTopic(String name, String ownerId, String description) {
        try {
            if (capabilities.isSupportTopic() && remoteMsgManager != null) {
                return remoteMsgManager.createTopic(name, ownerId, description);
            }
            return fallback.createTopic(name, ownerId, description);
        } catch (MsgException e) {
            logger.warn("Failed to create topic: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Topic getTopic(String topicId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.getTopic(topicId);
            }
            return fallback.getTopic(topicId);
        } catch (MsgException e) {
            logger.warn("Failed to get topic: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Topic> getTopics() {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.getTopics();
            }
            return fallback.getTopics();
        } catch (MsgException e) {
            logger.warn("Failed to get topics: {}", e.getMessage());
            return new ArrayList<Topic>();
        }
    }

    @Override
    public boolean subscribeTopic(String topicId, String userId) {
        try {
            if (capabilities.isSupportTopic() && remoteMsgManager != null) {
                return remoteMsgManager.subscribeTopic(topicId, userId);
            }
            return fallback.subscribeTopic(topicId, userId);
        } catch (MsgException e) {
            logger.warn("Failed to subscribe topic: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unsubscribeTopic(String topicId, String userId) {
        try {
            if (remoteMsgManager != null) {
                return remoteMsgManager.unsubscribeTopic(topicId, userId);
            }
            return fallback.unsubscribeTopic(topicId, userId);
        } catch (MsgException e) {
            logger.warn("Failed to unsubscribe topic: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public MsgCapabilities getMsgCapabilities() {
        return capabilities;
    }

    @Override
    public boolean isSupport(String capability) {
        switch (capability) {
            case "msg.push": return capabilities.isSupportPush();
            case "msg.p2p": return capabilities.isSupportP2P();
            case "msg.topic": return capabilities.isSupportTopic();
            case "msg.broadcast": return capabilities.isSupportBroadcast();
            case "msg.offline": return capabilities.isSupportOffline();
            case "msg.attachment": return capabilities.isSupportAttachment();
            default: return false;
        }
    }

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        logger.info("Invoking capability: {}", capability);
        
        try {
            switch (capability) {
                case "msg.send":
                    return sendMessage(
                        (String) params.get("senderId"),
                        (String) params.get("receiverId"),
                        (String) params.get("title"),
                        (String) params.get("content")
                    );
                    
                case "msg.topic.send":
                    return sendTopicMessage(
                        (String) params.get("senderId"),
                        (String) params.get("topicId"),
                        (String) params.get("title"),
                        (String) params.get("content")
                    );
                    
                case "msg.broadcast":
                    return sendBroadcast(
                        (String) params.get("senderId"),
                        (String) params.get("title"),
                        (String) params.get("content")
                    );
                    
                case "msg.get":
                    return getMessage((String) params.get("messageId"));
                    
                case "msg.list":
                    return getMessages(
                        (String) params.get("userId"),
                        (Integer) params.getOrDefault("limit", 50)
                    );
                    
                case "msg.unread":
                    return getUnreadMessages((String) params.get("userId"));
                    
                case "msg.unread.count":
                    return getUnreadCount((String) params.get("userId"));
                    
                case "msg.read":
                    return markAsRead((String) params.get("messageId"));
                    
                case "msg.read.all":
                    return markAllAsRead((String) params.get("userId"));
                    
                case "msg.delete":
                    return deleteMessage((String) params.get("messageId"));
                    
                case "topic.create":
                    return createTopic(
                        (String) params.get("name"),
                        (String) params.get("ownerId"),
                        (String) params.get("description")
                    );
                    
                case "topic.get":
                    return getTopic((String) params.get("topicId"));
                    
                case "topic.list":
                    return getTopics();
                    
                case "topic.subscribe":
                    return subscribeTopic(
                        (String) params.get("topicId"),
                        (String) params.get("userId")
                    );
                    
                case "topic.unsubscribe":
                    return unsubscribeTopic(
                        (String) params.get("topicId"),
                        (String) params.get("userId")
                    );
                    
                default:
                    logger.warn("Unknown capability: {}", capability);
                    return null;
            }
        } catch (Exception e) {
            logger.error("Failed to invoke capability: {}", capability, e);
            return null;
        }
    }
}
