package net.ooder.sdk.llm.config;

import net.ooder.sdk.llm.common.enums.LlmType;

import java.util.Map;

public class LlmSdkConfig {
    private String sdkVersion;
    private LlmType defaultLlmType;
    private String defaultEndpoint;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private Integer maxRetries;
    private Boolean enableCache;
    private Integer cacheExpireSeconds;
    private Map<String, Object> customConfig;

    public LlmSdkConfig() {
        this.sdkVersion = "0.8.0";
        this.connectionTimeout = 30000;
        this.readTimeout = 60000;
        this.maxRetries = 3;
        this.enableCache = true;
        this.cacheExpireSeconds = 300;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public LlmType getDefaultLlmType() {
        return defaultLlmType;
    }

    public void setDefaultLlmType(LlmType defaultLlmType) {
        this.defaultLlmType = defaultLlmType;
    }

    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }

    public void setDefaultEndpoint(String defaultEndpoint) {
        this.defaultEndpoint = defaultEndpoint;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Boolean getEnableCache() {
        return enableCache;
    }

    public void setEnableCache(Boolean enableCache) {
        this.enableCache = enableCache;
    }

    public Integer getCacheExpireSeconds() {
        return cacheExpireSeconds;
    }

    public void setCacheExpireSeconds(Integer cacheExpireSeconds) {
        this.cacheExpireSeconds = cacheExpireSeconds;
    }

    public Map<String, Object> getCustomConfig() {
        return customConfig;
    }

    public void setCustomConfig(Map<String, Object> customConfig) {
        this.customConfig = customConfig;
    }
}
