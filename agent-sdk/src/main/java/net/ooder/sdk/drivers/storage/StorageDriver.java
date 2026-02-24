package net.ooder.sdk.drivers.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface StorageDriver {
    
    void init(StorageConfig config);
    
    CompletableFuture<String> upload(String path, byte[] data);
    
    CompletableFuture<String> upload(String path, byte[] data, Map<String, String> metadata);
    
    CompletableFuture<byte[]> download(String path);
    
    CompletableFuture<Boolean> delete(String path);
    
    CompletableFuture<Boolean> deleteBatch(List<String> paths);
    
    CompletableFuture<List<StorageObject>> list(String prefix);
    
    CompletableFuture<StorageObject> stat(String path);
    
    CompletableFuture<Boolean> copy(String sourcePath, String destPath);
    
    CompletableFuture<String> getSignedUrl(String path, long expirySeconds);
    
    CompletableFuture<String> getPublicUrl(String path);
    
    CompletableFuture<Boolean> setMetadata(String path, Map<String, String> metadata);
    
    CompletableFuture<Map<String, String>> getMetadata(String path);
    
    CompletableFuture<Boolean> exists(String path);
    
    CompletableFuture<Long> getTotalSize();
    
    CompletableFuture<Long> getObjectCount();
    
    void close();
    
    boolean isConnected();
    
    String getDriverName();
    
    String getDriverVersion();
    
    class StorageConfig {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String region;
        private String bucket;
        private boolean secure = true;
        private long maxFileSize = 100 * 1024 * 1024;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
        
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
        
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        
        public String getBucket() { return bucket; }
        public void setBucket(String bucket) { this.bucket = bucket; }
        
        public boolean isSecure() { return secure; }
        public void setSecure(boolean secure) { this.secure = secure; }
        
        public long getMaxFileSize() { return maxFileSize; }
        public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
    
    class StorageObject {
        private String path;
        private String name;
        private long size;
        private String contentType;
        private String etag;
        private long lastModified;
        private Map<String, String> metadata;
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public String getEtag() { return etag; }
        public void setEtag(String etag) { this.etag = etag; }
        
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
        
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    }
}
