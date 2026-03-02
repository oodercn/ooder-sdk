package net.ooder.scene.skill.rag;

import java.util.Map;

/**
 * 知识库配置
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeBaseConfig {
    
    /** 知识库ID */
    private String kbId;
    
    /** 知识库名称 */
    private String name;
    
    /** 描述 */
    private String description;
    
    /** 检索权重 */
    private float weight;
    
    /** 相似度阈值 */
    private float similarityThreshold;
    
    /** 最大返回结果数 */
    private int maxResults;
    
    /** 扩展配置 */
    private Map<String, Object> params;
    
    public KnowledgeBaseConfig() {
        this.weight = 1.0f;
        this.similarityThreshold = 0.7f;
        this.maxResults = 5;
    }
    
    // Getters and Setters
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    
    public float getSimilarityThreshold() { return similarityThreshold; }
    public void setSimilarityThreshold(float similarityThreshold) { this.similarityThreshold = similarityThreshold; }
    
    public int getMaxResults() { return maxResults; }
    public void setMaxResults(int maxResults) { this.maxResults = maxResults; }
    
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
