package net.ooder.sdk.validator;

import java.util.Map;

public class CheckResult {
    private String checkId;
    private String checkName;
    private int level;
    private Severity severity;
    private Status status;
    private String message;
    private Map<String, Object> details;
    private long duration;
    
    public String getCheckId() { return checkId; }
    public void setCheckId(String checkId) { this.checkId = checkId; }
    
    public String getCheckName() { return checkName; }
    public void setCheckName(String checkName) { this.checkName = checkName; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}