package net.ooder.scene.skill.knowledge;

import java.util.List;
import java.util.Map;

/**
 * 知识库
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeBase {

    public static final String VISIBILITY_PRIVATE = "private";
    public static final String VISIBILITY_PUBLIC = "public";

    /** 知识库ID */
    private String kbId;

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

    /** 创建时间 */
    private long createdAt;

    /** 更新时间 */
    private long updatedAt;

    /** 文档数量 */
    private int documentCount;

    /** 总大小 */
    private long totalSize;

    /** 索引状态 */
    private String indexStatus;

    /** 扩展属性 */
    private Map<String, Object> metadata;

    public KnowledgeBase() {}

    public KnowledgeBase(String kbId, String name, String ownerId) {
        this.kbId = kbId;
        this.name = name;
        this.ownerId = ownerId;
        this.visibility = VISIBILITY_PRIVATE;
        this.chunkSize = 500;
        this.chunkOverlap = 50;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }

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

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public int getDocumentCount() { return documentCount; }
    public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }

    public long getTotalSize() { return totalSize; }
    public void setTotalSize(long totalSize) { this.totalSize = totalSize; }

    public String getIndexStatus() { return indexStatus; }
    public void setIndexStatus(String indexStatus) { this.indexStatus = indexStatus; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    // 业务方法
    public boolean isPublic() {
        return VISIBILITY_PUBLIC.equals(visibility);
    }

    public void incrementDocumentCount() {
        this.documentCount++;
    }

    public void decrementDocumentCount() {
        this.documentCount--;
    }
}
