package net.ooder.sdk.service.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.sdk.api.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Storage Service Implementation
 *
 * <p>Thread-safe JSON file-based storage implementation.</p>
 *
 * @author ooder Team
 * @since 2.3
 */
public class StorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);

    private final ObjectMapper objectMapper;
    /** 内存缓存，存储序列化后的对象 */
    private final Map<String, Object> cache;
    private final ExecutorService executorService;
    private String basePath;
    private boolean useCache;

    public StorageServiceImpl() {
        this("./data/storage");
    }

    public StorageServiceImpl(String basePath) {
        this.basePath = basePath;
        this.objectMapper = new ObjectMapper();
        this.cache = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(4);
        this.useCache = true;
        ensureDirectoryExists();
        log.info("StorageServiceImpl initialized with basePath: {}", basePath);
    }

    private void ensureDirectoryExists() {
        try {
            Path path = Paths.get(basePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created storage directory: {}", basePath);
            }
        } catch (IOException e) {
            log.error("Failed to create storage directory: {}", basePath, e);
        }
    }

    private Path getKeyPath(String key) {
        String safeKey = key.replaceAll("[^a-zA-Z0-9_\\-:.]", "_");
        return Paths.get(basePath, safeKey + ".json");
    }

    @Override
    public String save(String key, Object data) {
        log.debug("Saving data for key: {}", key);
        try {
            if (useCache) {
                cache.put(key, data);
            }
            Path path = getKeyPath(key);
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            Files.write(path, json.getBytes("UTF-8"));
            log.debug("Saved data for key: {}", key);
            return key;
        } catch (Exception e) {
            log.error("Failed to save data for key: {}", key, e);
            throw new RuntimeException("Failed to save data: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> Optional<T> load(String key, Class<T> type) {
        log.debug("Loading data for key: {} with type: {}", key, type.getSimpleName());
        try {
            if (useCache && cache.containsKey(key)) {
                Object cached = cache.get(key);
                if (type.isInstance(cached)) {
                    return Optional.of(type.cast(cached));
                }
            }
            Path path = getKeyPath(key);
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            String json = new String(Files.readAllBytes(path), "UTF-8");
            T data = objectMapper.readValue(json, type);
            if (useCache) {
                cache.put(key, data);
            }
            return Optional.ofNullable(data);
        } catch (Exception e) {
            log.error("Failed to load data for key: {}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> load(String key, net.ooder.sdk.api.storage.TypeReference<T> typeRef) {
        log.debug("Loading data for key: {} with TypeReference", key);
        try {
            if (useCache && cache.containsKey(key)) {
                Object cached = cache.get(key);
                JavaType javaType = objectMapper.constructType(typeRef.getType());
                if (javaType.getRawClass().isInstance(cached)) {
                    return Optional.of((T) cached);
                }
            }
            Path path = getKeyPath(key);
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            String json = new String(Files.readAllBytes(path), "UTF-8");
            JavaType javaType = objectMapper.constructType(typeRef.getType());
            T data = objectMapper.readValue(json, javaType);
            if (useCache) {
                cache.put(key, data);
            }
            return Optional.ofNullable(data);
        } catch (Exception e) {
            log.error("Failed to load data for key: {} with TypeReference", key, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String key) {
        log.debug("Deleting data for key: {}", key);
        try {
            cache.remove(key);
            Path path = getKeyPath(key);
            if (Files.exists(path)) {
                Files.delete(path);
                log.debug("Deleted data for key: {}", key);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to delete data for key: {}", key, e);
            return false;
        }
    }

    @Override
    public boolean exists(String key) {
        if (useCache && cache.containsKey(key)) {
            return true;
        }
        Path path = getKeyPath(key);
        return Files.exists(path);
    }

    @Override
    public List<String> listKeys(String prefix) {
        log.debug("Listing keys with prefix: {}", prefix);
        try {
            File dir = new File(basePath);
            if (!dir.exists() || !dir.isDirectory()) {
                return new ArrayList<String>();
            }
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files == null) {
                return new ArrayList<String>();
            }
            List<String> keys = new ArrayList<String>();
            for (File file : files) {
                String name = file.getName();
                String key = name.substring(0, name.length() - 5);
                if (prefix == null || prefix.isEmpty() || key.startsWith(prefix)) {
                    keys.add(key);
                }
            }
            return keys;
        } catch (Exception e) {
            log.error("Failed to list keys with prefix: {}", prefix, e);
            return new ArrayList<String>();
        }
    }

    @Override
    public CompletableFuture<String> saveAsync(String key, Object data) {
        return CompletableFuture.supplyAsync(() -> save(key, data), executorService);
    }

    @Override
    public <T> CompletableFuture<Optional<T>> loadAsync(String key, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> load(key, type), executorService);
    }

    @Override
    public <T> CompletableFuture<Optional<T>> loadAsync(String key, net.ooder.sdk.api.storage.TypeReference<T> typeRef) {
        return CompletableFuture.supplyAsync(() -> load(key, typeRef), executorService);
    }

    @Override
    public CompletableFuture<Boolean> deleteAsync(String key) {
        return CompletableFuture.supplyAsync(() -> delete(key), executorService);
    }

    /**
     * 批量保存数据
     * @param batch 批量数据，key 为存储键，value 为任意类型对象
     */
    @Override
    public void saveBatch(Map<String, Object> batch) {
        log.debug("Saving batch of {} items", batch.size());
        for (Map.Entry<String, Object> entry : batch.entrySet()) {
            save(entry.getKey(), entry.getValue());
        }
        log.debug("Batch save completed");
    }

    @Override
    public CompletableFuture<Void> saveBatchAsync(Map<String, Object> batch) {
        return CompletableFuture.runAsync(() -> saveBatch(batch), executorService);
    }

    /**
     * 批量加载数据
     * @param keys 存储键列表
     * @return 批量数据，key 为存储键，value 为反序列化后的对象
     */
    @Override
    public Map<String, Object> loadBatch(List<String> keys) {
        log.debug("Loading batch of {} items", keys.size());
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : keys) {
            Optional<Object> data = load(key, Object.class);
            if (data.isPresent()) {
                result.put(key, data.get());
            }
        }
        return result;
    }

    @Override
    public CompletableFuture<Map<String, Object>> loadBatchAsync(List<String> keys) {
        return CompletableFuture.supplyAsync(() -> loadBatch(keys), executorService);
    }

    /**
     * 批量保存数据（类型安全版本）
     * @param batch 批量数据，key 为存储键，value 为指定类型对象
     * @param type 数据类型
     * @param <T> 类型参数
     */
    @Override
    public <T> void saveBatch(Map<String, T> batch, Class<T> type) {
        log.debug("Saving typed batch of {} items with type {}", batch.size(), type.getSimpleName());
        for (Map.Entry<String, T> entry : batch.entrySet()) {
            save(entry.getKey(), entry.getValue());
        }
        log.debug("Typed batch save completed");
    }

    /**
     * 异步批量保存数据（类型安全版本）
     * @param batch 批量数据，key 为存储键，value 为指定类型对象
     * @param type 数据类型
     * @param <T> 类型参数
     * @return CompletableFuture
     */
    @Override
    public <T> CompletableFuture<Void> saveBatchAsync(Map<String, T> batch, Class<T> type) {
        return CompletableFuture.runAsync(() -> saveBatch(batch, type), executorService);
    }

    /**
     * 批量加载数据（类型安全版本）
     * @param keys 存储键列表
     * @param type 数据类型
     * @param <T> 类型参数
     * @return 批量数据，key 为存储键，value 为指定类型对象
     */
    @Override
    public <T> Map<String, T> loadBatch(List<String> keys, Class<T> type) {
        log.debug("Loading typed batch of {} items with type {}", keys.size(), type.getSimpleName());
        Map<String, T> result = new HashMap<>();
        for (String key : keys) {
            Optional<T> data = load(key, type);
            if (data.isPresent()) {
                result.put(key, data.get());
            }
        }
        return result;
    }

    /**
     * 异步批量加载数据（类型安全版本）
     * @param keys 存储键列表
     * @param type 数据类型
     * @param <T> 类型参数
     * @return CompletableFuture
     */
    @Override
    public <T> CompletableFuture<Map<String, T>> loadBatchAsync(List<String> keys, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> loadBatch(keys, type), executorService);
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public void setBasePath(String basePath) {
        this.basePath = basePath;
        ensureDirectoryExists();
        log.info("Base path changed to: {}", basePath);
    }

    @Override
    public void clear() {
        log.info("Clearing all stored data");
        cache.clear();
        try {
            File dir = new File(basePath);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to clear storage", e);
        }
        log.info("Storage cleared");
    }

    @Override
    public long size() {
        return listKeys(null).size();
    }

    @Override
    public void shutdown() {
        log.info("Shutting down StorageService");
        executorService.shutdown();
        cache.clear();
        log.info("StorageService shutdown complete");
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
        if (!useCache) {
            cache.clear();
        }
    }

    public boolean isUseCache() {
        return useCache;
    }
}
