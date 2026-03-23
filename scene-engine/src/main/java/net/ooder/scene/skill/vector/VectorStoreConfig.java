package net.ooder.scene.skill.vector;

import java.util.HashMap;
import java.util.Map;

public class VectorStoreConfig {

    public static final String TYPE_MEMORY = "memory";
    public static final String TYPE_JSON = "json";
    public static final String TYPE_MILVUS = "milvus";
    public static final String TYPE_CHROMA = "chroma";
    public static final String TYPE_PGVECTOR = "pgvector";

    private String type = TYPE_MEMORY;
    private int dimension = 1536;
    private String metricType = "COSINE";
    private Map<String, Object> properties = new HashMap<>();

    public VectorStoreConfig() {}

    public VectorStoreConfig(String type, int dimension) {
        this.type = type;
        this.dimension = dimension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public VectorStoreConfig property(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        Object value = properties.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public int getProperty(String key, int defaultValue) {
        Object value = properties.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    public static VectorStoreConfig memory(int dimension) {
        return new VectorStoreConfig(TYPE_MEMORY, dimension);
    }

    public static VectorStoreConfig json(int dimension, String basePath) {
        VectorStoreConfig config = new VectorStoreConfig(TYPE_JSON, dimension);
        config.property("basePath", basePath);
        return config;
    }

    public static VectorStoreConfig milvus(int dimension, String host, int port, String database, String collection) {
        VectorStoreConfig config = new VectorStoreConfig(TYPE_MILVUS, dimension);
        config.property("host", host);
        config.property("port", port);
        config.property("database", database);
        config.property("collection", collection);
        return config;
    }

    public static VectorStoreConfig chroma(int dimension, String host, int port, String collection) {
        VectorStoreConfig config = new VectorStoreConfig(TYPE_CHROMA, dimension);
        config.property("host", host);
        config.property("port", port);
        config.property("collection", collection);
        return config;
    }

    public static VectorStoreConfig pgvector(int dimension, String url, String table) {
        VectorStoreConfig config = new VectorStoreConfig(TYPE_PGVECTOR, dimension);
        config.property("url", url);
        config.property("table", table);
        return config;
    }
}
