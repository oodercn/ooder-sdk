package net.ooder.sdk.agent.installation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 安装状态同步 API
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface InstallationSyncApi {

    /**
     * 广播安装状态
     * @param sceneId 场景ID
     * @param status 安装状态
     * @return 是否成功
     */
    CompletableFuture<Boolean> broadcastInstallationStatus(String sceneId, InstallationStatus status);

    /**
     * 订阅安装状态
     * @param sceneId 场景ID
     * @param listener 状态监听器
     * @return 订阅ID
     */
    CompletableFuture<String> subscribeInstallationStatus(String sceneId, InstallationStatusListener listener);

    /**
     * 取消订阅
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unsubscribeInstallationStatus(String subscriptionId);

    /**
     * 同步安装状态
     * @param sceneId 场景ID
     * @param targetAgentId 目标Agent ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> syncInstallationStatus(String sceneId, String targetAgentId);

    /**
     * 获取安装状态
     * @param sceneId 场景ID
     * @return 安装状态
     */
    CompletableFuture<InstallationStatus> getInstallationStatus(String sceneId);

    /**
     * 安装状态
     */
    class InstallationStatus {
        private String sceneId;
        private String state;
        private int progress;
        private String currentStep;
        private List<String> completedSteps;
        private List<String> pendingSteps;
        private Map<String, Object> metadata;
        private long timestamp;

        // Getters and Setters
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        public List<String> getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(List<String> completedSteps) { this.completedSteps = completedSteps; }
        public List<String> getPendingSteps() { return pendingSteps; }
        public void setPendingSteps(List<String> pendingSteps) { this.pendingSteps = pendingSteps; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 安装状态监听器
     */
    interface InstallationStatusListener {
        void onStatusChanged(String sceneId, InstallationStatus status);
    }
}
