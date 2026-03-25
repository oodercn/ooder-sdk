package net.ooder.scene.fallback;

import net.ooder.scene.spi.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存存储提供者 - 降级实现
 *
 * <p>当没有其他 StorageProvider 实现时自动启用</p>
 *
 * <p>警告：重启后数据丢失，仅用于开发测试</p>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(StorageProvider.class)
@ConditionalOnProperty(prefix = "scene.engine.fallback", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InMemoryStorageProvider implements StorageProvider {

    private static final Logger log = LoggerFactory.getLogger(InMemoryStorageProvider.class);

    private final Map<String, Map<String, Object>> store = new ConcurrentHashMap<>();

    public InMemoryStorageProvider() {
        log.warn("Using InMemoryStorageProvider - data will be lost on restart!");
    }

    @Override
    public String getProviderType() {
        return "fallback";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String collection, String key, Class<T> type) {
        Map<String, Object> collectionData = store.get(collection);
        if (collectionData == null) {
            return Optional.empty();
        }

        Object value = collectionData.get(key);
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(type.cast(value));
        } catch (ClassCastException e) {
            log.error("Type mismatch for {}/{}", collection, key, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String collection, String key, T value) {
        Map<String, Object> collectionData = store.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
        collectionData.put(key, value);
        log.debug("Stored in memory: {}/{}", collection, key);
    }

    @Override
    public void remove(String collection, String key) {
        Map<String, Object> collectionData = store.get(collection);
        if (collectionData != null) {
            collectionData.remove(key);
            log.debug("Removed from memory: {}/{}", collection, key);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getAll(String collection, Class<T> type) {
        Map<String, Object> collectionData = store.get(collection);
        if (collectionData == null) {
            return new HashMap<>();
        }

        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : collectionData.entrySet()) {
            try {
                result.put(entry.getKey(), type.cast(entry.getValue()));
            } catch (ClassCastException e) {
                log.warn("Type mismatch for {}/{}", collection, entry.getKey());
            }
        }

        return result;
    }

    @Override
    public boolean exists(String collection, String key) {
        Map<String, Object> collectionData = store.get(collection);
        return collectionData != null && collectionData.containsKey(key);
    }

    @Override
    public void clear(String collection) {
        store.remove(collection);
        log.info("Cleared collection from memory: {}", collection);
    }
}
