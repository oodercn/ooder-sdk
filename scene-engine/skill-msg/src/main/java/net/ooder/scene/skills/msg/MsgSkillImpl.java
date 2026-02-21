package net.ooder.scene.skills.msg;

import net.ooder.scene.core.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MsgSkillImpl 消息技能实现
 * 
 * <p>默认实现，使用内存存储。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class MsgSkillImpl implements MsgSkill {

    private static final String SKILL_ID = "skill-msg-001";
    private static final String SKILL_NAME = "消息技能";
    private static final String SKILL_VERSION = "0.7.3";

    private Map<String, Map<String, Object>> messages = new ConcurrentHashMap<String, Map<String, Object>>();
    private Map<String, List<Map<String, Object>>> conversationMessages = new ConcurrentHashMap<String, List<Map<String, Object>>>();
    private Map<String, Map<String, Long>> readStatus = new ConcurrentHashMap<String, Map<String, Long>>();

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
        caps.add("message.send.text");
        caps.add("message.send.image");
        caps.add("message.send.file");
        caps.add("message.history");
        caps.add("message.markRead");
        caps.add("message.recall");
        caps.add("message.delete");
        caps.add("message.forward");
        caps.add("message.search");
        caps.add("message.unread");
        return caps;
    }

    // ==================== 消息发送 ====================

    @Override
    public Map<String, Object> sendTextMessage(String conversationId, String senderId, String senderName, String content) {
        String messageId = generateMessageId();
        long timestamp = System.currentTimeMillis();

        Map<String, Object> message = new HashMap<String, Object>();
        message.put("messageId", messageId);
        message.put("conversationId", conversationId);
        message.put("senderId", senderId);
        message.put("senderName", senderName);
        message.put("msgType", "text");
        message.put("content", content);
        message.put("createTime", timestamp);
        message.put("status", "sent");

        saveMessage(conversationId, message);
        return message;
    }

    @Override
    public Map<String, Object> sendImageMessage(String conversationId, String senderId, String senderName,
                                                  String imageUrl, String thumbnailUrl) {
        String messageId = generateMessageId();
        long timestamp = System.currentTimeMillis();

        Map<String, Object> message = new HashMap<String, Object>();
        message.put("messageId", messageId);
        message.put("conversationId", conversationId);
        message.put("senderId", senderId);
        message.put("senderName", senderName);
        message.put("msgType", "image");
        message.put("imageUrl", imageUrl);
        message.put("thumbnailUrl", thumbnailUrl);
        message.put("createTime", timestamp);
        message.put("status", "sent");

        saveMessage(conversationId, message);
        return message;
    }

    @Override
    public Map<String, Object> sendFileMessage(String conversationId, String senderId, String senderName,
                                                String fileName, String fileUrl, long fileSize) {
        String messageId = generateMessageId();
        long timestamp = System.currentTimeMillis();

        Map<String, Object> message = new HashMap<String, Object>();
        message.put("messageId", messageId);
        message.put("conversationId", conversationId);
        message.put("senderId", senderId);
        message.put("senderName", senderName);
        message.put("msgType", "file");
        message.put("fileName", fileName);
        message.put("fileUrl", fileUrl);
        message.put("fileSize", fileSize);
        message.put("createTime", timestamp);
        message.put("status", "sent");

        saveMessage(conversationId, message);
        return message;
    }

    // ==================== 消息历史 (SE-001 扩展) ====================

    @Override
    public List<Map<String, Object>> getMessageHistory(String conversationId, int limit, Long beforeTime) {
        List<Map<String, Object>> allMessages = conversationMessages.get(conversationId);
        if (allMessages == null) {
            return new ArrayList<Map<String, Object>>();
        }

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        int count = 0;

        for (int i = allMessages.size() - 1; i >= 0 && count < limit; i--) {
            Map<String, Object> msg = allMessages.get(i);
            Long createTime = (Long) msg.get("createTime");

            if (beforeTime == null || createTime < beforeTime) {
                if (!"recalled".equals(msg.get("status"))) {
                    result.add(0, msg);
                    count++;
                }
            }
        }

        return result;
    }

    @Override
    public Map<String, Object> getMessage(String messageId) {
        return messages.get(messageId);
    }

    @Override
    public List<Map<String, Object>> searchMessages(String userId, String keyword, int limit) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String lowerKeyword = keyword.toLowerCase();

        for (Map<String, Object> msg : messages.values()) {
            Object content = msg.get("content");
            if (content != null && content.toString().toLowerCase().contains(lowerKeyword)) {
                if (!"recalled".equals(msg.get("status"))) {
                    result.add(msg);
                    if (result.size() >= limit) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    // ==================== 已读标记 (SE-001 扩展) ====================

    @Override
    public boolean markAsRead(String conversationId, String userId, Long lastReadTime) {
        Map<String, Long> userReadStatus = readStatus.computeIfAbsent(conversationId, k -> new ConcurrentHashMap<String, Long>());
        userReadStatus.put(userId, lastReadTime != null ? lastReadTime : System.currentTimeMillis());
        return true;
    }

    @Override
    public int getUnreadCount(String conversationId, String userId) {
        List<Map<String, Object>> allMessages = conversationMessages.get(conversationId);
        if (allMessages == null) {
            return 0;
        }

        Map<String, Long> userReadStatus = readStatus.get(conversationId);
        Long lastReadTime = userReadStatus != null ? userReadStatus.get(userId) : 0L;

        int count = 0;
        for (Map<String, Object> msg : allMessages) {
            Long createTime = (Long) msg.get("createTime");
            String senderId = (String) msg.get("senderId");

            if (!userId.equals(senderId) && createTime > lastReadTime) {
                count++;
            }
        }

        return count;
    }

    @Override
    public Map<String, Object> getUnreadSummary(String userId) {
        Map<String, Object> summary = new HashMap<String, Object>();
        int totalUnread = 0;
        List<Map<String, Object>> conversationUnreads = new ArrayList<Map<String, Object>>();

        for (String conversationId : conversationMessages.keySet()) {
            int unread = getUnreadCount(conversationId, userId);
            if (unread > 0) {
                totalUnread += unread;
                Map<String, Object> convUnread = new HashMap<String, Object>();
                convUnread.put("conversationId", conversationId);
                convUnread.put("unreadCount", unread);
                conversationUnreads.add(convUnread);
            }
        }

        summary.put("totalUnread", totalUnread);
        summary.put("conversations", conversationUnreads);
        return summary;
    }

    // ==================== 消息撤回 (SE-001 扩展) ====================

    @Override
    public boolean recallMessage(String messageId, String userId) {
        Map<String, Object> message = messages.get(messageId);
        if (message == null) {
            return false;
        }

        String senderId = (String) message.get("senderId");
        Long createTime = (Long) message.get("createTime");

        if (!userId.equals(senderId)) {
            return false;
        }

        long twoMinutes = 2 * 60 * 1000;
        if (System.currentTimeMillis() - createTime > twoMinutes) {
            return false;
        }

        message.put("status", "recalled");
        message.put("recallTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean deleteMessage(String messageId, String userId) {
        Map<String, Object> message = messages.get(messageId);
        if (message == null) {
            return false;
        }

        message.put("deletedBy_" + userId, true);
        return true;
    }

    // ==================== 消息转发 ====================

    @Override
    public Map<String, Object> forwardMessage(String messageId, List<String> targetConversationIds, String senderId) {
        Map<String, Object> originalMessage = messages.get(messageId);
        if (originalMessage == null) {
            return null;
        }

        List<String> forwardedIds = new ArrayList<String>();
        String senderName = (String) originalMessage.get("senderName");

        for (String targetConvId : targetConversationIds) {
            Map<String, Object> forwarded = new HashMap<String, Object>(originalMessage);
            forwarded.put("messageId", generateMessageId());
            forwarded.put("conversationId", targetConvId);
            forwarded.put("senderId", senderId);
            forwarded.put("forwardedFrom", messageId);
            forwarded.put("createTime", System.currentTimeMillis());
            forwarded.put("status", "sent");

            saveMessage(targetConvId, forwarded);
            forwardedIds.add((String) forwarded.get("messageId"));
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("originalMessageId", messageId);
        result.put("forwardedMessageIds", forwardedIds);
        result.put("targetConversations", targetConversationIds);
        return result;
    }

    // ==================== 能力调用 ====================

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        switch (capability) {
            case "message.send.text":
                return sendTextMessage(
                    (String) params.get("conversationId"),
                    (String) params.get("senderId"),
                    (String) params.get("senderName"),
                    (String) params.get("content")
                );
            case "message.send.image":
                return sendImageMessage(
                    (String) params.get("conversationId"),
                    (String) params.get("senderId"),
                    (String) params.get("senderName"),
                    (String) params.get("imageUrl"),
                    (String) params.get("thumbnailUrl")
                );
            case "message.send.file":
                return sendFileMessage(
                    (String) params.get("conversationId"),
                    (String) params.get("senderId"),
                    (String) params.get("senderName"),
                    (String) params.get("fileName"),
                    (String) params.get("fileUrl"),
                    params.get("fileSize") != null ? ((Number) params.get("fileSize")).longValue() : 0L
                );
            case "message.history":
                return getMessageHistory(
                    (String) params.get("conversationId"),
                    params.get("limit") != null ? ((Number) params.get("limit")).intValue() : 50,
                    params.get("beforeTime") != null ? ((Number) params.get("beforeTime")).longValue() : null
                );
            case "message.markRead":
                return markAsRead(
                    (String) params.get("conversationId"),
                    (String) params.get("userId"),
                    params.get("lastReadTime") != null ? ((Number) params.get("lastReadTime")).longValue() : null
                );
            case "message.recall":
                return recallMessage(
                    (String) params.get("messageId"),
                    (String) params.get("userId")
                );
            case "message.delete":
                return deleteMessage(
                    (String) params.get("messageId"),
                    (String) params.get("userId")
                );
            case "message.forward":
                @SuppressWarnings("unchecked")
                List<String> targetIds = (List<String>) params.get("targetConversationIds");
                return forwardMessage(
                    (String) params.get("messageId"),
                    targetIds,
                    (String) params.get("senderId")
                );
            case "message.search":
                return searchMessages(
                    (String) params.get("userId"),
                    (String) params.get("keyword"),
                    params.get("limit") != null ? ((Number) params.get("limit")).intValue() : 20
                );
            case "message.unread":
                return getUnreadSummary((String) params.get("userId"));
            default:
                throw new IllegalArgumentException("Unknown capability: " + capability);
        }
    }

    // ==================== 私有方法 ====================

    private String generateMessageId() {
        return "msg-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void saveMessage(String conversationId, Map<String, Object> message) {
        String messageId = (String) message.get("messageId");
        messages.put(messageId, message);

        List<Map<String, Object>> convMessages = conversationMessages.computeIfAbsent(
            conversationId, k -> new ArrayList<Map<String, Object>>()
        );
        convMessages.add(message);
    }
}
