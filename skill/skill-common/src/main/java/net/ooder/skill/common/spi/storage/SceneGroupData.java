package net.ooder.skill.common.spi.storage;

import java.io.Serializable;
import java.util.List;

public class SceneGroupData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String ownerName;
    private String status;
    private List<String> capabilityIds;
    private List<String> participantIds;
    private Long createdAt;
    private Long updatedAt;
    
    public SceneGroupData() {}
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<String> getCapabilityIds() { return capabilityIds; }
    public void setCapabilityIds(List<String> capabilityIds) { this.capabilityIds = capabilityIds; }
    
    public List<String> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }
    
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
