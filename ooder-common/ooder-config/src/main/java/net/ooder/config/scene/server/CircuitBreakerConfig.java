package net.ooder.config.scene.server;

import java.io.Serializable;

public class CircuitBreakerConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = false;
    private int failureThreshold = 5;
    private long resetTimeout = 60000L;
    private int halfOpenRequests = 3;
    
    public CircuitBreakerConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getFailureThreshold() {
        return failureThreshold;
    }
    
    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }
    
    public long getResetTimeout() {
        return resetTimeout;
    }
    
    public void setResetTimeout(long resetTimeout) {
        this.resetTimeout = resetTimeout;
    }
    
    public int getHalfOpenRequests() {
        return halfOpenRequests;
    }
    
    public void setHalfOpenRequests(int halfOpenRequests) {
        this.halfOpenRequests = halfOpenRequests;
    }
}
