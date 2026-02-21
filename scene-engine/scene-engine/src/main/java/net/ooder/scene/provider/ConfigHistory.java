package net.ooder.scene.provider;

import java.util.List;

/**
 * 配置历史
 */
public class ConfigHistory {
    private String historyId;
    private String key;
    private String oldValue;
    private String newValue;
    private String changedBy;
    private long changedAt;
    private String reason;

    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public long getChangedAt() { return changedAt; }
    public void setChangedAt(long changedAt) { this.changedAt = changedAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
