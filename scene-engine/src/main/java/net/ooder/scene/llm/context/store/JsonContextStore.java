package net.ooder.scene.llm.context.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * JSON 上下文存储服务
 *
 * <p>提供异步的 JSON 文件存储能力，用于持久化上下文数据</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class JsonContextStore {

    private static final Logger log = LoggerFactory.getLogger(JsonContextStore.class);

    private final ObjectMapper objectMapper;
    private final Path storePath;
    private final ExecutorService executor;

    public JsonContextStore() {
        this("data/context");
    }

    public JsonContextStore(String storeDir) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        this.storePath = Paths.get(storeDir);
        this.executor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "json-store-worker");
            t.setDaemon(true);
            return t;
        });
        
        try {
            Files.createDirectories(storePath);
            log.info("JsonContextStore initialized at: {}", storePath.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Failed to create store directory: {}", storePath, e);
        }
    }

    public void saveAsync(String key, Object data) {
        if (key == null || data == null) {
            return;
        }
        executor.submit(() -> {
            try {
                saveSync(key, data);
            } catch (Exception e) {
                log.error("Failed to save data for key: {}", key, e);
            }
        });
    }

    public void saveSync(String key, Object data) throws IOException {
        if (key == null || data == null) {
            return;
        }
        Path filePath = storePath.resolve(key + ".json");
        objectMapper.writeValue(filePath.toFile(), data);
        log.debug("Saved data to: {}", filePath);
    }

    public <T> Optional<T> load(String key, Class<T> type) {
        if (key == null) {
            return Optional.empty();
        }
        Path filePath = storePath.resolve(key + ".json");
        File file = filePath.toFile();
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            T data = objectMapper.readValue(file, type);
            return Optional.ofNullable(data);
        } catch (IOException e) {
            log.error("Failed to load data for key: {}", key, e);
            return Optional.empty();
        }
    }

    public <T> Optional<T> load(String key, TypeReference<T> typeRef) {
        if (key == null) {
            return Optional.empty();
        }
        Path filePath = storePath.resolve(key + ".json");
        File file = filePath.toFile();
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            T data = objectMapper.readValue(file, typeRef);
            return Optional.ofNullable(data);
        } catch (IOException e) {
            log.error("Failed to load data for key: {}", key, e);
            return Optional.empty();
        }
    }

    public boolean delete(String key) {
        if (key == null) {
            return false;
        }
        Path filePath = storePath.resolve(key + ".json");
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete data for key: {}", key, e);
            return false;
        }
    }

    public void deleteAsync(String key) {
        if (key == null) {
            return;
        }
        executor.submit(() -> delete(key));
    }

    public boolean exists(String key) {
        if (key == null) {
            return false;
        }
        return storePath.resolve(key + ".json").toFile().exists();
    }

    public Set<String> listKeys() {
        File[] files = storePath.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return Collections.emptySet();
        }
        Set<String> keys = new HashSet<>();
        for (File file : files) {
            String name = file.getName();
            keys.add(name.substring(0, name.length() - 5));
        }
        return keys;
    }

    public void clear() {
        File[] files = storePath.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    log.warn("Failed to delete file: {}", file, e);
                }
            }
        }
        log.info("Cleared all stored data");
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("JsonContextStore shutdown");
    }
}
