package net.ooder.scene.core.activation.model;

import net.ooder.scene.core.template.SceneTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 激活流程
 * 
 * <p>表示一个场景技能的激活流程实例，记录完整的激活过程</p>
 *
 * @author Ooder Team
 * @version 3.1.0
 * @since 2.3.1
 */
public class ActivationProcess implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String processId;
    private String templateId;
    private String sceneGroupId;
    private String userId;
    private String roleId;
    private String roleName;
    
    private ProcessStatus status;
    private List<StepExecution> steps;
    private int currentStepIndex;
    private int totalSteps;
    
    private String keyId;
    private String keyStatus;
    
    private List<NetworkAction> networkActions;
    private List<SceneTemplate.PrivateCapabilityConfig> privateCapabilities;
    private List<String> enabledPrivateCapabilities;
    
    private String leaderId;
    private List<String> collaboratorIds;
    private Map<String, Object> config;
    
    private long createdAt;
    private long startedAt;
    private long completedAt;
    private long updatedAt;
    
    private String errorMessage;
    
    public ActivationProcess() {
        this.processId = generateProcessId();
        this.status = ProcessStatus.CREATED;
        this.steps = new ArrayList<>();
        this.networkActions = new ArrayList<>();
        this.privateCapabilities = new ArrayList<>();
        this.enabledPrivateCapabilities = new ArrayList<>();
        this.collaboratorIds = new ArrayList<>();
        this.config = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.currentStepIndex = 0;
        this.totalSteps = 0;
    }
    
    private static String generateProcessId() {
        return "proc-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 开始流程
     */
    public void start() {
        this.status = ProcessStatus.EXECUTING;
        this.startedAt = System.currentTimeMillis();
    }
    
    /**
     * 完成流程
     */
    public void complete() {
        this.status = ProcessStatus.COMPLETED;
        this.completedAt = System.currentTimeMillis();
    }
    
    /**
     * 失败
     */
    public void fail(String errorMessage) {
        this.status = ProcessStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = System.currentTimeMillis();
    }
    
    /**
     * 暂停流程
     */
    public void pause() {
        if (this.status == ProcessStatus.EXECUTING) {
            this.status = ProcessStatus.PAUSED;
        }
    }
    
    /**
     * 恢复流程
     */
    public void resume() {
        if (this.status == ProcessStatus.PAUSED) {
            this.status = ProcessStatus.EXECUTING;
        }
    }
    
    /**
     * 取消流程
     */
    public void cancel() {
        this.status = ProcessStatus.CANCELLED;
        this.completedAt = System.currentTimeMillis();
    }
    
    /**
     * 添加步骤执行记录
     */
    public void addStepExecution(StepExecution step) {
        this.steps.add(step);
    }
    
    /**
     * 获取当前步骤
     */
    public StepExecution getCurrentStep() {
        if (steps.isEmpty()) {
            return null;
        }
        return steps.get(steps.size() - 1);
    }
    
    /**
     * 获取已完成的步骤数
     */
    public int getCompletedStepCount() {
        return (int) steps.stream()
                .filter(s -> s.getStatus() == StepStatus.COMPLETED)
                .count();
    }
    
    /**
     * 获取总步骤数
     */
    public int getTotalStepCount() {
        return steps.size();
    }
    
    /**
     * 获取进度百分比
     */
    public int getProgress() {
        if (totalSteps == 0) return 0;
        return (currentStepIndex * 100) / totalSteps;
    }
    
    /**
     * 是否已完成
     */
    public boolean isCompleted() {
        return status == ProcessStatus.COMPLETED;
    }
    
    /**
     * 是否进行中
     */
    public boolean isInProgress() {
        return status == ProcessStatus.EXECUTING;
    }
    
    /**
     * 是否失败
     */
    public boolean isFailed() {
        return status == ProcessStatus.FAILED;
    }
    
    /**
     * 更新时间戳
     */
    public void touch() {
        this.updatedAt = System.currentTimeMillis();
    }
    
    /**
     * 获取当前步骤索引对应的步骤
     */
    public StepExecution getCurrentStepByIndex() {
        if (steps == null || currentStepIndex < 0 || currentStepIndex >= steps.size()) {
            return null;
        }
        return steps.get(currentStepIndex);
    }
    
    /**
     * 移动到下一步
     */
    public void moveToNextStep() {
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            touch();
        }
    }
    
    /**
     * 查找指定步骤
     */
    public StepExecution findStep(String stepId) {
        return steps.stream()
                .filter(s -> s.getStepId().equals(stepId))
                .findFirst()
                .orElse(null);
    }
    
    // Getters and Setters
    
    public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public String getSceneId() {
        return templateId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getRoleId() {
        return roleId;
    }
    
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public int getCurrentStepIndex() {
        return currentStepIndex;
    }
    
    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }
    
    public int getTotalSteps() {
        return totalSteps;
    }
    
    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
    
    public String getKeyStatus() {
        return keyStatus;
    }
    
    public void setKeyStatus(String keyStatus) {
        this.keyStatus = keyStatus;
    }
    
    public List<NetworkAction> getNetworkActions() {
        return networkActions != null ? networkActions : new ArrayList<>();
    }
    
    public void setNetworkActions(List<NetworkAction> networkActions) {
        this.networkActions = networkActions;
    }
    
    public List<SceneTemplate.PrivateCapabilityConfig> getPrivateCapabilities() {
        return privateCapabilities != null ? privateCapabilities : new ArrayList<>();
    }
    
    public void setPrivateCapabilities(List<SceneTemplate.PrivateCapabilityConfig> privateCapabilities) {
        this.privateCapabilities = privateCapabilities;
    }
    
    public List<String> getEnabledPrivateCapabilities() {
        return enabledPrivateCapabilities != null ? enabledPrivateCapabilities : new ArrayList<>();
    }
    
    public void setEnabledPrivateCapabilities(List<String> enabledPrivateCapabilities) {
        this.enabledPrivateCapabilities = enabledPrivateCapabilities;
    }
    
    public String getLeaderId() {
        return leaderId;
    }
    
    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }
    
    public List<String> getCollaboratorIds() {
        return collaboratorIds != null ? collaboratorIds : new ArrayList<>();
    }
    
    public void setCollaboratorIds(List<String> collaboratorIds) {
        this.collaboratorIds = collaboratorIds;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public ProcessStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
    
    public List<StepExecution> getSteps() {
        return steps;
    }
    
    public void setSteps(List<StepExecution> steps) {
        this.steps = steps;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }
    
    public long getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "ActivationProcess{" +
                "processId='" + processId + '\'' +
                ", templateId='" + templateId + '\'' +
                ", userId='" + userId + '\'' +
                ", roleId='" + roleId + '\'' +
                ", status=" + status +
                ", steps=" + steps.size() +
                '}';
    }
    
    /**
     * 流程状态
     */
    public enum ProcessStatus {
        CREATED,      // 已创建
        EXECUTING,    // 执行中
        PAUSED,       // 已暂停
        COMPLETED,    // 已完成
        FAILED,       // 失败
        CANCELLED     // 已取消
    }
    
    /**
     * 步骤执行记录
     */
    public static class StepExecution implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String stepId;           // 步骤ID
        private String stepName;         // 步骤名称
        private StepStatus status;       // 步骤状态
        private long startedAt;          // 开始时间
        private long completedAt;        // 完成时间
        private String result;           // 执行结果
        private String errorMessage;     // 错误信息
        
        public StepExecution() {
            this.status = StepStatus.PENDING;
        }
        
        public StepExecution(String stepId, String stepName) {
            this();
            this.stepId = stepId;
            this.stepName = stepName;
        }
        
        /**
         * 开始执行
         */
        public void start() {
            this.status = StepStatus.EXECUTING;
            this.startedAt = System.currentTimeMillis();
        }
        
        /**
         * 完成执行
         */
        public void complete(String result) {
            this.status = StepStatus.COMPLETED;
            this.result = result;
            this.completedAt = System.currentTimeMillis();
        }
        
        /**
         * 跳过
         */
        public void skip() {
            this.status = StepStatus.SKIPPED;
            this.completedAt = System.currentTimeMillis();
        }
        
        /**
         * 失败
         */
        public void fail(String errorMessage) {
            this.status = StepStatus.FAILED;
            this.errorMessage = errorMessage;
            this.completedAt = System.currentTimeMillis();
        }
        
        // Getters and Setters
        
        public String getStepId() {
            return stepId;
        }
        
        public void setStepId(String stepId) {
            this.stepId = stepId;
        }
        
        public String getStepName() {
            return stepName;
        }
        
        public void setStepName(String stepName) {
            this.stepName = stepName;
        }
        
        public StepStatus getStatus() {
            return status;
        }
        
        public void setStatus(StepStatus status) {
            this.status = status;
        }
        
        public long getStartedAt() {
            return startedAt;
        }
        
        public void setStartedAt(long startedAt) {
            this.startedAt = startedAt;
        }
        
        public long getCompletedAt() {
            return completedAt;
        }
        
        public void setCompletedAt(long completedAt) {
            this.completedAt = completedAt;
        }
        
        public String getResult() {
            return result;
        }
        
        public void setResult(String result) {
            this.result = result;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        @Override
        public String toString() {
            return "StepExecution{" +
                    "stepId='" + stepId + '\'' +
                    ", stepName='" + stepName + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
    
    /**
     * 步骤状态
     */
    public enum StepStatus {
        PENDING,      // 待执行
        EXECUTING,    // 执行中
        COMPLETED,    // 已完成
        SKIPPED,      // 已跳过
        FAILED        // 失败
    }
}
