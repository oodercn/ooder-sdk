package net.ooder.scene.discovery.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON文件缓存管理器
 *
 * @author ooder
 * @since 2.3.1
 */
public class JsonFileCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileCacheManager.class);

    private final String cacheDir;
    private final int maxEntries;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public JsonFileCacheManager() {
        this("./.ooder/cache/discovery", 100);
    }

    public JsonFileCacheManager(String cacheDir, int maxEntries) {
        this.cacheDir = cacheDir;
        this.maxEntries = maxEntries;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        initCacheDir();
    }

    private void initCacheDir() {
        try {
            Files.createDirectories(Paths.get(cacheDir));
            logger.info("Cache directory initialized: {}", cacheDir);
        } catch (IOException e) {
            logger.error("Failed to create cache directory: " + cacheDir, e);
        }
    }

    public void put(String key, List<SkillPackage> skills, long ttlMs) {
        try {
            CacheEntry entry = new CacheEntry();
            entry.setSkills(skills);
            entry.setTimestamp(System.currentTimeMillis());
            entry.setTtl(ttlMs);

            cache.put(key, entry);
            saveToFile(key, entry);

            logger.debug("Cached {} skills for key: {}", skills.size(), key);
        } catch (Exception e) {
            logger.error("Failed to cache skills for key: " + key, e);
        }
    }

    public List<SkillPackage> get(String key) {
        try {
            CacheEntry entry = cache.get(key);
            if (entry == null) {
                entry = loadFromFile(key);
                if (entry != null) {
                    cache.put(key, entry);
                }
            }

            if (entry != null && !entry.isExpired()) {
                return entry.getSkills();
            }
        } catch (Exception e) {
            logger.error("Failed to get cached skills for key: " + key, e);
        }
        return new ArrayList<>();
    }

    public boolean exists(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return true;
        }

        entry = loadFromFile(key);
        if (entry != null && !entry.isExpired()) {
            cache.put(key, entry);
            return true;
        }

        return false;
    }

    public void invalidate(String key) {
        cache.remove(key);
        deleteFile(key);
        logger.debug("Cache invalidated for key: {}", key);
    }

    public void clearAll() {
        cache.clear();
        try {
            Files.walk(Paths.get(cacheDir))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        logger.error("Failed to delete cache file: " + path, e);
                    }
                });
        } catch (IOException e) {
            logger.error("Failed to clear cache directory: " + cacheDir, e);
        }
    }

    private void saveToFile(String key, CacheEntry entry) {
        try {
            String fileName = getCacheFileName(key);
            Path filePath = Paths.get(cacheDir, fileName);

            objectMapper.writeValue(filePath.toFile(), entry);

            logger.debug("Saved cache to file: {}", filePath);
        } catch (Exception e) {
            logger.error("Failed to save cache to file for key: " + key, e);
        }
    }

    private CacheEntry loadFromFile(String key) {
        try {
            String fileName = getCacheFileName(key);
            Path filePath = Paths.get(cacheDir, fileName);

            if (!Files.exists(filePath)) {
                return null;
            }

            return objectMapper.readValue(filePath.toFile(), CacheEntry.class);
        } catch (Exception e) {
            logger.error("Failed to load cache from file for key: " + key, e);
            return null;
        }
    }

    private void deleteFile(String key) {
        try {
            String fileName = getCacheFileName(key);
            Path filePath = Paths.get(cacheDir, fileName);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            logger.error("Failed to delete cache file for key: " + key, e);
        }
    }

    private String getCacheFileName(String key) {
        return key.replaceAll("[^a-zA-Z0-9_-]", "_") + ".json";
    }

    public static class CacheEntry {
        
        @JsonProperty("skills")
        private List<SkillPackage> skills;
        
        @JsonProperty("timestamp")
        private long timestamp;
        
        @JsonProperty("ttl")
        private long ttl;

        public CacheEntry() {}

        public List<SkillPackage> getSkills() {
            return skills;
        }

        public void setSkills(List<SkillPackage> skills) {
            this.skills = skills;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getTtl() {
            return ttl;
        }

        public void setTtl(long ttl) {
            this.ttl = ttl;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > (timestamp + ttl);
        }
    }
}
