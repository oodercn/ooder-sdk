package net.ooder.scene.driver.tiny;

import com.alibaba.fastjson.JSON;
import net.ooder.scene.spi.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件存储提供者 - Tiny 实现
 *
 * <p>基于文件系统的简单存储实现，适用于开发测试环境</p>
 *
 * <p>配置项：</p>
 * <pre>
 * scene.engine.tiny.storage.path: ./data
 * </pre>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(StorageProvider.class)
public class TinyStorageProvider implements StorageProvider {

    private static final Logger log = LoggerFactory.getLogger(TinyStorageProvider.class);

    private final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();

    @Value("${scene.engine.tiny.storage.path:./data}")
    private String storagePath;

    @Override
    public String getProviderType() {
        return "tiny";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String collection, String key, Class<T> type) {
        Map<String, Object> collectionCache = cache.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
        Object cached = collectionCache.get(key);
        if (cached != null) {
            return Optional.of(type.cast(cached));
        }

        Path filePath = Paths.get(storagePath, collection, key + ".json");
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try {
            String content = Files.readString(filePath);
            T value = JSON.parseObject(content, type);
            collectionCache.put(key, value);
            return Optional.of(value);
        } catch (IOException e) {
            log.error("Failed to read from storage: {}/{}", collection, key, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String collection, String key, T value) {
        Map<String, Object> collectionCache = cache.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
        collectionCache.put(key, value);

        try {
            Path dirPath = Paths.get(storagePath, collection);
            Files.createDirectories(dirPath);

            Path filePath = dirPath.resolve(key + ".json");
            String content = JSON.toJSONString(value, true);
            Files.writeString(filePath, content);

            log.debug("Stored: {}/{}", collection, key);
        } catch (IOException e) {
            log.error("Failed to write to storage: {}/{}", collection, key, e);
            throw new RuntimeException("Storage write failed", e);
        }
    }

    @Override
    public void remove(String collection, String key) {
        Map<String, Object> collectionCache = cache.get(collection);
        if (collectionCache != null) {
            collectionCache.remove(key);
        }

        Path filePath = Paths.get(storagePath, collection, key + ".json");
        try {
            Files.deleteIfExists(filePath);
            log.debug("Removed: {}/{}", collection, key);
        } catch (IOException e) {
            log.error("Failed to remove from storage: {}/{}", collection, key, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getAll(String collection, Class<T> type) {
        Map<String, T> result = new HashMap<>();
        Path dirPath = Paths.get(storagePath, collection);

        if (!Files.exists(dirPath)) {
            return result;
        }

        File dir = dirPath.toFile();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) {
            return result;
        }

        for (File file : files) {
            String key = file.getName().replace(".json", "");
            get(collection, key, type).ifPresent(value -> result.put(key, value));
        }

        return result;
    }

    @Override
    public boolean exists(String collection, String key) {
        Path filePath = Paths.get(storagePath, collection, key + ".json");
        return Files.exists(filePath);
    }

    @Override
    public void clear(String collection) {
        cache.remove(collection);

        Path dirPath = Paths.get(storagePath, collection);
        if (Files.exists(dirPath)) {
            try {
                Files.walk(dirPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.warn("Failed to delete: {}", path, e);
                        }
                    });
                log.debug("Cleared collection: {}", collection);
            } catch (IOException e) {
                log.error("Failed to clear collection: {}", collection, e);
            }
        }
    }
}
