package net.ooder.skill.hotplug.ui;

import net.ooder.skill.hotplug.model.SkillPage;
import net.ooder.skill.hotplug.model.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 页面缓存管理器
 * 管理Skill页面的缓存策略
 */
public class PageCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(PageCacheManager.class);

    private final Map<String, PageCacheEntry> pageCache = new ConcurrentHashMap<>();
    private final Map<String, SkillPackage> packageRegistry = new ConcurrentHashMap<>();
    
    private long defaultCacheTtl = 3600000;
    private int maxCacheSize = 1000;

    public void registerPackage(String skillId, SkillPackage skillPackage) {
        packageRegistry.put(skillId, skillPackage);
        logger.info("[PageCacheManager] Registered package for skill: {}", skillId);
    }

    public void unregisterPackage(String skillId) {
        packageRegistry.remove(skillId);
        clearCacheForSkill(skillId);
        logger.info("[PageCacheManager] Unregistered package for skill: {}", skillId);
    }

    public String getPageContent(String skillId, String pagePath) {
        String cacheKey = buildCacheKey(skillId, pagePath);
        
        PageCacheEntry entry = pageCache.get(cacheKey);
        if (entry != null && !entry.isExpired()) {
            logger.debug("[PageCacheManager] Cache hit for: {}", cacheKey);
            return entry.getContent();
        }

        String content = loadPageContent(skillId, pagePath);
        if (content != null) {
            cachePage(cacheKey, content);
        }

        return content;
    }

    public Map<String, Object> getPageWithMetadata(String skillId, String pagePath) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String cacheKey = buildCacheKey(skillId, pagePath);
        PageCacheEntry entry = pageCache.get(cacheKey);
        
        if (entry != null && !entry.isExpired()) {
            result.put("content", entry.getContent());
            result.put("cached", true);
            result.put("cachedAt", entry.getCachedAt());
            result.put("expiresAt", entry.getExpiresAt());
            result.put("skillId", skillId);
            result.put("path", pagePath);
            return result;
        }

        String content = loadPageContent(skillId, pagePath);
        if (content != null) {
            cachePage(cacheKey, content);
            result.put("content", content);
            result.put("cached", false);
            result.put("skillId", skillId);
            result.put("path", pagePath);
        }

        return result;
    }

    public void cachePage(String cacheKey, String content) {
        if (pageCache.size() >= maxCacheSize) {
            evictOldestEntries(maxCacheSize / 10);
        }

        PageCacheEntry entry = new PageCacheEntry(content, System.currentTimeMillis(), defaultCacheTtl);
        pageCache.put(cacheKey, entry);
        logger.debug("[PageCacheManager] Cached page: {}", cacheKey);
    }

    public void invalidateCache(String skillId, String pagePath) {
        String cacheKey = buildCacheKey(skillId, pagePath);
        pageCache.remove(cacheKey);
        logger.debug("[PageCacheManager] Invalidated cache for: {}", cacheKey);
    }

    public void clearCacheForSkill(String skillId) {
        pageCache.keySet().removeIf(key -> key.startsWith(skillId + ":"));
        logger.info("[PageCacheManager] Cleared cache for skill: {}", skillId);
    }

    public void clearAllCache() {
        pageCache.clear();
        logger.info("[PageCacheManager] Cleared all page cache");
    }

    public void evictExpiredEntries() {
        int evicted = 0;
        Iterator<Map.Entry<String, PageCacheEntry>> it = pageCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, PageCacheEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                evicted++;
            }
        }
        if (evicted > 0) {
            logger.debug("[PageCacheManager] Evicted {} expired entries", evicted);
        }
    }

    private void evictOldestEntries(int count) {
        List<Map.Entry<String, PageCacheEntry>> entries = new ArrayList<>(pageCache.entrySet());
        entries.sort(Comparator.comparingLong(e -> e.getValue().getCachedAt()));
        
        int toEvict = Math.min(count, entries.size());
        for (int i = 0; i < toEvict; i++) {
            pageCache.remove(entries.get(i).getKey());
        }
        logger.debug("[PageCacheManager] Evicted {} oldest entries", toEvict);
    }

    private String loadPageContent(String skillId, String pagePath) {
        SkillPackage pkg = packageRegistry.get(skillId);
        if (pkg == null) {
            logger.warn("[PageCacheManager] Package not found for skill: {}", skillId);
            return null;
        }

        try {
            String resourcePath = normalizePath(pagePath);
            InputStream is = pkg.getResource(resourcePath);
            
            if (is == null) {
                is = pkg.getResource("static/" + resourcePath);
            }
            
            if (is == null) {
                logger.warn("[PageCacheManager] Page resource not found: {}", resourcePath);
                return null;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            return content.toString();

        } catch (Exception e) {
            logger.error("[PageCacheManager] Failed to load page content: {}", pagePath, e);
            return null;
        }
    }

    private String buildCacheKey(String skillId, String pagePath) {
        return skillId + ":" + normalizePath(pagePath);
    }

    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

    public void setDefaultCacheTtl(long ttlMs) {
        this.defaultCacheTtl = ttlMs;
    }

    public void setMaxCacheSize(int maxSize) {
        this.maxCacheSize = maxSize;
    }

    public int getCacheSize() {
        return pageCache.size();
    }

    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalEntries", pageCache.size());
        stats.put("maxCacheSize", maxCacheSize);
        stats.put("defaultTtl", defaultCacheTtl);
        
        long expiredCount = pageCache.values().stream()
                .filter(PageCacheEntry::isExpired)
                .count();
        stats.put("expiredEntries", expiredCount);
        
        return stats;
    }

    private static class PageCacheEntry {
        private final String content;
        private final long cachedAt;
        private final long ttl;

        public PageCacheEntry(String content, long cachedAt, long ttl) {
            this.content = content;
            this.cachedAt = cachedAt;
            this.ttl = ttl;
        }

        public String getContent() {
            return content;
        }

        public long getCachedAt() {
            return cachedAt;
        }

        public long getExpiresAt() {
            return cachedAt + ttl;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > getExpiresAt();
        }
    }
}
