package net.ooder.skill.hotplug.cache;

import net.ooder.skill.hotplug.model.SkillMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill元数据缓存
 * 缓存已解析的SkillMetadata，避免重复解析skill.yaml
 */
public class MetadataCache {

    private static final Logger logger = LoggerFactory.getLogger(MetadataCache.class);

    private final Map<String, CachedMetadata> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> fileLastModified = new ConcurrentHashMap<>();
    
    private int maxSize = 500;
    private long defaultTtl = 3600000;

    public SkillMetadata get(String skillId) {
        CachedMetadata cached = cache.get(skillId);
        if (cached == null) {
            return null;
        }

        if (cached.isExpired()) {
            cache.remove(skillId);
            logger.debug("[MetadataCache] Cache expired for skill: {}", skillId);
            return null;
        }

        cached.updateAccessTime();
        logger.debug("[MetadataCache] Cache hit for skill: {}", skillId);
        return cached.getMetadata();
    }

    public void put(String skillId, SkillMetadata metadata) {
        put(skillId, metadata, null);
    }

    public void put(String skillId, SkillMetadata metadata, Long lastModified) {
        if (cache.size() >= maxSize) {
            evictOldestEntries(maxSize / 10);
        }

        CachedMetadata cached = new CachedMetadata(metadata, System.currentTimeMillis(), defaultTtl);
        cache.put(skillId, cached);

        if (lastModified != null) {
            fileLastModified.put(skillId, lastModified);
        }

        logger.debug("[MetadataCache] Cached metadata for skill: {}", skillId);
    }

    public boolean isModified(String skillId, long currentLastModified) {
        Long cached = fileLastModified.get(skillId);
        if (cached == null) {
            return true;
        }
        return cached != currentLastModified;
    }

    public void remove(String skillId) {
        cache.remove(skillId);
        fileLastModified.remove(skillId);
        logger.debug("[MetadataCache] Removed cache for skill: {}", skillId);
    }

    public void clear() {
        cache.clear();
        fileLastModified.clear();
        logger.info("[MetadataCache] Cleared all metadata cache");
    }

    public boolean contains(String skillId) {
        return cache.containsKey(skillId);
    }

    public int size() {
        return cache.size();
    }

    private void evictOldestEntries(int count) {
        List<Map.Entry<String, CachedMetadata>> entries = new ArrayList<>(cache.entrySet());
        entries.sort(Comparator.comparingLong(e -> e.getValue().getLastAccessTime()));

        int toEvict = Math.min(count, entries.size());
        for (int i = 0; i < toEvict; i++) {
            String skillId = entries.get(i).getKey();
            cache.remove(skillId);
            fileLastModified.remove(skillId);
        }

        if (toEvict > 0) {
            logger.debug("[MetadataCache] Evicted {} oldest entries", toEvict);
        }
    }

    public void evictExpired() {
        int evicted = 0;
        Iterator<Map.Entry<String, CachedMetadata>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CachedMetadata> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                fileLastModified.remove(entry.getKey());
                evicted++;
            }
        }
        if (evicted > 0) {
            logger.debug("[MetadataCache] Evicted {} expired entries", evicted);
        }
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalEntries", cache.size());
        stats.put("maxSize", maxSize);
        stats.put("defaultTtl", defaultTtl);

        long expiredCount = cache.values().stream()
                .filter(CachedMetadata::isExpired)
                .count();
        stats.put("expiredEntries", expiredCount);

        return stats;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setDefaultTtl(long ttlMs) {
        this.defaultTtl = ttlMs;
    }

    private static class CachedMetadata {
        private final SkillMetadata metadata;
        private final long cachedAt;
        private final long ttl;
        private long lastAccessTime;

        public CachedMetadata(SkillMetadata metadata, long cachedAt, long ttl) {
            this.metadata = metadata;
            this.cachedAt = cachedAt;
            this.ttl = ttl;
            this.lastAccessTime = cachedAt;
        }

        public SkillMetadata getMetadata() {
            return metadata;
        }

        public long getCachedAt() {
            return cachedAt;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public void updateAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > (cachedAt + ttl);
        }
    }
}
