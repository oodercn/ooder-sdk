package net.ooder.scene.provider.model.share;

public class ReceivedSkill {
    
    private String shareId;
    private String skillId;
    private String name;
    private String description;
    private String from;
    private String fromName;
    private long receivedAt;
    private String status;
    
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
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getFromName() {
        return fromName;
    }
    
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    
    public long getReceivedAt() {
        return receivedAt;
    }
    
    public void setReceivedAt(long receivedAt) {
        this.receivedAt = receivedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
