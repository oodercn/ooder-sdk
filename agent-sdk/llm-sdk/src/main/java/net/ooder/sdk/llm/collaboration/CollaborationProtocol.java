package net.ooder.sdk.llm.collaboration;

import net.ooder.sdk.llm.model.CollaborationMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 协作协议接口 - llm-sdk 2.3
 * 定义智能体间协作的标准协议
 */
public interface CollaborationProtocol {

    /**
     * 发起协作请求
     * @param sessionId 会话ID
     * @param initiatorId 发起者ID
     * @param participants 参与者列表
     * @param task 协作任务
     * @return 协作会话
     */
    CollaborationSession initiateCollaboration(
            String sessionId,
            String initiatorId,
            List<String> participants,
            String task);

    /**
     * 发送协作消息
     * @param sessionId 会话ID
     * @param message 协作消息
     */
    void sendCollaborationMessage(String sessionId, CollaborationMessage message);

    /**
     * 等待协作完成
     * @param sessionId 会话ID
     * @param timeoutMs 超时时间
     * @return 协作结果
     */
    CompletableFuture<CollaborationResult> awaitCompletion(String sessionId, long timeoutMs);

    /**
     * 结束协作会话
     * @param sessionId 会话ID
     */
    void endCollaboration(String sessionId);

    /**
     * 获取协作会话状态
     * @param sessionId 会话ID
     * @return 会话状态
     */
    CollaborationStatus getStatus(String sessionId);

    /**
     * 设置协议处理器
     * @param handler 协议处理器
     */
    void setProtocolHandler(ProtocolHandler handler);

    /**
     * 协作会话
     */
    interface CollaborationSession {
        String getSessionId();
        List<String> getParticipants();
        String getTask();
        long getStartTime();
    }

    /**
     * 协作结果
     */
    class CollaborationResult {
        private String sessionId;
        private boolean success;
        private String summary;
        private List<AgentContribution> contributions;
        private long duration;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<AgentContribution> getContributions() {
            return contributions;
        }

        public void setContributions(List<AgentContribution> contributions) {
            this.contributions = contributions;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    /**
     * 智能体贡献
     */
    class AgentContribution {
        private String agentId;
        private String agentName;
        private String content;
        private long timestamp;

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * 协作状态
     */
    enum CollaborationStatus {
        PENDING,
        ACTIVE,
        PAUSED,
        COMPLETED,
        FAILED,
        TIMEOUT
    }

    /**
     * 协议处理器
     */
    interface ProtocolHandler {
        /**
         * 处理协作消息
         */
        void onMessage(String sessionId, CollaborationMessage message);

        /**
         * 处理协作完成
         */
        void onCompleted(String sessionId, CollaborationResult result);

        /**
         * 处理协作失败
         */
        void onFailed(String sessionId, String reason);
    }
}
