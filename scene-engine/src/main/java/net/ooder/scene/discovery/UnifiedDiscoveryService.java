package net.ooder.scene.discovery;

import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 统一发现服务接口
 * 
 * 封装GitHub和Gitee的能力发现机制，提供统一的访问接口。
 * 使用本地JSON文件缓存避免频繁访问远程API（Gitee/GitHub每小时60次限制）。
 * 
 * 设计原则：
 * 1. 统一入口：所有发现操作通过此接口进行
 * 2. 智能缓存：优先使用本地缓存，过期后才访问远程
 * 3. 限流保护：内置访问频率控制，避免触发平台限制
 * 4. 地址识别：自动识别GitHub/Gitee地址
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface UnifiedDiscoveryService {
    
    /**
     * 发现指定地址的Skills
     * 
     * 自动识别平台类型（GitHub/Gitee），使用缓存机制
     *
     * @param repositoryUrl 仓库地址，支持格式：
     *                      - https://github.com/owner/repo
     *                      - https://gitee.com/owner/repo
     *                      - github://owner/repo
     *                      - gitee://owner/repo
     * @return Skill包列表
     */
    CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl);
    
    /**
     * 发现指定地址的Skills（指定路径）
     *
     * @param repositoryUrl 仓库地址
     * @param skillsPath Skills所在路径
     * @return Skill包列表
     */
    CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl, String skillsPath);
    
    /**
     * 发现单个Skill
     *
     * @param repositoryUrl 仓库地址
     * @param skillName Skill名称
     * @return Skill包
     */
    CompletableFuture<SkillPackage> discoverSkill(String repositoryUrl, String skillName);
    
    /**
     * 获取Skill清单内容
     *
     * @param repositoryUrl 仓库地址
     * @param skillName Skill名称
     * @return 清单内容（YAML格式）
     */
    CompletableFuture<String> getSkillManifest(String repositoryUrl, String skillName);
    
    /**
     * 获取Releases列表
     *
     * @param repositoryUrl 仓库地址
     * @return Release信息列表
     */
    CompletableFuture<List<ReleaseInfo>> getReleases(String repositoryUrl);
    
    /**
     * 获取最新Release
     *
     * @param repositoryUrl 仓库地址
     * @return 最新Release信息
     */
    CompletableFuture<ReleaseInfo> getLatestRelease(String repositoryUrl);
    
    /**
     * 强制刷新缓存
     *
     * @param repositoryUrl 仓库地址
     * @return 是否成功
     */
    CompletableFuture<Boolean> refreshCache(String repositoryUrl);
    
    /**
     * 清除所有缓存
     */
    void clearAllCache();
    
    /**
     * 获取缓存状态
     *
     * @param repositoryUrl 仓库地址
     * @return 缓存状态
     */
    CacheStatus getCacheStatus(String repositoryUrl);
    
    /**
     * 设置缓存配置
     *
     * @param config 缓存配置
     */
    void setCacheConfig(CacheConfig config);
    
    // ========== 数据类定义 ==========
    
    /**
     * Release信息
     */
    class ReleaseInfo {
        private String tagName;
        private String name;
        private String description;
        private boolean prerelease;
        private boolean draft;
        private long publishedAt;
        private List<AssetInfo> assets;
        
        // Getters and Setters
        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isPrerelease() { return prerelease; }
        public void setPrerelease(boolean prerelease) { this.prerelease = prerelease; }
        public boolean isDraft() { return draft; }
        public void setDraft(boolean draft) { this.draft = draft; }
        public long getPublishedAt() { return publishedAt; }
        public void setPublishedAt(long publishedAt) { this.publishedAt = publishedAt; }
        public List<AssetInfo> getAssets() { return assets; }
        public void setAssets(List<AssetInfo> assets) { this.assets = assets; }
    }
    
    /**
     * 资源信息
     */
    class AssetInfo {
        private String assetId;
        private String name;
        private String downloadUrl;
        private long size;
        private String contentType;
        
        // Getters and Setters
        public String getAssetId() { return assetId; }
        public void setAssetId(String assetId) { this.assetId = assetId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
    }
    
    /**
     * 缓存配置
     */
    class CacheConfig {
        private long cacheTtlMs = 3600000;  // 默认1小时
        private String cacheDir = "./.ooder/cache/discovery";  // 缓存目录
        private int maxCacheEntries = 100;  // 最大缓存条目数
        private boolean enableMemoryCache = true;  // 启用内存缓存
        private boolean enableFileCache = true;  // 启用文件缓存
        
        // Getters and Setters
        public long getCacheTtlMs() { return cacheTtlMs; }
        public void setCacheTtlMs(long cacheTtlMs) { this.cacheTtlMs = cacheTtlMs; }
        public String getCacheDir() { return cacheDir; }
        public void setCacheDir(String cacheDir) { this.cacheDir = cacheDir; }
        public int getMaxCacheEntries() { return maxCacheEntries; }
        public void setMaxCacheEntries(int maxCacheEntries) { this.maxCacheEntries = maxCacheEntries; }
        public boolean isEnableMemoryCache() { return enableMemoryCache; }
        public void setEnableMemoryCache(boolean enableMemoryCache) { this.enableMemoryCache = enableMemoryCache; }
        public boolean isEnableFileCache() { return enableFileCache; }
        public void setEnableFileCache(boolean enableFileCache) { this.enableFileCache = enableFileCache; }
    }
    
    /**
     * 缓存状态
     */
    class CacheStatus {
        private boolean cached;
        private long cacheTime;
        private long expireTime;
        private long size;
        private String cacheFile;
        
        // Getters and Setters
        public boolean isCached() { return cached; }
        public void setCached(boolean cached) { this.cached = cached; }
        public long getCacheTime() { return cacheTime; }
        public void setCacheTime(long cacheTime) { this.cacheTime = cacheTime; }
        public long getExpireTime() { return expireTime; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public String getCacheFile() { return cacheFile; }
        public void setCacheFile(String cacheFile) { this.cacheFile = cacheFile; }
    }
}
