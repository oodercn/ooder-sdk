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
 * <p>支持多级加载：BASIC、ADVANCED、EXPERT、FULL</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
public class KnowledgeContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String skillId;
    private String knowledgeBaseId;
    private String knowledgeBaseType;
    private List<String> accessibleKnowledgeBases;
    private Map<String, Object> searchFilters;
    private int maxResults = 5;
    private float similarityThreshold = 0.7f;
    
    private Map<String, Object> metadata;
    
    private KnowledgeLoadLevel loadLevel = KnowledgeLoadLevel.ADVANCED;
    private List<KnowledgeChunk> loadedChunks = new ArrayList<>();
    private String ragIndexId;

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
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public KnowledgeLoadLevel getLoadLevel() { return loadLevel; }
    public void setLoadLevel(KnowledgeLoadLevel loadLevel) { this.loadLevel = loadLevel; }
    
    public List<KnowledgeChunk> getLoadedChunks() { return loadedChunks; }
    public void setLoadedChunks(List<KnowledgeChunk> loadedChunks) { this.loadedChunks = loadedChunks; }
    
    public String getRagIndexId() { return ragIndexId; }
    public void setRagIndexId(String ragIndexId) { this.ragIndexId = ragIndexId; }
    
    /**
     * 构建知识提示词部分
     */
    public String buildPromptSection() {
        StringBuilder sb = new StringBuilder();
        
        if (!loadedChunks.isEmpty()) {
            sb.append("## 知识库内容\n\n");
            for (KnowledgeChunk chunk : loadedChunks) {
                sb.append(chunk.getContent()).append("\n\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 添加知识块
     */
    public void addChunk(KnowledgeChunk chunk) {
        loadedChunks.add(chunk);
    }
    
    /**
     * 知识加载级别
     */
    public enum KnowledgeLoadLevel {
        BASIC(1, "基础知识", 2048),
        ADVANCED(2, "进阶知识", 4096),
        EXPERT(3, "专家知识", 8192),
        FULL(4, "完整知识", -1);
        
        private final int level;
        private final String description;
        private final int maxTokens;
        
        KnowledgeLoadLevel(int level, String description, int maxTokens) {
            this.level = level;
            this.description = description;
            this.maxTokens = maxTokens;
        }
        
        public int getLevel() { return level; }
        public String getDescription() { return description; }
        public int getMaxTokens() { return maxTokens; }
    }
    
    /**
     * 知识块
     */
    public static class KnowledgeChunk implements Serializable {
        private String id;
        private String content;
        private String source;
        private float score;
        private Map<String, Object> metadata = new HashMap<>();
        
        public KnowledgeChunk() {}
        
        public KnowledgeChunk(String content) {
            this.content = content;
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public float getScore() { return score; }
        public void setScore(float score) { this.score = score; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
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
