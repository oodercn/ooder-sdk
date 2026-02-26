package net.ooder.sdk.llm.broadcast;

import net.ooder.sdk.llm.model.AgentResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 多智能体广播接口 - llm-sdk 2.3
 * 支持向多个智能体同时发送消息并收集回复
 */
public interface MultiAgentBroadcast {

    /**
     * 发送消息到多个智能体（异步）
     * @param agentIds 目标智能体ID列表
     * @param message 用户消息
     * @return 所有智能体的回复
     */
    CompletableFuture<List<AgentResponse>> broadcast(List<String> agentIds, String message);

    /**
     * 发送消息到多个智能体（流式）
     * @param agentIds 目标智能体ID列表
     * @param message 用户消息
     * @param onResponse 每个智能体回复时的回调
     */
    void broadcastStream(List<String> agentIds, String message, Consumer<AgentResponse> onResponse);

    /**
     * 发送消息到多个智能体（带超时）
     * @param agentIds 目标智能体ID列表
     * @param message 用户消息
     * @param timeoutMs 超时时间（毫秒）
     * @return 所有智能体的回复（超时返回已收到的）
     */
    CompletableFuture<List<AgentResponse>> broadcastWithTimeout(
            List<String> agentIds, String message, long timeoutMs);

    /**
     * 并行发送消息到多个智能体
     * @param agentIds 目标智能体ID列表
     * @param message 用户消息
     * @return 所有智能体的回复
     */
    CompletableFuture<List<AgentResponse>> broadcastParallel(List<String> agentIds, String message);

    /**
     * 顺序发送消息到多个智能体
     * @param agentIds 目标智能体ID列表
     * @param message 用户消息
     * @return 所有智能体的回复
     */
    CompletableFuture<List<AgentResponse>> broadcastSequential(List<String> agentIds, String message);

    /**
     * 获取广播统计信息
     */
    BroadcastStatistics getStatistics();

    /**
     * 广播统计
     */
    class BroadcastStatistics {
        private int totalBroadcasts;
        private int successfulResponses;
        private int failedResponses;
        private long totalResponseTime;
        private double averageResponseTime;

        public int getTotalBroadcasts() {
            return totalBroadcasts;
        }

        public void setTotalBroadcasts(int totalBroadcasts) {
            this.totalBroadcasts = totalBroadcasts;
        }

        public int getSuccessfulResponses() {
            return successfulResponses;
        }

        public void setSuccessfulResponses(int successfulResponses) {
            this.successfulResponses = successfulResponses;
        }

        public int getFailedResponses() {
            return failedResponses;
        }

        public void setFailedResponses(int failedResponses) {
            this.failedResponses = failedResponses;
        }

        public long getTotalResponseTime() {
            return totalResponseTime;
        }

        public void setTotalResponseTime(long totalResponseTime) {
            this.totalResponseTime = totalResponseTime;
        }

        public double getAverageResponseTime() {
            return averageResponseTime;
        }

        public void setAverageResponseTime(double averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }
    }
}
