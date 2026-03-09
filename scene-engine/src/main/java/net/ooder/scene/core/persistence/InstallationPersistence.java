package net.ooder.scene.core.persistence;

import java.util.List;
import java.util.Map;

/**
 * 安装状态持久化接口
 *
 * <p>管理安装状态的持久化存储和恢复。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface InstallationPersistence {

    /**
     * 保存安装状态
     *
     * @param state 安装状态
     * @return 保存结果
     */
    SaveResult saveState(InstallationState state);

    /**
     * 加载安装状态
     *
     * @param installId 安装ID
     * @return 安装状态
     */
    InstallationState loadState(String installId);

    /**
     * 更新安装状态
     *
     * @param installId 安装ID
     * @param updates 更新内容
     * @return 更新结果
     */
    UpdateResult updateState(String installId, Map<String, Object> updates);

    /**
     * 删除安装状态
     *
     * @param installId 安装ID
     * @return 是否成功
     */
    boolean deleteState(String installId);

    /**
     * 查询安装状态
     *
     * @param query 查询条件
     * @return 安装状态列表
     */
    List<InstallationState> queryStates(InstallationQuery query);

    /**
     * 保存检查点
     *
     * @param installId 安装ID
     * @param checkpoint 检查点
     * @return 保存结果
     */
    SaveResult saveCheckpoint(String installId, Checkpoint checkpoint);

    /**
     * 加载检查点
     *
     * @param installId 安装ID
     * @param checkpointId 检查点ID
     * @return 检查点
     */
    Checkpoint loadCheckpoint(String installId, String checkpointId);

    /**
     * 获取最新检查点
     *
     * @param installId 安装ID
     * @return 最新检查点
     */
    Checkpoint getLatestCheckpoint(String installId);

    /**
     * 列出所有检查点
     *
     * @param installId 安装ID
     * @return 检查点列表
     */
    List<Checkpoint> listCheckpoints(String installId);

    /**
     * 删除检查点
     *
     * @param installId 安装ID
     * @param checkpointId 检查点ID
     * @return 是否成功
     */
    boolean deleteCheckpoint(String installId, String checkpointId);

    /**
     * 清理过期状态
     *
     * @param expireTime 过期时间
     * @return 清理数量
     */
    int cleanupExpiredStates(long expireTime);

    /**
     * 导出安装状态
     *
     * @param installId 安装ID
     * @return 导出数据
     */
    ExportData exportState(String installId);

    /**
     * 导入安装状态
     *
     * @param data 导出数据
     * @return 导入结果
     */
    ImportResult importState(ExportData data);

    /**
     * 安装状态
     */
    class InstallationState {
        private String installId;
        private String sceneId;
        private String userId;
        private String skillId;
        private InstallationPhase phase;
        private String currentStep;
        private int totalSteps;
        private int completedSteps;
        private double progress;
        private Map<String, Object> context;
        private Map<String, Object> results;
        private long createTime;
        private long updateTime;
        private long expireTime;
        private String errorMessage;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public InstallationPhase getPhase() { return phase; }
        public void setPhase(InstallationPhase phase) { this.phase = phase; }
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        public int getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        public Map<String, Object> getResults() { return results; }
        public void setResults(Map<String, Object> results) { this.results = results; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getUpdateTime() { return updateTime; }
        public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
        public long getExpireTime() { return expireTime; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 安装阶段
     */
    enum InstallationPhase {
        INITIALIZING, DOWNLOADING, INSTALLING, CONFIGURING, ACTIVATING, COMPLETED, FAILED, CANCELLED
    }

    /**
     * 检查点
     */
    class Checkpoint {
        private String checkpointId;
        private String installId;
        private String stepId;
        private String stepName;
        private Map<String, Object> state;
        private long createTime;
        private String description;

        public String getCheckpointId() { return checkpointId; }
        public void setCheckpointId(String checkpointId) { this.checkpointId = checkpointId; }
        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public Map<String, Object> getState() { return state; }
        public void setState(Map<String, Object> state) { this.state = state; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * 安装查询
     */
    class InstallationQuery {
        private String sceneId;
        private String userId;
        private String skillId;
        private InstallationPhase phase;
        private long startTime;
        private long endTime;
        private int limit;
        private int offset;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public InstallationPhase getPhase() { return phase; }
        public void setPhase(InstallationPhase phase) { this.phase = phase; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        public int getOffset() { return offset; }
        public void setOffset(int offset) { this.offset = offset; }
    }

    /**
     * 保存结果
     */
    class SaveResult {
        private boolean success;
        private String id;
        private String errorMessage;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 更新结果
     */
    class UpdateResult {
        private boolean success;
        private int updatedCount;
        private String errorMessage;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getUpdatedCount() { return updatedCount; }
        public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 导出数据
     */
    class ExportData {
        private String installId;
        private String format;
        private byte[] data;
        private long exportTime;

        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public byte[] getData() { return data; }
        public void setData(byte[] data) { this.data = data; }
        public long getExportTime() { return exportTime; }
        public void setExportTime(long exportTime) { this.exportTime = exportTime; }
    }

    /**
     * 导入结果
     */
    class ImportResult {
        private boolean success;
        private String installId;
        private String errorMessage;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
