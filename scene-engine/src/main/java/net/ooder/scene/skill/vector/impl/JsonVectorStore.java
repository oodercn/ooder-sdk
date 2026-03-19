package net.ooder.scene.skill.vector.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.VectorData;
import net.ooder.scene.skill.vector.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JSON文件向量存储实现
 *
 * <p>基于JSON文件的持久化向量存储，支持数据恢复。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class JsonVectorStore implements VectorStore {

    private static final Logger log = LoggerFactory.getLogger(JsonVectorStore.class);

    private static final String VECTORS_FILE = "vectors.json";

    private final String basePath;
    private final int dimension;
    private final ObjectMapper objectMapper;
    private final boolean autoSave;
    private final long saveIntervalMs;

    private final Map<String, VectorEntry> vectors = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private volatile boolean dirty = false;
    private boolean initialized = false;

    public JsonVectorStore(String basePath, int dimension) {
        this(basePath, dimension, true, 5000);
    }

    public JsonVectorStore(String basePath, int dimension, boolean autoSave, long saveIntervalMs) {
        this.basePath = basePath;
        this.dimension = dimension;
        this.autoSave = autoSave;
        this.saveIntervalMs = saveIntervalMs;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void initialize() {
        log.info("Initializing JsonVectorStore at: {}", basePath);

        try {
            Path baseDir = Paths.get(basePath);
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
                log.info("Created base directory: {}", basePath);
            }

            loadVectors();

            if (autoSave) {
                startAutoSave();
            }

            initialized = true;
            log.info("JsonVectorStore initialized. Loaded {} vectors, dimension: {}", vectors.size(), dimension);

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize JsonVectorStore: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (dirty) {
            saveVectors();
        }

        initialized = false;
        log.info("JsonVectorStore closed");
    }

    private void loadVectors() {
        File file = Paths.get(basePath, VECTORS_FILE).toFile();
        if (!file.exists()) {
            return;
        }

        try {
            List<VectorEntry> list = objectMapper.readValue(file, new TypeReference<List<VectorEntry>>() {});
            for (VectorEntry entry : list) {
                vectors.put(entry.id, entry);
            }
            log.debug("Loaded {} vectors", vectors.size());
        } catch (IOException e) {
            log.error("Failed to load vectors: {}", e.getMessage(), e);
        }
    }

    private synchronized void saveVectors() {
        try {
            File file = Paths.get(basePath, VECTORS_FILE).toFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<>(vectors.values()));
            dirty = false;
            log.debug("Saved {} vectors", vectors.size());
        } catch (IOException e) {
            log.error("Failed to save vectors: {}", e.getMessage(), e);
        }
    }

    private void startAutoSave() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "json-vector-autosave");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(() -> {
            if (dirty) {
                saveVectors();
            }
        }, saveIntervalMs, saveIntervalMs, TimeUnit.MILLISECONDS);
        log.debug("Auto-save started with interval: {}ms", saveIntervalMs);
    }

    private void markDirty() {
        dirty = true;
    }

    @Override
    public void insert(String id, float[] vector, Map<String, Object> metadata) {
        validateVector(vector);

        VectorEntry entry = new VectorEntry(id, vector.clone(), metadata != null ? new HashMap<>(metadata) : new HashMap<>());
        vectors.put(id, entry);
        markDirty();

        log.debug("Inserted vector: {}, total: {}", id, vectors.size());
    }

    @Override
    public void batchInsert(List<VectorData> dataList) {
        for (VectorData data : dataList) {
            insert(data.getId(), data.getVector(), data.getMetadata());
        }
        log.info("Batch inserted {} vectors, total: {}", dataList.size(), vectors.size());
    }

    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        validateVector(queryVector);

        List<SearchResult> results = new ArrayList<>();

        for (VectorEntry entry : vectors.values()) {
            if (!matchesFilters(entry.metadata, filters)) {
                continue;
            }

            float score = cosineSimilarity(queryVector, entry.vector);
            SearchResult result = new SearchResult(entry.id, score, entry.metadata);
            result.setVector(entry.vector);
            results.add(result);
        }

        results.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));

        if (results.size() > topK) {
            results = results.subList(0, topK);
        }

        log.debug("Search returned {} results", results.size());
        return results;
    }

    @Override
    public void delete(String id) {
        VectorEntry removed = vectors.remove(id);
        if (removed != null) {
            markDirty();
            log.debug("Deleted vector: {}", id);
        }
    }

    @Override
    public void deleteByMetadata(Map<String, Object> filters) {
        List<String> toDelete = new ArrayList<>();

        for (Map.Entry<String, VectorEntry> entry : vectors.entrySet()) {
            if (matchesFilters(entry.getValue().metadata, filters)) {
                toDelete.add(entry.getKey());
            }
        }

        for (String id : toDelete) {
            vectors.remove(id);
        }

        if (!toDelete.isEmpty()) {
            markDirty();
        }

        log.info("Deleted {} vectors by metadata filters", toDelete.size());
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public long count() {
        return vectors.size();
    }

    @Override
    public void clear() {
        vectors.clear();
        markDirty();
        log.info("Cleared all vectors");
    }

    private void validateVector(float[] vector) {
        if (vector == null || vector.length != dimension) {
            throw new IllegalArgumentException(
                "Vector dimension mismatch. Expected: " + dimension + ", Actual: " + 
                (vector != null ? vector.length : "null")
            );
        }
    }

    private boolean matchesFilters(Map<String, Object> metadata, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object value = metadata.get(filter.getKey());
            if (!Objects.equals(value, filter.getValue())) {
                return false;
            }
        }

        return true;
    }

    private float cosineSimilarity(float[] v1, float[] v2) {
        float dotProduct = 0;
        float norm1 = 0;
        float norm2 = 0;

        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }

        return dotProduct / (float) (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public void forceSave() {
        saveVectors();
    }

    public String getStorageType() {
        return "json";
    }

    public static class VectorEntry {
        public String id;
        public float[] vector;
        public Map<String, Object> metadata;

        public VectorEntry() {}

        public VectorEntry(String id, float[] vector, Map<String, Object> metadata) {
            this.id = id;
            this.vector = vector;
            this.metadata = metadata;
        }
    }
}
