package net.ooder.config.scene.client;

import java.io.Serializable;

public class CacheConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = true;
    private String type = "memory";
    private long maxSize = 10485760L;
    private long expireTime = 300000L;
    
    public CacheConfig() {
    }
    
    public CacheConfig(boolean enabled) {
        this.enabled = enabled;
    }
    
    public static CacheConfigBuilder builder() {
        return new CacheConfigBuilder();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public long getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    public boolean isMemory() {
        return "memory".equals(type);
    }
    
    public boolean isFile() {
        return "file".equals(type);
    }
    
    public boolean isRedis() {
        return "redis".equals(type);
    }
    
    public static class CacheConfigBuilder {
        private CacheConfig config = new CacheConfig();
        
        public CacheConfigBuilder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }
        
        public CacheConfigBuilder type(String type) {
            config.type = type;
            return this;
        }
        
        public CacheConfigBuilder maxSize(long maxSize) {
            config.maxSize = maxSize;
            return this;
        }
        
        public CacheConfigBuilder expireTime(long expireTime) {
            config.expireTime = expireTime;
            return this;
        }
        
        public CacheConfig build() {
            return config;
        }
    }
}
