package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识上下文
 * 
 * <p>封装知识库相关信息，支持 LLM 访问知识资料库。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class KnowledgeContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String knowledgeBaseId;
    private String knowledgeBaseType;
    private List<String> accessibleKnowledgeBases;
    private Map<String, Object> searchFilters;
    private int maxResults = 5;
    private float similarityThreshold = 0.7f;
    
    private Map<String, Object> metadata;

    public KnowledgeContext() {
        this.accessibleKnowledgeBases = new ArrayList<>();
        this.searchFilters = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    public KnowledgeContext(String knowledgeBaseId) {
        this();
        this.knowledgeBaseId = knowledgeBaseId;
    }
    
    public void addAccessibleKnowledgeBase(String kbId) {
        if (accessibleKnowledgeBases == null) {
            accessibleKnowledgeBases = new ArrayList<>();
        }
        if (!accessibleKnowledgeBases.contains(kbId)) {
            accessibleKnowledgeBases.add(kbId);
        }
    }
    
    public void addSearchFilter(String key, Object value) {
        if (searchFilters == null) {
            searchFilters = new HashMap<>();
        }
        searchFilters.put(key, value);
    }
    
    public boolean hasAccessTo(String kbId) {
        return accessibleKnowledgeBases != null && accessibleKnowledgeBases.contains(kbId);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getKnowledgeBaseId() { return knowledgeBaseId; }
    public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }
    
    public String getKnowledgeBaseType() { return knowledgeBaseType; }
    public void setKnowledgeBaseType(String knowledgeBaseType) { this.knowledgeBaseType = knowledgeBaseType; }
    
    public List<String> getAccessibleKnowledgeBases() { return accessibleKnowledgeBases; }
    public void setAccessibleKnowledgeBases(List<String> accessibleKnowledgeBases) { this.accessibleKnowledgeBases = accessibleKnowledgeBases; }
    
    public Map<String, Object> getSearchFilters() { return searchFilters; }
    public void setSearchFilters(Map<String, Object> searchFilters) { this.searchFilters = searchFilters; }
    
    public int getMaxResults() { return maxResults; }
    public void setMaxResults(int maxResults) { this.maxResults = maxResults; }
    
    public float getSimilarityThreshold() { return similarityThreshold; }
    public void setSimilarityThreshold(float similarityThreshold) { this.similarityThreshold = similarityThreshold; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public static class Builder {
        private KnowledgeContext context = new KnowledgeContext();
        
        public Builder knowledgeBaseId(String knowledgeBaseId) {
            context.setKnowledgeBaseId(knowledgeBaseId);
            return this;
        }
        
        public Builder knowledgeBaseType(String knowledgeBaseType) {
            context.setKnowledgeBaseType(knowledgeBaseType);
            return this;
        }
        
        public Builder accessibleKnowledgeBase(String kbId) {
            context.addAccessibleKnowledgeBase(kbId);
            return this;
        }
        
        public Builder searchFilter(String key, Object value) {
            context.addSearchFilter(key, value);
            return this;
        }
        
        public Builder maxResults(int maxResults) {
            context.setMaxResults(maxResults);
            return this;
        }
        
        public Builder similarityThreshold(float threshold) {
            context.setSimilarityThreshold(threshold);
            return this;
        }
        
        public KnowledgeContext build() {
            return context;
        }
    }
}
