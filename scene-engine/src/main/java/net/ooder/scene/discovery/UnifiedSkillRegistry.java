package net.ooder.scene.discovery;

import net.ooder.skills.api.SkillPackage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 统一Skill注册中心
 * 
 * 统一管理所有渠道的Skill发现结果，提供：
 * 1. 全局Skill索引 - 跨渠道去重
 * 2. 渠道管理 - 支持多个GitHub/Gitee仓库
 * 3. 历史发现记录 - 追踪发现历史
 * 4. 统一存储策略 - 与SDK存储层集成
 * 
 * 设计原则：
 * - 统一入口：所有发现结果统一注册到此中心
 * - 去重机制：基于skillId+version唯一标识
 * - 渠道追踪：记录每个Skill的来源渠道
 * - 历史保留：保留历史发现记录，支持回滚
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface UnifiedSkillRegistry {
    
    /**
     * 注册发现结果
     * 
     * 将来自任意渠道的Skill发现结果注册到统一中心
     *
     * @param channelId 渠道ID（如：github:oodercn/skills, gitee:ooder/skills）
     * @param packages 发现的Skill包列表
     * @return 注册结果
     */
    CompletableFuture<RegisterResult> register(String channelId, List<SkillPackage> packages);
    
    /**
     * 注册单个Skill
     *
     * @param channelId 渠道ID
     * @param pkg Skill包
     * @return 注册结果
     */
    CompletableFuture<RegisterResult> register(String channelId, SkillPackage pkg);
    
    /**
     * 获取所有已注册的Skills（去重后）
     * 
     * 返回全局唯一的Skill列表，自动合并重复项
     *
     * @return Skill包列表
     */
    CompletableFuture<List<SkillPackage>> getAllSkills();
    
    /**
     * 获取指定渠道的Skills
     *
     * @param channelId 渠道ID
     * @return Skill包列表
     */
    CompletableFuture<List<SkillPackage>> getSkillsByChannel(String channelId);
    
    /**
     * 获取指定Skill（最新版本）
     *
     * @param skillId Skill ID
     * @return Skill包
     */
    CompletableFuture<SkillPackage> getSkill(String skillId);
    
    /**
     * 获取指定Skill的指定版本
     *
     * @param skillId Skill ID
     * @param version 版本号
     * @return Skill包
     */
    CompletableFuture<SkillPackage> getSkill(String skillId, String version);
    
    /**
     * 获取Skill的所有可用版本
     *
     * @param skillId Skill ID
     * @return 版本列表
     */
    CompletableFuture<List<String>> getSkillVersions(String skillId);
    
    /**
     * 获取Skill的来源渠道
     *
     * @param skillId Skill ID
     * @return 渠道ID列表
     */
    CompletableFuture<List<String>> getSkillChannels(String skillId);
    
    /**
     * 搜索Skills
     *
     * @param keyword 关键词
     * @return 匹配的Skill列表
     */
    CompletableFuture<List<SkillPackage>> searchSkills(String keyword);
    
    /**
     * 按标签搜索Skills
     *
     * @param tag 标签
     * @return 匹配的Skill列表
     */
    CompletableFuture<List<SkillPackage>> searchByTag(String tag);
    
    /**
     * 获取发现历史
     *
     * @param channelId 渠道ID（可选，null表示所有渠道）
     * @param limit 限制数量
     * @return 历史发现记录
     */
    CompletableFuture<List<DiscoveryHistory>> getDiscoveryHistory(String channelId, int limit);
    
    /**
     * 获取Skill的历史版本
     *
     * @param skillId Skill ID
     * @return 历史版本列表
     */
    CompletableFuture<List<SkillHistory>> getSkillHistory(String skillId);
    
    /**
     * 添加渠道
     *
     * @param channelConfig 渠道配置
     * @return 渠道ID
     */
    CompletableFuture<String> addChannel(ChannelConfig channelConfig);
    
    /**
     * 移除渠道
     *
     * @param channelId 渠道ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> removeChannel(String channelId);
    
    /**
     * 获取所有渠道
     *
     * @return 渠道配置列表
     */
    CompletableFuture<List<ChannelConfig>> getAllChannels();
    
    /**
     * 刷新指定渠道
     *
     * @param channelId 渠道ID
     * @return 刷新结果
     */
    CompletableFuture<RefreshResult> refreshChannel(String channelId);
    
    /**
     * 刷新所有渠道
     *
     * @return 刷新结果列表
     */
    CompletableFuture<List<RefreshResult>> refreshAllChannels();
    
    /**
     * 获取注册统计
     *
     * @return 统计信息
     */
    CompletableFuture<RegistryStats> getStats();
    
    /**
     * 导出注册表
     *
     * @param format 格式（json/yaml）
     * @return 导出内容
     */
    CompletableFuture<String> exportRegistry(String format);
    
    /**
     * 导入注册表
     *
     * @param content 导入内容
     * @param format 格式（json/yaml）
     * @return 导入结果
     */
    CompletableFuture<ImportResult> importRegistry(String content, String format);
    
    // ========== 数据类定义 ==========
    
    /**
     * 注册结果
     */
    class RegisterResult {
        private boolean success;
        private int totalCount;
        private int newCount;
        private int updatedCount;
        private int duplicateCount;
        private List<String> errors;
        private long timestamp;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getNewCount() { return newCount; }
        public void setNewCount(int newCount) { this.newCount = newCount; }
        public int getUpdatedCount() { return updatedCount; }
        public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }
        public int getDuplicateCount() { return duplicateCount; }
        public void setDuplicateCount(int duplicateCount) { this.duplicateCount = duplicateCount; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 渠道配置
     */
    class ChannelConfig {
        private String channelId;
        private String name;
        private String type;  // github, gitee, local, etc.
        private String repositoryUrl;
        private String skillsPath;
        private String token;
        private boolean enabled;
        private long lastRefreshTime;
        private Map<String, Object> metadata;
        
        // Getters and Setters
        public String getChannelId() { return channelId; }
        public void setChannelId(String channelId) { this.channelId = channelId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getRepositoryUrl() { return repositoryUrl; }
        public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }
        public String getSkillsPath() { return skillsPath; }
        public void setSkillsPath(String skillsPath) { this.skillsPath = skillsPath; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public long getLastRefreshTime() { return lastRefreshTime; }
        public void setLastRefreshTime(long lastRefreshTime) { this.lastRefreshTime = lastRefreshTime; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    /**
     * 发现历史
     */
    class DiscoveryHistory {
        private String historyId;
        private String channelId;
        private long discoverTime;
        private int skillCount;
        private List<String> skillIds;
        private String status;  // success, partial, failed
        private String message;
        
        // Getters and Setters
        public String getHistoryId() { return historyId; }
        public void setHistoryId(String historyId) { this.historyId = historyId; }
        public String getChannelId() { return channelId; }
        public void setChannelId(String channelId) { this.channelId = channelId; }
        public long getDiscoverTime() { return discoverTime; }
        public void setDiscoverTime(long discoverTime) { this.discoverTime = discoverTime; }
        public int getSkillCount() { return skillCount; }
        public void setSkillCount(int skillCount) { this.skillCount = skillCount; }
        public List<String> getSkillIds() { return skillIds; }
        public void setSkillIds(List<String> skillIds) { this.skillIds = skillIds; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * Skill历史
     */
    class SkillHistory {
        private String version;
        private String channelId;
        private long registerTime;
        private String downloadUrl;
        private long size;
        private Map<String, Object> metadata;
        
        // Getters and Setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getChannelId() { return channelId; }
        public void setChannelId(String channelId) { this.channelId = channelId; }
        public long getRegisterTime() { return registerTime; }
        public void setRegisterTime(long registerTime) { this.registerTime = registerTime; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    /**
     * 刷新结果
     */
    class RefreshResult {
        private String channelId;
        private boolean success;
        private int discoveredCount;
        private int registeredCount;
        private long duration;
        private String message;
        
        // Getters and Setters
        public String getChannelId() { return channelId; }
        public void setChannelId(String channelId) { this.channelId = channelId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getDiscoveredCount() { return discoveredCount; }
        public void setDiscoveredCount(int discoveredCount) { this.discoveredCount = discoveredCount; }
        public int getRegisteredCount() { return registeredCount; }
        public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 注册统计
     */
    class RegistryStats {
        private int totalChannels;
        private int activeChannels;
        private int totalSkills;
        private int uniqueSkills;
        private long lastUpdateTime;
        private Map<String, Integer> skillsByChannel;
        
        // Getters and Setters
        public int getTotalChannels() { return totalChannels; }
        public void setTotalChannels(int totalChannels) { this.totalChannels = totalChannels; }
        public int getActiveChannels() { return activeChannels; }
        public void setActiveChannels(int activeChannels) { this.activeChannels = activeChannels; }
        public int getTotalSkills() { return totalSkills; }
        public void setTotalSkills(int totalSkills) { this.totalSkills = totalSkills; }
        public int getUniqueSkills() { return uniqueSkills; }
        public void setUniqueSkills(int uniqueSkills) { this.uniqueSkills = uniqueSkills; }
        public long getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
        public Map<String, Integer> getSkillsByChannel() { return skillsByChannel; }
        public void setSkillsByChannel(Map<String, Integer> skillsByChannel) { this.skillsByChannel = skillsByChannel; }
    }
    
    /**
     * 导入结果
     */
    class ImportResult {
        private boolean success;
        private int importedChannels;
        private int importedSkills;
        private List<String> errors;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getImportedChannels() { return importedChannels; }
        public void setImportedChannels(int importedChannels) { this.importedChannels = importedChannels; }
        public int getImportedSkills() { return importedSkills; }
        public void setImportedSkills(int importedSkills) { this.importedSkills = importedSkills; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
}
