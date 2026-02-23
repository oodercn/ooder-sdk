package net.ooder.sdk.drivers.vector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VectorDriver {
    
    void init(VectorConfig config);
    
    CompletableFuture<String> createCollection(String collectionName, int dimension, Map<String, Object> metadata);
    
    CompletableFuture<Void> dropCollection(String collectionName);
    
    CompletableFuture<List<String>> listCollections();
    
    CompletableFuture<String> insert(String collectionName, String id, float[] vector, Map<String, Object> metadata);
    
    CompletableFuture<Void> update(String collectionName, String id, float[] vector, Map<String, Object> metadata);
    
    CompletableFuture<Void> delete(String collectionName, String id);
    
    CompletableFuture<Void> deleteBatch(String collectionName, List<String> ids);
    
    CompletableFuture<VectorResult> search(String collectionName, float[] queryVector, int topK, Map<String, Object> filter);
    
    CompletableFuture<VectorResult> searchWithScore(String collectionName, float[] queryVector, int topK, float minScore, Map<String, Object> filter);
    
    CompletableFuture<VectorRecord> get(String collectionName, String id);
    
    CompletableFuture<List<VectorRecord>> getBatch(String collectionName, List<String> ids);
    
    CompletableFuture<Long> count(String collectionName);
    
    CompletableFuture<Void> createIndex(String collectionName, String fieldName, String indexType);
    
    void close();
    
    boolean isConnected();
    
    String getDriverName();
    
    String getDriverVersion();
    
    class VectorConfig {
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;
        private int dimension = 1536;
        private String metricType = "COSINE";
        private int maxConnections = 10;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public String getDatabase() { return database; }
        public void setDatabase(String database) { this.database = database; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public int getDimension() { return dimension; }
        public void setDimension(int dimension) { this.dimension = dimension; }
        
        public String getMetricType() { return metricType; }
        public void setMetricType(String metricType) { this.metricType = metricType; }
        
        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
    
    class VectorResult {
        private List<VectorRecord> records;
        private List<Float> scores;
        private long queryTime;
        
        public List<VectorRecord> getRecords() { return records; }
        public void setRecords(List<VectorRecord> records) { this.records = records; }
        
        public List<Float> getScores() { return scores; }
        public void setScores(List<Float> scores) { this.scores = scores; }
        
        public long getQueryTime() { return queryTime; }
        public void setQueryTime(long queryTime) { this.queryTime = queryTime; }
    }
    
    class VectorRecord {
        private String id;
        private float[] vector;
        private Map<String, Object> metadata;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public float[] getVector() { return vector; }
        public void setVector(float[] vector) { this.vector = vector; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}
