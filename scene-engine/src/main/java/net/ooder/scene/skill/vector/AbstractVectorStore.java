package net.ooder.scene.skill.vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractVectorStore implements VectorStore {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final VectorStoreConfig config;
    protected final int dimension;
    protected final String metricType;

    protected AbstractVectorStore(VectorStoreConfig config) {
        this.config = config;
        this.dimension = config.getDimension();
        this.metricType = config.getMetricType();
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    public String getMetricType() {
        return metricType;
    }

    public VectorStoreConfig getConfig() {
        return config;
    }

    protected void validateVector(float[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("Vector cannot be null");
        }
        if (vector.length != dimension) {
            throw new IllegalArgumentException(
                "Vector dimension mismatch. Expected: " + dimension + ", Actual: " + vector.length
            );
        }
    }

    protected Map<String, Object> normalizeMetadata(Map<String, Object> metadata) {
        if (metadata == null) {
            return new HashMap<>();
        }
        return new HashMap<>(metadata);
    }

    protected float cosineSimilarity(float[] v1, float[] v2) {
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

    protected float euclideanDistance(float[] v1, float[] v2) {
        float sum = 0;
        for (int i = 0; i < v1.length; i++) {
            float diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }

    protected float calculateScore(float[] v1, float[] v2, String metric) {
        switch (metric.toUpperCase()) {
            case "COSINE":
                return cosineSimilarity(v1, v2);
            case "EUCLIDEAN":
            case "L2":
                return 1.0f / (1.0f + euclideanDistance(v1, v2));
            case "IP":
            case "INNER_PRODUCT":
                float dot = 0;
                for (int i = 0; i < v1.length; i++) {
                    dot += v1[i] * v2[i];
                }
                return dot;
            default:
                return cosineSimilarity(v1, v2);
        }
    }

    protected boolean matchesFilters(Map<String, Object> metadata, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object value = metadata.get(filter.getKey());
            if (!matchesFilterValue(value, filter.getValue())) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesFilterValue(Object actualValue, Object filterValue) {
        if (actualValue == null) {
            return filterValue == null;
        }
        return actualValue.equals(filterValue);
    }

    public abstract void initialize();
    public abstract void shutdown();
}
