package net.ooder.skill.hotplug.cache;

import net.ooder.skill.hotplug.config.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由缓存
 * 缓存路由映射，加速路由查找
 */
public class RouteCache {

    private static final Logger logger = LoggerFactory.getLogger(RouteCache.class);

    private final Map<String, RouteCacheEntry> pathCache = new ConcurrentHashMap<>();
    private final Map<String, List<RouteDefinition>> skillRoutesCache = new ConcurrentHashMap<>();

    private int maxSize = 2000;
    private long defaultTtl = 3600000;

    public void cacheRoute(String skillId, RouteDefinition route) {
        String cacheKey = buildCacheKey(route.getPath(), route.getMethod());

        RouteCacheEntry entry = new RouteCacheEntry(skillId, route, System.currentTimeMillis(), defaultTtl);
        pathCache.put(cacheKey, entry);

        skillRoutesCache.computeIfAbsent(skillId, k -> new ArrayList<>()).add(route);

        logger.debug("[RouteCache] Cached route: {} {}", route.getMethod(), route.getPath());
    }

    public void cacheRoutes(String skillId, List<RouteDefinition> routes) {
        for (RouteDefinition route : routes) {
            cacheRoute(skillId, route);
        }
        logger.info("[RouteCache] Cached {} routes for skill: {}", routes.size(), skillId);
    }

    public RouteCacheEntry lookup(String path, String method) {
        String cacheKey = buildCacheKey(path, method);
        RouteCacheEntry entry = pathCache.get(cacheKey);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            pathCache.remove(cacheKey);
            logger.debug("[RouteCache] Cache expired for: {}", cacheKey);
            return null;
        }

        entry.updateAccessTime();
        logger.debug("[RouteCache] Cache hit for: {}", cacheKey);
        return entry;
    }

    public List<RouteDefinition> getRoutesBySkill(String skillId) {
        List<RouteDefinition> routes = skillRoutesCache.get(skillId);
        return routes != null ? new ArrayList<>(routes) : Collections.emptyList();
    }

    public void removeSkill(String skillId) {
        List<RouteDefinition> routes = skillRoutesCache.remove(skillId);
        if (routes != null) {
            for (RouteDefinition route : routes) {
                String cacheKey = buildCacheKey(route.getPath(), route.getMethod());
                pathCache.remove(cacheKey);
            }
        }
        logger.info("[RouteCache] Removed routes for skill: {}", skillId);
    }

    public void clear() {
        pathCache.clear();
        skillRoutesCache.clear();
        logger.info("[RouteCache] Cleared all route cache");
    }

    public boolean contains(String path, String method) {
        return pathCache.containsKey(buildCacheKey(path, method));
    }

    public int size() {
        return pathCache.size();
    }

    private String buildCacheKey(String path, String method) {
        return method.toUpperCase() + ":" + path;
    }

    public void evictExpired() {
        int evicted = 0;
        Iterator<Map.Entry<String, RouteCacheEntry>> it = pathCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, RouteCacheEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                evicted++;
            }
        }
        if (evicted > 0) {
            logger.debug("[RouteCache] Evicted {} expired entries", evicted);
        }
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRoutes", pathCache.size());
        stats.put("skillCount", skillRoutesCache.size());
        stats.put("maxSize", maxSize);
        stats.put("defaultTtl", defaultTtl);

        long expiredCount = pathCache.values().stream()
                .filter(RouteCacheEntry::isExpired)
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

    public static class RouteCacheEntry {
        private final String skillId;
        private final RouteDefinition route;
        private final long cachedAt;
        private final long ttl;
        private long lastAccessTime;

        public RouteCacheEntry(String skillId, RouteDefinition route, long cachedAt, long ttl) {
            this.skillId = skillId;
            this.route = route;
            this.cachedAt = cachedAt;
            this.ttl = ttl;
            this.lastAccessTime = cachedAt;
        }

        public String getSkillId() {
            return skillId;
        }

        public RouteDefinition getRoute() {
            return route;
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
