package net.ooder.config.scene.server;

import java.io.Serializable;

public class HealthCheckConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = true;
    private long interval = 60000L;
    private long timeout = 5000L;
    private int failureThreshold = 3;
    private int successThreshold = 2;
    
    public HealthCheckConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public void setInterval(long interval) {
        this.interval = interval;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public int getFailureThreshold() {
        return failureThreshold;
    }
    
    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }
    
    public int getSuccessThreshold() {
        return successThreshold;
    }
    
    public void setSuccessThreshold(int successThreshold) {
        this.successThreshold = successThreshold;
    }
}
