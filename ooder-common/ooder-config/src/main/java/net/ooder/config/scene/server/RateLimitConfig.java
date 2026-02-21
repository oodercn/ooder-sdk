package net.ooder.config.scene.server;

import java.io.Serializable;

public class RateLimitConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = false;
    private int requestsPerSecond = 100;
    private int burstSize = 200;
    
    public RateLimitConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getRequestsPerSecond() {
        return requestsPerSecond;
    }
    
    public void setRequestsPerSecond(int requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
    }
    
    public int getBurstSize() {
        return burstSize;
    }
    
    public void setBurstSize(int burstSize) {
        this.burstSize = burstSize;
    }
}
