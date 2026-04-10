package net.ooder.skill.common.spi.agent;

import net.ooder.skill.common.spi.storage.PageResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AgentStorage {
    
    AgentData save(AgentData agent);
    
    Optional<AgentData> findById(String id);
    
    Optional<AgentData> findByName(String name);
    
    PageResult<AgentData> findByOwnerId(String ownerId, int pageNum, int pageSize);
    
    List<AgentData> findByType(String type);
    
    List<AgentData> findByStatus(String status);
    
    void deleteById(String id);
    
    boolean existsByName(String name);
    
    class AgentData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String name;
        private String type;
        private String description;
        private String ownerId;
        private String status;
        private Map<String, Object> config;
        private Map<String, Object> capabilities;
        private Long createdAt;
        private Long updatedAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public Map<String, Object> getCapabilities() { return capabilities; }
        public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
        public Long getCreatedAt() { return createdAt; }
        public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
        public Long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    }
}
