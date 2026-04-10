package net.ooder.skill.common.spi.agent;

import net.ooder.skill.common.spi.storage.PageResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AgentMessageStorage {
    
    AgentMessageData save(AgentMessageData message);
    
    Optional<AgentMessageData> findById(String id);
    
    PageResult<AgentMessageData> findBySessionId(String sessionId, int pageNum, int pageSize);
    
    List<AgentMessageData> findBySessionIdOrderByTimestamp(String sessionId);
    
    void deleteBySessionId(String sessionId);
    
    long countBySessionId(String sessionId);
    
    class AgentMessageData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String sessionId;
        private String role;
        private String content;
        private Map<String, Object> metadata;
        private Long timestamp;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }
}
