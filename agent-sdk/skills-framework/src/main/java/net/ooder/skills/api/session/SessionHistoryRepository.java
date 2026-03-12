package net.ooder.skills.api.session;

import java.util.List;
import java.util.Map;

/**
 * 会话历史仓库接口
 *
 * <p>负责会话历史的持久化存储和查询</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface SessionHistoryRepository {

    /**
     * 保存会话历史
     *
     * @param sessionId 会话ID
     * @param messages  消息列表
     */
    void save(String sessionId, List<Message> messages);

    /**
     * 加载会话历史
     *
     * @param sessionId 会话ID
     * @param limit     限制条数
     * @return 消息列表
     */
    List<Message> load(String sessionId, int limit);

    /**
     * 获取用户最近会话
     *
     * @param userId 用户ID
     * @param limit  限制条数
     * @return 会话摘要列表
     */
    List<SessionSummary> getRecentSessions(String userId, int limit);

    /**
     * 删除会话历史
     *
     * @param sessionId 会话ID
     */
    void delete(String sessionId);

    /**
     * 清空用户所有会话
     *
     * @param userId 用户ID
     */
    void clearUserSessions(String userId);

    /**
     * 获取会话元数据
     *
     * @param sessionId 会话ID
     * @return 元数据
     */
    SessionMetadata getSessionMetadata(String sessionId);

    /**
     * 更新会话元数据
     *
     * @param sessionId 会话ID
     * @param metadata  元数据
     */
    void updateSessionMetadata(String sessionId, SessionMetadata metadata);

    /**
     * 消息
     */
    class Message {
        private String messageId;
        private String role;        // user, assistant, system
        private String content;
        private long timestamp;
        private Map<String, Object> metadata;

        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 会话摘要
     */
    class SessionSummary {
        private String sessionId;
        private String userId;
        private String title;
        private String preview;
        private long lastMessageTime;
        private int messageCount;
        private Map<String, Object> metadata;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getPreview() { return preview; }
        public void setPreview(String preview) { this.preview = preview; }

        public long getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(long lastMessageTime) { this.lastMessageTime = lastMessageTime; }

        public int getMessageCount() { return messageCount; }
        public void setMessageCount(int messageCount) { this.messageCount = messageCount; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 会话元数据
     */
    class SessionMetadata {
        private String sessionId;
        private String userId;
        private String skillId;
        private long createdAt;
        private long updatedAt;
        private Map<String, Object> attributes;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

        public long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

        public Map<String, Object> getAttributes() { return attributes; }
        public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    }
}
