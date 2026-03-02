package net.ooder.scene.skill.knowledge;

import java.util.Map;

/**
 * 知识库
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeBase {
    
    /** 知识库ID */
    private String kbId;
    
    /** 知识库名称 */
    private String name;
    
    /** 知识库描述 */
    private String description;
    
    /** 所有者ID */
    private String ownerId;
    
    /** 创建时间 */
    private long createdAt;
    
    /** 更新时间 */
    private long updatedAt;
    
    /** 文档数量 */
    private int documentCount;
    
    /** 索引状态 */
    private String indexStatus;
    
    /** 扩展属性 */
    private Map<String, Object> metadata;
    
    public KnowledgeBase() {}
    
    // Getters and Setters
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    public int getDocumentCount() { return documentCount; }
    public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }
    
    public String getIndexStatus() { return indexStatus; }
    public void setIndexStatus(String indexStatus) { this.indexStatus = indexStatus; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
