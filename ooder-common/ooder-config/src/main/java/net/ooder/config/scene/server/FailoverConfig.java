package net.ooder.config.scene.server;

import java.io.Serializable;

public class FailoverConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = false;
    private String fallbackDataSource;
    private int retryAttempts = 3;
    private long retryDelay = 1000L;
    
    public FailoverConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getFallbackDataSource() {
        return fallbackDataSource;
    }
    
    public void setFallbackDataSource(String fallbackDataSource) {
        this.fallbackDataSource = fallbackDataSource;
    }
    
    public int getRetryAttempts() {
        return retryAttempts;
    }
    
    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }
    
    public long getRetryDelay() {
        return retryDelay;
    }
    
    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }
}
