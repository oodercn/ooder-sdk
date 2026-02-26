package net.ooder.sdk.drivers.vector.impl;

import net.ooder.sdk.api.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.vector.VectorDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@DriverImplementation(value = "VectorDriver", skillId = "skill-vector-local")
public class LocalVectorDriver implements VectorDriver {
    
    private static final Logger log = LoggerFactory.getLogger(LocalVectorDriver.class);
    
    private final Map<String, InternalVectorCollection> collections = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private VectorConfig config;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    
    @Override
    public void init(VectorConfig config) {
        this.config = config;
        connected.set(true);
        log.info("Local vector store initialized");
    }
    
    @Override
    public CompletableFuture<String> createCollection(String collectionName, int dimension, Map<String, Object> metadata) {
        return CompletableFuture.supplyAsync(() -> {
            InternalVectorCollection collection = new InternalVectorCollection();
            collection.name = collectionName;
            collection.dimension = dimension;
            collection.metadata = metadata != null ? metadata : new HashMap<>();
            collection.records = new ConcurrentHashMap<>();
            
            collections.put(collectionName, collection);
            log.info("Collection created: {} (dimension={})", collectionName, dimension);
            return collectionName;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> dropCollection(String collectionName) {
        return CompletableFuture.runAsync(() -> {
            collections.remove(collectionName);
            log.info("Collection dropped: {}", collectionName);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<String>> listCollections() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(collections.keySet()), executor);
    }
    
    @Override
    public CompletableFuture<String> insert(String collectionName, String id, float[] vector, Map<String, Object> metadata) {
        return CompletableFuture.supplyAsync(() -> {
            InternalVectorCollection collection = collections.get(collectionName);
            if (collection == null) {
                throw new RuntimeException("Collection not found: " + collectionName);
            }
            
            if (vector.length != collection.dimension) {
                throw new RuntimeException("Vector dimension mismatch");
            }
            
            InternalVectorRecord record = new InternalVectorRecord();
            record.id = id;
            record.vector = Arrays.copyOf(vector, vector.length);
            record.metadata = metadata != null ? metadata : new HashMap<>();
            
            collection.records.put(id, record);
            
            log.debug("Vector inserted: {} in {}", id, collectionName);
            return id;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> update(String collectionName, String id, float[] vector, Map<String, Object> metadata) {
        return CompletableFuture.runAsync(() -> {
            InternalVectorCollection collection = collections.get(collectionName);
            if (collection == null) {
                throw new RuntimeException("Collection not found: " + collectionName);
            }
            
            InternalVectorRecord record = collection.records.get(id);
            if (record == null) {
                throw new RuntimeException("Record not found: " + id);
            }
            
            if (vector != null) {
                record.vector = Arrays.copyOf(vector, vector.length);
            }
            if (metadata != null) {
                record.metadata = metadata;
            }
            
            log.debug("Vector updated: {} in {}", id, collectionName);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> delete(String collectionName, String id) {
        return CompletableFuture.runAsync(() -> {
            InternalVectorCollection collection = collections.get(collectionName);
            if (collection != null) {
                collection.records.remove(id);
                log.debug("Vector deleted: {} from {}", id, collectionName);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> deleteBatch(String collectionName, List<String> ids) {
        return CompletableFuture.runAsync(() -> {
            InternalVectorCollection collection = collections.get(collectionName);
            if (collection != null) {
                for (String id : ids) {
                    collection.records.remove(id);
                }
                log.debug("Batch deleted {} vectors from {}", ids.size(), collectionName);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<VectorResult> search(String collectionName, float[] queryVector, int topK, Map<String, Object> filter) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            
            InternalVectorCollection collection = collections.get(collectionName);
            if (collection == null) {
                VectorResult result = new VectorResult();
                result.setRecords(Collections.emptyList());
                result.setScores(Collections.emptyList());
                result.setQueryTime(0);
                return result;
            }
            
            List<ScoredRecord> scoredRecords = new ArrayList<>();
            
            for (InternalVectorRecord record : collection.records.values()) {
                if (filterMatches(record.metadata, filter)) {
                    float score = calculateSimilarity(queryVector, record.vector);
                    scoredRecords.add(new ScoredRecord(record, score));
                }
            }
            
            scoredRecords.sort((a, b) -> Float.compare(b.score, a.score));
            
            int resultCount = Math.min(topK, scoredRecords.size());
            List<VectorRecord> records = new ArrayList<>();
            List<Float> scores = new ArrayList<>();
            
            for (int i = 0; i < resultCount; i++) {
                ScoredRecord sr = scoredRecords.get(i);
                records.add(toDriverRecord(sr.record));
                scores.add(sr.score);
            }
            
            VectorResult result = new VectorResult();
            result.setRecords(records);
            result.setScores(scores);
            result.setQueryTime(System.currentTimeMillis() - startTime);
            
            return result;
        }, executor);
    }
    
    @Override
    public CompletableFuture<VectorResult> searchWithScore(String collectionName, float[] queryVector, int topK, float minScore, Map<String, Object> filter) {
        return search(collectionName, queryVector, topK, filter).thenApply(result -> {
            List<VectorRecord> filteredRecords = new ArrayList<>();
            List<Float> filteredScores = new ArrayList<>();
            
            for (int i = 0; i < result.getRecords().size(); i++) {
                if (result.getScores().get(i) >= minScore) {
                    filteredRecords.add(result.getRecords().get(i));
                    filteredScores.add(result.getScores().get(i));
                }
            }
            
            result.setRecords(filteredRecords);
            result.setScores(filteredScores);
            return result;
        });
    }
    
    @Override
    public CompletableFuture<VectorRecord> get(String collectionName, String id) {
        return CompletableFuture.supplyAsync(() -> {
            InternalVectorCollection collection = collections.get(collectionName);
            if (collection == null) {
                return null;
            }
            InternalVectorRecord record = collection.records.get(id);
            return record != null ? toDriverRecord(record) : null;
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<VectorRecord>> getBatch(String collectionName, List<String> ids) {
        return CompletableFuture.supplyAsync(() -> {
            InternalVectorCollection collection = collections.get(collectionName);
            if (collection == null) {
                return Collections.emptyList();
            }
            
            List<VectorRecord> result = new ArrayList<>();
            for (String id : ids) {
                InternalVectorRecord record = collection.records.get(id);
                if (record != null) {
                    result.add(toDriverRecord(record));
                }
            }
            return result;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Long> count(String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            InternalVectorCollection collection = collections.get(collectionName);
            return collection != null ? (long) collection.records.size() : 0L;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> createIndex(String collectionName, String fieldName, String indexType) {
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public void close() {
        executor.shutdown();
        collections.clear();
        connected.set(false);
        log.info("Local vector store closed");
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getDriverName() {
        return "LocalVector";
    }
    
    @Override
    public String getDriverVersion() {
        return "1.0.0";
    }
    
    private float calculateSimilarity(float[] v1, float[] v2) {
        String metricType = config != null ? config.getMetricType() : "COSINE";
        
        switch (metricType.toUpperCase()) {
            case "COSINE":
                return cosineSimilarity(v1, v2);
            case "EUCLIDEAN":
                return 1.0f / (1.0f + euclideanDistance(v1, v2));
            case "DOT":
                return dotProduct(v1, v2);
            default:
                return cosineSimilarity(v1, v2);
        }
    }
    
    private float cosineSimilarity(float[] v1, float[] v2) {
        float dot = 0, norm1 = 0, norm2 = 0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        return (float) (dot / (Math.sqrt(norm1) * Math.sqrt(norm2) + 1e-8));
    }
    
    private float euclideanDistance(float[] v1, float[] v2) {
        float sum = 0;
        for (int i = 0; i < v1.length; i++) {
            float diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }
    
    private float dotProduct(float[] v1, float[] v2) {
        float sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }
    
    private boolean filterMatches(Map<String, Object> metadata, Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }
        
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            Object value = metadata.get(entry.getKey());
            if (!Objects.equals(value, entry.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    private VectorRecord toDriverRecord(InternalVectorRecord record) {
        VectorRecord result = new VectorRecord();
        result.setId(record.id);
        result.setVector(Arrays.copyOf(record.vector, record.vector.length));
        result.setMetadata(new HashMap<>(record.metadata));
        return result;
    }
    
    private static class InternalVectorCollection {
        String name;
        int dimension;
        Map<String, Object> metadata;
        Map<String, InternalVectorRecord> records;
    }
    
    private static class InternalVectorRecord {
        String id;
        float[] vector;
        Map<String, Object> metadata;
    }
    
    private static class ScoredRecord {
        InternalVectorRecord record;
        float score;
        
        ScoredRecord(InternalVectorRecord record, float score) {
            this.record = record;
            this.score = score;
        }
    }
}
