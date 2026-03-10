package net.ooder.scene.core.activation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 激活状态持久化模型
 * 
 * <p>用于持久化存储场景技能的激活状态，支持断点续激活和状态恢复</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ActivationState implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 状态标识
    private String stateId;                  // 状态ID
    private String skillId;                  // 技能ID
    private String templateId;               // 模板ID
    private String instanceId;               // 实例ID
    
    // 激活状态
    private ActivationStatus status;         // 当前状态
    private String currentStepId;            // 当前步骤ID
    private String currentRoleId;            // 当前角色ID
    
    // 进度信息
    private int totalSteps;                  // 总步骤数
    private int completedSteps;              // 已完成步骤数
    private double progressPercentage;       // 进度百分比
    
    // 上下文数据
    private Map<String, Object> contextData; // 上下文数据
    private Map<String, Object> stepResults; // 步骤执行结果
    
    // 时间戳
    private LocalDateTime createdAt;         // 创建时间
    private LocalDateTime updatedAt;         // 更新时间
    private LocalDateTime completedAt;       // 完成时间
    
    // 错误信息
    private String errorCode;                // 错误码
    private String errorMessage;             // 错误信息
    
    /**
     * 激活状态枚举
     */
    public enum ActivationStatus {
        PENDING,      // 待激活
        INITIALIZING, // 初始化中
        EXECUTING,    // 执行中
        PAUSED,       // 已暂停
        COMPLETED,    // 已完成
        FAILED,       // 失败
        ROLLING_BACK, // 回滚中
        CANCELLED     // 已取消
    }
    
    // === 构造方法 ===
    
    public ActivationState() {
        this.createdAt = LocalDateTime.now();
        this.status = ActivationStatus.PENDING;
    }
    
    public ActivationState(String skillId, String templateId) {
        this();
        this.skillId = skillId;
        this.templateId = templateId;
    }
    
    // === Getter/Setter ===
    
    public String getStateId() {
        return stateId;
    }
    
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public ActivationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ActivationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        
        if (status == ActivationStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public String getCurrentStepId() {
        return currentStepId;
    }
    
    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCurrentRoleId() {
        return currentRoleId;
    }
    
    public void setCurrentRoleId(String currentRoleId) {
        this.currentRoleId = currentRoleId;
    }
    
    public int getTotalSteps() {
        return totalSteps;
    }
    
    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
        updateProgress();
    }
    
    public int getCompletedSteps() {
        return completedSteps;
    }
    
    public void setCompletedSteps(int completedSteps) {
        this.completedSteps = completedSteps;
        updateProgress();
    }
    
    public double getProgressPercentage() {
        return progressPercentage;
    }
    
    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    
    public Map<String, Object> getContextData() {
        return contextData;
    }
    
    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }
    
    public Map<String, Object> getStepResults() {
        return stepResults;
    }
    
    public void setStepResults(Map<String, Object> stepResults) {
        this.stepResults = stepResults;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    // === 业务方法 ===
    
    /**
     * 更新进度
     */
    private void updateProgress() {
        if (totalSteps > 0) {
            this.progressPercentage = (double) completedSteps / totalSteps * 100;
        } else {
            this.progressPercentage = 0;
        }
    }
    
    /**
     * 增加已完成步骤数
     */
    public void incrementCompletedSteps() {
        this.completedSteps++;
        updateProgress();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 记录步骤结果
     */
    public void recordStepResult(String stepId, Object result) {
        if (this.stepResults != null) {
            this.stepResults.put(stepId, result);
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取步骤结果
     */
    @SuppressWarnings("unchecked")
    public <T> T getStepResult(String stepId) {
        if (this.stepResults == null) {
            return null;
        }
        return (T) this.stepResults.get(stepId);
    }
    
    /**
     * 设置上下文数据
     */
    public void setContextValue(String key, Object value) {
        if (this.contextData != null) {
            this.contextData.put(key, value);
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取上下文数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getContextValue(String key) {
        if (this.contextData == null) {
            return null;
        }
        return (T) this.contextData.get(key);
    }
    
    /**
     * 标记为失败
     */
    public void markFailed(String errorCode, String errorMessage) {
        this.status = ActivationStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 是否可以恢复
     */
    public boolean isResumable() {
        return status == ActivationStatus.PAUSED || 
               status == ActivationStatus.FAILED;
    }
    
    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return status == ActivationStatus.COMPLETED;
    }
    
    /**
     * 是否正在执行
     */
    public boolean isExecuting() {
        return status == ActivationStatus.EXECUTING ||
               status == ActivationStatus.INITIALIZING;
    }
    
    @Override
    public String toString() {
        return "ActivationState{" +
                "stateId='" + stateId + '\'' +
                ", skillId='" + skillId + '\'' +
                ", status=" + status +
                ", progress=" + String.format("%.1f%%", progressPercentage) +
                ", currentStep='" + currentStepId + '\'' +
                '}';
    }
}
