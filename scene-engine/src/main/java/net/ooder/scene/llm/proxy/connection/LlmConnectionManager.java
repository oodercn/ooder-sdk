package net.ooder.scene.llm.proxy.connection;

import net.ooder.scene.llm.config.SceneLlmConfig;
import net.ooder.scene.llm.proxy.LlmConfigAdapter;
import net.ooder.sdk.drivers.llm.LlmDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM连接管理器
 * 管理所有LLM连接池，支持配置相同的Agent共享连接池
 *
 * 设计参考：JDSServer SessionCacheManager
 */
public class LlmConnectionManager {

    private static final Logger log = LoggerFactory.getLogger(LlmConnectionManager.class);

    // 连接池缓存：poolKey -> pool
    private final Map<LlmConnectionPoolKey, LlmConnectionPool> pools;

    public LlmConnectionManager() {
        this.pools = new ConcurrentHashMap<>();
    }

    /**
     * 获取或创建连接池
     * 关键：相同配置的LLM共享连接池
     *
     * @param config LLM配置
     * @return 连接池
     */
    public LlmConnectionPool getOrCreatePool(SceneLlmConfig config) {
        LlmConnectionPoolKey key = LlmConnectionPoolKey.fromConfig(config);

        return pools.computeIfAbsent(key, k -> {
            log.info("Creating new connection pool for config: endpoint={}, model={}",
                    config.getEndpoint(), config.getModel());

            LlmDriver driver = createDriver(config);
            String poolId = key.toString();
            return new LlmConnectionPool(poolId, config, driver);
        });
    }

    /**
     * 获取已存在的连接池
     */
    public LlmConnectionPool getPool(LlmConnectionPoolKey key) {
        return pools.get(key);
    }

    /**
     * 根据配置获取连接池
     */
    public LlmConnectionPool getPool(SceneLlmConfig config) {
        LlmConnectionPoolKey key = LlmConnectionPoolKey.fromConfig(config);
        return pools.get(key);
    }

    /**
     * 移除连接池
     */
    public void removePool(LlmConnectionPoolKey key) {
        LlmConnectionPool pool = pools.remove(key);
        if (pool != null) {
            pool.shutdown();
            log.info("Connection pool removed: poolId={}", pool.getPoolId());
        }
    }

    /**
     * 创建LLM驱动
     */
    private LlmDriver createDriver(SceneLlmConfig config) {
        try {
            // 从 endpoint 提取 provider
            String endpoint = config.getEndpoint();
            String provider = extractProvider(endpoint);

            // 使用 SceneEngineLlmProxy 作为驱动
            // 实际驱动创建由 SceneEngine 内部处理
            log.info("Creating LLM driver for provider: {}", provider);

            // 返回一个代理驱动，实际调用由 SceneEngine 处理
            return new SceneEngineLlmProxyDriver(config);
        } catch (Exception e) {
            log.error("Failed to create LLM driver: endpoint={}, model={}",
                    config.getEndpoint(), config.getModel(), e);
            throw new RuntimeException("Failed to create LLM driver", e);
        }
    }

    /**
     * 从 endpoint 提取 provider
     */
    private String extractProvider(String endpoint) {
        if (endpoint == null || endpoint.isEmpty()) {
            return "default";
        }
        if (endpoint.contains("openai")) {
            return "openai";
        } else if (endpoint.contains("baidu") || endpoint.contains("wenxin")) {
            return "baidu";
        } else if (endpoint.contains("spark") || endpoint.contains("xfyun")) {
            return "spark";
        } else if (endpoint.contains("qianwen") || endpoint.contains("aliyun")) {
            return "qianwen";
        }
        return "default";
    }

    /**
     * 获取所有连接池统计信息
     */
    public Map<String, LlmConnectionPool.PoolStats> getAllPoolStats() {
        Map<String, LlmConnectionPool.PoolStats> stats = new ConcurrentHashMap<>();
        for (Map.Entry<LlmConnectionPoolKey, LlmConnectionPool> entry : pools.entrySet()) {
            stats.put(entry.getKey().toString(), entry.getValue().getStats());
        }
        return stats;
    }

    /**
     * 关闭所有连接池
     */
    public void shutdown() {
        log.info("Shutting down all connection pools, count={}", pools.size());

        for (LlmConnectionPool pool : pools.values()) {
            try {
                pool.shutdown();
            } catch (Exception e) {
                log.error("Error shutting down pool: poolId={}", pool.getPoolId(), e);
            }
        }
        pools.clear();

        log.info("All connection pools shutdown complete");
    }

    /**
     * 获取连接池数量
     */
    public int getPoolCount() {
        return pools.size();
    }

    /**
     * 检查连接池是否存在
     */
    public boolean containsPool(LlmConnectionPoolKey key) {
        return pools.containsKey(key);
    }

    /**
     * 内部代理驱动类
     */
    private static class SceneEngineLlmProxyDriver implements LlmDriver {
        private final net.ooder.scene.llm.config.SceneLlmConfig sceneConfig;
        private LlmDriver.LlmConfig driverConfig;

        public SceneEngineLlmProxyDriver(net.ooder.scene.llm.config.SceneLlmConfig config) {
            this.sceneConfig = config;
            this.driverConfig = convertToDriverConfig(config);
        }

        private LlmDriver.LlmConfig convertToDriverConfig(net.ooder.scene.llm.config.SceneLlmConfig sceneConfig) {
            return LlmConfigAdapter.fromSceneConfig(sceneConfig).toDriverConfig();
        }

        @Override
        public void init(LlmDriver.LlmConfig config) {
            this.driverConfig = config;
        }

        @Override
        public java.util.concurrent.CompletableFuture<net.ooder.sdk.llm.model.DriverChatResponse> chat(net.ooder.sdk.llm.model.DriverChatRequest request) {
            // 实际调用由 SceneEngine 处理
            return java.util.concurrent.CompletableFuture.completedFuture(new net.ooder.sdk.llm.model.DriverChatResponse());
        }

        @Override
        public java.util.concurrent.CompletableFuture<net.ooder.sdk.llm.model.DriverChatResponse> chatStream(net.ooder.sdk.llm.model.DriverChatRequest request, net.ooder.sdk.drivers.llm.LlmDriver.ChatStreamHandler handler) {
            return java.util.concurrent.CompletableFuture.completedFuture(new net.ooder.sdk.llm.model.DriverChatResponse());
        }

        @Override
        public java.util.concurrent.CompletableFuture<net.ooder.sdk.llm.model.EmbeddingResponse> embed(net.ooder.sdk.llm.model.EmbeddingRequest request) {
            return java.util.concurrent.CompletableFuture.completedFuture(new net.ooder.sdk.llm.model.EmbeddingResponse());
        }

        @Override
        public java.util.concurrent.CompletableFuture<net.ooder.sdk.llm.model.CompletionResponse> complete(net.ooder.sdk.llm.model.CompletionRequest request) {
            return java.util.concurrent.CompletableFuture.completedFuture(new net.ooder.sdk.llm.model.CompletionResponse());
        }

        @Override
        public java.util.concurrent.CompletableFuture<net.ooder.sdk.llm.model.TokenCountResponse> countTokens(String text) {
            return java.util.concurrent.CompletableFuture.completedFuture(new net.ooder.sdk.llm.model.TokenCountResponse());
        }

        @Override
        public java.util.concurrent.CompletableFuture<java.util.List<String>> listModels() {
            return java.util.concurrent.CompletableFuture.completedFuture(java.util.Collections.emptyList());
        }

        @Override
        public java.util.concurrent.CompletableFuture<net.ooder.sdk.llm.model.ModelInfo> getModelInfo(String modelId) {
            return java.util.concurrent.CompletableFuture.completedFuture(new net.ooder.sdk.llm.model.ModelInfo());
        }

        @Override
        public boolean supportsStreaming() {
            return true;
        }

        @Override
        public boolean supportsEmbeddings() {
            return true;
        }

        @Override
        public boolean supportsFunctionCalling() {
            return false;
        }

        @Override
        public int getMaxContextLength(String modelId) {
            return 8192;
        }

        @Override
        public void close() {
            // 清理资源
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        @Override
        public String getDriverName() {
            return "scene-engine-proxy";
        }

        @Override
        public String getDriverVersion() {
            return "2.3.1";
        }
    }
}
