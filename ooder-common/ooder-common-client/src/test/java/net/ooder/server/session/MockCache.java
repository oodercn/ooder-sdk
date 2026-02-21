package net.ooder.server.session;

import net.ooder.common.cache.Cache;

import java.util.*;

public class MockCache<K, V> implements Cache<K, V> {
    
    private final Map<K, V> store = new HashMap<K, V>();
    private String name;
    private int maxCacheSize = -1;
    private long maxLifetime = -1;
    private long cacheHits = 0;
    private long cacheMisses = 0;
    private boolean stopPutWhenFull = false;
    
    public MockCache() {
        this.name = "mock-cache";
    }
    
    public MockCache(String name) {
        this.name = name;
    }
    
    @Override
    public K getName() {
        return (K) name;
    }
    
    @Override
    public int getMaxCacheSize() {
        return maxCacheSize;
    }
    
    @Override
    public void setMaxCacheSize(int maxSize) {
        this.maxCacheSize = maxSize;
    }
    
    @Override
    public long getMaxLifetime() {
        return maxLifetime;
    }
    
    @Override
    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }
    
    @Override
    public int getCacheSize() {
        return store.size();
    }
    
    @Override
    public long getCacheHits() {
        return cacheHits;
    }
    
    @Override
    public long getCacheMisses() {
        return cacheMisses;
    }
    
    @Override
    public void setStopPutWhenFull(boolean flag) {
        this.stopPutWhenFull = flag;
    }
    
    @Override
    public void clear() {
        store.clear();
    }
    
    @Override
    public int size() {
        return store.size();
    }
    
    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return store.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object value) {
        return store.containsValue(value);
    }
    
    @Override
    public V get(Object key) {
        V value = store.get(key);
        if (value != null) {
            cacheHits++;
        } else {
            cacheMisses++;
        }
        return value;
    }
    
    @Override
    public V put(K key, V value) {
        return store.put(key, value);
    }
    
    @Override
    public V remove(Object key) {
        return store.remove(key);
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        store.putAll(m);
    }
    
    @Override
    public Set<K> keySet() {
        return new HashSet<K>(store.keySet());
    }
    
    @Override
    public Collection<V> values() {
        return new ArrayList<V>(store.values());
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<Entry<K, V>>(store.entrySet());
    }
}
