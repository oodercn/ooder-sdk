package net.ooder.scene.llm.proxy.connection;

import net.ooder.scene.llm.config.SceneLlmConfig;
import net.ooder.scene.llm.proxy.common.PoolState;
import net.ooder.sdk.drivers.llm.LlmDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LLM连接池
 * 管理物理连接，支持连接复用和引用计数
 */
public class LlmConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(LlmConnectionPool.class);

    private final String poolId;
    private final SceneLlmConfig llmConfig;
    private final LlmDriver driver;
    private final int maxConnections;
    private final long connectionTimeoutMs;
    private final long idleTimeoutMs;

    // 可用连接队列
    private final BlockingQueue<LlmConnection> availableConnections;
    // 活跃连接数
    private final AtomicInteger activeConnections;
    // 引用计数（有多少Agent在使用此池）
    private final AtomicInteger referenceCount;
    // 池状态
    private volatile PoolState state;

    public LlmConnectionPool(String poolId, SceneLlmConfig llmConfig, LlmDriver driver) {
        this(poolId, llmConfig, driver, 10, 30000L, 300000L);
    }

    public LlmConnectionPool(String poolId, SceneLlmConfig llmConfig, LlmDriver driver,
                             int maxConnections, long connectionTimeoutMs, long idleTimeoutMs) {
        this.poolId = poolId;
        this.llmConfig = llmConfig;
        this.driver = driver;
        this.maxConnections = maxConnections;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.idleTimeoutMs = idleTimeoutMs;

        this.availableConnections = new LinkedBlockingQueue<>();
        this.activeConnections = new AtomicInteger(0);
        this.referenceCount = new AtomicInteger(0);
        this.state = PoolState.ACTIVE;

        log.info("LlmConnectionPool created: poolId={}, maxConnections={}", poolId, maxConnections);
    }

    /**
     * 获取连接
     */
    public LlmConnection acquireConnection() throws InterruptedException {
        checkState();

        // 1. 尝试从队列获取可用连接
        LlmConnection connection = availableConnections.poll();
        if (connection != null && connection.acquire()) {
            log.debug("Acquired connection from pool: poolId={}, connectionId={}", poolId, connection.getConnectionId());
            return connection;
        }

        // 2. 如果没有可用连接且未达到上限，创建新连接
        if (activeConnections.get() < maxConnections) {
            connection = createConnection();
            if (connection != null && connection.acquire()) {
                activeConnections.incrementAndGet();
                log.debug("Created and acquired new connection: poolId={}, connectionId={}", poolId, connection.getConnectionId());
                return connection;
            }
        }

        // 3. 等待可用连接
        connection = availableConnections.poll(connectionTimeoutMs, TimeUnit.MILLISECONDS);
        if (connection != null && connection.acquire()) {
            log.debug("Acquired connection after waiting: poolId={}, connectionId={}", poolId, connection.getConnectionId());
            return connection;
        }

        throw new RuntimeException("Failed to acquire connection from pool: " + poolId);
    }

    /**
     * 释放连接
     */
    public void releaseConnection(LlmConnection connection) {
        if (connection == null || state == PoolState.CLOSED) {
            return;
        }

        // 检查连接是否有效
        if (connection.isIdleTimeout(idleTimeoutMs)) {
            log.debug("Connection idle timeout, closing: connectionId={}", connection.getConnectionId());
            closeConnection(connection);
            return;
        }

        // 归还到队列
        if (availableConnections.offer(connection)) {
            log.debug("Connection released back to pool: poolId={}, connectionId={}", poolId, connection.getConnectionId());
        } else {
            // 队列已满，关闭连接
            closeConnection(connection);
        }
    }

    /**
     * 创建新连接
     */
    private LlmConnection createConnection() {
        try {
            String connectionId = UUID.randomUUID().toString().replace("-", "");
            return new LlmConnection(connectionId, driver, this);
        } catch (Exception e) {
            log.error("Failed to create connection: poolId={}", poolId, e);
            return null;
        }
    }

    /**
     * 关闭连接
     */
    private void closeConnection(LlmConnection connection) {
        try {
            connection.close();
            activeConnections.decrementAndGet();
        } catch (Exception e) {
            log.warn("Error closing connection: connectionId={}", connection.getConnectionId(), e);
        }
    }

    /**
     * 检查池状态
     */
    private void checkState() {
        if (state == PoolState.CLOSED) {
            throw new IllegalStateException("Connection pool is closed: " + poolId);
        }
        if (state == PoolState.ERROR) {
            throw new IllegalStateException("Connection pool is in error state: " + poolId);
        }
    }

    /**
     * 增加引用计数
     */
    public void incrementReference() {
        referenceCount.incrementAndGet();
        log.debug("Pool reference incremented: poolId={}, count={}", poolId, referenceCount.get());
    }

    /**
     * 减少引用计数
     */
    public void decrementReference() {
        int count = referenceCount.decrementAndGet();
        log.debug("Pool reference decremented: poolId={}, count={}", poolId, count);
        if (count <= 0) {
            // 无引用时关闭池
            shutdown();
        }
    }

    /**
     * 关闭连接池
     */
    public void shutdown() {
        if (state == PoolState.CLOSED) {
            return;
        }

        state = PoolState.CLOSED;
        log.info("Shutting down connection pool: poolId={}", poolId);

        // 关闭所有可用连接
        LlmConnection connection;
        while ((connection = availableConnections.poll()) != null) {
            closeConnection(connection);
        }

        // 关闭驱动
        if (driver != null) {
            driver.close();
        }

        log.info("Connection pool shutdown complete: poolId={}", poolId);
    }

    /**
     * 获取池统计信息
     */
    public PoolStats getStats() {
        PoolStats stats = new PoolStats();
        stats.setPoolId(poolId);
        stats.setState(state);
        stats.setMaxConnections(maxConnections);
        stats.setActiveConnections(activeConnections.get());
        stats.setAvailableConnections(availableConnections.size());
        stats.setReferenceCount(referenceCount.get());
        return stats;
    }

    public String getPoolId() {
        return poolId;
    }

    public SceneLlmConfig getLlmConfig() {
        return llmConfig;
    }

    public LlmDriver getDriver() {
        return driver;
    }

    public boolean isClosed() {
        return state == PoolState.CLOSED;
    }

    public PoolState getState() {
        return state;
    }

    public int getReferenceCount() {
        return referenceCount.get();
    }

    /**
     * 池统计信息
     */
    public static class PoolStats {
        private String poolId;
        private PoolState state;
        private int maxConnections;
        private int activeConnections;
        private int availableConnections;
        private int referenceCount;

        public String getPoolId() { return poolId; }
        public void setPoolId(String poolId) { this.poolId = poolId; }

        public PoolState getState() { return state; }
        public void setState(PoolState state) { this.state = state; }

        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }

        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }

        public int getAvailableConnections() { return availableConnections; }
        public void setAvailableConnections(int availableConnections) { this.availableConnections = availableConnections; }

        public int getReferenceCount() { return referenceCount; }
        public void setReferenceCount(int referenceCount) { this.referenceCount = referenceCount; }

        @Override
        public String toString() {
            return "PoolStats{" +
                    "poolId='" + poolId + '\'' +
                    ", state=" + state +
                    ", maxConnections=" + maxConnections +
                    ", activeConnections=" + activeConnections +
                    ", availableConnections=" + availableConnections +
                    ", referenceCount=" + referenceCount +
                    '}';
        }
    }
}
