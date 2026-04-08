package net.ooder.skill.hotplug.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓存管理器
 * 统一管理所有缓存
 */
@Component
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private MetadataCache metadataCache;
    private RouteCache routeCache;
    private ServiceCache serviceCache;
    private LazySkillLoader lazySkillLoader;

    public void setMetadataCache(MetadataCache metadataCache) {
        this.metadataCache = metadataCache;
    }

    public void setRouteCache(RouteCache routeCache) {
        this.routeCache = routeCache;
    }

    public void setServiceCache(ServiceCache serviceCache) {
        this.serviceCache = serviceCache;
    }

    public void setLazySkillLoader(LazySkillLoader lazySkillLoader) {
        this.lazySkillLoader = lazySkillLoader;
    }

    public MetadataCache getMetadataCache() {
        return metadataCache;
    }

    public RouteCache getRouteCache() {
        return routeCache;
    }

    public ServiceCache getServiceCache() {
        return serviceCache;
    }

    public LazySkillLoader getLazySkillLoader() {
        return lazySkillLoader;
    }

    public void clearAll() {
        if (metadataCache != null) {
            metadataCache.clear();
        }
        if (routeCache != null) {
            routeCache.clear();
        }
        if (serviceCache != null) {
            serviceCache.clear();
        }
        logger.info("[CacheManager] Cleared all caches");
    }

    public void evictAllExpired() {
        int totalEvicted = 0;

        if (metadataCache != null) {
            metadataCache.evictExpired();
            totalEvicted++;
        }
        if (routeCache != null) {
            routeCache.evictExpired();
            totalEvicted++;
        }
        if (serviceCache != null) {
            serviceCache.evictExpired();
            totalEvicted++;
        }

        logger.debug("[CacheManager] Evicted expired entries from {} caches", totalEvicted);
    }

    public Map<String, Object> getAllStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("timestamp", System.currentTimeMillis());

        if (metadataCache != null) {
            stats.put("metadataCache", metadataCache.getStats());
        }

        if (routeCache != null) {
            stats.put("routeCache", routeCache.getStats());
        }

        if (serviceCache != null) {
            stats.put("serviceCache", serviceCache.getStats());
        }

        if (lazySkillLoader != null) {
            stats.put("lazyLoader", lazySkillLoader.getStats());
        }

        return stats;
    }

    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());

        Map<String, Object> components = new LinkedHashMap<>();

        if (metadataCache != null) {
            Map<String, Object> metaHealth = new LinkedHashMap<>();
            metaHealth.put("status", "UP");
            metaHealth.put("size", metadataCache.size());
            components.put("metadataCache", metaHealth);
        }

        if (routeCache != null) {
            Map<String, Object> routeHealth = new LinkedHashMap<>();
            routeHealth.put("status", "UP");
            routeHealth.put("size", routeCache.size());
            components.put("routeCache", routeHealth);
        }

        if (serviceCache != null) {
            Map<String, Object> serviceHealth = new LinkedHashMap<>();
            serviceHealth.put("status", "UP");
            serviceHealth.put("size", serviceCache.size());
            components.put("serviceCache", serviceHealth);
        }

        health.put("components", components);

        return health;
    }
}
