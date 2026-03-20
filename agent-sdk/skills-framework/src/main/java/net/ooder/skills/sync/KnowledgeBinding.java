package net.ooder.skills.sync;

import java.util.UUID;

public class KnowledgeBinding {
    
    private String bindingId;
    private String knowledgeBaseId;
    private String sceneGroupId;
    private String layer;
    private String status;
    private long createTime;
    private long lastUpdateTime;
    
    public KnowledgeBinding() {
        this.bindingId = UUID.randomUUID().toString();
        this.status = "active";
        this.createTime = System.currentTimeMillis();
        this.lastUpdateTime = this.createTime;
    }
    
    public KnowledgeBinding(String knowledgeBaseId, String sceneGroupId, String layer) {
        this();
        this.knowledgeBaseId = knowledgeBaseId;
        this.sceneGroupId = sceneGroupId;
        this.layer = layer;
    }
    
    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }
    
    public String getKnowledgeBaseId() { return knowledgeBaseId; }
    public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public String getLayer() { return layer; }
    public void setLayer(String layer) { this.layer = layer; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    
    public long getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    
    public void activate() {
        this.status = "active";
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public void deactivate() {
        this.status = "inactive";
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public boolean isActive() {
        return "active".equals(status);
    }
}
