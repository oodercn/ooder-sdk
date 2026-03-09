package net.ooder.sdk.llm.pool;

import net.ooder.sdk.llm.model.ModelInfo;

import java.util.List;

/**
 * LLM 连接池接口
 * 由 LLM-SDK 实现，Engine 调用
 */
public interface LlmConnectionPool {

    /**
     * 获取连接
     *
     * @param modelId 模型ID
     * @return 连接对象
     */
    LlmConnection getConnection(String modelId);

    /**
     * 释放连接
     *
     * @param connection 连接对象
     */
    void releaseConnection(LlmConnection connection);

    /**
     * 获取连接池状态
     *
     * @param modelId 模型ID
     * @return 连接池状态
     */
    PoolStatus getPoolStatus(String modelId);

    /**
     * 预热连接池
     *
     * @param modelId 模型ID
     * @param minConnections 最小连接数
     */
    void warmup(String modelId, int minConnections);

    /**
     * 关闭连接池
     *
     * @param modelId 模型ID
     */
    void closePool(String modelId);

    /**
     * 关闭所有连接池
     */
    void closeAll();

    /**
     * 获取所有连接池状态
     *
     * @return 连接池状态列表
     */
    List<PoolStatus> getAllPoolStatus();

    /**
     * LLM 连接接口
     */
    interface LlmConnection {
        /**
         * 获取模型信息
         */
        ModelInfo getModelInfo();

        /**
         * 检查连接是否有效
         */
        boolean isValid();

        /**
         * 获取创建时间
         */
        long getCreatedAt();

        /**
         * 获取最后使用时间
         */
        long getLastUsedAt();

        /**
         * 标记为已使用
         */
        void markUsed();

        /**
         * 关闭连接
         */
        void close();
    }

    /**
     * 连接池状态
     */
    class PoolStatus {
        private String modelId;
        private int activeConnections;
        private int idleConnections;
        private int maxConnections;
        private int minConnections;
        private long totalRequests;
        private long failedRequests;
        private double averageWaitTime;
        private boolean healthy;

        // Getters and Setters
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }

        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }

        public int getIdleConnections() { return idleConnections; }
        public void setIdleConnections(int idleConnections) { this.idleConnections = idleConnections; }

        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }

        public int getMinConnections() { return minConnections; }
        public void setMinConnections(int minConnections) { this.minConnections = minConnections; }

        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }

        public long getFailedRequests() { return failedRequests; }
        public void setFailedRequests(long failedRequests) { this.failedRequests = failedRequests; }

        public double getAverageWaitTime() { return averageWaitTime; }
        public void setAverageWaitTime(double averageWaitTime) { this.averageWaitTime = averageWaitTime; }

        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
    }
}
