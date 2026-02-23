package net.ooder.sdk.drivers.cache.impl;

import net.ooder.sdk.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.cache.CacheDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@DriverImplementation(value = "CacheDriver", skillId = "skill-cache-local")
public class LocalCacheDriver implements CacheDriver {
    
    private static final Logger log = LoggerFactory.getLogger(LocalCacheDriver.class);
    
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private CacheConfig config;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    
    @Override
    public void init(CacheConfig config) {
        this.config = config;
        
        scheduler.scheduleAtFixedRate(this::evictExpired, 60, 60, TimeUnit.SECONDS);
        
        connected.set(true);
        log.info("Local cache initialized");
    }
    
    @Override
    public CompletableFuture<Void> set(String key, Object value) {
        return CompletableFuture.runAsync(() -> {
            long ttl = config != null ? config.getDefaultTtl() : 3600;
            setInternal(key, value, System.currentTimeMillis() + ttl * 1000);
        });
    }
    
    @Override
    public CompletableFuture<Void> set(String key, Object value, long ttl, TimeUnit unit) {
        return CompletableFuture.runAsync(() -> {
            long expireTime = System.currentTimeMillis() + unit.toMillis(ttl);
            setInternal(key, value, expireTime);
        });
    }
    
    private void setInternal(String key, Object value, long expireTime) {
        CacheEntry entry = new CacheEntry();
        entry.value = value;
        entry.expireTime = expireTime;
        cache.put(key, entry);
        log.debug("Cache set: {}", key);
    }
    
    @Override
    public <T> CompletableFuture<T> get(String key, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                cache.remove(key);
                return null;
            }
            
            try {
                return type.cast(entry.value);
            } catch (ClassCastException e) {
                log.warn("Cache cast failed for key: {}", key);
                return null;
            }
        });
    }
    
    @Override
    public CompletableFuture<Object> get(String key) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                cache.remove(key);
                return null;
            }
            return entry.value;
        });
    }
    
    @Override
    public CompletableFuture<Void> delete(String key) {
        return CompletableFuture.runAsync(() -> {
            cache.remove(key);
            log.debug("Cache deleted: {}", key);
        });
    }
    
    @Override
    public CompletableFuture<Void> deleteBatch(List<String> keys) {
        return CompletableFuture.runAsync(() -> {
            for (String key : keys) {
                cache.remove(key);
            }
            log.debug("Cache batch deleted: {} keys", keys.size());
        });
    }
    
    @Override
    public CompletableFuture<Boolean> exists(String key) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                cache.remove(key);
                return false;
            }
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> expire(String key, long ttl, TimeUnit unit) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                cache.remove(key);
                return false;
            }
            entry.expireTime = System.currentTimeMillis() + unit.toMillis(ttl);
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Long> getTtl(String key) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null) {
                return -2L;
            }
            if (entry.isExpired()) {
                cache.remove(key);
                return -2L;
            }
            long remaining = entry.expireTime - System.currentTimeMillis();
            return remaining > 0 ? remaining / 1000 : -1L;
        });
    }
    
    @Override
    public CompletableFuture<Long> increment(String key) {
        return increment(key, 1);
    }
    
    @Override
    public CompletableFuture<Long> increment(String key, long delta) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.computeIfAbsent(key, k -> new CacheEntry());
            if (entry.value == null) {
                entry.value = delta;
            } else if (entry.value instanceof Number) {
                entry.value = ((Number) entry.value).longValue() + delta;
            } else {
                throw new RuntimeException("Value is not a number");
            }
            return ((Number) entry.value).longValue();
        });
    }
    
    @Override
    public CompletableFuture<Long> decrement(String key) {
        return decrement(key, 1);
    }
    
    @Override
    public CompletableFuture<Long> decrement(String key, long delta) {
        return increment(key, -delta);
    }
    
    @Override
    public CompletableFuture<Void> setMap(String key, Map<String, Object> map) {
        return CompletableFuture.runAsync(() -> {
            CacheEntry entry = new CacheEntry();
            entry.value = new ConcurrentHashMap<>(map);
            entry.expireTime = System.currentTimeMillis() + config.getDefaultTtl() * 1000;
            cache.put(key, entry);
        });
    }
    
    @Override
    public CompletableFuture<Map<String, Object>> getMap(String key) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired() || !(entry.value instanceof Map)) {
                return null;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) entry.value;
            return new HashMap<>(map);
        });
    }
    
    @Override
    public CompletableFuture<Void> setMapField(String key, String field, Object value) {
        return CompletableFuture.runAsync(() -> {
            CacheEntry entry = cache.computeIfAbsent(key, k -> {
                CacheEntry e = new CacheEntry();
                e.value = new ConcurrentHashMap<String, Object>();
                return e;
            });
            if (entry.value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) entry.value;
                map.put(field, value);
            }
        });
    }
    
    @Override
    public CompletableFuture<Object> getMapField(String key, String field) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired() || !(entry.value instanceof Map)) {
                return null;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) entry.value;
            return map.get(field);
        });
    }
    
    @Override
    public CompletableFuture<Void> addToSet(String key, String... members) {
        return CompletableFuture.runAsync(() -> {
            CacheEntry entry = cache.computeIfAbsent(key, k -> {
                CacheEntry e = new CacheEntry();
                e.value = ConcurrentHashMap.newKeySet();
                return e;
            });
            if (entry.value instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> set = (Set<String>) entry.value;
                for (String member : members) {
                    set.add(member);
                }
            }
        });
    }
    
    @Override
    public CompletableFuture<List<String>> getSetMembers(String key) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired() || !(entry.value instanceof Set)) {
                return Collections.emptyList();
            }
            @SuppressWarnings("unchecked")
            Set<String> set = (Set<String>) entry.value;
            return new ArrayList<>(set);
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isSetMember(String key, String member) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired() || !(entry.value instanceof Set)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Set<String> set = (Set<String>) entry.value;
            return set.contains(member);
        });
    }
    
    @Override
    public CompletableFuture<Void> pushToList(String key, String... values) {
        return CompletableFuture.runAsync(() -> {
            CacheEntry entry = cache.computeIfAbsent(key, k -> {
                CacheEntry e = new CacheEntry();
                e.value = new CopyOnWriteArrayList<String>();
                return e;
            });
            if (entry.value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) entry.value;
                for (String value : values) {
                    list.add(value);
                }
            }
        });
    }
    
    @Override
    public CompletableFuture<List<String>> getListRange(String key, long start, long end) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired() || !(entry.value instanceof List)) {
                return Collections.emptyList();
            }
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) entry.value;
            int size = list.size();
            int startIdx = (int) Math.max(0, start < 0 ? size + start : start);
            int endIdx = (int) Math.min(size - 1, end < 0 ? size + end : end);
            if (startIdx > endIdx || startIdx >= size) {
                return Collections.emptyList();
            }
            return new ArrayList<>(list.subList(startIdx, endIdx + 1));
        });
    }
    
    @Override
    public CompletableFuture<Long> getListSize(String key) {
        return CompletableFuture.supplyAsync(() -> {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired() || !(entry.value instanceof List)) {
                return 0L;
            }
            @SuppressWarnings("unchecked")
            List<?> list = (List<?>) entry.value;
            return (long) list.size();
        });
    }
    
    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.runAsync(() -> {
            cache.clear();
            log.info("Cache cleared");
        });
    }
    
    @Override
    public CompletableFuture<Long> size() {
        return CompletableFuture.supplyAsync(() -> (long) cache.size());
    }
    
    @Override
    public CompletableFuture<List<String>> keys(String pattern) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> result = new ArrayList<>();
            String regex = pattern.replace("*", ".*").replace("?", ".");
            for (String key : cache.keySet()) {
                if (key.matches(regex)) {
                    result.add(key);
                }
            }
            return result;
        });
    }
    
    @Override
    public void close() {
        scheduler.shutdown();
        cache.clear();
        connected.set(false);
        log.info("Local cache closed");
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getDriverName() {
        return "LocalCache";
    }
    
    @Override
    public String getDriverVersion() {
        return "1.0.0";
    }
    
    private void evictExpired() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    private static class CacheEntry {
        Object value;
        long expireTime;
        
        boolean isExpired() {
            return expireTime > 0 && System.currentTimeMillis() > expireTime;
        }
    }
}
