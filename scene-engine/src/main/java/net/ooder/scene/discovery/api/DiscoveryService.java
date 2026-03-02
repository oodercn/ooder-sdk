package net.ooder.scene.discovery.api;

import net.ooder.skills.api.SkillPackage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 统一发现服务接口
 * 
 * 对外提供的统一发现入口，封装所有发现细节：
 * - 本地发现
 * - 网络发现（GitHub/Gitee）
 * - 缓存管理
 * - 分批处理
 * - 进度反馈
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface DiscoveryService {
    
    /**
     * 发现Skills（统一入口）
     * 
     * 自动处理：缓存检查 → 本地发现 → 网络发现 → 结果合并 → 缓存更新
     *
     * @param request 发现请求
     * @return 发现结果
     */
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);
    
    /**
     * 刷新发现（强制更新）
     *
     * @param request 发现请求
     * @return 发现结果
     */
    CompletableFuture<DiscoveryResult> refresh(DiscoveryRequest request);
    
    /**
     * 搜索Skills
     *
     * @param keyword 关键词
     * @return 匹配的Skill列表
     */
    CompletableFuture<List<SkillInfo>> search(String keyword);
    
    /**
     * 按分类搜索
     *
     * @param category 分类
     * @return 匹配的Skill列表
     */
    CompletableFuture<List<SkillInfo>> searchByCategory(String category);
    
    /**
     * 获取已安装的Skills
     *
     * @return 已安装Skill列表
     */
    CompletableFuture<List<SkillInfo>> getInstalled();
    
    /**
     * 获取本地缓存的Skills
     *
     * @return 缓存Skill列表
     */
    CompletableFuture<List<SkillInfo>> getCached();
    
    /**
     * 获取Skill详情
     *
     * @param skillId Skill ID
     * @return Skill详情
     */
    CompletableFuture<SkillInfo> getSkillInfo(String skillId);
    
    /**
     * 获取Skill详情（指定版本）
     *
     * @param skillId Skill ID
     * @param version 版本号
     * @return Skill详情
     */
    CompletableFuture<SkillInfo> getSkillInfo(String skillId, String version);
    
    /**
     * 检查完整性
     *
     * @param skillId Skill ID
     * @return 完整性检查结果
     */
    CompletableFuture<IntegrityCheckResult> checkIntegrity(String skillId);
    
    /**
     * 检查安装依赖
     *
     * @param skillId Skill ID
     * @return 依赖检查结果
     */
    CompletableFuture<DependencyCheckResult> checkDependencies(String skillId);
    
    /**
     * 安装依赖
     *
     * @param skillId Skill ID
     * @return 安装结果
     */
    CompletableFuture<DependencyInstallResult> installDependencies(String skillId);
    
    /**
     * 添加发现监听器
     *
     * @param listener 监听器
     */
    void addDiscoveryListener(DiscoveryListener listener);
    
    /**
     * 移除发现监听器
     *
     * @param listener 监听器
     */
    void removeDiscoveryListener(DiscoveryListener listener);
    
    // ========== 数据类定义 ==========
    
    /**
     * 发现请求
     */
    class DiscoveryRequest {
        private String source;  // 来源：local/github/gitee/all
        private String repositoryUrl;  // 仓库地址（网络发现时）
        private String skillsPath;  // Skills路径
        private boolean useCache;  // 是否使用缓存
        private boolean forceRefresh;  // 强制刷新
        private int batchSize;  // 分批大小
        private long timeout;  // 超时时间
        
        public DiscoveryRequest() {
            this.source = "all";
            this.useCache = true;
            this.forceRefresh = false;
            this.batchSize = 10;
            this.timeout = 60000;
        }
        
        // Getters and Setters
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getRepositoryUrl() { return repositoryUrl; }
        public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }
        public String getSkillsPath() { return skillsPath; }
        public void setSkillsPath(String skillsPath) { this.skillsPath = skillsPath; }
        public boolean isUseCache() { return useCache; }
        public void setUseCache(boolean useCache) { this.useCache = useCache; }
        public boolean isForceRefresh() { return forceRefresh; }
        public void setForceRefresh(boolean forceRefresh) { this.forceRefresh = forceRefresh; }
        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }
    }
    
    /**
     * 发现结果
     */
    class DiscoveryResult {
        private boolean success;
        private String source;
        private List<SkillInfo> skills;
        private int totalCount;
        private int fromCache;
        private int fromLocal;
        private int fromNetwork;
        private long duration;
        private String message;
        private DiscoveryProgress progress;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public List<SkillInfo> getSkills() { return skills; }
        public void setSkills(List<SkillInfo> skills) { this.skills = skills; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getFromCache() { return fromCache; }
        public void setFromCache(int fromCache) { this.fromCache = fromCache; }
        public int getFromLocal() { return fromLocal; }
        public void setFromLocal(int fromLocal) { this.fromLocal = fromLocal; }
        public int getFromNetwork() { return fromNetwork; }
        public void setFromNetwork(int fromNetwork) { this.fromNetwork = fromNetwork; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public DiscoveryProgress getProgress() { return progress; }
        public void setProgress(DiscoveryProgress progress) { this.progress = progress; }
    }
    
    /**
     * Skill信息
     */
    class SkillInfo {
        private String skillId;
        private String name;
        private String version;
        private String description;
        private String category;
        private List<String> tags;
        private String source;  // 来源渠道
        private String location;  // 存储位置
        private long size;
        private long lastModified;
        private boolean installed;
        private boolean cached;
        private String downloadUrl;
        private String manifestUrl;
        private List<String> dependencies;
        
        // Getters and Setters
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
        public boolean isInstalled() { return installed; }
        public void setInstalled(boolean installed) { this.installed = installed; }
        public boolean isCached() { return cached; }
        public void setCached(boolean cached) { this.cached = cached; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public String getManifestUrl() { return manifestUrl; }
        public void setManifestUrl(String manifestUrl) { this.manifestUrl = manifestUrl; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    }
    
    /**
     * 发现进度
     */
    class DiscoveryProgress {
        private int totalSteps;
        private int currentStep;
        private String currentPhase;
        private int percentage;
        private String message;
        private long startTime;
        private long estimatedEndTime;
        
        // Getters and Setters
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        public int getCurrentStep() { return currentStep; }
        public void setCurrentStep(int currentStep) { this.currentStep = currentStep; }
        public String getCurrentPhase() { return currentPhase; }
        public void setCurrentPhase(String currentPhase) { this.currentPhase = currentPhase; }
        public int getPercentage() { return percentage; }
        public void setPercentage(int percentage) { this.percentage = percentage; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEstimatedEndTime() { return estimatedEndTime; }
        public void setEstimatedEndTime(long estimatedEndTime) { this.estimatedEndTime = estimatedEndTime; }
    }
    
    /**
     * 完整性检查结果
     */
    class IntegrityCheckResult {
        private boolean valid;
        private String skillId;
        private List<String> missingFiles;
        private List<String> corruptedFiles;
        private String message;
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public List<String> getMissingFiles() { return missingFiles; }
        public void setMissingFiles(List<String> missingFiles) { this.missingFiles = missingFiles; }
        public List<String> getCorruptedFiles() { return corruptedFiles; }
        public void setCorruptedFiles(List<String> corruptedFiles) { this.corruptedFiles = corruptedFiles; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 依赖检查结果
     */
    class DependencyCheckResult {
        private boolean satisfied;
        private String skillId;
        private List<String> missingDependencies;
        private List<String> versionMismatches;
        private String message;
        
        // Getters and Setters
        public boolean isSatisfied() { return satisfied; }
        public void setSatisfied(boolean satisfied) { this.satisfied = satisfied; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public List<String> getMissingDependencies() { return missingDependencies; }
        public void setMissingDependencies(List<String> missingDependencies) { this.missingDependencies = missingDependencies; }
        public List<String> getVersionMismatches() { return versionMismatches; }
        public void setVersionMismatches(List<String> versionMismatches) { this.versionMismatches = versionMismatches; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 依赖安装结果
     */
    class DependencyInstallResult {
        private boolean success;
        private String skillId;
        private List<String> installedDependencies;
        private List<String> failedDependencies;
        private String message;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public List<String> getInstalledDependencies() { return installedDependencies; }
        public void setInstalledDependencies(List<String> installedDependencies) { this.installedDependencies = installedDependencies; }
        public List<String> getFailedDependencies() { return failedDependencies; }
        public void setFailedDependencies(List<String> failedDependencies) { this.failedDependencies = failedDependencies; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 发现监听器
     */
    interface DiscoveryListener {
        void onDiscoveryStarted(DiscoveryRequest request);
        void onDiscoveryProgress(DiscoveryProgress progress);
        void onDiscoveryCompleted(DiscoveryResult result);
        void onDiscoveryFailed(String error);
        void onSkillFound(SkillInfo skill);
    }
}
