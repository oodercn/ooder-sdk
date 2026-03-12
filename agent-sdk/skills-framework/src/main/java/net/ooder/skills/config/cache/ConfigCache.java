package net.ooder.skills.config.cache;

import net.ooder.skills.config.ConfigNode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 配置缓存
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class ConfigCache {

    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public ConfigCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public ConfigCache() {
        this(300000); // 默认5分钟
    }

    public void put(String key, ConfigNode config) {
        cache.put(key, new CacheEntry(config, System.currentTimeMillis()));
    }

    public ConfigNode get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (System.currentTimeMillis() - entry.timestamp > ttlMillis) {
            cache.remove(key);
            return null;
        }
        return entry.config;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void invalidateAll() {
        cache.clear();
    }

    private static class CacheEntry {
        final ConfigNode config;
        final long timestamp;

        CacheEntry(ConfigNode config, long timestamp) {
            this.config = config;
            this.timestamp = timestamp;
        }
    }
}
