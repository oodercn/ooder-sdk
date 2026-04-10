package net.ooder.skill.common.spi.knowledge;

import net.ooder.skill.common.spi.storage.PageResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface KnowledgeBaseStorage {
    
    KnowledgeBaseData save(KnowledgeBaseData knowledgeBase);
    
    Optional<KnowledgeBaseData> findById(String id);
    
    Optional<KnowledgeBaseData> findByName(String name);
    
    PageResult<KnowledgeBaseData> findByOwnerId(String ownerId, int pageNum, int pageSize);
    
    List<KnowledgeBaseData> findByStatus(String status);
    
    void deleteById(String id);
    
    boolean existsByName(String name);
    
    class KnowledgeBaseData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String name;
        private String description;
        private String ownerId;
        private String status;
        private String embeddingModel;
        private Integer dimension;
        private Map<String, Object> config;
        private Long createdAt;
        private Long updatedAt;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getEmbeddingModel() { return embeddingModel; }
        public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }
        public Integer getDimension() { return dimension; }
        public void setDimension(Integer dimension) { this.dimension = dimension; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public Long getCreatedAt() { return createdAt; }
        public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
        public Long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    }
}
