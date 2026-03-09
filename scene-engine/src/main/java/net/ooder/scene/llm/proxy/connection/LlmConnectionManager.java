package net.ooder.scene.llm.proxy.connection;

import net.ooder.sdk.service.llm.LlmConfig;
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
    public LlmConnectionPool getOrCreatePool(LlmConfig config) {
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
    public LlmConnectionPool getPool(LlmConfig config) {
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
    private LlmDriver createDriver(LlmConfig config) {
        try {
            // 从 endpoint 提取 provider
            String endpoint = config.getEndpoint();
            String provider = extractProvider(endpoint);
            
            switch (provider.toLowerCase()) {
                case "baidu":
                case "wenxin":
                    return createBaiduWenxinDriver(config);
                case "spark":
                case "xfyun":
                    return createSparkDriver(config);
                case "mock":
                default:
                    return createMockDriver(config);
            }
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
            return "mock";
        }
        if (endpoint.contains("baidu") || endpoint.contains("wenxin")) {
            return "baidu";
        } else if (endpoint.contains("spark") || endpoint.contains("xfyun")) {
            return "spark";
        }
        return "mock";
    }
    
    /**
     * 创建百度文心驱动
     */
    private LlmDriver createBaiduWenxinDriver(LlmConfig config) {
        // 使用反射或SPI加载驱动实现
        try {
            Class<?> driverClass = Class.forName("net.ooder.sdk.drivers.llm.BaiduWenxinDriver");
            LlmDriver driver = (LlmDriver) driverClass.newInstance();
            driver.init(convertConfig(config));
            return driver;
        } catch (Exception e) {
            log.warn("Failed to load BaiduWenxinDriver, falling back to mock driver", e);
            return createMockDriver(config);
        }
    }
    
    /**
     * 创建讯飞星火驱动
     */
    private LlmDriver createSparkDriver(LlmConfig config) {
        try {
            Class<?> driverClass = Class.forName("net.ooder.sdk.drivers.llm.SparkLlmDriver");
            LlmDriver driver = (LlmDriver) driverClass.newInstance();
            driver.init(convertConfig(config));
            return driver;
        } catch (Exception e) {
            log.warn("Failed to load SparkLlmDriver, falling back to mock driver", e);
            return createMockDriver(config);
        }
    }
    
    /**
     * 创建Mock驱动
     */
    private LlmDriver createMockDriver(LlmConfig config) {
        try {
            Class<?> driverClass = Class.forName("net.ooder.sdk.drivers.llm.MockLlmDriver");
            LlmDriver driver = (LlmDriver) driverClass.newInstance();
            driver.init(convertConfig(config));
            return driver;
        } catch (Exception e) {
            log.error("Failed to load MockLlmDriver", e);
            throw new RuntimeException("Failed to create mock driver", e);
        }
    }
    
    /**
     * 转换配置类型
     */
    private LlmDriver.LlmConfig convertConfig(LlmConfig config) {
        LlmDriver.LlmConfig driverConfig = new LlmDriver.LlmConfig();
        driverConfig.setApiKey(config.getApiKey());
        driverConfig.setBaseUrl(config.getEndpoint());
        driverConfig.setDefaultModel(config.getModel());
        driverConfig.setMaxTokens(config.getMaxTokens());
        driverConfig.setTemperature((float) config.getTemperature());
        driverConfig.setTimeout((int) config.getTimeout());
        return driverConfig;
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
}
