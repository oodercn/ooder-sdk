package net.ooder.scene.discovery.internal;

import net.ooder.scene.discovery.api.DiscoveryService;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 内部发现服务接口
 * 
 * 此接口仅供SceneEngine内部使用，不对外暴露
 * 提供统一的发现、验证、安装、依赖管理功能
 * 
 * @author ooder Team
 * @since 2.3
 */
public interface InternalDiscoveryService {

    /**
     * 发现技能（内部方法，带缓存策略）
     * 
     * @param request 发现请求
     * @param useCache 是否使用缓存
     * @return 发现结果
     */
    CompletableFuture<DiscoveryResult> discoverInternal(DiscoveryRequest request, boolean useCache);

    /**
     * 强制刷新缓存
     * 
     * @param request 发现请求
     * @return 发现结果
     */
    CompletableFuture<DiscoveryResult> refreshInternal(DiscoveryRequest request);

    /**
     * 验证技能完整性（内部方法）
     * 
     * @param skillId 技能ID
     * @param source 来源（本地/远程）
     * @return 验证结果
     */
    CompletableFuture<IntegrityCheckResult> checkIntegrityInternal(String skillId, String source);

    /**
     * 检查依赖关系（内部方法）
     * 
     * @param skillId 技能ID
     * @param version 版本
     * @return 依赖检查结果
     */
    CompletableFuture<DependencyCheckResult> checkDependenciesInternal(String skillId, String version);

    /**
     * 安装依赖（内部方法，带进度回调）
     * 
     * @param skillId 技能ID
     * @param version 版本
     * @param progressCallback 进度回调
     * @return 安装结果
     */
    CompletableFuture<DependencyInstallResult> installDependenciesInternal(
            String skillId, 
            String version,
            InstallProgressCallback progressCallback);

    /**
     * 恢复中断的安装
     * 
     * @param installId 安装任务ID
     * @param progressCallback 进度回调
     * @return 安装结果
     */
    CompletableFuture<DependencyInstallResult> resumeInstallInternal(
            String installId,
            InstallProgressCallback progressCallback);

    /**
     * 获取安装进度
     * 
     * @param installId 安装任务ID
     * @return 安装进度
     */
    CompletableFuture<InstallProgress> getInstallProgress(String installId);

    /**
     * 取消安装
     * 
     * @param installId 安装任务ID
     * @return 是否成功取消
     */
    CompletableFuture<Boolean> cancelInstall(String installId);

    /**
     * 清理失败的安装残留
     * 
     * @param installId 安装任务ID
     * @return 是否成功清理
     */
    CompletableFuture<Boolean> cleanupFailedInstall(String installId);

    /**
     * 获取历史发现数据
     * 
     * @return 历史发现记录
     */
    CompletableFuture<List<DiscoveryHistory>> getDiscoveryHistory();

    /**
     * 获取多渠道发现的技能去重后列表
     * 
     * @return 去重后的技能列表
     */
    CompletableFuture<List<UnifiedSkillInfo>> getUnifiedSkillList();

    /**
     * 安装进度回调接口
     */
    interface InstallProgressCallback {
        void onProgress(InstallProgress progress);
        void onComplete(DependencyInstallResult result);
        void onError(String error);
    }

    /**
     * 完整性检查结果
     */
    class IntegrityCheckResult {
        private String skillId;
        private String version;
        private boolean valid;
        private List<String> missingFiles;
        private List<String> corruptedFiles;
        private String checksum;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getMissingFiles() { return missingFiles; }
        public void setMissingFiles(List<String> missingFiles) { this.missingFiles = missingFiles; }
        public List<String> getCorruptedFiles() { return corruptedFiles; }
        public void setCorruptedFiles(List<String> corruptedFiles) { this.corruptedFiles = corruptedFiles; }
        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }
    }

    /**
     * 依赖检查结果
         */
    class DependencyCheckResult {
        private String skillId;
        private String version;
        private boolean satisfied;
        private List<DependencyInfo> missingDependencies;
        private List<DependencyInfo> installedDependencies;
        private List<DependencyConflict> conflicts;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isSatisfied() { return satisfied; }
        public void setSatisfied(boolean satisfied) { this.satisfied = satisfied; }
        public List<DependencyInfo> getMissingDependencies() { return missingDependencies; }
        public void setMissingDependencies(List<DependencyInfo> missingDependencies) { this.missingDependencies = missingDependencies; }
        public List<DependencyInfo> getInstalledDependencies() { return installedDependencies; }
        public void setInstalledDependencies(List<DependencyInfo> installedDependencies) { this.installedDependencies = installedDependencies; }
        public List<DependencyConflict> getConflicts() { return conflicts; }
        public void setConflicts(List<DependencyConflict> conflicts) { this.conflicts = conflicts; }
    }

    /**
     * 依赖安装结果
     */
    class DependencyInstallResult {
        private String installId;
        private String skillId;
        private boolean success;
        private List<String> installedSkills;
        private List<String> failedSkills;
        private long startTime;
        private long endTime;
        private String errorMessage;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<String> getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
        public List<String> getFailedSkills() { return failedSkills; }
        public void setFailedSkills(List<String> failedSkills) { this.failedSkills = failedSkills; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 安装进度
     */
    class InstallProgress {
        private String installId;
        private String currentSkill;
        private int totalSkills;
        private int completedSkills;
        private String status;
        private String message;
        private long timestamp;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getCurrentSkill() { return currentSkill; }
        public void setCurrentSkill(String currentSkill) { this.currentSkill = currentSkill; }
        public int getTotalSkills() { return totalSkills; }
        public void setTotalSkills(int totalSkills) { this.totalSkills = totalSkills; }
        public int getCompletedSkills() { return completedSkills; }
        public void setCompletedSkills(int completedSkills) { this.completedSkills = completedSkills; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 依赖信息
     */
    class DependencyInfo {
        private String skillId;
        private String version;
        private String source;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    /**
     * 依赖冲突
     */
    class DependencyConflict {
        private String skillId;
        private String requiredVersion;
        private String installedVersion;
        private String conflictType;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getRequiredVersion() { return requiredVersion; }
        public void setRequiredVersion(String requiredVersion) { this.requiredVersion = requiredVersion; }
        public String getInstalledVersion() { return installedVersion; }
        public void setInstalledVersion(String installedVersion) { this.installedVersion = installedVersion; }
        public String getConflictType() { return conflictType; }
        public void setConflictType(String conflictType) { this.conflictType = conflictType; }
    }

    /**
     * 发现历史记录
     */
    class DiscoveryHistory {
        private String id;
        private long timestamp;
        private String source;
        private int skillCount;
        private List<String> skillIds;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public int getSkillCount() { return skillCount; }
        public void setSkillCount(int skillCount) { this.skillCount = skillCount; }
        public List<String> getSkillIds() { return skillIds; }
        public void setSkillIds(List<String> skillIds) { this.skillIds = skillIds; }
    }

    /**
     * 统一技能信息（多渠道去重后）
     */
    class UnifiedSkillInfo {
        private String skillId;
        private String name;
        private String version;
        private List<String> sources;
        private String preferredSource;
        private long lastDiscovered;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public List<String> getSources() { return sources; }
        public void setSources(List<String> sources) { this.sources = sources; }
        public String getPreferredSource() { return preferredSource; }
        public void setPreferredSource(String preferredSource) { this.preferredSource = preferredSource; }
        public long getLastDiscovered() { return lastDiscovered; }
        public void setLastDiscovered(long lastDiscovered) { this.lastDiscovered = lastDiscovered; }
    }
}
