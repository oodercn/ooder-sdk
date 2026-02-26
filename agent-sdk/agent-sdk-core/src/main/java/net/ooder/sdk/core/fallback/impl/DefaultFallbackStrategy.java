package net.ooder.sdk.core.fallback.impl;

import net.ooder.sdk.core.fallback.FallbackStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultFallbackStrategy implements FallbackStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultFallbackStrategy.class);
    
    private static final int DEFAULT_MAX_FAILURES = 3;
    private static final long DEFAULT_RECOVERY_TIME = 60000;
    
    private final Map<String, FallbackStats> statsMap = new ConcurrentHashMap<>();
    private final Map<String, List<String>> fallbackChains = new ConcurrentHashMap<>();
    private final Map<String, Integer> failureCounts = new ConcurrentHashMap<>();
    
    private volatile int maxFailures = DEFAULT_MAX_FAILURES;
    private volatile long recoveryTime = DEFAULT_RECOVERY_TIME;
    
    public DefaultFallbackStrategy() {
    }
    
    @Override
    public String selectImplementation(String interfaceId, List<String> availableImplementations) {
        if (availableImplementations == null || availableImplementations.isEmpty()) {
            return null;
        }
        
        FallbackStats stats = getOrCreateStats(interfaceId);
        
        for (String impl : availableImplementations) {
            if (isHealthy(interfaceId, impl)) {
                stats.setCurrentImplementation(impl);
                return impl;
            }
        }
        
        log.warn("No healthy implementation found for interface: {}", interfaceId);
        return availableImplementations.get(0);
    }
    
    @Override
    public boolean shouldFallback(String interfaceId, String currentImplementation, Throwable error) {
        if (currentImplementation == null) {
            return true;
        }
        
        String key = buildKey(interfaceId, currentImplementation);
        Integer failures = failureCounts.get(key);
        
        if (failures != null && failures >= maxFailures) {
            log.info("Fallback triggered for {} -> {}: max failures ({}) reached", 
                interfaceId, currentImplementation, failures);
            return true;
        }
        
        if (error != null && isCriticalError(error)) {
            log.info("Fallback triggered for {} -> {}: critical error: {}", 
                interfaceId, currentImplementation, error.getClass().getSimpleName());
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getFallbackImplementation(String interfaceId) {
        List<String> chain = fallbackChains.get(interfaceId);
        if (chain == null || chain.isEmpty()) {
            return null;
        }
        
        FallbackStats stats = getOrCreateStats(interfaceId);
        String current = stats.getCurrentImplementation();
        
        if (current == null) {
            return chain.get(0);
        }
        
        int currentIndex = chain.indexOf(current);
        if (currentIndex >= 0 && currentIndex < chain.size() - 1) {
            return chain.get(currentIndex + 1);
        }
        
        return null;
    }
    
    @Override
    public void recordSuccess(String interfaceId, String implementation) {
        FallbackStats stats = getOrCreateStats(interfaceId);
        stats.setTotalRequests(stats.getTotalRequests() + 1);
        stats.setSuccessCount(stats.getSuccessCount() + 1);
        stats.setLastSuccessTime(System.currentTimeMillis());
        
        String key = buildKey(interfaceId, implementation);
        failureCounts.remove(key);
        
        log.debug("Success recorded for {} -> {}", interfaceId, implementation);
    }
    
    @Override
    public void recordFailure(String interfaceId, String implementation, Throwable error) {
        FallbackStats stats = getOrCreateStats(interfaceId);
        stats.setTotalRequests(stats.getTotalRequests() + 1);
        stats.setFailureCount(stats.getFailureCount() + 1);
        stats.setLastFailureTime(System.currentTimeMillis());
        
        String key = buildKey(interfaceId, implementation);
        failureCounts.merge(key, 1, Integer::sum);
        
        log.debug("Failure recorded for {} -> {}: {}", interfaceId, implementation, 
            error != null ? error.getMessage() : "unknown");
    }
    
    @Override
    public FallbackStats getStats(String interfaceId) {
        return getOrCreateStats(interfaceId);
    }
    
    @Override
    public void resetStats(String interfaceId) {
        statsMap.remove(interfaceId);
        
        for (String key : new ArrayList<>(failureCounts.keySet())) {
            if (key.startsWith(interfaceId + ":")) {
                failureCounts.remove(key);
            }
        }
        
        log.info("Stats reset for interface: {}", interfaceId);
    }
    
    @Override
    public void setMaxFailures(int maxFailures) {
        this.maxFailures = Math.max(1, maxFailures);
    }
    
    @Override
    public int getMaxFailures() {
        return maxFailures;
    }
    
    @Override
    public void setRecoveryTime(long recoveryTimeMs) {
        this.recoveryTime = Math.max(1000, recoveryTimeMs);
    }
    
    @Override
    public long getRecoveryTime() {
        return recoveryTime;
    }
    
    public void setFallbackChain(String interfaceId, List<String> implementations) {
        if (implementations != null) {
            fallbackChains.put(interfaceId, new ArrayList<>(implementations));
        }
    }
    
    public List<String> getFallbackChain(String interfaceId) {
        return fallbackChains.get(interfaceId);
    }
    
    private FallbackStats getOrCreateStats(String interfaceId) {
        return statsMap.computeIfAbsent(interfaceId, id -> {
            FallbackStats stats = new FallbackStats();
            stats.setInterfaceId(id);
            return stats;
        });
    }
    
    private boolean isHealthy(String interfaceId, String implementation) {
        String key = buildKey(interfaceId, implementation);
        Integer failures = failureCounts.get(key);
        
        if (failures == null || failures < maxFailures) {
            return true;
        }
        
        FallbackStats stats = statsMap.get(interfaceId);
        if (stats != null && stats.getLastFailureTime() > 0) {
            long elapsed = System.currentTimeMillis() - stats.getLastFailureTime();
            if (elapsed >= recoveryTime) {
                failureCounts.remove(key);
                log.info("Implementation recovered: {} -> {}", interfaceId, implementation);
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isCriticalError(Throwable error) {
        if (error == null) {
            return false;
        }
        
        String className = error.getClass().getName();
        return className.contains("OutOfMemoryError") ||
               className.contains("StackOverflowError") ||
               className.contains("LinkageError") ||
               className.contains("ClassNotFoundException") ||
               className.contains("NoClassDefFoundError");
    }
    
    private String buildKey(String interfaceId, String implementation) {
        return interfaceId + ":" + implementation;
    }
}
