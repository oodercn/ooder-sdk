package net.ooder.config.scene.client;

import java.io.Serializable;

public class OfflineConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = false;
    private String storagePath = "./offline-data";
    private boolean syncOnReconnect = true;
    private int maxQueueSize = 1000;
    
    public OfflineConfig() {
    }
    
    public OfflineConfig(boolean enabled) {
        this.enabled = enabled;
    }
    
    public static OfflineConfigBuilder builder() {
        return new OfflineConfigBuilder();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getStoragePath() {
        return storagePath;
    }
    
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
    
    public boolean isSyncOnReconnect() {
        return syncOnReconnect;
    }
    
    public void setSyncOnReconnect(boolean syncOnReconnect) {
        this.syncOnReconnect = syncOnReconnect;
    }
    
    public int getMaxQueueSize() {
        return maxQueueSize;
    }
    
    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }
    
    public static class OfflineConfigBuilder {
        private OfflineConfig config = new OfflineConfig();
        
        public OfflineConfigBuilder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }
        
        public OfflineConfigBuilder storagePath(String storagePath) {
            config.storagePath = storagePath;
            return this;
        }
        
        public OfflineConfigBuilder syncOnReconnect(boolean syncOnReconnect) {
            config.syncOnReconnect = syncOnReconnect;
            return this;
        }
        
        public OfflineConfigBuilder maxQueueSize(int maxQueueSize) {
            config.maxQueueSize = maxQueueSize;
            return this;
        }
        
        public OfflineConfig build() {
            return config;
        }
    }
}
