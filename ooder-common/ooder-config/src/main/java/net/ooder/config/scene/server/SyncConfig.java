package net.ooder.config.scene.server;

import java.io.Serializable;

public class SyncConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = false;
    private String mode = "push";
    private long interval = 60000L;
    private int batchSize = 100;
    
    public SyncConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public void setInterval(long interval) {
        this.interval = interval;
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
    
    public boolean isPush() {
        return "push".equals(mode);
    }
    
    public boolean isPull() {
        return "pull".equals(mode);
    }
}
