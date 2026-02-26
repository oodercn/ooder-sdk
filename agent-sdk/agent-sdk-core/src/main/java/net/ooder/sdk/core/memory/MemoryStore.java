package net.ooder.sdk.core.memory;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemoryStore {
    
    String store(MemoryEntry entry);
    
    Optional<MemoryEntry> retrieve(String memoryId);
    
    List<MemoryEntry> search(MemoryQuery query);
    
    List<MemoryEntry> searchSimilar(float[] embedding, int limit);
    
    void update(String memoryId, MemoryEntry entry);
    
    void delete(String memoryId);
    
    void clear();
    
    long count();
    
    MemoryStats getStats();
    
    class MemoryEntry {
        private String memoryId;
        private String content;
        private float[] embedding;
        private Map<String, Object> metadata;
        private Instant createdAt;
        private Instant expiresAt;
        private float importance;
        private String source;
        private List<String> tags;
        
        public String getMemoryId() { return memoryId; }
        public void setMemoryId(String memoryId) { this.memoryId = memoryId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public float[] getEmbedding() { return embedding; }
        public void setEmbedding(float[] embedding) { this.embedding = embedding; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
        
        public float getImportance() { return importance; }
        public void setImportance(float importance) { this.importance = importance; }
        
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public static MemoryEntry of(String content) {
            MemoryEntry entry = new MemoryEntry();
            entry.setContent(content);
            entry.setCreatedAt(Instant.now());
            entry.setImportance(0.5f);
            return entry;
        }
    }
    
    class MemoryQuery {
        private String content;
        private String source;
        private List<String> tags;
        private Instant startTime;
        private Instant endTime;
        private Float minImportance;
        private int limit = 10;
        private int offset = 0;
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        
        public Float getMinImportance() { return minImportance; }
        public void setMinImportance(Float minImportance) { this.minImportance = minImportance; }
        
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        
        public int getOffset() { return offset; }
        public void setOffset(int offset) { this.offset = offset; }
        
        public static MemoryQuery create() { return new MemoryQuery(); }
        
        public MemoryQuery withContent(String content) { this.content = content; return this; }
        public MemoryQuery withLimit(int limit) { this.limit = limit; return this; }
    }
    
    class MemoryStats {
        private long totalMemories;
        private long totalSize;
        private long expiredMemories;
        private Map<String, Long> memoriesBySource;
        
        public long getTotalMemories() { return totalMemories; }
        public void setTotalMemories(long totalMemories) { this.totalMemories = totalMemories; }
        
        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
        
        public long getExpiredMemories() { return expiredMemories; }
        public void setExpiredMemories(long expiredMemories) { this.expiredMemories = expiredMemories; }
        
        public Map<String, Long> getMemoriesBySource() { return memoriesBySource; }
        public void setMemoriesBySource(Map<String, Long> memoriesBySource) { this.memoriesBySource = memoriesBySource; }
    }
}
