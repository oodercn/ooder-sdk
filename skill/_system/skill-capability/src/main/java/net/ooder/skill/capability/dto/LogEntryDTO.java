package net.ooder.skill.capability.dto;

import java.util.Map;

public class LogEntryDTO {
    
    private long timestamp;
    private String capabilityId;
    private String capabilityName;
    private String level;
    private String message;
    private long duration;
    private Map<String, Object> metadata;

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getCapabilityName() { return capabilityName; }
    public void setCapabilityName(String capabilityName) { this.capabilityName = capabilityName; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
