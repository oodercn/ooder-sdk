package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景间通信接口
 *
 * 实现场景组内的消息传递机制
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SceneCommunication {

    /**
     * 发送消息到指定场景
     *
     * @param fromScene 发送方场景ID
     * @param toScene 接收方场景ID
     * @param message 消息
     * @return 发送结果
     */
    CompletableFuture<MessageResult> sendMessage(String fromScene, String toScene, SceneMessage message);

    /**
     * 广播消息到场景组
     *
     * @param groupId 场景组ID
     * @param message 消息
     * @return 广播结果
     */
    CompletableFuture<BroadcastResult> broadcast(String groupId, SceneMessage message);

    /**
     * 广播消息到场景组（指定发送方）
     *
     * @param fromScene 发送方场景ID
     * @param groupId 场景组ID
     * @param message 消息
     * @return 广播结果
     */
    CompletableFuture<BroadcastResult> broadcast(String fromScene, String groupId, SceneMessage message);

    /**
     * 注册消息处理器
     *
     * @param sceneId 场景ID
     * @param handler 消息处理器
     */
    void registerHandler(String sceneId, MessageHandler handler);

    /**
     * 注销消息处理器
     *
     * @param sceneId 场景ID
     */
    void unregisterHandler(String sceneId);

    /**
     * 注册组消息处理器
     *
     * @param groupId 场景组ID
     * @param handler 消息处理器
     */
    void registerGroupHandler(String groupId, MessageHandler handler);

    /**
     * 注销组消息处理器
     *
     * @param groupId 场景组ID
     */
    void unregisterGroupHandler(String groupId);

    /**
     * 同步状态到场景组
     *
     * @param groupId 场景组ID
     * @param state 状态数据
     * @return 同步结果
     */
    CompletableFuture<SyncResult> syncState(String groupId, Map<String, Object> state);

    /**
     * 获取场景组共享状态
     *
     * @param groupId 场景组ID
     * @return 共享状态
     */
    CompletableFuture<Map<String, Object>> getGroupState(String groupId);

    /**
     * 添加通信监听器
     *
     * @param listener 监听器
     */
    void addCommunicationListener(CommunicationListener listener);

    /**
     * 移除通信监听器
     *
     * @param listener 监听器
     */
    void removeCommunicationListener(CommunicationListener listener);

    // ========== 数据类定义 ==========

    /**
     * 场景消息
     */
    class SceneMessage {
        private String messageId;
        private String messageType;
        private String fromScene;
        private String toScene;
        private Map<String, Object> payload;
        private long timestamp;
        private int priority;
        private Map<String, Object> metadata;

        public SceneMessage() {
            this.messageId = generateMessageId();
            this.timestamp = System.currentTimeMillis();
            this.priority = 5;  // 默认优先级
        }

        private String generateMessageId() {
            return "msg-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        public String getFromScene() { return fromScene; }
        public void setFromScene(String fromScene) { this.fromScene = fromScene; }
        public String getToScene() { return toScene; }
        public void setToScene(String toScene) { this.toScene = toScene; }
        public Map<String, Object> getPayload() { return payload; }
        public void setPayload(Map<String, Object> payload) { this.payload = payload; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 消息发送结果
     */
    class MessageResult {
        private boolean success;
        private String messageId;
        private String message;
        private long deliveryTime;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getDeliveryTime() { return deliveryTime; }
        public void setDeliveryTime(long deliveryTime) { this.deliveryTime = deliveryTime; }

        public static MessageResult success(String messageId) {
            MessageResult result = new MessageResult();
            result.setSuccess(true);
            result.setMessageId(messageId);
            result.setMessage("消息发送成功");
            result.setDeliveryTime(System.currentTimeMillis());
            return result;
        }

        public static MessageResult failure(String messageId, String error) {
            MessageResult result = new MessageResult();
            result.setSuccess(false);
            result.setMessageId(messageId);
            result.setMessage(error);
            return result;
        }
    }

    /**
     * 广播结果
     */
    class BroadcastResult {
        private boolean success;
        private String messageId;
        private int totalTargets;
        private int successCount;
        private int failureCount;
        private List<String> failedTargets;
        private String message;

        public BroadcastResult() {
            this.failedTargets = new java.util.ArrayList<>();
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public int getTotalTargets() { return totalTargets; }
        public void setTotalTargets(int totalTargets) { this.totalTargets = totalTargets; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public List<String> getFailedTargets() { return failedTargets; }
        public void setFailedTargets(List<String> failedTargets) { this.failedTargets = failedTargets; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public void addFailedTarget(String target) {
            if (this.failedTargets == null) {
                this.failedTargets = new java.util.ArrayList<>();
            }
            this.failedTargets.add(target);
        }
    }

    /**
     * 状态同步结果
     */
    class SyncResult {
        private boolean success;
        private String groupId;
        private int syncedScenes;
        private String message;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public int getSyncedScenes() { return syncedScenes; }
        public void setSyncedScenes(int syncedScenes) { this.syncedScenes = syncedScenes; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * 消息处理器
     */
    interface MessageHandler {
        /**
         * 处理消息
         *
         * @param message 消息
         * @return 处理结果
         */
        CompletableFuture<HandleResult> handle(SceneMessage message);
    }

    /**
     * 处理结果
     */
    class HandleResult {
        private boolean success;
        private String message;
        private Map<String, Object> response;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getResponse() { return response; }
        public void setResponse(Map<String, Object> response) { this.response = response; }

        public static HandleResult success() {
            HandleResult result = new HandleResult();
            result.setSuccess(true);
            result.setMessage("处理成功");
            return result;
        }

        public static HandleResult success(Map<String, Object> response) {
            HandleResult result = new HandleResult();
            result.setSuccess(true);
            result.setMessage("处理成功");
            result.setResponse(response);
            return result;
        }

        public static HandleResult failure(String error) {
            HandleResult result = new HandleResult();
            result.setSuccess(false);
            result.setMessage(error);
            return result;
        }
    }

    /**
     * 通信监听器
     */
    interface CommunicationListener {
        void onMessageSent(String fromScene, String toScene, SceneMessage message);
        void onMessageReceived(String sceneId, SceneMessage message);
        void onBroadcastSent(String groupId, SceneMessage message);
        void onStateSynced(String groupId, Map<String, Object> state);
    }
}
