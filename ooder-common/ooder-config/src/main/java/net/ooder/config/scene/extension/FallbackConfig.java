package net.ooder.config.scene.extension;

import java.io.Serializable;

public class FallbackConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = false;
    private String dataPath = "./data/fallback";
    private boolean autoReload = true;
    private long reloadInterval = 60000L;
    private boolean cacheEnabled = true;
    private long cacheExpireTime = 3600000L;
    
    public FallbackConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getDataPath() {
        return dataPath;
    }
    
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    
    public boolean isAutoReload() {
        return autoReload;
    }
    
    public void setAutoReload(boolean autoReload) {
        this.autoReload = autoReload;
    }
    
    public long getReloadInterval() {
        return reloadInterval;
    }
    
    public void setReloadInterval(long reloadInterval) {
        this.reloadInterval = reloadInterval;
    }
    
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
    
    public long getCacheExpireTime() {
        return cacheExpireTime;
    }
    
    public void setCacheExpireTime(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
    }
    
    @Override
    public String toString() {
        return "FallbackConfig{" +
            "enabled=" + enabled +
            ", dataPath='" + dataPath + '\'' +
            ", autoReload=" + autoReload +
            '}';
    }
}
