package net.ooder.skill.hotplug.cache;

import net.ooder.skill.hotplug.config.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务缓存
 * 缓存服务实例，避免重复创建
 */
public class ServiceCache {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCache.class);

    private final Map<String, ServiceCacheEntry> serviceCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> skillServicesCache = new ConcurrentHashMap<>();

    private int maxSize = 500;
    private long defaultTtl = 1800000;

    public void cacheService(String skillId, String serviceId, Object serviceInstance, ServiceDefinition definition) {
        if (serviceCache.size() >= maxSize) {
            evictOldestEntries(maxSize / 10);
        }

        ServiceCacheEntry entry = new ServiceCacheEntry(
                skillId, serviceId, serviceInstance, definition,
                System.currentTimeMillis(), defaultTtl
        );
        serviceCache.put(serviceId, entry);

        skillServicesCache.computeIfAbsent(skillId, k -> new ArrayList<>()).add(serviceId);

        logger.debug("[ServiceCache] Cached service: {} for skill: {}", serviceId, skillId);
    }

    public ServiceCacheEntry lookup(String serviceId) {
        ServiceCacheEntry entry = serviceCache.get(serviceId);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            serviceCache.remove(serviceId);
            logger.debug("[ServiceCache] Cache expired for service: {}", serviceId);
            return null;
        }

        entry.updateAccessTime();
        logger.debug("[ServiceCache] Cache hit for service: {}", serviceId);
        return entry;
    }

    public Object getServiceInstance(String serviceId) {
        ServiceCacheEntry entry = lookup(serviceId);
        return entry != null ? entry.getServiceInstance() : null;
    }

    public List<String> getServicesBySkill(String skillId) {
        List<String> services = skillServicesCache.get(skillId);
        return services != null ? new ArrayList<>(services) : Collections.emptyList();
    }

    public void removeService(String serviceId) {
        ServiceCacheEntry entry = serviceCache.remove(serviceId);
        if (entry != null) {
            List<String> skillServices = skillServicesCache.get(entry.getSkillId());
            if (skillServices != null) {
                skillServices.remove(serviceId);
            }
        }
        logger.debug("[ServiceCache] Removed service: {}", serviceId);
    }

    public void removeSkill(String skillId) {
        List<String> services = skillServicesCache.remove(skillId);
        if (services != null) {
            for (String serviceId : services) {
                serviceCache.remove(serviceId);
            }
        }
        logger.info("[ServiceCache] Removed services for skill: {}", skillId);
    }

    public void clear() {
        serviceCache.clear();
        skillServicesCache.clear();
        logger.info("[ServiceCache] Cleared all service cache");
    }

    public boolean contains(String serviceId) {
        return serviceCache.containsKey(serviceId);
    }

    public int size() {
        return serviceCache.size();
    }

    private void evictOldestEntries(int count) {
        List<Map.Entry<String, ServiceCacheEntry>> entries = new ArrayList<>(serviceCache.entrySet());
        entries.sort(Comparator.comparingLong(e -> e.getValue().getLastAccessTime()));

        int toEvict = Math.min(count, entries.size());
        for (int i = 0; i < toEvict; i++) {
            String serviceId = entries.get(i).getKey();
            ServiceCacheEntry entry = serviceCache.remove(serviceId);
            if (entry != null) {
                List<String> skillServices = skillServicesCache.get(entry.getSkillId());
                if (skillServices != null) {
                    skillServices.remove(serviceId);
                }
            }
        }

        if (toEvict > 0) {
            logger.debug("[ServiceCache] Evicted {} oldest entries", toEvict);
        }
    }

    public void evictExpired() {
        int evicted = 0;
        Iterator<Map.Entry<String, ServiceCacheEntry>> it = serviceCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ServiceCacheEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                evicted++;
            }
        }
        if (evicted > 0) {
            logger.debug("[ServiceCache] Evicted {} expired entries", evicted);
        }
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalServices", serviceCache.size());
        stats.put("skillCount", skillServicesCache.size());
        stats.put("maxSize", maxSize);
        stats.put("defaultTtl", defaultTtl);

        long expiredCount = serviceCache.values().stream()
                .filter(ServiceCacheEntry::isExpired)
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

    public static class ServiceCacheEntry {
        private final String skillId;
        private final String serviceId;
        private final Object serviceInstance;
        private final ServiceDefinition definition;
        private final long cachedAt;
        private final long ttl;
        private long lastAccessTime;

        public ServiceCacheEntry(String skillId, String serviceId, Object serviceInstance,
                                  ServiceDefinition definition, long cachedAt, long ttl) {
            this.skillId = skillId;
            this.serviceId = serviceId;
            this.serviceInstance = serviceInstance;
            this.definition = definition;
            this.cachedAt = cachedAt;
            this.ttl = ttl;
            this.lastAccessTime = cachedAt;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getServiceId() {
            return serviceId;
        }

        public Object getServiceInstance() {
            return serviceInstance;
        }

        public ServiceDefinition getDefinition() {
            return definition;
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
