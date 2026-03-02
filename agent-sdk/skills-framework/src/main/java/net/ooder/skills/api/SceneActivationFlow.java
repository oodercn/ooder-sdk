package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景激活流程接口
 *
 * 实现场景激活时自动创建场景组、建立场景间通信等流程
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SceneActivationFlow {

    /**
     * 激活场景
     *
     * @param sceneId 场景ID
     * @return 激活结果
     */
    CompletableFuture<ActivationResult> activate(String sceneId);

    /**
     * 激活场景（带配置）
     *
     * @param sceneId 场景ID
     * @param config 激活配置
     * @return 激活结果
     */
    CompletableFuture<ActivationResult> activate(String sceneId, ActivationConfig config);

    /**
     * 停用场景
     *
     * @param sceneId 场景ID
     * @return 停用结果
     */
    CompletableFuture<DeactivationResult> deactivate(String sceneId);

    /**
     * 获取场景状态
     *
     * @param sceneId 场景ID
     * @return 场景状态
     */
    CompletableFuture<SceneState> getSceneState(String sceneId);

    /**
     * 检查场景是否已激活
     *
     * @param sceneId 场景ID
     * @return 是否已激活
     */
    CompletableFuture<Boolean> isActive(String sceneId);

    /**
     * 添加激活监听器
     *
     * @param listener 监听器
     */
    void addActivationListener(ActivationListener listener);

    /**
     * 移除激活监听器
     *
     * @param listener 监听器
     */
    void removeActivationListener(ActivationListener listener);

    // ========== 数据类定义 ==========

    /**
     * 激活配置
     */
    class ActivationConfig {
        private boolean autoCreateGroup;  // 自动创建场景组
        private boolean autoBindCapabilities;  // 自动绑定能力
        private boolean autoStartCollaboration;  // 自动启动协作
        private Map<String, Object> initParams;  // 初始化参数
        private long timeout;  // 超时时间

        public ActivationConfig() {
            this.autoCreateGroup = true;
            this.autoBindCapabilities = true;
            this.autoStartCollaboration = true;
            this.timeout = 60000;  // 默认60秒
        }

        // Getters and Setters
        public boolean isAutoCreateGroup() { return autoCreateGroup; }
        public void setAutoCreateGroup(boolean autoCreateGroup) { this.autoCreateGroup = autoCreateGroup; }
        public boolean isAutoBindCapabilities() { return autoBindCapabilities; }
        public void setAutoBindCapabilities(boolean autoBindCapabilities) { this.autoBindCapabilities = autoBindCapabilities; }
        public boolean isAutoStartCollaboration() { return autoStartCollaboration; }
        public void setAutoStartCollaboration(boolean autoStartCollaboration) { this.autoStartCollaboration = autoStartCollaboration; }
        public Map<String, Object> getInitParams() { return initParams; }
        public void setInitParams(Map<String, Object> initParams) { this.initParams = initParams; }
        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }
    }

    /**
     * 激活结果
     */
    class ActivationResult {
        private boolean success;
        private String sceneId;
        private String message;
        private ActivationStatus status;
        private String groupId;  // 创建的场景组ID
        private List<String> boundCapabilities;  // 绑定的能力列表
        private List<String> collaborativeScenes;  // 协作场景列表
        private long startTime;
        private long endTime;
        private List<ActivationStep> steps;

        public ActivationResult() {
            this.boundCapabilities = new java.util.ArrayList<>();
            this.collaborativeScenes = new java.util.ArrayList<>();
            this.steps = new java.util.ArrayList<>();
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public ActivationStatus getStatus() { return status; }
        public void setStatus(ActivationStatus status) { this.status = status; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public List<String> getBoundCapabilities() { return boundCapabilities; }
        public void setBoundCapabilities(List<String> boundCapabilities) { this.boundCapabilities = boundCapabilities; }
        public List<String> getCollaborativeScenes() { return collaborativeScenes; }
        public void setCollaborativeScenes(List<String> collaborativeScenes) { this.collaborativeScenes = collaborativeScenes; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public List<ActivationStep> getSteps() { return steps; }
        public void setSteps(List<ActivationStep> steps) { this.steps = steps; }

        public void addStep(ActivationStep step) {
            if (this.steps == null) {
                this.steps = new java.util.ArrayList<>();
            }
            this.steps.add(step);
        }

        public static ActivationResult success(String sceneId) {
            ActivationResult result = new ActivationResult();
            result.setSuccess(true);
            result.setSceneId(sceneId);
            result.setStatus(ActivationStatus.ACTIVE);
            result.setMessage("场景激活成功");
            return result;
        }

        public static ActivationResult failure(String sceneId, String message) {
            ActivationResult result = new ActivationResult();
            result.setSuccess(false);
            result.setSceneId(sceneId);
            result.setStatus(ActivationStatus.FAILED);
            result.setMessage(message);
            return result;
        }
    }

    /**
     * 激活步骤
     */
    class ActivationStep {
        private String stepName;
        private StepStatus status;
        private String message;
        private long startTime;
        private long endTime;

        public enum StepStatus {
            PENDING,    // 待执行
            RUNNING,    // 执行中
            COMPLETED,  // 完成
            FAILED,     // 失败
            SKIPPED     // 跳过
        }

        // Getters and Setters
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public StepStatus getStatus() { return status; }
        public void setStatus(StepStatus status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
    }

    /**
     * 停用结果
     */
    class DeactivationResult {
        private boolean success;
        private String sceneId;
        private String message;
        private long timestamp;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 场景状态
     */
    enum SceneState {
        INACTIVE,     // 未激活
        ACTIVATING,   // 激活中
        ACTIVE,       // 已激活
        DEACTIVATING, // 停用中
        ERROR         // 错误
    }

    /**
     * 激活状态
     */
    enum ActivationStatus {
        PENDING,      // 待激活
        CHECKING,     // 检查中
        STARTING,     // 启动中
        BINDING,      // 绑定中
        CREATING_GROUP, // 创建场景组中
        COLLABORATING, // 建立协作中
        ACTIVE,       // 已激活
        FAILED        // 失败
    }

    /**
     * 激活监听器
     */
    interface ActivationListener {
        void onActivationStarted(String sceneId);
        void onActivationStep(String sceneId, String stepName, ActivationStep.StepStatus status);
        void onActivationCompleted(String sceneId, ActivationResult result);
        void onActivationFailed(String sceneId, String error);
        void onDeactivationCompleted(String sceneId, DeactivationResult result);
    }
}
