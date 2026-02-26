package net.ooder.sdk.core.fallback;

import java.util.List;

public interface FallbackStrategy {
    
    String selectImplementation(String interfaceId, List<String> availableImplementations);
    
    boolean shouldFallback(String interfaceId, String currentImplementation, Throwable error);
    
    String getFallbackImplementation(String interfaceId);
    
    void recordSuccess(String interfaceId, String implementation);
    
    void recordFailure(String interfaceId, String implementation, Throwable error);
    
    FallbackStats getStats(String interfaceId);
    
    void resetStats(String interfaceId);
    
    void setMaxFailures(int maxFailures);
    
    int getMaxFailures();
    
    void setRecoveryTime(long recoveryTimeMs);
    
    long getRecoveryTime();
    
    class FallbackStats {
        private String interfaceId;
        private String currentImplementation;
        private int totalRequests;
        private int successCount;
        private int failureCount;
        private int fallbackCount;
        private long lastFailureTime;
        private long lastSuccessTime;
        
        public String getInterfaceId() { return interfaceId; }
        public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
        
        public String getCurrentImplementation() { return currentImplementation; }
        public void setCurrentImplementation(String currentImplementation) { this.currentImplementation = currentImplementation; }
        
        public int getTotalRequests() { return totalRequests; }
        public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        
        public int getFallbackCount() { return fallbackCount; }
        public void setFallbackCount(int fallbackCount) { this.fallbackCount = fallbackCount; }
        
        public long getLastFailureTime() { return lastFailureTime; }
        public void setLastFailureTime(long lastFailureTime) { this.lastFailureTime = lastFailureTime; }
        
        public long getLastSuccessTime() { return lastSuccessTime; }
        public void setLastSuccessTime(long lastSuccessTime) { this.lastSuccessTime = lastSuccessTime; }
        
        public double getSuccessRate() {
            return totalRequests > 0 ? (double) successCount / totalRequests : 0;
        }
        
        public double getFailureRate() {
            return totalRequests > 0 ? (double) failureCount / totalRequests : 0;
        }
    }
}
