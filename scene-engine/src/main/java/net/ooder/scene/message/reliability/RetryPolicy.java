package net.ooder.scene.message.reliability;

/**
 * 重试策略
 *
 * <p>定义消息重试的策略参数</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class RetryPolicy {
    
    private int maxAttempts = 3;
    private long initialDelay = 1000L;
    private long maxDelay = 30000L;
    private double multiplier = 2.0;
    private RetryStrategy strategy = RetryStrategy.EXPONENTIAL_BACKOFF;
    
    public RetryPolicy() {
    }
    
    public RetryPolicy(int maxAttempts, RetryStrategy strategy) {
        this.maxAttempts = maxAttempts;
        this.strategy = strategy;
    }
    
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    
    public long getInitialDelay() {
        return initialDelay;
    }
    
    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }
    
    public long getMaxDelay() {
        return maxDelay;
    }
    
    public void setMaxDelay(long maxDelay) {
        this.maxDelay = maxDelay;
    }
    
    public double getMultiplier() {
        return multiplier;
    }
    
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
    
    public RetryStrategy getStrategy() {
        return strategy;
    }
    
    public void setStrategy(RetryStrategy strategy) {
        this.strategy = strategy;
    }
    
    public long calculateDelay(int attemptNumber) {
        if (attemptNumber <= 0) {
            return 0;
        }
        
        long delay;
        switch (strategy) {
            case IMMEDIATE:
                delay = 0;
                break;
            case FIXED_INTERVAL:
                delay = initialDelay;
                break;
            case LINEAR_BACKOFF:
                delay = initialDelay * attemptNumber;
                break;
            case EXPONENTIAL_BACKOFF:
            default:
                delay = (long) (initialDelay * Math.pow(multiplier, attemptNumber - 1));
                break;
        }
        
        return Math.min(delay, maxDelay);
    }
    
    public boolean shouldRetry(int currentAttempts) {
        return currentAttempts < maxAttempts;
    }
    
    public static RetryPolicy immediate(int maxAttempts) {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxAttempts(maxAttempts);
        policy.setStrategy(RetryStrategy.IMMEDIATE);
        return policy;
    }
    
    public static RetryPolicy fixedInterval(int maxAttempts, long intervalMs) {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxAttempts(maxAttempts);
        policy.setInitialDelay(intervalMs);
        policy.setStrategy(RetryStrategy.FIXED_INTERVAL);
        return policy;
    }
    
    public static RetryPolicy exponentialBackoff(int maxAttempts, long initialDelayMs) {
        RetryPolicy policy = new RetryPolicy();
        policy.setMaxAttempts(maxAttempts);
        policy.setInitialDelay(initialDelayMs);
        policy.setStrategy(RetryStrategy.EXPONENTIAL_BACKOFF);
        return policy;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final RetryPolicy policy = new RetryPolicy();
        
        public Builder maxAttempts(int maxAttempts) {
            policy.setMaxAttempts(maxAttempts);
            return this;
        }
        
        public Builder initialDelay(long initialDelay) {
            policy.setInitialDelay(initialDelay);
            return this;
        }
        
        public Builder maxDelay(long maxDelay) {
            policy.setMaxDelay(maxDelay);
            return this;
        }
        
        public Builder multiplier(double multiplier) {
            policy.setMultiplier(multiplier);
            return this;
        }
        
        public Builder strategy(RetryStrategy strategy) {
            policy.setStrategy(strategy);
            return this;
        }
        
        public RetryPolicy build() {
            return policy;
        }
    }
    
    @Override
    public String toString() {
        return "RetryPolicy{" +
                "maxAttempts=" + maxAttempts +
                ", initialDelay=" + initialDelay +
                ", maxDelay=" + maxDelay +
                ", strategy=" + strategy +
                '}';
    }
}
