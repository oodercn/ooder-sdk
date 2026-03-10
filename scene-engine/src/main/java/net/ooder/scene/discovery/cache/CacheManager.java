package net.ooder.scene.discovery.cache;

import net.ooder.scene.discovery.api.DiscoveryService;

import java.util.List;

/**
 * 缓存管理器接口
 *
 * 统一管理多级缓存：内存缓存 + 文件缓存
 * 提供缓存策略：TTL、容量限制、去重
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface CacheManager {

    /**
     * 获取缓存的Skill
     *
     * @param skillId Skill ID
     * @return Skill信息，如果不存在或已过期返回null
     */
    DiscoveryService.SkillInfo getSkill(String skillId);

    /**
     * 获取缓存的Skill（指定版本）
     *
     * @param skillId Skill ID
     * @param version 版本号
     * @return Skill信息
     */
    DiscoveryService.SkillInfo getSkill(String skillId, String version);

    /**
     * 获取所有缓存的Skills
     *
     * @return Skill列表
     */
    List<DiscoveryService.SkillInfo> getAllSkills();

    /**
     * 缓存Skill
     *
     * @param skill Skill信息
     */
    void putSkill(DiscoveryService.SkillInfo skill);

    /**
     * 批量缓存Skills
     *
     * @param skills Skill列表
     */
    void putSkills(List<DiscoveryService.SkillInfo> skills);

    /**
     * 移除缓存
     *
     * @param skillId Skill ID
     */
    void removeSkill(String skillId);

    /**
     * 检查是否存在有效缓存
     *
     * @param skillId Skill ID
     * @return 是否存在
     */
    boolean exists(String skillId);

    /**
     * 检查是否存在有效缓存（指定版本）
     *
     * @param skillId Skill ID
     * @param version 版本号
     * @return 是否存在
     */
    boolean exists(String skillId, String version);

    /**
     * 获取缓存状态
     *
     * @param skillId Skill ID
     * @return 缓存状态
     */
    CacheStatus getStatus(String skillId);

    /**
     * 清除所有缓存
     */
    void clear();

    /**
     * 清除过期缓存
     *
     * @return 清除数量
     */
    int clearExpired();

    /**
     * 获取缓存统计
     *
     * @return 统计信息
     */
    CacheStats getStats();

    /**
     * 设置缓存配置
     *
     * @param config 配置
     */
    void setConfig(CacheConfig config);

    // ========== 数据类定义 ==========

    /**
     * 缓存配置
     */
    class CacheConfig {
        private long memoryCacheTtlMs = 3600000;  // 内存缓存TTL：1小时
        private long fileCacheTtlMs = 86400000;   // 文件缓存TTL：24小时
        private int maxMemoryCacheSize = 100;     // 最大内存缓存条目数
        private int maxFileCacheSize = 1000;      // 最大文件缓存条目数
        private String cacheDir = "./.ooder/cache/skills";  // 缓存目录
        private boolean enableMemoryCache = true;
        private boolean enableFileCache = true;

        // Getters and Setters
        public long getMemoryCacheTtlMs() { return memoryCacheTtlMs; }
        public void setMemoryCacheTtlMs(long memoryCacheTtlMs) { this.memoryCacheTtlMs = memoryCacheTtlMs; }
        public long getFileCacheTtlMs() { return fileCacheTtlMs; }
        public void setFileCacheTtlMs(long fileCacheTtlMs) { this.fileCacheTtlMs = fileCacheTtlMs; }
        public int getMaxMemoryCacheSize() { return maxMemoryCacheSize; }
        public void setMaxMemoryCacheSize(int maxMemoryCacheSize) { this.maxMemoryCacheSize = maxMemoryCacheSize; }
        public int getMaxFileCacheSize() { return maxFileCacheSize; }
        public void setMaxFileCacheSize(int maxFileCacheSize) { this.maxFileCacheSize = maxFileCacheSize; }
        public String getCacheDir() { return cacheDir; }
        public void setCacheDir(String cacheDir) { this.cacheDir = cacheDir; }
        public boolean isEnableMemoryCache() { return enableMemoryCache; }
        public void setEnableMemoryCache(boolean enableMemoryCache) { this.enableMemoryCache = enableMemoryCache; }
        public boolean isEnableFileCache() { return enableFileCache; }
        public void setEnableFileCache(boolean enableFileCache) { this.enableFileCache = enableFileCache; }
    }

    /**
     * 缓存状态
     */
    class CacheStatus {
        private boolean cached;
        private boolean expired;
        private long cacheTime;
        private long expireTime;
        private long size;
        private String location;  // memory/file

        // Getters and Setters
        public boolean isCached() { return cached; }
        public void setCached(boolean cached) { this.cached = cached; }
        public boolean isExpired() { return expired; }
        public void setExpired(boolean expired) { this.expired = expired; }
        public long getCacheTime() { return cacheTime; }
        public void setCacheTime(long cacheTime) { this.cacheTime = cacheTime; }
        public long getExpireTime() { return expireTime; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    /**
     * 缓存统计
     */
    class CacheStats {
        private int memoryCacheSize;
        private int fileCacheSize;
        private long totalSize;
        private int hitCount;
        private int missCount;
        private double hitRate;

        // Getters and Setters
        public int getMemoryCacheSize() { return memoryCacheSize; }
        public void setMemoryCacheSize(int memoryCacheSize) { this.memoryCacheSize = memoryCacheSize; }
        public int getFileCacheSize() { return fileCacheSize; }
        public void setFileCacheSize(int fileCacheSize) { this.fileCacheSize = fileCacheSize; }
        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
        public int getHitCount() { return hitCount; }
        public void setHitCount(int hitCount) { this.hitCount = hitCount; }
        public int getMissCount() { return missCount; }
        public void setMissCount(int missCount) { this.missCount = missCount; }
        public double getHitRate() { return hitRate; }
        public void setHitRate(double hitRate) { this.hitRate = hitRate; }
    }
}
