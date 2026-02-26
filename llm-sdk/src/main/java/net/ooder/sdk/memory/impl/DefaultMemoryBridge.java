package net.ooder.sdk.memory.impl;

import net.ooder.sdk.memory.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultMemoryBridge implements MemoryBridge {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultMemoryBridge.class);
    
    private final Map<String, Object> shortTermMemory = new ConcurrentHashMap<>();
    private final Map<String, Long> shortTermExpiry = new ConcurrentHashMap<>();
    private final MemoryStore longTermMemory;
    private final Map<String, Map<String, Object>> contexts = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> associations = new ConcurrentHashMap<>();
    private final List<MemoryListener> listeners = new CopyOnWriteArrayList<>();
    
    public DefaultMemoryBridge() {
        this.longTermMemory = new InMemoryMemoryStore();
    }
    
    public DefaultMemoryBridge(MemoryStore longTermMemory) {
        this.longTermMemory = longTermMemory != null ? longTermMemory : new InMemoryMemoryStore();
    }
    
    @Override
    public void remember(String key, Object value) {
        remember(key, value, -1);
    }
    
    @Override
    public void remember(String key, Object value, long ttlMillis) {
        shortTermMemory.put(key, value);
        if (ttlMillis > 0) {
            shortTermExpiry.put(key, System.currentTimeMillis() + ttlMillis);
        } else {
            shortTermExpiry.remove(key);
        }
        
        notifyRemember(key, value);
        log.debug("Remembered: {} = {}", key, value);
    }
    
    @Override
    public Object recall(String key) {
        if (isExpired(key)) {
            shortTermMemory.remove(key);
            shortTermExpiry.remove(key);
            return null;
        }
        
        Object value = shortTermMemory.get(key);
        if (value != null) {
            notifyRecall(key, value);
        }
        return value;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T recall(String key, Class<T> type) {
        Object value = recall(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    @Override
    public void forget(String key) {
        shortTermMemory.remove(key);
        shortTermExpiry.remove(key);
        notifyForget(key);
        log.debug("Forgot: {}", key);
    }
    
    @Override
    public boolean knows(String key) {
        if (isExpired(key)) {
            shortTermMemory.remove(key);
            shortTermExpiry.remove(key);
            return false;
        }
        return shortTermMemory.containsKey(key);
    }
    
    private boolean isExpired(String key) {
        Long expiry = shortTermExpiry.get(key);
        return expiry != null && System.currentTimeMillis() > expiry;
    }
    
    @Override
    public void associate(String key1, String key2) {
        associations.computeIfAbsent(key1, k -> ConcurrentHashMap.newKeySet()).add(key2);
        associations.computeIfAbsent(key2, k -> ConcurrentHashMap.newKeySet()).add(key1);
        log.debug("Associated: {} <-> {}", key1, key2);
    }
    
    @Override
    public List<String> getAssociations(String key) {
        Set<String> associated = associations.get(key);
        return associated != null ? new ArrayList<>(associated) : Collections.emptyList();
    }
    
    @Override
    public void setContext(String contextId, Map<String, Object> context) {
        contexts.put(contextId, new ConcurrentHashMap<>(context));
        log.debug("Context set: {}", contextId);
    }
    
    @Override
    public Map<String, Object> getContext(String contextId) {
        return new HashMap<>(contexts.getOrDefault(contextId, Collections.emptyMap()));
    }
    
    @Override
    public void mergeContext(String contextId, Map<String, Object> context) {
        contexts.computeIfAbsent(contextId, k -> new ConcurrentHashMap<>()).putAll(context);
        log.debug("Context merged: {}", contextId);
    }
    
    @Override
    public void clearContext(String contextId) {
        contexts.remove(contextId);
        log.debug("Context cleared: {}", contextId);
    }
    
    @Override
    public List<MemoryStore.MemoryEntry> searchMemories(String query, int limit) {
        MemoryStore.MemoryQuery q = MemoryStore.MemoryQuery.create()
            .withContent(query)
            .withLimit(limit);
        return longTermMemory.search(q);
    }
    
    @Override
    public String storeMemory(String content, Map<String, Object> metadata) {
        MemoryStore.MemoryEntry entry = MemoryStore.MemoryEntry.of(content);
        if (metadata != null) {
            entry.setMetadata(metadata);
        }
        return longTermMemory.store(entry);
    }
    
    @Override
    public MemoryStore.MemoryEntry retrieveMemory(String memoryId) {
        return longTermMemory.retrieve(memoryId).orElse(null);
    }
    
    @Override
    public void addMemoryListener(MemoryListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeMemoryListener(MemoryListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public MemoryBridgeStats getStats() {
        MemoryBridgeStats stats = new MemoryBridgeStats();
        stats.setShortTermCount(shortTermMemory.size());
        stats.setLongTermCount(longTermMemory.count());
        stats.setContextCount(contexts.size());
        stats.setAssociationCount(associations.values().stream().mapToInt(Set::size).sum() / 2);
        return stats;
    }
    
    private void notifyRemember(String key, Object value) {
        for (MemoryListener listener : listeners) {
            try {
                listener.onRemember(key, value);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyRecall(String key, Object value) {
        for (MemoryListener listener : listeners) {
            try {
                listener.onRecall(key, value);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyForget(String key) {
        for (MemoryListener listener : listeners) {
            try {
                listener.onForget(key);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
}
