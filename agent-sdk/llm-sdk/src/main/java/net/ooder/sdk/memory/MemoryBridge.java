package net.ooder.sdk.memory;

import java.util.List;
import java.util.Map;

public interface MemoryBridge {
    
    void remember(String key, Object value);
    
    void remember(String key, Object value, long ttlMillis);
    
    Object recall(String key);
    
    <T> T recall(String key, Class<T> type);
    
    void forget(String key);
    
    boolean knows(String key);
    
    void associate(String key1, String key2);
    
    List<String> getAssociations(String key);
    
    void setContext(String contextId, Map<String, Object> context);
    
    Map<String, Object> getContext(String contextId);
    
    void mergeContext(String contextId, Map<String, Object> context);
    
    void clearContext(String contextId);
    
    List<MemoryStore.MemoryEntry> searchMemories(String query, int limit);
    
    String storeMemory(String content, Map<String, Object> metadata);
    
    MemoryStore.MemoryEntry retrieveMemory(String memoryId);
    
    void addMemoryListener(MemoryListener listener);
    
    void removeMemoryListener(MemoryListener listener);
    
    MemoryBridgeStats getStats();
    
    class MemoryBridgeStats {
        private long shortTermCount;
        private long longTermCount;
        private long contextCount;
        private long associationCount;
        
        public long getShortTermCount() { return shortTermCount; }
        public void setShortTermCount(long shortTermCount) { this.shortTermCount = shortTermCount; }
        
        public long getLongTermCount() { return longTermCount; }
        public void setLongTermCount(long longTermCount) { this.longTermCount = longTermCount; }
        
        public long getContextCount() { return contextCount; }
        public void setContextCount(long contextCount) { this.contextCount = contextCount; }
        
        public long getAssociationCount() { return associationCount; }
        public void setAssociationCount(long associationCount) { this.associationCount = associationCount; }
    }
    
    interface MemoryListener {
        void onRemember(String key, Object value);
        void onRecall(String key, Object value);
        void onForget(String key);
    }
}
