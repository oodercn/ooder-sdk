package net.ooder.scene.discovery.cache;

import net.ooder.scene.discovery.api.DiscoveryService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单缓存管理器实现
 *
 * <p>基于内存的简单缓存实现，用于开发和测试环境。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SimpleCacheManager implements CacheManager {

    private final Map<String, DiscoveryService.SkillInfo> cache = new ConcurrentHashMap<>();
    private CacheConfig config = new CacheConfig();

    @Override
    public DiscoveryService.SkillInfo getSkill(String skillId) {
        return cache.get(skillId);
    }

    @Override
    public DiscoveryService.SkillInfo getSkill(String skillId, String version) {
        return cache.get(skillId + "@" + version);
    }

    @Override
    public List<DiscoveryService.SkillInfo> getAllSkills() {
        return new java.util.ArrayList<>(cache.values());
    }

    @Override
    public void putSkill(DiscoveryService.SkillInfo skill) {
        if (skill != null) {
            cache.put(skill.getSkillId(), skill);
        }
    }

    @Override
    public void putSkills(List<DiscoveryService.SkillInfo> skills) {
        if (skills != null) {
            skills.forEach(this::putSkill);
        }
    }

    @Override
    public void removeSkill(String skillId) {
        cache.remove(skillId);
    }

    @Override
    public boolean exists(String skillId) {
        return cache.containsKey(skillId);
    }

    @Override
    public boolean exists(String skillId, String version) {
        return cache.containsKey(skillId + "@" + version);
    }

    @Override
    public CacheStatus getStatus(String skillId) {
        CacheStatus status = new CacheStatus();
        status.setCached(exists(skillId));
        status.setExpired(false);
        return status;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int clearExpired() {
        // 简单实现，不处理过期
        return 0;
    }

    @Override
    public CacheStats getStats() {
        CacheStats stats = new CacheStats();
        stats.setMemoryCacheSize(cache.size());
        return stats;
    }

    @Override
    public void setConfig(CacheConfig config) {
        this.config = config;
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public void put(String key, Object value) {
        if (value instanceof DiscoveryService.SkillInfo) {
            cache.put(key, (DiscoveryService.SkillInfo) value);
        }
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }
}
