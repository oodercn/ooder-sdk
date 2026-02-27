package net.ooder.scene.provider;

import java.util.Map;

/**
 * 日志条目
 */
public class LogEntry {
    private String logId;
    private String level;
    private String message;
    private String source;
    private long timestamp;
    private String threadName;
    private String loggerName;
    private String exception;
    private Map<String, Object> details;

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getThreadName() { return threadName; }
    public void setThreadName(String threadName) { this.threadName = threadName; }
    public String getLoggerName() { return loggerName; }
    public void setLoggerName(String loggerName) { this.loggerName = loggerName; }
    public String getException() { return exception; }
    public void setException(String exception) { this.exception = exception; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
