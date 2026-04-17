package net.ooder.skill.knowledge.dto;

import java.util.List;

public class KnowledgeBaseDTO {
    private String kbId;
    private String name;
    private String description;
    private String ownerId;
    private String visibility;
    private String embeddingModel;
    private int chunkSize;
    private int chunkOverlap;
    private int documentCount;
    private List<String> tags;
    private KnowledgeLayerConfig layerConfig;
    private long createTime;
    private long updateTime;

    public static class KnowledgeLayerConfig {
        private String layer;
        private int priority;
        private boolean enabled;

        public String getLayer() { return layer; }
        public void setLayer(String layer) { this.layer = layer; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

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
    public int getDocumentCount() { return documentCount; }
    public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public KnowledgeLayerConfig getLayerConfig() { return layerConfig; }
    public void setLayerConfig(KnowledgeLayerConfig layerConfig) { this.layerConfig = layerConfig; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
}
