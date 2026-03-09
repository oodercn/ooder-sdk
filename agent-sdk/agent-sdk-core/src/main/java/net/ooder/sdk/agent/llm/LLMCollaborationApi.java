package net.ooder.sdk.agent.llm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM 协作 API
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface LLMCollaborationApi {

    /**
     * 分配 LLM 能力给场景
     * @param sceneId 场景ID
     * @param llmCapabilities LLM能力列表
     * @return 分配结果
     */
    CompletableFuture<LlmAllocationResult> allocateLlmCapabilities(String sceneId, List<LlmCapability> llmCapabilities);

    /**
     * 释放 LLM 能力
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> releaseLlmCapability(String sceneId, String capabilityId);

    /**
     * 创建协作对话
     * @param sceneId 场景ID
     * @param participants 参与者列表
     * @param context 上下文
     * @return 对话会话
     */
    CompletableFuture<CollaborationSession> createCollaborationSession(String sceneId, List<String> participants, Map<String, Object> context);

    /**
     * 发送协作消息
     * @param sessionId 会话ID
     * @param message 消息
     * @return 响应
     */
    CompletableFuture<CollaborationMessage> sendCollaborationMessage(String sessionId, CollaborationMessage message);

    /**
     * 获取会话历史
     * @param sessionId 会话ID
     * @return 消息列表
     */
    CompletableFuture<List<CollaborationMessage>> getSessionHistory(String sessionId);

    /**
     * 关闭协作会话
     * @param sessionId 会话ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> closeCollaborationSession(String sessionId);

    /**
     * 获取 LLM 能力状态
     * @param sceneId 场景ID
     * @return 状态列表
     */
    CompletableFuture<List<LlmCapabilityStatus>> getLlmCapabilityStatus(String sceneId);

    /**
     * LLM 能力定义
     */
    class LlmCapability {
        private String id;
        private String provider;
        private String model;
        private Map<String, Object> config;
        private List<String> features;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public List<String> getFeatures() { return features; }
        public void setFeatures(List<String> features) { this.features = features; }
    }

    /**
     * LLM 分配结果
     */
    class LlmAllocationResult {
        private boolean success;
        private List<String> allocatedCapabilityIds;
        private List<String> failedCapabilities;
        private String error;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<String> getAllocatedCapabilityIds() { return allocatedCapabilityIds; }
        public void setAllocatedCapabilityIds(List<String> allocatedCapabilityIds) { this.allocatedCapabilityIds = allocatedCapabilityIds; }
        public List<String> getFailedCapabilities() { return failedCapabilities; }
        public void setFailedCapabilities(List<String> failedCapabilities) { this.failedCapabilities = failedCapabilities; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    /**
     * 协作会话
     */
    class CollaborationSession {
        private String sessionId;
        private String sceneId;
        private List<String> participants;
        private String status;
        private long createdAt;

        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public List<String> getParticipants() { return participants; }
        public void setParticipants(List<String> participants) { this.participants = participants; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }

    /**
     * 协作消息
     */
    class CollaborationMessage {
        private String messageId;
        private String senderId;
        private String content;
        private String type;
        private long timestamp;
        private Map<String, Object> metadata;

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * LLM 能力状态
     */
    class LlmCapabilityStatus {
        private String capabilityId;
        private String state;
        private int activeSessions;
        private long tokenUsage;
        private Map<String, Object> metrics;

        // Getters and Setters
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public int getActiveSessions() { return activeSessions; }
        public void setActiveSessions(int activeSessions) { this.activeSessions = activeSessions; }
        public long getTokenUsage() { return tokenUsage; }
        public void setTokenUsage(long tokenUsage) { this.tokenUsage = tokenUsage; }
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }
}
