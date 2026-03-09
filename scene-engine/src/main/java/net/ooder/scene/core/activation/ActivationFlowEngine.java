package net.ooder.scene.core.activation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 激活流程引擎接口
 *
 * <p>管理场景激活的完整流程，包括领导激活和员工激活。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface ActivationFlowEngine {

    /**
     * 启动激活流程
     *
     * @param request 激活请求
     * @return 激活结果
     */
    CompletableFuture<ActivationResult> startActivation(ActivationRequest request);

    /**
     * 获取激活状态
     *
     * @param activationId 激活ID
     * @return 激活状态
     */
    ActivationStatus getActivationStatus(String activationId);

    /**
     * 取消激活
     *
     * @param activationId 激活ID
     * @return 是否成功
     */
    boolean cancelActivation(String activationId);

    /**
     * 暂停激活
     *
     * @param activationId 激活ID
     * @return 是否成功
     */
    boolean pauseActivation(String activationId);

    /**
     * 恢复激活
     *
     * @param activationId 激活ID
     * @return 是否成功
     */
    boolean resumeActivation(String activationId);

    /**
     * 执行激活步骤
     *
     * @param activationId 激活ID
     * @param stepId 步骤ID
     * @param input 输入参数
     * @return 步骤结果
     */
    CompletableFuture<StepResult> executeStep(String activationId, String stepId, Map<String, Object> input);

    /**
     * 跳过步骤
     *
     * @param activationId 激活ID
     * @param stepId 步骤ID
     * @param reason 跳过原因
     * @return 是否成功
     */
    boolean skipStep(String activationId, String stepId, String reason);

    /**
     * 重试步骤
     *
     * @param activationId 激活ID
     * @param stepId 步骤ID
     * @return 步骤结果
     */
    CompletableFuture<StepResult> retryStep(String activationId, String stepId);

    /**
     * 获取激活进度
     *
     * @param activationId 激活ID
     * @return 激活进度
     */
    ActivationProgress getProgress(String activationId);

    /**
     * 获取激活历史
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @return 激活历史列表
     */
    List<ActivationRecord> getActivationHistory(String sceneId, String userId);

    /**
     * 订阅激活事件
     *
     * @param activationId 激活ID
     * @param listener 事件监听器
     * @return 订阅ID
     */
    String subscribeActivationEvent(String activationId, ActivationEventListener listener);

    /**
     * 取消订阅
     *
     * @param subscriptionId 订阅ID
     */
    void unsubscribeActivationEvent(String subscriptionId);

    /**
     * 激活请求
     */
    class ActivationRequest {
        private String sceneId;
        private String userId;
        private ActivationType type;
        private String role;
        private Map<String, Object> config;
        private List<String> selectedSkills;
        private String pushFrom;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public ActivationType getType() { return type; }
        public void setType(ActivationType type) { this.type = type; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public List<String> getSelectedSkills() { return selectedSkills; }
        public void setSelectedSkills(List<String> selectedSkills) { this.selectedSkills = selectedSkills; }
        public String getPushFrom() { return pushFrom; }
        public void setPushFrom(String pushFrom) { this.pushFrom = pushFrom; }
    }

    /**
     * 激活类型
     */
    enum ActivationType {
        LEADER("领导激活"),
        EMPLOYEE("员工激活"),
        AUTO("自动激活");

        private final String description;

        ActivationType(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    /**
     * 激活结果
     */
    class ActivationResult {
        private String activationId;
        private String sceneId;
        private String userId;
        private boolean success;
        private ActivationPhase completedPhase;
        private String errorMessage;
        private long duration;

        public String getActivationId() { return activationId; }
        public void setActivationId(String activationId) { this.activationId = activationId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public ActivationPhase getCompletedPhase() { return completedPhase; }
        public void setCompletedPhase(ActivationPhase completedPhase) { this.completedPhase = completedPhase; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
    }

    /**
     * 激活阶段
     */
    enum ActivationPhase {
        INITIALIZING("初始化"),
        INSTALLING("安装中"),
        CONFIGURING("配置中"),
        ACTIVATING("激活中"),
        COMPLETED("已完成"),
        FAILED("失败");

        private final String description;

        ActivationPhase(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    /**
     * 激活状态
     */
    class ActivationStatus {
        private String activationId;
        private ActivationPhase phase;
        private String currentStep;
        private int totalSteps;
        private int completedSteps;
        private double progress;
        private long startTime;
        private long estimatedEndTime;

        public String getActivationId() { return activationId; }
        public void setActivationId(String activationId) { this.activationId = activationId; }
        public ActivationPhase getPhase() { return phase; }
        public void setPhase(ActivationPhase phase) { this.phase = phase; }
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        public int getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEstimatedEndTime() { return estimatedEndTime; }
        public void setEstimatedEndTime(long estimatedEndTime) { this.estimatedEndTime = estimatedEndTime; }
    }

    /**
     * 步骤结果
     */
    class StepResult {
        private String stepId;
        private String stepName;
        private boolean success;
        private Map<String, Object> output;
        private String errorMessage;

        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Map<String, Object> getOutput() { return output; }
        public void setOutput(Map<String, Object> output) { this.output = output; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 激活进度
     */
    class ActivationProgress {
        private String activationId;
        private List<StepProgress> steps;
        private int currentStepIndex;
        private long startTime;
        private long elapsedTime;

        public String getActivationId() { return activationId; }
        public void setActivationId(String activationId) { this.activationId = activationId; }
        public List<StepProgress> getSteps() { return steps; }
        public void setSteps(List<StepProgress> steps) { this.steps = steps; }
        public int getCurrentStepIndex() { return currentStepIndex; }
        public void setCurrentStepIndex(int currentStepIndex) { this.currentStepIndex = currentStepIndex; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getElapsedTime() { return elapsedTime; }
        public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }

        public static class StepProgress {
            private String stepId;
            private String stepName;
            private StepStatus status;
            private long startTime;
            private long endTime;

            public enum StepStatus {
                PENDING, RUNNING, COMPLETED, FAILED, SKIPPED
            }

            public String getStepId() { return stepId; }
            public void setStepId(String stepId) { this.stepId = stepId; }
            public String getStepName() { return stepName; }
            public void setStepName(String stepName) { this.stepName = stepName; }
            public StepStatus getStatus() { return status; }
            public void setStatus(StepStatus status) { this.status = status; }
            public long getStartTime() { return startTime; }
            public void setStartTime(long startTime) { this.startTime = startTime; }
            public long getEndTime() { return endTime; }
            public void setEndTime(long endTime) { this.endTime = endTime; }
        }
    }

    /**
     * 激活记录
     */
    class ActivationRecord {
        private String activationId;
        private String sceneId;
        private String userId;
        private ActivationType type;
        private boolean success;
        private long activationTime;
        private long duration;

        public String getActivationId() { return activationId; }
        public void setActivationId(String activationId) { this.activationId = activationId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public ActivationType getType() { return type; }
        public void setType(ActivationType type) { this.type = type; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public long getActivationTime() { return activationTime; }
        public void setActivationTime(long activationTime) { this.activationTime = activationTime; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
    }

    /**
     * 激活事件监听器
     */
    interface ActivationEventListener {
        void onActivationEvent(ActivationEvent event);
    }

    /**
     * 激活事件
     */
    class ActivationEvent {
        private String activationId;
        private String sceneId;
        private ActivationEventType eventType;
        private String stepId;
        private String message;
        private long timestamp;

        public enum ActivationEventType {
            STARTED, PHASE_CHANGED, STEP_STARTED, STEP_COMPLETED, STEP_FAILED, COMPLETED, FAILED, CANCELLED
        }

        public String getActivationId() { return activationId; }
        public void setActivationId(String activationId) { this.activationId = activationId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public ActivationEventType getEventType() { return eventType; }
        public void setEventType(ActivationEventType eventType) { this.eventType = eventType; }
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
