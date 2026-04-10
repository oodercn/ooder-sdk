package net.ooder.skill.common.spi.llm;

import net.ooder.skill.common.spi.storage.PageResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConversationStorage {
    
    ConversationData save(ConversationData conversation);
    
    Optional<ConversationData> findById(String id);
    
    PageResult<ConversationData> findByUserId(String userId, int pageNum, int pageSize);
    
    List<ConversationData> findByStatus(String status);
    
    void deleteById(String id);
    
    void archive(String id);
    
    long countByUserId(String userId);
    
    class ConversationData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String userId;
        private String title;
        private String status;
        private List<MessageData> messages;
        private Map<String, Object> metadata;
        private Long createdAt;
        private Long updatedAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<MessageData> getMessages() { return messages; }
        public void setMessages(List<MessageData> messages) { this.messages = messages; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public Long getCreatedAt() { return createdAt; }
        public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
        public Long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    }
    
    class MessageData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String conversationId;
        private String role;
        private String content;
        private Long timestamp;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }
}
