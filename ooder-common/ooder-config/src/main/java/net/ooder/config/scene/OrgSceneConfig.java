package net.ooder.config.scene;

import net.ooder.config.scene.extension.DataSourceConfig;
import net.ooder.config.scene.extension.FallbackConfig;
import net.ooder.config.scene.extension.SyncConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OrgSceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private String configName;
    
    private String dbDriver;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    
    private boolean cacheEnabled = true;
    private long cacheExpireTime = 604800000L;
    private int cacheSize = 10485760;
    
    private Integer connectTimeout = 5000;
    private Integer readTimeout = 30000;
    
    private Map<String, CapabilityConfig> capabilities = new HashMap<String, CapabilityConfig>();
    
    // ========== 扩展字段：多数据源支持 ==========
    
    private DataSourceConfig dataSource;
    
    private FallbackConfig fallback;
    
    // ========== 扩展字段：同步任务配置 ==========
    
    private SyncConfig sync;
    
    public OrgSceneConfig() {
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
    
    public String getDbDriver() {
        return dbDriver;
    }
    
    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }
    
    public String getDbUrl() {
        return dbUrl;
    }
    
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
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
    
    // ========== 扩展方法：多数据源支持 ==========
    
    public DataSourceConfig getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }
    
    public boolean hasDataSource() {
        return dataSource != null && dataSource.isEnabled();
    }
    
    public String getDataSourceType() {
        return dataSource != null ? dataSource.getType() : DataSourceConfig.TYPE_DATABASE;
    }
    
    public boolean isThirdPartyDataSource() {
        return dataSource != null && dataSource.isThirdParty();
    }
    
    public FallbackConfig getFallback() {
        return fallback;
    }
    
    public void setFallback(FallbackConfig fallback) {
        this.fallback = fallback;
    }
    
    public boolean hasFallback() {
        return fallback != null && fallback.isEnabled();
    }
    
    // ========== 扩展方法：同步任务配置 ==========
    
    public SyncConfig getSync() {
        return sync;
    }
    
    public void setSync(SyncConfig sync) {
        this.sync = sync;
    }
    
    public boolean hasSync() {
        return sync != null && sync.isEnabled();
    }
    
    // ========== 便捷方法 ==========
    
    public OrgSceneConfig enableCache() {
        this.cacheEnabled = true;
        return this;
    }
    
    public OrgSceneConfig disableCache() {
        this.cacheEnabled = false;
        return this;
    }
    
    public OrgSceneConfig withDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
        return this;
    }
    
    public OrgSceneConfig withFallback(FallbackConfig fallback) {
        this.fallback = fallback;
        return this;
    }
    
    public OrgSceneConfig withSync(SyncConfig sync) {
        this.sync = sync;
        return this;
    }
    
    public OrgSceneConfig withCapability(String code, CapabilityConfig capability) {
        this.capabilities.put(code, capability);
        return this;
    }
    
    @Override
    public String toString() {
        return "OrgSceneConfig{" +
                "sceneId='" + sceneId + '\'' +
                ", configName='" + configName + '\'' +
                ", cacheEnabled=" + cacheEnabled +
                ", dataSource=" + (dataSource != null ? dataSource.getType() : "none") +
                ", sync=" + (sync != null && sync.isEnabled() ? "enabled" : "disabled") +
                '}';
    }
}
