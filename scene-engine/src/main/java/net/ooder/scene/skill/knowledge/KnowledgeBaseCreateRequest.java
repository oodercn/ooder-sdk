package net.ooder.scene.skill.knowledge;

import java.util.List;
import java.util.Map;

/**
 * 知识库创建请求
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeBaseCreateRequest {
    
    /** 知识库名称 */
    private String name;
    
    /** 知识库描述 */
    private String description;
    
    /** 所有者ID */
    private String ownerId;
    
    /** 可见性 */
    private String visibility;
    
    /** 嵌入模型 */
    private String embeddingModel;
    
    /** 分块大小 */
    private int chunkSize;
    
    /** 分块重叠 */
    private int chunkOverlap;
    
    /** 标签 */
    private List<String> tags;
    
    /** 扩展属性 */
    private Map<String, Object> metadata;
    
    public KnowledgeBaseCreateRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    
    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }
    
    public int getChunkSize() { return chunkSize; }
    public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }
    
    public int getChunkOverlap() { return chunkOverlap; }
    public void setChunkOverlap(int chunkOverlap) { this.chunkOverlap = chunkOverlap; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
