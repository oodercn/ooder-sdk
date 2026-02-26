package net.ooder.sdk.llm.memory.model;

import java.time.Instant;

public class SyncResult {
    private String syncId;
    private Integer syncedRecords;
    private Instant lastSyncTime;
    private String status;
    private String message;

    public SyncResult() {
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public Integer getSyncedRecords() {
        return syncedRecords;
    }

    public void setSyncedRecords(Integer syncedRecords) {
        this.syncedRecords = syncedRecords;
    }

    public Instant getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Instant lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
