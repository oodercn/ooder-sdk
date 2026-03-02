package net.ooder.scene.skill.proxy;

import net.ooder.scene.core.SkillInstallResult;
import net.ooder.scene.core.SkillInstallProgress;
import net.ooder.scene.core.SkillUninstallResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Skill安装代理接口
 * 
 * <p>作为SceneEngine层与SDK层之间的代理，统一封装多种类型Skill的安装逻辑。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>统一封装多种来源Skill的安装（本地/GitHub/Gitee/UDP/SkillCenter）</li>
 *   <li>协调依赖安装与主Skill安装</li>
 *   <li>提供统一的安装进度跟踪</li>
 *   <li>处理安装失败回滚</li>
 *   <li>验证安装完整性</li>
 * </ul>
 * 
 * <h3>分层架构：</h3>
 * <pre>
 * 应用层 (SceneClient)
 *     ↓
 * 代理层 (SkillInstallProxy) ← 本接口
 *     ↓
 * 服务层 (SkillService)
 *     ↓
 * SDK层 (SkillInstaller/Discoverer)
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SkillInstallProxy {

    /**
     * 安装Skill（自动识别来源）
     * 
     * <p>根据skillId自动识别来源并执行安装：</p>
     * <ul>
     *   <li>local: 从本地缓存安装</li>
     *   <li>github: 从GitHub仓库安装</li>
     *   <li>gitee: 从Gitee仓库安装</li>
     *   <li>udp: 从UDP发现安装</li>
     *   <li>skillcenter: 从技能中心安装</li>
     * </ul>
     *
     * @param userId 用户ID
     * @param skillId Skill ID（格式: source:skillName 或 skillName）
     * @param config 安装配置
     * @return 安装结果
     */
    SkillInstallResult install(String userId, String skillId, Map<String, Object> config);

    /**
     * 从指定来源安装Skill
     *
     * @param userId 用户ID
     * @param source 来源（local/github/gitee/udp/skillcenter）
     * @param skillId Skill ID
     * @param version 版本号
     * @param config 安装配置
     * @return 安装结果
     */
    SkillInstallResult installFromSource(String userId, String source, String skillId, 
                                          String version, Map<String, Object> config);

    /**
     * 批量安装Skill
     *
     * @param userId 用户ID
     * @param skillIds Skill ID列表
     * @param config 安装配置
     * @return 批量安装结果
     */
    BatchInstallResult installBatch(String userId, java.util.List<String> skillIds, 
                                     Map<String, Object> config);

    /**
     * 安装Skill及其依赖
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     * @param config 安装配置
     * @return 安装结果（包含依赖安装情况）
     */
    SkillInstallResult installWithDependencies(String userId, String skillId, 
                                                Map<String, Object> config);

    /**
     * 卸载Skill
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     * @param removeData 是否移除数据
     * @return 卸载结果
     */
    SkillUninstallResult uninstall(String userId, String skillId, boolean removeData);

    /**
     * 获取安装进度
     *
     * @param installId 安装ID
     * @return 安装进度
     */
    SkillInstallProgress getInstallProgress(String installId);

    /**
     * 取消安装
     *
     * @param installId 安装ID
     * @return 是否成功取消
     */
    boolean cancelInstall(String installId);

    /**
     * 暂停安装
     *
     * @param installId 安装ID
     * @return 是否成功暂停
     */
    boolean pauseInstall(String installId);

    /**
     * 恢复安装
     *
     * @param installId 安装ID
     * @return 是否成功恢复
     */
    boolean resumeInstall(String installId);

    /**
     * 验证Skill安装完整性
     *
     * @param skillId Skill ID
     * @return 验证结果
     */
    IntegrityCheckResult checkIntegrity(String skillId);

    /**
     * 修复损坏的Skill安装
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     * @return 修复结果
     */
    RepairResult repair(String userId, String skillId);

    /**
     * 获取安装历史
     *
     * @param userId 用户ID
     * @return 安装历史列表
     */
    java.util.List<InstallHistory> getInstallHistory(String userId);

    /**
     * 批量安装结果
     */
    class BatchInstallResult {
        private String batchId;
        private int totalCount;
        private int successCount;
        private int failedCount;
        private java.util.List<SkillInstallResult> results;
        private long startTime;
        private long endTime;

        public String getBatchId() { return batchId; }
        public void setBatchId(String batchId) { this.batchId = batchId; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailedCount() { return failedCount; }
        public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
        public java.util.List<SkillInstallResult> getResults() { return results; }
        public void setResults(java.util.List<SkillInstallResult> results) { this.results = results; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
    }

    /**
     * 完整性检查结果
     */
    class IntegrityCheckResult {
        private String skillId;
        private boolean valid;
        private java.util.List<String> missingFiles;
        private java.util.List<String> corruptedFiles;
        private String checksum;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public java.util.List<String> getMissingFiles() { return missingFiles; }
        public void setMissingFiles(java.util.List<String> missingFiles) { this.missingFiles = missingFiles; }
        public java.util.List<String> getCorruptedFiles() { return corruptedFiles; }
        public void setCorruptedFiles(java.util.List<String> corruptedFiles) { this.corruptedFiles = corruptedFiles; }
        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }
    }

    /**
     * 修复结果
     */
    class RepairResult {
        private String skillId;
        private boolean success;
        private java.util.List<String> repairedFiles;
        private String message;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public java.util.List<String> getRepairedFiles() { return repairedFiles; }
        public void setRepairedFiles(java.util.List<String> repairedFiles) { this.repairedFiles = repairedFiles; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * 安装历史记录
     */
    class InstallHistory {
        private String installId;
        private String skillId;
        private String skillName;
        private String version;
        private String source;
        private String status;
        private long installTime;
        private String userId;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getInstallTime() { return installTime; }
        public void setInstallTime(long installTime) { this.installTime = installTime; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
}
