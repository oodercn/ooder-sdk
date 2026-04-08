package net.ooder.scene.group.metrics;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 场景组监控指标
 *
 * <p>用于收集和统计场景组的运行指标，包括参与者数量、消息数量、
 * LLM 调用次数、知识库检索次数等。</p>
 *
 * <h3>指标分类：</h3>
 * <ul>
 *   <li><b>参与者指标</b>: 当前参与者数、累计加入/离开数</li>
 *   <li><b>消息指标</b>: 消息总数、LLM 调用次数</li>
 *   <li><b>知识库指标</b>: 检索次数、平均响应时间</li>
 *   <li><b>性能指标</b>: 平均响应时间、错误率</li>
 * </ul>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneGroupMetrics {

    private final String sceneGroupId;

    // 时间戳
    private final AtomicReference<Instant> lastUpdated = new AtomicReference<>(Instant.now());

    // 参与者指标
    private final AtomicLong currentParticipants = new AtomicLong(0);
    private final AtomicLong totalParticipantsJoined = new AtomicLong(0);
    private final AtomicLong totalParticipantsLeft = new AtomicLong(0);

    // 消息指标
    private final AtomicLong totalMessages = new AtomicLong(0);
    private final AtomicLong totalLlmCalls = new AtomicLong(0);
    private final AtomicLong totalLlmTokens = new AtomicLong(0);

    // 知识库指标
    private final AtomicLong totalKnowledgeQueries = new AtomicLong(0);
    private final AtomicLong totalKnowledgeResults = new AtomicLong(0);
    private final AtomicLong knowledgeQueryTimeMs = new AtomicLong(0);

    // 性能指标
    private final AtomicLong totalResponseTimeMs = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);

    public SceneGroupMetrics(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    // ===== 参与者指标 =====

    /**
     * 记录参与者加入
     */
    public void recordParticipantJoined() {
        currentParticipants.incrementAndGet();
        totalParticipantsJoined.incrementAndGet();
        updateTimestamp();
    }

    /**
     * 记录参与者离开
     */
    public void recordParticipantLeft() {
        currentParticipants.decrementAndGet();
        totalParticipantsLeft.incrementAndGet();
        updateTimestamp();
    }

    /**
     * 获取当前参与者数
     */
    public long getCurrentParticipants() {
        return currentParticipants.get();
    }

    /**
     * 获取累计加入参与者数
     */
    public long getTotalParticipantsJoined() {
        return totalParticipantsJoined.get();
    }

    /**
     * 获取累计离开参与者数
     */
    public long getTotalParticipantsLeft() {
        return totalParticipantsLeft.get();
    }

    // ===== 消息指标 =====

    /**
     * 记录消息发送
     */
    public void recordMessage() {
        totalMessages.incrementAndGet();
        updateTimestamp();
    }

    /**
     * 记录 LLM 调用
     *
     * @param tokens 使用的令牌数
     */
    public void recordLlmCall(long tokens) {
        totalLlmCalls.incrementAndGet();
        totalLlmTokens.addAndGet(tokens);
        updateTimestamp();
    }

    /**
     * 获取消息总数
     */
    public long getTotalMessages() {
        return totalMessages.get();
    }

    /**
     * 获取 LLM 调用次数
     */
    public long getTotalLlmCalls() {
        return totalLlmCalls.get();
    }

    /**
     * 获取 LLM 令牌总数
     */
    public long getTotalLlmTokens() {
        return totalLlmTokens.get();
    }

    /**
     * 获取平均每次 LLM 调用的令牌数
     */
    public double getAverageLlmTokensPerCall() {
        long calls = totalLlmCalls.get();
        return calls > 0 ? (double) totalLlmTokens.get() / calls : 0;
    }

    // ===== 知识库指标 =====

    /**
     * 记录知识库查询
     *
     * @param resultCount 返回结果数
     * @param responseTimeMs 响应时间（毫秒）
     */
    public void recordKnowledgeQuery(int resultCount, long responseTimeMs) {
        totalKnowledgeQueries.incrementAndGet();
        totalKnowledgeResults.addAndGet(resultCount);
        knowledgeQueryTimeMs.addAndGet(responseTimeMs);
        updateTimestamp();
    }

    /**
     * 获取知识库查询次数
     */
    public long getTotalKnowledgeQueries() {
        return totalKnowledgeQueries.get();
    }

    /**
     * 获取知识库返回结果总数
     */
    public long getTotalKnowledgeResults() {
        return totalKnowledgeResults.get();
    }

    /**
     * 获取平均每次查询返回的结果数
     */
    public double getAverageKnowledgeResultsPerQuery() {
        long queries = totalKnowledgeQueries.get();
        return queries > 0 ? (double) totalKnowledgeResults.get() / queries : 0;
    }

    /**
     * 获取知识库查询平均响应时间（毫秒）
     */
    public double getAverageKnowledgeQueryTimeMs() {
        long queries = totalKnowledgeQueries.get();
        return queries > 0 ? (double) knowledgeQueryTimeMs.get() / queries : 0;
    }

    // ===== 性能指标 =====

    /**
     * 记录响应时间
     *
     * @param responseTimeMs 响应时间（毫秒）
     */
    public void recordResponseTime(long responseTimeMs) {
        totalResponseTimeMs.addAndGet(responseTimeMs);
        updateTimestamp();
    }

    /**
     * 记录错误
     */
    public void recordError() {
        totalErrors.incrementAndGet();
        updateTimestamp();
    }

    /**
     * 获取平均响应时间（毫秒）
     */
    public double getAverageResponseTimeMs() {
        long messages = totalMessages.get();
        return messages > 0 ? (double) totalResponseTimeMs.get() / messages : 0;
    }

    /**
     * 获取错误总数
     */
    public long getTotalErrors() {
        return totalErrors.get();
    }

    /**
     * 获取错误率
     */
    public double getErrorRate() {
        long messages = totalMessages.get();
        return messages > 0 ? (double) totalErrors.get() / messages : 0;
    }

    // ===== 通用方法 =====

    /**
     * 获取场景组ID
     */
    public String getSceneGroupId() {
        return sceneGroupId;
    }

    /**
     * 获取最后更新时间
     */
    public Instant getLastUpdated() {
        return lastUpdated.get();
    }

    private void updateTimestamp() {
        lastUpdated.set(Instant.now());
    }

    /**
     * 重置所有指标
     */
    public void reset() {
        currentParticipants.set(0);
        totalParticipantsJoined.set(0);
        totalParticipantsLeft.set(0);
        totalMessages.set(0);
        totalLlmCalls.set(0);
        totalLlmTokens.set(0);
        totalKnowledgeQueries.set(0);
        totalKnowledgeResults.set(0);
        knowledgeQueryTimeMs.set(0);
        totalResponseTimeMs.set(0);
        totalErrors.set(0);
        updateTimestamp();
    }

    @Override
    public String toString() {
        return "SceneGroupMetrics{" +
            "sceneGroupId='" + sceneGroupId + '\'' +
            ", currentParticipants=" + getCurrentParticipants() +
            ", totalMessages=" + getTotalMessages() +
            ", totalLlmCalls=" + getTotalLlmCalls() +
            ", totalKnowledgeQueries=" + getTotalKnowledgeQueries() +
            ", avgResponseTimeMs=" + String.format("%.2f", getAverageResponseTimeMs()) +
            ", errorRate=" + String.format("%.2f%%", getErrorRate() * 100) +
            ", lastUpdated=" + getLastUpdated() +
            '}';
    }
}
