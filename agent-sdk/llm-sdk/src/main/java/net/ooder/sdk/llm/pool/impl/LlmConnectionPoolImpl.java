package net.ooder.sdk.llm.pool.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.model.ModelInfo;
import net.ooder.sdk.llm.pool.LlmConnectionPool;
import net.ooder.sdk.llm.pool.LlmPoolManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LLM 连接池实现
 * 
 * FIXME: 当前为简化实现，仅提供基本功能。
 * 实际生产环境建议使用 Apache Commons Pool 或 HikariCP 等成熟连接池方案。
 */
@Slf4j
public class LlmConnectionPoolImpl implements LlmConnectionPool {

    private final Map<String, ModelPool> pools = new ConcurrentHashMap<>();
    private final LlmPoolManager poolManager;

    // 默认连接池配置
    private static final int DEFAULT_MIN_CONNECTIONS = 1;
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private static final long CONNECTION_TIMEOUT_MS = 30000; // 30秒

    public LlmConnectionPoolImpl(LlmPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    @Override
    public LlmConnection getConnection(String modelId) {
        ModelPool pool = pools.get(modelId);
        if (pool == null) {
            // 自动创建连接池
            pool = createPool(modelId);
        }
        return pool.borrowConnection();
    }

    @Override
    public void releaseConnection(LlmConnection connection) {
        if (connection == null) {
            return;
        }
        String modelId = connection.getModelInfo().getModelId();
        ModelPool pool = pools.get(modelId);
        if (pool != null) {
            pool.returnConnection(connection);
        }
    }

    @Override
    public PoolStatus getPoolStatus(String modelId) {
        ModelPool pool = pools.get(modelId);
        if (pool == null) {
            return null;
        }
        return pool.getStatus();
    }

    @Override
    public void warmup(String modelId, int minConnections) {
        ModelPool pool = pools.computeIfAbsent(modelId, this::createPool);
        pool.warmup(minConnections);
        log.info("Pool warmed up for model: {} with {} connections", modelId, minConnections);
    }

    @Override
    public void closePool(String modelId) {
        ModelPool pool = pools.remove(modelId);
        if (pool != null) {
            pool.close();
            log.info("Pool closed for model: {}", modelId);
        }
    }

    @Override
    public void closeAll() {
        for (Map.Entry<String, ModelPool> entry : pools.entrySet()) {
            entry.getValue().close();
            log.info("Pool closed for model: {}", entry.getKey());
        }
        pools.clear();
    }

    @Override
    public List<PoolStatus> getAllPoolStatus() {
        List<PoolStatus> statuses = new ArrayList<>();
        for (ModelPool pool : pools.values()) {
            statuses.add(pool.getStatus());
        }
        return statuses;
    }

    /**
     * 创建连接池
     */
    private ModelPool createPool(String modelId) {
        ModelInfo modelInfo = poolManager.getModel(modelId);
        if (modelInfo == null) {
            throw new IllegalArgumentException("Model not found: " + modelId);
        }
        return new ModelPool(modelInfo);
    }

    /**
     * 模型连接池
     */
    private class ModelPool {
        private final ModelInfo modelInfo;
        private final BlockingQueue<LlmConnection> idleConnections;
        private final Set<LlmConnection> activeConnections;
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong failedRequests = new AtomicLong(0);
        private volatile boolean closed = false;

        ModelPool(ModelInfo modelInfo) {
            this.modelInfo = modelInfo;
            this.idleConnections = new LinkedBlockingQueue<>(DEFAULT_MAX_CONNECTIONS);
            this.activeConnections = ConcurrentHashMap.newKeySet();
        }

        /**
         * 借用连接
         */
        LlmConnection borrowConnection() {
            if (closed) {
                throw new IllegalStateException("Pool is closed for model: " + modelInfo.getModelId());
            }

            totalRequests.incrementAndGet();

            // 尝试从空闲队列获取
            LlmConnection connection = idleConnections.poll();
            if (connection != null && connection.isValid()) {
                activeConnections.add(connection);
                connection.markUsed();
                return connection;
            }

            // 创建新连接
            if (activeConnections.size() < DEFAULT_MAX_CONNECTIONS) {
                connection = createNewConnection();
                activeConnections.add(connection);
                return connection;
            }

            // 等待可用连接
            try {
                connection = idleConnections.poll(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (connection != null && connection.isValid()) {
                    activeConnections.add(connection);
                    connection.markUsed();
                    return connection;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            failedRequests.incrementAndGet();
            throw new RuntimeException("Unable to get connection from pool for model: " + modelInfo.getModelId());
        }

        /**
         * 归还连接
         */
        void returnConnection(LlmConnection connection) {
            activeConnections.remove(connection);
            if (connection.isValid() && !closed) {
                idleConnections.offer(connection);
            } else {
                connection.close();
            }
        }

        /**
         * 预热连接池
         */
        void warmup(int minConnections) {
            for (int i = 0; i < minConnections && i < DEFAULT_MAX_CONNECTIONS; i++) {
                LlmConnection connection = createNewConnection();
                idleConnections.offer(connection);
            }
        }

        /**
         * 获取状态
         */
        PoolStatus getStatus() {
            PoolStatus status = new PoolStatus();
            status.setModelId(modelInfo.getModelId());
            status.setActiveConnections(activeConnections.size());
            status.setIdleConnections(idleConnections.size());
            status.setMaxConnections(DEFAULT_MAX_CONNECTIONS);
            status.setMinConnections(DEFAULT_MIN_CONNECTIONS);
            status.setTotalRequests(totalRequests.get());
            status.setFailedRequests(failedRequests.get());
            status.setHealthy(!closed && activeConnections.size() < DEFAULT_MAX_CONNECTIONS);
            return status;
        }

        /**
         * 关闭连接池
         */
        void close() {
            closed = true;
            for (LlmConnection connection : activeConnections) {
                connection.close();
            }
            activeConnections.clear();
            for (LlmConnection connection : idleConnections) {
                connection.close();
            }
            idleConnections.clear();
        }

        /**
         * 创建新连接
         */
        private LlmConnection createNewConnection() {
            return new LlmConnectionImpl(modelInfo);
        }
    }

    /**
     * LLM 连接实现
     */
    private static class LlmConnectionImpl implements LlmConnection {
        private final ModelInfo modelInfo;
        private final long createdAt;
        private volatile long lastUsedAt;
        private volatile boolean valid = true;

        LlmConnectionImpl(ModelInfo modelInfo) {
            this.modelInfo = modelInfo;
            this.createdAt = System.currentTimeMillis();
            this.lastUsedAt = createdAt;
        }

        @Override
        public ModelInfo getModelInfo() {
            return modelInfo;
        }

        @Override
        public boolean isValid() {
            return valid && (System.currentTimeMillis() - lastUsedAt) < 300000; // 5分钟超时
        }

        @Override
        public long getCreatedAt() {
            return createdAt;
        }

        @Override
        public long getLastUsedAt() {
            return lastUsedAt;
        }

        @Override
        public void markUsed() {
            this.lastUsedAt = System.currentTimeMillis();
        }

        @Override
        public void close() {
            valid = false;
        }
    }
}
