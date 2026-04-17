package net.ooder.skill.capability.dto;

public class CreateBindingRequest {
    
    private String capabilityId;
    private String linkId;
    private String linkType;
    private String status;

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getLinkId() { return linkId; }
    public void setLinkId(String linkId) { this.linkId = linkId; }
    public String getLinkType() { return linkType; }
    public void setLinkType(String linkType) { this.linkType = linkType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
