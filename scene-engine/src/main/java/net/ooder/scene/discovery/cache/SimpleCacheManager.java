package net.ooder.scene.discovery.cache;

import net.ooder.scene.discovery.api.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单缓存管理器实现
 *
 * 两级缓存策略：
 * 1. 内存缓存：ConcurrentHashMap，快速访问
 * 2. 文件缓存：本地文件系统，持久化存储
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SimpleCacheManager implements CacheManager {

    private static final Logger log = LoggerFactory.getLogger(SimpleCacheManager.class);

    // 内存缓存
    private final ConcurrentHashMap<String, CacheEntry> memoryCache = new ConcurrentHashMap<>();

    // 配置
    private CacheConfig config = new CacheConfig();

    // 统计
    private final AtomicInteger hitCount = new AtomicInteger(0);
    private final AtomicInteger missCount = new AtomicInteger(0);

    // 定时清理任务
    private ScheduledExecutorService cleanupExecutor;

    /**
     * 缓存条目
     */
    private static class CacheEntry {
        final DiscoveryService.SkillInfo skill;
        final long cacheTime;
        final long expireTime;

        CacheEntry(DiscoveryService.SkillInfo skill, long ttlMs) {
            this.skill = skill;
            this.cacheTime = System.currentTimeMillis();
            this.expireTime = this.cacheTime + ttlMs;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    public SimpleCacheManager() {
        this(new CacheConfig());
    }

    public SimpleCacheManager(CacheConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        // 创建缓存目录
        if (config.isEnableFileCache()) {
            try {
                Files.createDirectories(Paths.get(config.getCacheDir()));
            } catch (IOException e) {
                log.warn("Failed to create cache directory: {}", config.getCacheDir(), e);
            }
        }

        // 启动定时清理任务
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cache-cleanup");
            t.setDaemon(true);
            return t;
        });
        cleanupExecutor.scheduleAtFixedRate(
            this::clearExpired,
            5, 5, TimeUnit.MINUTES
        );

        log.info("CacheManager initialized: memory={}, file={}",
            config.isEnableMemoryCache(), config.isEnableFileCache());
    }

    @Override
    public DiscoveryService.SkillInfo getSkill(String skillId) {
        return getSkill(skillId, null);
    }

    @Override
    public DiscoveryService.SkillInfo getSkill(String skillId, String version) {
        String cacheKey = buildCacheKey(skillId, version);

        // 1. 检查内存缓存
        if (config.isEnableMemoryCache()) {
            CacheEntry entry = memoryCache.get(cacheKey);
            if (entry != null) {
                if (!entry.isExpired()) {
                    hitCount.incrementAndGet();
                    log.debug("Memory cache hit: {}", skillId);
                    return entry.skill;
                } else {
                    memoryCache.remove(cacheKey);
                }
            }
        }

        // 2. 检查文件缓存
        if (config.isEnableFileCache()) {
            DiscoveryService.SkillInfo skill = loadFromFile(cacheKey);
            if (skill != null) {
                hitCount.incrementAndGet();
                // 回填内存缓存
                if (config.isEnableMemoryCache()) {
                    memoryCache.put(cacheKey, new CacheEntry(skill, config.getMemoryCacheTtlMs()));
                }
                return skill;
            }
        }

        missCount.incrementAndGet();
        return null;
    }

    @Override
    public List<DiscoveryService.SkillInfo> getAllSkills() {
        List<DiscoveryService.SkillInfo> skills = new ArrayList<>();

        // 从内存缓存获取
        memoryCache.values().stream()
            .filter(e -> !e.isExpired())
            .forEach(e -> skills.add(e.skill));

        return skills;
    }

    @Override
    public void putSkill(DiscoveryService.SkillInfo skill) {
        if (skill == null || skill.getSkillId() == null) {
            return;
        }

        String cacheKey = buildCacheKey(skill.getSkillId(), skill.getVersion());

        // 1. 存入内存缓存
        if (config.isEnableMemoryCache()) {
            memoryCache.put(cacheKey, new CacheEntry(skill, config.getMemoryCacheTtlMs()));

            // 检查容量限制
            if (memoryCache.size() > config.getMaxMemoryCacheSize()) {
                evictOldestMemoryEntry();
            }
        }

        // 2. 存入文件缓存
        if (config.isEnableFileCache()) {
            saveToFile(cacheKey, skill);
        }

        log.debug("Skill cached: {} v{}", skill.getSkillId(), skill.getVersion());
    }

    @Override
    public void putSkills(List<DiscoveryService.SkillInfo> skills) {
        if (skills == null) return;
        for (DiscoveryService.SkillInfo skill : skills) {
            putSkill(skill);
        }
    }

    @Override
    public void removeSkill(String skillId) {
        // 从内存缓存移除
        memoryCache.keySet().removeIf(key -> key.startsWith(skillId + ":"));

        // 从文件缓存移除
        if (config.isEnableFileCache()) {
            try {
                Path cacheDir = Paths.get(config.getCacheDir());
                Files.list(cacheDir)
                    .filter(p -> p.getFileName().toString().startsWith(skillId + "_"))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            log.warn("Failed to delete cache file: {}", p, e);
                        }
                    });
            } catch (IOException e) {
                log.warn("Failed to list cache directory", e);
            }
        }

        log.debug("Skill removed from cache: {}", skillId);
    }

    @Override
    public boolean exists(String skillId) {
        return exists(skillId, null);
    }

    @Override
    public boolean exists(String skillId, String version) {
        return getStatus(skillId).isCached();
    }

    @Override
    public CacheStatus getStatus(String skillId) {
        CacheStatus status = new CacheStatus();
        String cacheKey = buildCacheKey(skillId, null);

        // 检查内存缓存
        CacheEntry entry = memoryCache.get(cacheKey);
        if (entry != null) {
            status.setCached(true);
            status.setExpired(entry.isExpired());
            status.setCacheTime(entry.cacheTime);
            status.setExpireTime(entry.expireTime);
            status.setLocation("memory");
            return status;
        }

        // 检查文件缓存
        if (config.isEnableFileCache()) {
            Path filePath = getCacheFilePath(cacheKey);
            if (Files.exists(filePath)) {
                try {
                    long lastModified = Files.getLastModifiedTime(filePath).toMillis();
                    long expireTime = lastModified + config.getFileCacheTtlMs();
                    status.setCached(true);
                    status.setExpired(System.currentTimeMillis() > expireTime);
                    status.setCacheTime(lastModified);
                    status.setExpireTime(expireTime);
                    status.setLocation("file");
                    status.setSize(Files.size(filePath));
                } catch (IOException e) {
                    log.warn("Failed to get file attributes: {}", filePath, e);
                }
            }
        }

        return status;
    }

    @Override
    public void clear() {
        memoryCache.clear();

        if (config.isEnableFileCache()) {
            try {
                Path cacheDir = Paths.get(config.getCacheDir());
                Files.list(cacheDir).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        log.warn("Failed to delete cache file: {}", p, e);
                    }
                });
            } catch (IOException e) {
                log.warn("Failed to clear file cache", e);
            }
        }

        log.info("Cache cleared");
    }

    @Override
    public int clearExpired() {
        int count = 0;

        // 清理内存缓存
        Iterator<Map.Entry<String, CacheEntry>> it = memoryCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CacheEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                count++;
            }
        }

        // 清理文件缓存
        if (config.isEnableFileCache()) {
            try {
                Path cacheDir = Paths.get(config.getCacheDir());
                long now = System.currentTimeMillis();
                Files.list(cacheDir).forEach(p -> {
                    try {
                        long lastModified = Files.getLastModifiedTime(p).toMillis();
                        if (now - lastModified > config.getFileCacheTtlMs()) {
                            Files.deleteIfExists(p);
                        }
                    } catch (IOException e) {
                        log.warn("Failed to check/delete cache file: {}", p, e);
                    }
                });
            } catch (IOException e) {
                log.warn("Failed to clean file cache", e);
            }
        }

        if (count > 0) {
            log.debug("Cleared {} expired cache entries", count);
        }
        return count;
    }

    @Override
    public CacheStats getStats() {
        CacheStats stats = new CacheStats();
        stats.setMemoryCacheSize(memoryCache.size());

        // 计算命中率
        int total = hitCount.get() + missCount.get();
        stats.setHitCount(hitCount.get());
        stats.setMissCount(missCount.get());
        stats.setHitRate(total > 0 ? (double) hitCount.get() / total : 0);

        // 计算文件缓存大小
        if (config.isEnableFileCache()) {
            try {
                Path cacheDir = Paths.get(config.getCacheDir());
                long size = Files.list(cacheDir)
                    .filter(Files::isRegularFile)
                    .count();
                stats.setFileCacheSize((int) size);
            } catch (IOException e) {
                log.warn("Failed to get file cache stats", e);
            }
        }

        return stats;
    }

    @Override
    public void setConfig(CacheConfig config) {
        this.config = config;
    }

    // ========== 私有方法 ==========

    private String buildCacheKey(String skillId, String version) {
        return version != null ? skillId + ":" + version : skillId + ":latest";
    }

    private Path getCacheFilePath(String cacheKey) {
        String fileName = cacheKey.replace(":", "_") + ".cache";
        return Paths.get(config.getCacheDir(), fileName);
    }

    private void saveToFile(String cacheKey, DiscoveryService.SkillInfo skill) {
        Path filePath = getCacheFilePath(cacheKey);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(filePath)))) {
            oos.writeObject(skill);
        } catch (IOException e) {
            log.warn("Failed to save cache file: {}", filePath, e);
        }
    }

    @SuppressWarnings("unchecked")
    private DiscoveryService.SkillInfo loadFromFile(String cacheKey) {
        Path filePath = getCacheFilePath(cacheKey);
        if (!Files.exists(filePath)) {
            return null;
        }

        try {
            // 检查是否过期
            long lastModified = Files.getLastModifiedTime(filePath).toMillis();
            if (System.currentTimeMillis() - lastModified > config.getFileCacheTtlMs()) {
                Files.deleteIfExists(filePath);
                return null;
            }

            try (ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(Files.newInputStream(filePath)))) {
                return (DiscoveryService.SkillInfo) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Failed to load cache file: {}", filePath, e);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }
        return null;
    }

    private void evictOldestMemoryEntry() {
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;

        for (Map.Entry<String, CacheEntry> entry : memoryCache.entrySet()) {
            if (entry.getValue().cacheTime < oldestTime) {
                oldestTime = entry.getValue().cacheTime;
                oldestKey = entry.getKey();
            }
        }

        if (oldestKey != null) {
            memoryCache.remove(oldestKey);
            log.debug("Evicted oldest cache entry: {}", oldestKey);
        }
    }

    /**
     * 关闭缓存管理器
     */
    public void shutdown() {
        if (cleanupExecutor != null) {
            cleanupExecutor.shutdown();
        }
    }
}
