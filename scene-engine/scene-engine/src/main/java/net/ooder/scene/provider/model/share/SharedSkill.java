package net.ooder.scene.provider.model.share;

import java.util.List;

public class SharedSkill {
    
    private String shareId;
    private String skillId;
    private String name;
    private String description;
    private List<String> sharedWith;
    private long createdAt;
    private String status;
    private String shareType;
    
    public String getShareId() {
        return shareId;
    }
    
    public void setShareId(String shareId) {
        this.shareId = shareId;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getSharedWith() {
        return sharedWith;
    }
    
    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getShareType() {
        return shareType;
    }
    
    public void setShareType(String shareType) {
        this.shareType = shareType;
    }
}
