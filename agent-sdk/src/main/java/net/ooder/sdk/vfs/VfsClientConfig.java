package net.ooder.sdk.vfs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VfsClientConfig {
    
    private String clientId;
    private String endpoint;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private String region;
    private long maxFileSize = 100 * 1024 * 1024;
    private int uploadTimeout = 60000;
    private int downloadTimeout = 60000;
    private boolean versioningEnabled = true;
    private Map<String, Object> extensions = new ConcurrentHashMap<>();
    
    public VfsClientConfig() {}
    
    public VfsClientConfig(String clientId, String endpoint) {
        this.clientId = clientId;
        this.endpoint = endpoint;
    }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    
    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public long getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }
    
    public int getUploadTimeout() { return uploadTimeout; }
    public void setUploadTimeout(int uploadTimeout) { this.uploadTimeout = uploadTimeout; }
    
    public int getDownloadTimeout() { return downloadTimeout; }
    public void setDownloadTimeout(int downloadTimeout) { this.downloadTimeout = downloadTimeout; }
    
    public boolean isVersioningEnabled() { return versioningEnabled; }
    public void setVersioningEnabled(boolean versioningEnabled) { this.versioningEnabled = versioningEnabled; }
    
    public Map<String, Object> getExtensions() { return extensions; }
    public void setExtensions(Map<String, Object> extensions) { 
        this.extensions = extensions != null ? extensions : new ConcurrentHashMap<>(); 
    }
    
    public VfsClientConfig clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
    
    public VfsClientConfig endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }
}
