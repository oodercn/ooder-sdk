package net.ooder.config.core;

import java.io.Serializable;

public class CacheConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean enabled = true;
    private boolean dumpCache = true;
    private String dbUser = "sa";
    private String dbPassword = "";
    private String dbUrl;
    private String dataPath;
    private String dbName = "cache";
    private int maxSize = 10 * 1024 * 1024;
    private long expireTime = 24 * 60 * 60 * 1000L;
    
    public CacheConfig() {
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isDumpCache() {
        return dumpCache;
    }
    
    public void setDumpCache(boolean dumpCache) {
        this.dumpCache = dumpCache;
    }
    
    public String getDbUser() {
        return dbUser;
    }
    
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }
    
    public String getDbPassword() {
        return dbPassword;
    }
    
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
    
    public String getDbUrl() {
        return dbUrl;
    }
    
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    
    public String getDataPath() {
        return dataPath;
    }
    
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
    
    public String getDbName() {
        return dbName;
    }
    
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    public String getValue(String key) {
        switch (key) {
            case "enabled":
                return String.valueOf(enabled);
            case "dumpCache":
                return String.valueOf(dumpCache);
            case "dbUser":
            case "cacheDbUser":
                return dbUser;
            case "dbPassword":
            case "cacheDbPassword":
                return dbPassword;
            case "dbUrl":
            case "cacheDbURL":
                return dbUrl;
            case "dataPath":
                return dataPath;
            case "dbName":
                return dbName;
            case "maxSize":
                return String.valueOf(maxSize);
            case "expireTime":
                return String.valueOf(expireTime);
            default:
                return null;
        }
    }
}
