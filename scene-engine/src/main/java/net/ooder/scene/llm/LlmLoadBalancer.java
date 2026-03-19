package net.ooder.scene.llm;

import net.ooder.scene.skill.llm.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LLM 负载均衡器
 * 
 * <p>支持多个 LLM 提供者的负载均衡， * 
 * 
 * <h3>负载均衡策略：</h3>
 * <ul>
 *   <li>轮询（Round Robin）- 按顺序轮流使用</li>
 *   <li>加权轮询（Weighted Round Robin）- 根据权重分配</li>
 *   <li>最少连接（Least Connections）- 选择连接数最少的</li>
 *   <li>响应时间（Response Time）- 选择响应最快的</li>
 *   <li>随机（Random）- 随机选择</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class LlmLoadBalancer {
    
    private static final Logger log = LoggerFactory.getLogger(LlmLoadBalancer.class);
    
    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    private final Map<String, ProviderStats> stats = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    private LoadBalanceStrategy strategy = LoadBalanceStrategy.WEIGHTED_ROUND_ROBIN;
    private int maxRetries = 3;
    private long healthCheckInterval = 60000; // 60秒
    
    public LlmLoadBalancer() {
        // 启动健康检查
        Thread healthCheckThread = new Thread(this::healthCheck, "llm-health-check");
        healthCheckThread.setDaemon(true);
        healthCheckThread.start();
    }
    
    /**
     * 注册 LLM 提供者
     *
     * @param name 提供者名称
     * @param provider LLM 提供者
     * @param weight 权重
     */
    public void registerProvider(String name, LlmProvider provider, int weight) {
        providers.put(name, provider);
        stats.put(name, new ProviderStats(name, weight));
        log.info("Registered LLM provider: {} with weight: {}", name, weight);
    }
    
    /**
     * 注销 LLM 提供者
     *
     * @param name 提供者名称
     */
    public void unregisterProvider(String name) {
        providers.remove(name);
        stats.remove(name);
        log.info("Unregistered LLM provider: {}", name);
    }
    
    /**
     * 选择最佳提供者
     *
     * @return 选中的提供者
     */
    public LlmProvider selectProvider() {
        if (providers.isEmpty()) {
            throw new IllegalStateException("No LLM providers available");
        }
        
        List<String> availableProviders = getAvailableProviders();
        if (availableProviders.isEmpty()) {
            throw new IllegalStateException("No healthy LLM providers available");
        }
        
        String selectedName = selectProviderName(availableProviders);
        LlmProvider selected = providers.get(selectedName);
        
        // 更新统计
        ProviderStats providerStats = stats.get(selectedName);
        if (providerStats != null) {
            providerStats.incrementConnections();
        }
        
        log.debug("Selected LLM provider: {}", selectedName);
        return selected;
    }
    
    /**
     * 选择提供者名称
     */
    private String selectProviderName(List<String> availableProviders) {
        switch (strategy) {
            case ROUND_ROBIN:
                return selectRoundRobin(availableProviders);
            case WEIGHTED_ROUND_ROBIN:
                return selectWeightedRoundRobin(availableProviders);
            case LEAST_CONNECTIONS:
                return selectLeastConnections(availableProviders);
            case RESPONSE_TIME:
                return selectResponseTime(availableProviders);
            case RANDOM:
                return selectRandom(availableProviders);
            default:
                return selectWeightedRoundRobin(availableProviders);
        }
    }
    
    /**
     * 轮询策略
     */
    private String selectRoundRobin(List<String> availableProviders) {
        AtomicInteger counter = new AtomicInteger(0);
        int index = counter.getAndIncrement() % availableProviders.size();
        return availableProviders.get(index);
    }
    
    /**
     * 加权轮询策略
     */
    private String selectWeightedRoundRobin(List<String> availableProviders) {
        int totalWeight = 0;
        for (String name : availableProviders) {
            ProviderStats s = stats.get(name);
            if (s != null) {
                totalWeight += s.getWeight();
            }
        }
        
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (String name : availableProviders) {
            ProviderStats s = stats.get(name);
            if (s != null) {
                currentWeight += s.getWeight();
                if (currentWeight > randomWeight) {
                    return name;
                }
            }
        }
        
        return availableProviders.get(0);
    }
    
    /**
     * 最少连接策略
     */
    private String selectLeastConnections(List<String> availableProviders) {
        String selected = null;
        int minConnections = Integer.MAX_VALUE;
        
        for (String name : availableProviders) {
            ProviderStats s = stats.get(name);
            if (s != null && s.getActiveConnections() < minConnections) {
                minConnections = s.getActiveConnections();
                selected = name;
            }
        }
        
        return selected != null ? selected : availableProviders.get(0);
    }
    
    /**
     * 响应时间策略
     */
    private String selectResponseTime(List<String> availableProviders) {
        String selected = null;
        long minResponseTime = Long.MAX_VALUE;
        
        for (String name : availableProviders) {
            ProviderStats s = stats.get(name);
            if (s != null && s.getAvgResponseTime() < minResponseTime) {
                minResponseTime = s.getAvgResponseTime();
                selected = name;
            }
        }
        
        return selected != null ? selected : availableProviders.get(0);
    }
    
    /**
     * 随机策略
     */
    private String selectRandom(List<String> availableProviders) {
        int index = random.nextInt(availableProviders.size());
        return availableProviders.get(index);
    }
    
    /**
     * 获取可用的提供者列表
     */
    private List<String> getAvailableProviders() {
        List<String> available = new ArrayList<>();
        
        for (Map.Entry<String, ProviderStats> entry : stats.entrySet()) {
            if (entry.getValue().isHealthy()) {
                available.add(entry.getKey());
            }
        }
        
        return available;
    }
    
    /**
     * 记录请求成功
     *
     * @param providerName 提供者名称
     * @param responseTime 响应时间（毫秒）
     */
    public void recordSuccess(String providerName, long responseTime) {
        ProviderStats s = stats.get(providerName);
        if (s != null) {
            s.recordSuccess(responseTime);
        }
    }
    
    /**
     * 记录请求失败
     *
     * @param providerName 提供者名称
     */
    public void recordFailure(String providerName) {
        ProviderStats s = stats.get(providerName);
        if (s != null) {
            s.recordFailure();
        }
    }
    
    /**
     * 健康检查
     */
    private void healthCheck() {
        while (true) {
            try {
                Thread.sleep(healthCheckInterval);
                
                for (Map.Entry<String, LlmProvider> entry : providers.entrySet()) {
                    String name = entry.getKey();
                    LlmProvider provider = entry.getValue();
                    ProviderStats s = stats.get(name);
                    
                    if (s != null) {
                        try {
                            long start = System.currentTimeMillis();
                            boolean healthy = !provider.getSupportedModels().isEmpty();
                            long responseTime = System.currentTimeMillis() - start;
                            
                            s.setHealthy(healthy);
                            if (healthy) {
                                s.setLastHealthCheckTime(System.currentTimeMillis());
                                s.setLastHealthCheckResponseTime(responseTime);
                            }
                        } catch (Exception e) {
                            log.warn("Health check failed for provider: {}", name, e);
                            s.setHealthy(false);
                        }
                    }
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * 获取提供者统计信息
     */
    public Map<String, ProviderStats> getStats() {
        return new HashMap<>(stats);
    }
    
    /**
     * 设置负载均衡策略
     */
    public void setStrategy(LoadBalanceStrategy strategy) {
        this.strategy = strategy;
        log.info("Load balance strategy changed to: {}", strategy);
    }
    
    /**
     * 设置最大重试次数
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    /**
     * 负载均衡策略
     */
    public enum LoadBalanceStrategy {
        ROUND_ROBIN,           // 轮询
        WEIGHTED_ROUND_ROBIN,  // 加权轮询
        LEAST_CONNECTIONS,     // 最少连接
        RESPONSE_TIME,         // 响应时间
        RANDOM                 // 随机
    }
    
    /**
     * 提供者统计信息
     */
    public static class ProviderStats {
        private final String name;
        private final int weight;
        private volatile boolean healthy = true;
        private volatile int activeConnections = 0;
        private volatile int totalRequests = 0;
        private volatile int successRequests = 0;
        private volatile int failedRequests = 0;
        private volatile long totalResponseTime = 0;
        private volatile long lastHealthCheckTime = 0;
        private volatile long lastHealthCheckResponseTime = 0;
        
        public ProviderStats(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }
        
        public String getName() { return name; }
        public int getWeight() { return weight; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        public int getActiveConnections() { return activeConnections; }
        public int getTotalRequests() { return totalRequests; }
        public int getSuccessRequests() { return successRequests; }
        public int getFailedRequests() { return failedRequests; }
        
        public long getAvgResponseTime() {
            if (successRequests == 0) return Long.MAX_VALUE;
            return totalResponseTime / successRequests;
        }
        
        public long getLastHealthCheckTime() { return lastHealthCheckTime; }
        public void setLastHealthCheckTime(long time) { this.lastHealthCheckTime = time; }
        
        public long getLastHealthCheckResponseTime() { return lastHealthCheckResponseTime; }
        public void setLastHealthCheckResponseTime(long time) { this.lastHealthCheckResponseTime = time; }
        
        public void incrementConnections() {
            activeConnections++;
            totalRequests++;
        }
        
        public void recordSuccess(long responseTime) {
            activeConnections--;
            successRequests++;
            totalResponseTime += responseTime;
        }
        
        public void recordFailure() {
            activeConnections--;
            failedRequests++;
        }
        
        public double getSuccessRate() {
            if (totalRequests == 0) return 0;
            return (double) successRequests / totalRequests;
        }
    }
}
