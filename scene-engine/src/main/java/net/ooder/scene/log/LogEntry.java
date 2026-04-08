package net.ooder.scene.log;

import java.util.Map;

public class LogEntry {

    protected String logId;

    protected LogCategory category;

    protected String subType;

    protected LogLevel level;

    protected LogPriority priority;

    protected LogStatus status;

    protected long timestamp;

    protected long createdAt;

    protected String companyId;

    protected String companyName;

    protected String departmentId;

    protected String departmentName;

    protected String userId;

    protected String userName;

    protected String sessionId;

    protected String traceId;

    protected String parentLogId;

    protected String source;

    protected String clientIp;

    protected String action;

    protected String message;

    protected String errorMessage;

    protected Map<String, Object> metadata;

    protected String resourceType;

    protected String resourceId;

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public LogCategory getCategory() { return category; }
    public void setCategory(LogCategory category) { this.category = category; }

    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }

    public LogLevel getLevel() { return level; }
    public void setLevel(LogLevel level) { this.level = level; }

    public LogPriority getPriority() { return priority; }
    public void setPriority(LogPriority priority) { this.priority = priority; }

    public LogStatus getStatus() { return status; }
    public void setStatus(LogStatus status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public String getParentLogId() { return parentLogId; }
    public void setParentLogId(String parentLogId) { this.parentLogId = parentLogId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getMetadataValue(String key, String defaultValue) {
        if (metadata == null) return defaultValue;
        Object value = metadata.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public long getMetadataValue(String key, long defaultValue) {
        if (metadata == null) return defaultValue;
        Object value = metadata.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    public double getMetadataValue(String key, double defaultValue) {
        if (metadata == null) return defaultValue;
        Object value = metadata.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
}
