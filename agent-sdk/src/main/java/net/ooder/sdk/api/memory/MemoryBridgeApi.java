package net.ooder.sdk.api.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MemoryBridgeApi {
    
    void store(String key, Object value);
    
    void store(String key, Object value, long ttlMillis);
    
    Optional<Object> retrieve(String key);
    
    <T> Optional<T> retrieve(String key, Class<T> type);
    
    void delete(String key);
    
    boolean exists(String key);
    
    void setExpiry(String key, long ttlMillis);
    
    CompletableFuture<Void> storeAsync(String key, Object value);
    
    CompletableFuture<Optional<Object>> retrieveAsync(String key);
    
    List<String> listKeys(String pattern);
    
    Map<String, Object> listAll();
    
    void clear();
    
    long size();
    
    MemoryStats getStats();
    
    class MemoryStats {
        private long totalEntries;
        private long totalSize;
        private long hitCount;
        private long missCount;
        private double hitRate;
        
        public long getTotalEntries() { return totalEntries; }
        public void setTotalEntries(long totalEntries) { this.totalEntries = totalEntries; }
        
        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
        
        public long getHitCount() { return hitCount; }
        public void setHitCount(long hitCount) { this.hitCount = hitCount; }
        
        public long getMissCount() { return missCount; }
        public void setMissCount(long missCount) { this.missCount = missCount; }
        
        public double getHitRate() { return hitRate; }
        public void setHitRate(double hitRate) { this.hitRate = hitRate; }
    }
}
