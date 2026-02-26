package net.ooder.sdk.core.memory.impl;

import net.ooder.sdk.core.memory.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMemoryStore implements MemoryStore {
    
    private static final Logger log = LoggerFactory.getLogger(InMemoryMemoryStore.class);
    
    private final Map<String, MemoryEntry> memories = new ConcurrentHashMap<>();
    private final Map<String, float[]> embeddings = new ConcurrentHashMap<>();
    
    @Override
    public String store(MemoryEntry entry) {
        if (entry.getMemoryId() == null) {
            entry.setMemoryId("mem-" + System.currentTimeMillis());
        }
        if (entry.getCreatedAt() == null) {
            entry.setCreatedAt(Instant.now());
        }
        
        memories.put(entry.getMemoryId(), entry);
        
        if (entry.getEmbedding() != null) {
            embeddings.put(entry.getMemoryId(), entry.getEmbedding());
        }
        
        log.debug("Memory stored: {}", entry.getMemoryId());
        return entry.getMemoryId();
    }
    
    @Override
    public Optional<MemoryEntry> retrieve(String memoryId) {
        MemoryEntry entry = memories.get(memoryId);
        if (entry != null && entry.getExpiresAt() != null && entry.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.ofNullable(entry);
    }
    
    @Override
    public List<MemoryEntry> search(MemoryQuery query) {
        return memories.values().stream()
            .filter(entry -> matchesQuery(entry, query))
            .sorted((a, b) -> Double.compare(b.getImportance(), a.getImportance()))
            .skip(query.getOffset())
            .limit(query.getLimit())
            .collect(Collectors.toList());
    }
    
    private boolean matchesQuery(MemoryEntry entry, MemoryQuery query) {
        if (entry.getExpiresAt() != null && entry.getExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        
        if (query.getContent() != null && !query.getContent().isEmpty()) {
            if (entry.getContent() == null || !entry.getContent().toLowerCase().contains(query.getContent().toLowerCase())) {
                return false;
            }
        }
        
        if (query.getSource() != null && !query.getSource().equals(entry.getSource())) {
            return false;
        }
        
        if (query.getMinImportance() != null && entry.getImportance() < query.getMinImportance()) {
            return false;
        }
        
        if (query.getStartTime() != null && entry.getCreatedAt().isBefore(query.getStartTime())) {
            return false;
        }
        
        if (query.getEndTime() != null && entry.getCreatedAt().isAfter(query.getEndTime())) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<MemoryEntry> searchSimilar(float[] embedding, int limit) {
        return memories.values().stream()
            .filter(entry -> entry.getEmbedding() != null)
            .map(entry -> new AbstractMap.SimpleEntry<>(entry, cosineSimilarity(embedding, entry.getEmbedding())))
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) return 0;
        
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0 || normB == 0) return 0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    @Override
    public void update(String memoryId, MemoryEntry entry) {
        if (memories.containsKey(memoryId)) {
            entry.setMemoryId(memoryId);
            memories.put(memoryId, entry);
            if (entry.getEmbedding() != null) {
                embeddings.put(memoryId, entry.getEmbedding());
            }
            log.debug("Memory updated: {}", memoryId);
        }
    }
    
    @Override
    public void delete(String memoryId) {
        memories.remove(memoryId);
        embeddings.remove(memoryId);
        log.debug("Memory deleted: {}", memoryId);
    }
    
    @Override
    public void clear() {
        memories.clear();
        embeddings.clear();
        log.info("All memories cleared");
    }
    
    @Override
    public long count() {
        return memories.size();
    }
    
    @Override
    public MemoryStats getStats() {
        MemoryStats stats = new MemoryStats();
        stats.setTotalMemories(memories.size());
        stats.setTotalSize(memories.values().stream()
            .mapToLong(e -> e.getContent() != null ? e.getContent().length() : 0)
            .sum());
        stats.setExpiredMemories((int) memories.values().stream()
            .filter(e -> e.getExpiresAt() != null && e.getExpiresAt().isBefore(Instant.now()))
            .count());
        
        Map<String, Long> bySource = new HashMap<>();
        for (MemoryEntry entry : memories.values()) {
            String source = entry.getSource() != null ? entry.getSource() : "unknown";
            bySource.merge(source, 1L, Long::sum);
        }
        stats.setMemoriesBySource(bySource);
        
        return stats;
    }
}
