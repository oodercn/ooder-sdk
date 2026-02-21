package net.ooder.config.scene;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VfsSceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private String configName;
    
    private String storageType = "local";
    private String basePath;
    private long maxFileSize = 52428800L;
    private String allowedTypes = "*";
    
    private String ossEndpoint;
    private String ossBucket;
    private String ossAccessKey;
    private String ossSecretKey;
    
    private String localCachePath;
    private Integer bigFileSize = 10485760;
    
    private boolean cacheEnabled = true;
    private long cacheExpireTime = 604800000L;
    private int cacheSize = 52428800;
    
    private Integer connectTimeout = 5000;
    private Integer readTimeout = 60000;
    
    private Map<String, CapabilityConfig> capabilities = new HashMap<String, CapabilityConfig>();
    
    public VfsSceneConfig() {
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    
    public String getStorageType() {
        return storageType;
    }
    
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }
    
    public String getBasePath() {
        return basePath;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public String getAllowedTypes() {
        return allowedTypes;
    }
    
    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }
    
    public String getOssEndpoint() {
        return ossEndpoint;
    }
    
    public void setOssEndpoint(String ossEndpoint) {
        this.ossEndpoint = ossEndpoint;
    }
    
    public String getOssBucket() {
        return ossBucket;
    }
    
    public void setOssBucket(String ossBucket) {
        this.ossBucket = ossBucket;
    }
    
    public String getOssAccessKey() {
        return ossAccessKey;
    }
    
    public void setOssAccessKey(String ossAccessKey) {
        this.ossAccessKey = ossAccessKey;
    }
    
    public String getOssSecretKey() {
        return ossSecretKey;
    }
    
    public void setOssSecretKey(String ossSecretKey) {
        this.ossSecretKey = ossSecretKey;
    }
    
    public String getLocalCachePath() {
        return localCachePath;
    }
    
    public void setLocalCachePath(String localCachePath) {
        this.localCachePath = localCachePath;
    }
    
    public Integer getBigFileSize() {
        return bigFileSize;
    }
    
    public void setBigFileSize(Integer bigFileSize) {
        this.bigFileSize = bigFileSize;
    }
    
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
    
    public long getCacheExpireTime() {
        return cacheExpireTime;
    }
    
    public void setCacheExpireTime(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
    }
    
    public int getCacheSize() {
        return cacheSize;
    }
    
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public Integer getConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public Integer getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public Map<String, CapabilityConfig> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, CapabilityConfig> capabilities) {
        this.capabilities = capabilities;
    }
    
    @Override
    public String toString() {
        return "VfsSceneConfig{" +
                "sceneId='" + sceneId + '\'' +
                ", configName='" + configName + '\'' +
                ", storageType='" + storageType + '\'' +
                '}';
    }
}
