package net.ooder.scene.driver.tiny;

import net.ooder.scene.spi.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 向量存储 - Tiny 实现
 *
 * <p>基于内存的简单向量存储，适用于开发测试环境</p>
 *
 * <p>注意：重启后数据丢失</p>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(VectorStore.class)
public class TinyVectorStore implements VectorStore {

    private static final Logger log = LoggerFactory.getLogger(TinyVectorStore.class);

    private final Map<String, VectorData> store = new ConcurrentHashMap<>();

    @Override
    public String getProviderType() {
        return "tiny";
    }

    @Override
    public void addVector(String id, float[] embedding, Map<String, Object> metadata) {
        store.put(id, new VectorData(id, embedding.clone(), metadata));
        log.debug("Added vector: {}", id);
    }

    @Override
    public void addVectors(List<VectorData> vectors) {
        for (VectorData vector : vectors) {
            store.put(vector.id(), new VectorData(
                vector.id(),
                vector.embedding().clone(),
                vector.metadata()
            ));
        }
        log.debug("Added {} vectors", vectors.size());
    }

    @Override
    public List<SearchResult> search(float[] embedding, int topK) {
        return search(embedding, topK, null);
    }

    @Override
    public List<SearchResult> search(float[] embedding, int topK, Map<String, Object> filter) {
        List<SearchResult> results = new ArrayList<>();

        for (VectorData data : store.values()) {
            if (filter != null && !matchesFilter(data.metadata(), filter)) {
                continue;
            }

            float score = cosineSimilarity(embedding, data.embedding());
            results.add(new SearchResult(data.id(), score, data.metadata()));
        }

        return results.stream()
            .sorted((a, b) -> Float.compare(b.score(), a.score()))
            .limit(topK)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteVector(String id) {
        store.remove(id);
        log.debug("Deleted vector: {}", id);
    }

    @Override
    public void clear() {
        store.clear();
        log.info("Cleared all vectors");
    }

    @Override
    public int size() {
        return store.size();
    }

    private boolean matchesFilter(Map<String, Object> metadata, Map<String, Object> filter) {
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            Object value = metadata.get(entry.getKey());
            if (!Objects.equals(value, entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            return 0f;
        }

        float dotProduct = 0f;
        float normA = 0f;
        float normB = 0f;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            return 0f;
        }

        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
