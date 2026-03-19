package net.ooder.scene.bridge.circuitbreaker;

/**
 * 熔断器配置
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class CircuitBreakerConfig {
    
    private int failureThreshold = 5;
    private long openToHalfOpenTimeoutMs = 30000;
    private int halfOpenSuccessThreshold = 3;
    private boolean fallbackEnabled = true;
    
    public CircuitBreakerConfig() {
    }
    
    public CircuitBreakerConfig(int failureThreshold, long openToHalfOpenTimeoutMs, 
                                  int halfOpenSuccessThreshold, boolean fallbackEnabled) {
        this.failureThreshold = failureThreshold;
        this.openToHalfOpenTimeoutMs = openToHalfOpenTimeoutMs;
        this.halfOpenSuccessThreshold = halfOpenSuccessThreshold;
        this.fallbackEnabled = fallbackEnabled;
    }
    
    public static CircuitBreakerConfig defaultConfig() {
        return new CircuitBreakerConfig();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public int getFailureThreshold() {
        return failureThreshold;
    }
    
    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }
    
    public long getOpenToHalfOpenTimeoutMs() {
        return openToHalfOpenTimeoutMs;
    }
    
    public void setOpenToHalfOpenTimeoutMs(long openToHalfOpenTimeoutMs) {
        this.openToHalfOpenTimeoutMs = openToHalfOpenTimeoutMs;
    }
    
    public int getHalfOpenSuccessThreshold() {
        return halfOpenSuccessThreshold;
    }
    
    public void setHalfOpenSuccessThreshold(int halfOpenSuccessThreshold) {
        this.halfOpenSuccessThreshold = halfOpenSuccessThreshold;
    }
    
    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }
    
    public void setFallbackEnabled(boolean fallbackEnabled) {
        this.fallbackEnabled = fallbackEnabled;
    }
    
    public static class Builder {
        private int failureThreshold = 5;
        private long openToHalfOpenTimeoutMs = 30000;
        private int halfOpenSuccessThreshold = 3;
        private boolean fallbackEnabled = true;
        
        public Builder failureThreshold(int threshold) {
            this.failureThreshold = threshold;
            return this;
        }
        
        public Builder openToHalfOpenTimeoutMs(long timeoutMs) {
            this.openToHalfOpenTimeoutMs = timeoutMs;
            return this;
        }
        
        public Builder halfOpenSuccessThreshold(int threshold) {
            this.halfOpenSuccessThreshold = threshold;
            return this;
        }
        
        public Builder fallbackEnabled(boolean enabled) {
            this.fallbackEnabled = enabled;
            return this;
        }
        
        public CircuitBreakerConfig build() {
            return new CircuitBreakerConfig(failureThreshold, openToHalfOpenTimeoutMs, 
                halfOpenSuccessThreshold, fallbackEnabled);
        }
    }
}
