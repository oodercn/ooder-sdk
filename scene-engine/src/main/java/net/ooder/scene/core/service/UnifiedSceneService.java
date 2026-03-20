package net.ooder.scene.core.service;

import net.ooder.engine.ConnectInfo;
import net.ooder.scene.core.security.AuditLog;
import net.ooder.scene.core.security.OperationContext;
import net.ooder.scene.core.security.OperationResult;
import net.ooder.scene.session.SessionContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SceneEngine统一服务入口
 * 
 * <p>作为SceneEngine的核心服务门面，整合所有功能模块，提供统一的安全认证和审计日志。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>统一用户认证和会话管理</li>
 *   <li>统一操作审计日志</li>
 *   <li>统一权限检查</li>
 *   <li>统一服务路由</li>
 * </ul>
 * 
 * <h3>分层架构：</h3>
 * <pre>
 * 应用层 (SceneClient/AdminClient)
 *     ↓
 * 统一服务层 (UnifiedSceneService) ← 本接口
 *     ↓
 * 业务服务层 (SkillService/DiscoveryService/SceneService)
 *     ↓
 * SDK层 (Agent SDK)
 * </pre>
 * 
 * <h3>安全认证流程：</h3>
 * <pre>
 * 1. 用户调用API → 检查SessionContext
 * 2. 验证用户权限 → 记录AuditLog
 * 3. 执行操作 → 更新AuditLog结果
 * 4. 返回结果
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface UnifiedSceneService {

    // ==================== 会话管理 ====================

    /**
     * 用户登录
     * 
     * @param connectInfo 用户连接信息
     * @return 会话上下文
     */
    SessionContext login(ConnectInfo connectInfo);

    /**
     * 用户登出
     * 
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean logout(String sessionId);

    /**
     * 验证会话有效性
     * 
     * @param sessionId 会话ID
     * @return 会话上下文，无效返回null
     */
    SessionContext validateSession(String sessionId);

    /**
     * 获取当前会话
     * 
     * @return 会话上下文
     */
    SessionContext getCurrentSession();

    // ==================== Skill发现 ====================

    /**
     * 发现Skill（统一入口）
     * 
     * @param sessionId 会话ID
     * @param source 来源（local/github/gitee/udp/skillcenter/all）
     * @param filters 过滤条件
     * @return Skill列表
     */
    CompletableFuture<DiscoveryResult> discoverSkills(String sessionId, String source, Map<String, Object> filters);

    /**
     * 搜索Skill
     * 
     * @param sessionId 会话ID
     * @param keyword 关键词
     * @return 搜索结果
     */
    CompletableFuture<DiscoveryResult> searchSkills(String sessionId, String keyword);

    /**
     * 获取Skill详情
     * 
     * @param sessionId 会话ID
     * @param skillId Skill ID
     * @return Skill详情
     */
    SkillDetail getSkillDetail(String sessionId, String skillId);

    // ==================== Skill安装 ====================

    /**
     * 安装Skill（统一入口）
     * 
     * @param sessionId 会话ID
     * @param skillId Skill ID
     * @param config 安装配置
     * @return 安装结果
     */
    InstallResult installSkill(String sessionId, String skillId, Map<String, Object> config);

    /**
     * 从指定来源安装Skill
     * 
     * @param sessionId 会话ID
     * @param source 来源
     * @param skillId Skill ID
     * @param version 版本
     * @param config 安装配置
     * @return 安装结果
     */
    InstallResult installSkillFromSource(String sessionId, String source, String skillId, 
                                          String version, Map<String, Object> config);

    /**
     * 卸载Skill
     * 
     * @param sessionId 会话ID
     * @param skillId Skill ID
     * @param removeData 是否移除数据
     * @return 卸载结果
     */
    UninstallResult uninstallSkill(String sessionId, String skillId, boolean removeData);

    /**
     * 获取安装进度
     * 
     * @param sessionId 会话ID
     * @param installId 安装ID
     * @return 安装进度
     */
    InstallProgress getInstallProgress(String sessionId, String installId);

    /**
     * 取消安装
     * 
     * @param sessionId 会话ID
     * @param installId 安装ID
     * @return 是否成功
     */
    boolean cancelInstall(String sessionId, String installId);

    // ==================== Skill管理 ====================

    /**
     * 获取已安装Skill列表
     * 
     * @param sessionId 会话ID
     * @return Skill列表
     */
    List<InstalledSkillInfo> getInstalledSkills(String sessionId);

    /**
     * 验证Skill完整性
     * 
     * @param sessionId 会话ID
     * @param skillId Skill ID
     * @return 验证结果
     */
    IntegrityCheckResult checkSkillIntegrity(String sessionId, String skillId);

    /**
     * 修复Skill
     * 
     * @param sessionId 会话ID
     * @param skillId Skill ID
     * @return 修复结果
     */
    RepairResult repairSkill(String sessionId, String skillId);

    /**
     * 更新Skill
     * 
     * @param sessionId 会话ID
     * @param skillId Skill ID
     * @param targetVersion 目标版本
     * @return 更新结果
     */
    UpdateResult updateSkill(String sessionId, String skillId, String targetVersion);

    // ==================== 场景管理 ====================

    /**
     * 创建场景
     * 
     * @param sessionId 会话ID
     * @param config 场景配置
     * @return 场景信息
     */
    SceneInfo createScene(String sessionId, Map<String, Object> config);

    /**
     * 获取场景列表
     * 
     * @param sessionId 会话ID
     * @return 场景列表
     */
    List<SceneInfo> getScenes(String sessionId);

    /**
     * 获取单个场景详情
     * 
     * @param sessionId 会话ID
     * @param sceneId 场景ID
     * @return 场景信息，不存在返回null
     */
    SceneInfo getScene(String sessionId, String sceneId);

    /**
     * 删除场景
     * 
     * @param sessionId 会话ID
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean deleteScene(String sessionId, String sceneId);

    /**
     * 激活场景
     * 
     * @param sessionId 会话ID
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean activateScene(String sessionId, String sceneId);

    /**
     * 关闭场景
     * 
     * @param sessionId 会话ID
     * @param sceneId 场景ID
     * @return 是否成功
     */
    boolean deactivateScene(String sessionId, String sceneId);

    // ==================== 审计日志 ====================

    /**
     * 查询审计日志
     * 
     * @param sessionId 会话ID（需管理员权限）
     * @param filters 过滤条件
     * @return 审计日志列表
     */
    List<AuditLog> queryAuditLogs(String sessionId, Map<String, Object> filters);

    /**
     * 获取用户操作历史
     * 
     * @param sessionId 会话ID
     * @return 操作历史
     */
    List<AuditLog> getUserOperationHistory(String sessionId);

    // ==================== 系统管理 ====================

    /**
     * 获取系统状态
     * 
     * @param sessionId 会话ID
     * @return 系统状态
     */
    SystemStatus getSystemStatus(String sessionId);

    /**
     * 配置多仓库
     * 
     * @param sessionId 会话ID
     * @param source 来源（github/gitee）
     * @param configs 仓库配置列表
     * @return 是否成功
     */
    boolean configureRepositories(String sessionId, String source, List<RepositoryConfig> configs);

    /**
     * 获取仓库配置
     * 
     * @param sessionId 会话ID
     * @param source 来源
     * @return 仓库配置列表
     */
    List<RepositoryConfig> getRepositoryConfigs(String sessionId, String source);

    // ==================== 内部方法（供实现类使用） ====================

    /**
     * 创建操作上下文
     * 
     * @param sessionId 会话ID
     * @param operation 操作类型
     * @param resource 资源类型
     * @return 操作上下文
     */
    OperationContext createOperationContext(String sessionId, String operation, String resource);

    /**
     * 记录审计日志
     * 
     * @param context 操作上下文
     * @param result 操作结果
     * @param message 消息
     */
    void logAudit(OperationContext context, OperationResult result, String message);

    /**
     * 检查权限
     * 
     * @param sessionId 会话ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean checkPermission(String sessionId, String permission);

    // ==================== 数据类定义 ====================

    /**
     * 发现结果
     */
    class DiscoveryResult {
        private List<SkillInfo> skills;
        private int totalCount;
        private String source;
        private long timestamp;
        private boolean fromCache;

        public List<SkillInfo> getSkills() { return skills; }
        public void setSkills(List<SkillInfo> skills) { this.skills = skills; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public boolean isFromCache() { return fromCache; }
        public void setFromCache(boolean fromCache) { this.fromCache = fromCache; }
    }

    /**
     * Skill信息
     */
    class SkillInfo {
        private String skillId;
        private String name;
        private String version;
        private String description;
        private String source;
        private List<String> tags;
        private boolean installed;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public boolean isInstalled() { return installed; }
        public void setInstalled(boolean installed) { this.installed = installed; }
    }

    /**
     * Skill详情
     */
    class SkillDetail extends SkillInfo {
        private Map<String, Object> manifest;
        private List<String> dependencies;
        private List<String> capabilities;
        private long size;
        private String checksum;

        public Map<String, Object> getManifest() { return manifest; }
        public void setManifest(Map<String, Object> manifest) { this.manifest = manifest; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }
    }

    /**
     * 安装结果
     */
    class InstallResult {
        private String installId;
        private String skillId;
        private boolean success;
        private String message;
        private List<String> installedDependencies;
        private long startTime;
        private long endTime;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<String> getInstalledDependencies() { return installedDependencies; }
        public void setInstalledDependencies(List<String> installedDependencies) { this.installedDependencies = installedDependencies; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
    }

    /**
     * 安装进度
     */
    class InstallProgress {
        private String installId;
        private String currentStep;
        private int totalSteps;
        private int completedSteps;
        private String status;
        private String message;
        private long timestamp;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        public int getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 卸载结果
     */
    class UninstallResult {
        private String skillId;
        private boolean success;
        private String message;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * 已安装Skill信息
     */
    class InstalledSkillInfo extends SkillInfo {
        private long installTime;
        private String installPath;
        private boolean active;

        public long getInstallTime() { return installTime; }
        public void setInstallTime(long installTime) { this.installTime = installTime; }
        public String getInstallPath() { return installPath; }
        public void setInstallPath(String installPath) { this.installPath = installPath; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    /**
     * 完整性检查结果
     */
    class IntegrityCheckResult {
        private String skillId;
        private boolean valid;
        private List<String> missingFiles;
        private List<String> corruptedFiles;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getMissingFiles() { return missingFiles; }
        public void setMissingFiles(List<String> missingFiles) { this.missingFiles = missingFiles; }
        public List<String> getCorruptedFiles() { return corruptedFiles; }
        public void setCorruptedFiles(List<String> corruptedFiles) { this.corruptedFiles = corruptedFiles; }
    }

    /**
     * 修复结果
     */
    class RepairResult {
        private String skillId;
        private boolean success;
        private List<String> repairedFiles;
        private String message;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<String> getRepairedFiles() { return repairedFiles; }
        public void setRepairedFiles(List<String> repairedFiles) { this.repairedFiles = repairedFiles; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * 更新结果
     */
    class UpdateResult {
        private String skillId;
        private boolean success;
        private String fromVersion;
        private String toVersion;
        private String message;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getFromVersion() { return fromVersion; }
        public void setFromVersion(String fromVersion) { this.fromVersion = fromVersion; }
        public String getToVersion() { return toVersion; }
        public void setToVersion(String toVersion) { this.toVersion = toVersion; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * 场景信息
     */
    class SceneInfo {
        private String sceneId;
        private String name;
        private String description;
        private String type;
        private boolean active;
        private List<String> skills;
        private List<String> collaborativeScenes;
        private Map<String, Object> config;
        private long createTime;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public List<String> getCollaborativeScenes() { return collaborativeScenes; }
        public void setCollaborativeScenes(List<String> collaborativeScenes) { this.collaborativeScenes = collaborativeScenes; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
    }

    /**
     * 系统状态
     */
    class SystemStatus {
        private boolean online;
        private int activeScenes;
        private int installedSkills;
        private long uptime;
        private String version;

        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
        public int getActiveScenes() { return activeScenes; }
        public void setActiveScenes(int activeScenes) { this.activeScenes = activeScenes; }
        public int getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(int installedSkills) { this.installedSkills = installedSkills; }
        public long getUptime() { return uptime; }
        public void setUptime(long uptime) { this.uptime = uptime; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    /**
     * 仓库配置
     */
    class RepositoryConfig {
        private String name;
        private String source;
        private String owner;
        private String repo;
        private String token;
        private boolean isDefault;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        public String getRepo() { return repo; }
        public void setRepo(String repo) { this.repo = repo; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    }
}
