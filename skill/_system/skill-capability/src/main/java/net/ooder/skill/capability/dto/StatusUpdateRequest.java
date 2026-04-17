package net.ooder.skill.capability.dto;

public class StatusUpdateRequest {
    
    private String status;
    private String reason;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
