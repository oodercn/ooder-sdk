package net.ooder.scene.skill.vector.impl;

import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.VectorData;
import net.ooder.scene.skill.vector.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存向量存储实现
 *
 * <p>提供基于内存的向量存储，适用于开发测试环境。</p>
 *
 * <p>生产环境应使用 MilvusVectorStore 实现。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class InMemoryVectorStore implements VectorStore {
    
    private static final Logger log = LoggerFactory.getLogger(InMemoryVectorStore.class);
    
    private final int dimension;
    private final Map<String, VectorEntry> vectors = new ConcurrentHashMap<>();
    
    public InMemoryVectorStore(int dimension) {
        this.dimension = dimension;
        log.info("InMemoryVectorStore initialized with dimension: {}", dimension);
    }
    
    @Override
    public void insert(String id, float[] vector, Map<String, Object> metadata) {
        validateVector(vector);
        
        VectorEntry entry = new VectorEntry(id, vector, metadata);
        vectors.put(id, entry);
        
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
        
        // 按相似度降序排序
        results.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
        
        // 返回 topK 结果
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
        log.info("Cleared all vectors");
    }
    
    // ========== 私有方法 ==========
    
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
    
    // ========== 内部类 ==========
    
    private static class VectorEntry {
        final String id;
        final float[] vector;
        final Map<String, Object> metadata;
        
        VectorEntry(String id, float[] vector, Map<String, Object> metadata) {
            this.id = id;
            this.vector = vector.clone();
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        }
    }
}
