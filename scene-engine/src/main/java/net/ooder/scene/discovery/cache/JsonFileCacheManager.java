package net.ooder.scene.discovery.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.scene.discovery.UnifiedDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON文件缓存管理器
 * 
 * 管理本地JSON文件缓存，用于存储GitHub/Gitee的发现结果
 * 避免频繁访问远程API（每小时60次限制）
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class JsonFileCacheManager {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonFileCacheManager.class);
    private static final String CACHE_FILE_EXTENSION = ".json";
    private static final String CACHE_METADATA_FILE = "cache-metadata.json";
    
    private final ObjectMapper objectMapper;
    private final UnifiedDiscoveryService.CacheConfig config;
    private final Map<String, CacheEntry> memoryCache;
    private final Path cacheDirPath;
    
    public JsonFileCacheManager(UnifiedDiscoveryService.CacheConfig config) {
        this.config = config != null ? config : new UnifiedDiscoveryService.CacheConfig();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.memoryCache = new ConcurrentHashMap<>();
        this.cacheDirPath = Paths.get(this.config.getCacheDir());
        
        // 确保缓存目录存在
        ensureCacheDirExists();
    }
    
    public JsonFileCacheManager() {
        this(new UnifiedDiscoveryService.CacheConfig());
    }
    
    /**
     * 获取缓存
     *
     * @param cacheKey 缓存键
     * @param type 数据类型
     * @param <T> 类型参数
     * @return 缓存数据，如果不存在或已过期则返回null
     */
    public <T> T get(String cacheKey, Class<T> type) {
        // 1. 检查内存缓存
        if (config.isEnableMemoryCache()) {
            CacheEntry entry = memoryCache.get(cacheKey);
            if (entry != null && !entry.isExpired()) {
                logger.debug("Memory cache hit: {}", cacheKey);
                return entry.getData(type);
            }
        }
        
        // 2. 检查文件缓存
        if (config.isEnableFileCache()) {
            T data = readFromFile(cacheKey, type);
            if (data != null) {
                logger.debug("File cache hit: {}", cacheKey);
                // 同步到内存缓存
                if (config.isEnableMemoryCache()) {
                    CacheEntry entry = readCacheEntryFromFile(cacheKey);
                    if (entry != null) {
                        memoryCache.put(cacheKey, entry);
                    }
                }
                return data;
            }
        }
        
        return null;
    }
    
    /**
     * 设置缓存
     *
     * @param cacheKey 缓存键
     * @param data 缓存数据
     * @param <T> 类型参数
     */
    public <T> void put(String cacheKey, T data) {
        CacheEntry entry = new CacheEntry(data, System.currentTimeMillis(), 
            System.currentTimeMillis() + config.getCacheTtlMs());
        
        // 1. 写入内存缓存
        if (config.isEnableMemoryCache()) {
            memoryCache.put(cacheKey, entry);
        }
        
        // 2. 写入文件缓存
        if (config.isEnableFileCache()) {
            writeToFile(cacheKey, entry);
        }
        
        logger.debug("Cache put: {}", cacheKey);
    }
    
    /**
     * 检查缓存是否存在且有效
     *
     * @param cacheKey 缓存键
     * @return 是否存在有效缓存
     */
    public boolean isValid(String cacheKey) {
        // 检查内存缓存
        if (config.isEnableMemoryCache()) {
            CacheEntry entry = memoryCache.get(cacheKey);
            if (entry != null && !entry.isExpired()) {
                return true;
            }
        }
        
        // 检查文件缓存
        if (config.isEnableFileCache()) {
            CacheEntry entry = readCacheEntryFromFile(cacheKey);
            if (entry != null && !entry.isExpired()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取缓存状态
     *
     * @param cacheKey 缓存键
     * @return 缓存状态
     */
    public UnifiedDiscoveryService.CacheStatus getCacheStatus(String cacheKey) {
        UnifiedDiscoveryService.CacheStatus status = new UnifiedDiscoveryService.CacheStatus();
        
        // 检查内存缓存
        CacheEntry memoryEntry = memoryCache.get(cacheKey);
        if (memoryEntry != null) {
            status.setCached(true);
            status.setCacheTime(memoryEntry.getCacheTime());
            status.setExpireTime(memoryEntry.getExpireTime());
        }
        
        // 检查文件缓存
        Path cacheFile = getCacheFilePath(cacheKey);
        if (Files.exists(cacheFile)) {
            try {
                status.setCached(true);
                status.setSize(Files.size(cacheFile));
                status.setCacheFile(cacheFile.toString());
                
                CacheEntry fileEntry = readCacheEntryFromFile(cacheKey);
                if (fileEntry != null) {
                    status.setCacheTime(fileEntry.getCacheTime());
                    status.setExpireTime(fileEntry.getExpireTime());
                }
            } catch (IOException e) {
                logger.warn("Failed to get cache file size: {}", cacheFile, e);
            }
        }
        
        return status;
    }
    
    /**
     * 清除指定缓存
     *
     * @param cacheKey 缓存键
     */
    public void invalidate(String cacheKey) {
        // 清除内存缓存
        memoryCache.remove(cacheKey);
        
        // 清除文件缓存
        if (config.isEnableFileCache()) {
            try {
                Path cacheFile = getCacheFilePath(cacheKey);
                Files.deleteIfExists(cacheFile);
                logger.debug("Cache invalidated: {}", cacheKey);
            } catch (IOException e) {
                logger.warn("Failed to delete cache file: {}", cacheKey, e);
            }
        }
    }
    
    /**
     * 清除所有缓存
     */
    public void clearAll() {
        // 清除内存缓存
        memoryCache.clear();
        
        // 清除文件缓存
        if (config.isEnableFileCache()) {
            try {
                File cacheDir = cacheDirPath.toFile();
                if (cacheDir.exists() && cacheDir.isDirectory()) {
                    File[] files = cacheDir.listFiles((dir, name) -> name.endsWith(CACHE_FILE_EXTENSION));
                    if (files != null) {
                        for (File file : files) {
                            file.delete();
                        }
                    }
                }
                logger.info("All cache cleared");
            } catch (Exception e) {
                logger.error("Failed to clear all cache", e);
            }
        }
    }
    
    /**
     * 生成缓存键
     *
     * @param repositoryUrl 仓库地址
     * @param operation 操作类型
     * @param params 参数
     * @return 缓存键
     */
    public String generateCacheKey(String repositoryUrl, String operation, String... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(repositoryUrl.hashCode()).append("_").append(operation);
        for (String param : params) {
            if (param != null) {
                sb.append("_").append(param.hashCode());
            }
        }
        return sb.toString();
    }
    
    // ========== 私有方法 ==========
    
    private void ensureCacheDirExists() {
        try {
            if (!Files.exists(cacheDirPath)) {
                Files.createDirectories(cacheDirPath);
                logger.info("Cache directory created: {}", cacheDirPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create cache directory: {}", cacheDirPath, e);
        }
    }
    
    private Path getCacheFilePath(String cacheKey) {
        String fileName = hashCacheKey(cacheKey) + CACHE_FILE_EXTENSION;
        return cacheDirPath.resolve(fileName);
    }
    
    private String hashCacheKey(String cacheKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(cacheKey.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果SHA-256不可用，使用简单哈希
            return String.valueOf(cacheKey.hashCode());
        }
    }
    
    private <T> T readFromFile(String cacheKey, Class<T> type) {
        try {
            Path cacheFile = getCacheFilePath(cacheKey);
            if (!Files.exists(cacheFile)) {
                return null;
            }
            
            String content = new String(Files.readAllBytes(cacheFile), StandardCharsets.UTF_8);
            CacheEntry entry = objectMapper.readValue(content, CacheEntry.class);
            
            if (entry.isExpired()) {
                // 过期缓存，删除
                Files.deleteIfExists(cacheFile);
                return null;
            }
            
            return entry.getData(type);
        } catch (IOException e) {
            logger.warn("Failed to read cache from file: {}", cacheKey, e);
            return null;
        }
    }
    
    private CacheEntry readCacheEntryFromFile(String cacheKey) {
        try {
            Path cacheFile = getCacheFilePath(cacheKey);
            if (!Files.exists(cacheFile)) {
                return null;
            }
            
            String content = new String(Files.readAllBytes(cacheFile), StandardCharsets.UTF_8);
            return objectMapper.readValue(content, CacheEntry.class);
        } catch (IOException e) {
            logger.warn("Failed to read cache entry from file: {}", cacheKey, e);
            return null;
        }
    }
    
    private void writeToFile(String cacheKey, CacheEntry entry) {
        try {
            Path cacheFile = getCacheFilePath(cacheKey);
            String content = objectMapper.writeValueAsString(entry);
            Files.write(cacheFile, content.getBytes(StandardCharsets.UTF_8));
            logger.debug("Cache written to file: {}", cacheFile);
        } catch (IOException e) {
            logger.error("Failed to write cache to file: {}", cacheKey, e);
        }
    }
    
    /**
     * 缓存条目
     */
    private static class CacheEntry {
        private Object data;
        private long cacheTime;
        private long expireTime;
        
        public CacheEntry() {}
        
        public CacheEntry(Object data, long cacheTime, long expireTime) {
            this.data = data;
            this.cacheTime = cacheTime;
            this.expireTime = expireTime;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
        
        @SuppressWarnings("unchecked")
        public <T> T getData(Class<T> type) {
            if (data == null) {
                return null;
            }
            // 使用ObjectMapper进行类型转换
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(data, type);
        }
        
        // Getters and Setters
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public long getCacheTime() { return cacheTime; }
        public void setCacheTime(long cacheTime) { this.cacheTime = cacheTime; }
        public long getExpireTime() { return expireTime; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
    }
}
