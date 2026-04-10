package net.ooder.skill.common.spi.agent;

import net.ooder.skill.common.spi.storage.PageResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AgentSessionStorage {
    
    AgentSessionData save(AgentSessionData session);
    
    Optional<AgentSessionData> findById(String id);
    
    PageResult<AgentSessionData> findByAgentId(String agentId, int pageNum, int pageSize);
    
    List<AgentSessionData> findByStatus(String status);
    
    void deleteById(String id);
    
    void terminate(String id);
    
    long countByAgentId(String agentId);
    
    class AgentSessionData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String agentId;
        private String userId;
        private String status;
        private Map<String, Object> context;
        private Long startedAt;
        private Long endedAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        public Long getStartedAt() { return startedAt; }
        public void setStartedAt(Long startedAt) { this.startedAt = startedAt; }
        public Long getEndedAt() { return endedAt; }
        public void setEndedAt(Long endedAt) { this.endedAt = endedAt; }
    }
}
