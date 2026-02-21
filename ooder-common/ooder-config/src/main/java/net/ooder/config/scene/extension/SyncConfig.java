package net.ooder.config.scene.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SyncConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final String TYPE_FULL = "full";
    public static final String TYPE_INCREMENTAL = "incremental";
    
    private boolean enabled = false;
    private String schedule = "0 0 2 * * ?";
    private String syncType = TYPE_INCREMENTAL;
    private List<String> scope = new ArrayList<String>();
    private int batchSize = 100;
    private int retryCount = 3;
    private long retryInterval = 5000L;
    private boolean notifyOnSuccess = false;
    private boolean notifyOnError = true;
    private long lastSyncTime;
    private String lastSyncStatus;
    
    public SyncConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    public String getSyncType() {
        return syncType;
    }
    
    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }
    
    public List<String> getScope() {
        return scope;
    }
    
    public void setScope(List<String> scope) {
        this.scope = scope != null ? scope : new ArrayList<String>();
    }
    
    public void addScope(String scopeItem) {
        this.scope.add(scopeItem);
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public long getRetryInterval() {
        return retryInterval;
    }
    
    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }
    
    public boolean isNotifyOnSuccess() {
        return notifyOnSuccess;
    }
    
    public void setNotifyOnSuccess(boolean notifyOnSuccess) {
        this.notifyOnSuccess = notifyOnSuccess;
    }
    
    public boolean isNotifyOnError() {
        return notifyOnError;
    }
    
    public void setNotifyOnError(boolean notifyOnError) {
        this.notifyOnError = notifyOnError;
    }
    
    public long getLastSyncTime() {
        return lastSyncTime;
    }
    
    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
    
    public String getLastSyncStatus() {
        return lastSyncStatus;
    }
    
    public void setLastSyncStatus(String lastSyncStatus) {
        this.lastSyncStatus = lastSyncStatus;
    }
    
    public boolean isFullSync() {
        return TYPE_FULL.equals(syncType);
    }
    
    public boolean isIncrementalSync() {
        return TYPE_INCREMENTAL.equals(syncType);
    }
    
    public static SyncConfig full() {
        SyncConfig config = new SyncConfig();
        config.setSyncType(TYPE_FULL);
        return config;
    }
    
    public static SyncConfig incremental() {
        SyncConfig config = new SyncConfig();
        config.setSyncType(TYPE_INCREMENTAL);
        return config;
    }
    
    @Override
    public String toString() {
        return "SyncConfig{" +
            "enabled=" + enabled +
            ", schedule='" + schedule + '\'' +
            ", syncType='" + syncType + '\'' +
            ", batchSize=" + batchSize +
            '}';
    }
}
