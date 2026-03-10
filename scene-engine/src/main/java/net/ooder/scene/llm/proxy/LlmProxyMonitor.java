package net.ooder.scene.llm.proxy;

import net.ooder.scene.llm.proxy.agent.AgentLlmSessionContext;
import net.ooder.scene.llm.proxy.agent.AgentSessionManager;
import net.ooder.scene.llm.proxy.common.AgentState;
import net.ooder.scene.llm.proxy.connection.LlmConnectionManager;
import net.ooder.scene.llm.proxy.connection.LlmConnectionPool;
import net.ooder.scene.llm.proxy.user.UserLlmSessionContext;
import net.ooder.scene.llm.proxy.user.UserLlmSessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * LLM代理层监控器
 * 定期收集和报告监控指标
 */
public class LlmProxyMonitor {

    private static final Logger log = LoggerFactory.getLogger(LlmProxyMonitor.class);

    private final UserLlmSessionManager userSessionManager;
    private final AgentSessionManager agentSessionManager;
    private final LlmConnectionManager connectionManager;

    private ScheduledExecutorService scheduler;
    private long monitorIntervalMs = 60000; // 默认60秒
    private volatile boolean running = false;

    public LlmProxyMonitor(UserLlmSessionManager userSessionManager,
                           AgentSessionManager agentSessionManager,
                           LlmConnectionManager connectionManager) {
        this.userSessionManager = userSessionManager;
        this.agentSessionManager = agentSessionManager;
        this.connectionManager = connectionManager;
    }

    /**
     * 启动监控
     */
    public void start() {
        if (running) {
            return;
        }

        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "llm-proxy-monitor");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(this::collectMetrics, monitorIntervalMs, monitorIntervalMs, TimeUnit.MILLISECONDS);

        log.info("LLM Proxy Monitor started, interval={}ms", monitorIntervalMs);
    }

    /**
     * 停止监控
     */
    public void stop() {
        if (!running) {
            return;
        }

        running = false;
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("LLM Proxy Monitor stopped");
    }

    /**
     * 设置监控间隔
     */
    public void setMonitorInterval(long intervalMs) {
        this.monitorIntervalMs = intervalMs;
    }

    /**
     * 收集监控指标
     */
    private void collectMetrics() {
        try {
            MonitorMetrics metrics = gatherMetrics();

            // 记录指标
            log.info("[LlmProxyMetrics] Users={}, Agents={} (active={}), Pools={}",
                    metrics.getTotalUsers(),
                    metrics.getTotalAgents(),
                    metrics.getActiveAgents(),
                    metrics.getTotalPools());

            // 检查异常
            checkAnomalies(metrics);

        } catch (Exception e) {
            log.error("Error collecting metrics", e);
        }
    }

    /**
     * 收集所有指标
     */
    public MonitorMetrics gatherMetrics() {
        MonitorMetrics metrics = new MonitorMetrics();

        // 用户统计
        metrics.setTotalUsers(userSessionManager.getUserCount());

        // Agent统计
        AgentSessionManager.AgentSessionStats agentStats = agentSessionManager.getStats();
        metrics.setTotalAgents(agentStats.getTotalAgents());
        metrics.setActiveAgents(agentStats.getActiveAgents());

        // 连接池统计
        Map<String, LlmConnectionPool.PoolStats> poolStats = connectionManager.getAllPoolStats();
        metrics.setTotalPools(poolStats.size());

        int totalConnections = 0;
        int activeConnections = 0;
        for (LlmConnectionPool.PoolStats stats : poolStats.values()) {
            totalConnections += stats.getActiveConnections();
            activeConnections += (stats.getActiveConnections() - stats.getAvailableConnections());
        }
        metrics.setTotalConnections(totalConnections);
        metrics.setActiveConnections(activeConnections);

        return metrics;
    }

    /**
     * 检查异常情况
     */
    private void checkAnomalies(MonitorMetrics metrics) {
        // 检查连接池是否耗尽
        Map<String, LlmConnectionPool.PoolStats> poolStats = connectionManager.getAllPoolStats();
        for (Map.Entry<String, LlmConnectionPool.PoolStats> entry : poolStats.entrySet()) {
            LlmConnectionPool.PoolStats stats = entry.getValue();
            if (stats.getState().name().equals("EXHAUSTED")) {
                log.warn("[LlmProxyAlert] Connection pool exhausted: poolId={}", entry.getKey());
            }
        }

        // 检查空闲Agent
        for (String agentId : agentSessionManager.getAllAgentIds()) {
            AgentLlmSessionContext context = agentSessionManager.getAgentContext(agentId);
            if (context != null && context.isIdleTimeout()) {
                log.warn("[LlmProxyAlert] Agent idle timeout detected: agentId={}", agentId);
            }
        }
    }

    /**
     * 获取详细报告
     */
    public String generateReport() {
        MonitorMetrics metrics = gatherMetrics();

        StringBuilder report = new StringBuilder();
        report.append("========== LLM Proxy Monitor Report ==========\n");
        report.append("Timestamp: ").append(new java.util.Date()).append("\n\n");

        report.append("[User Statistics]\n");
        report.append("  Total Users: ").append(metrics.getTotalUsers()).append("\n\n");

        report.append("[Agent Statistics]\n");
        report.append("  Total Agents: ").append(metrics.getTotalAgents()).append("\n");
        report.append("  Active Agents: ").append(metrics.getActiveAgents()).append("\n\n");

        report.append("[Connection Statistics]\n");
        report.append("  Total Pools: ").append(metrics.getTotalPools()).append("\n");
        report.append("  Total Connections: ").append(metrics.getTotalConnections()).append("\n");
        report.append("  Active Connections: ").append(metrics.getActiveConnections()).append("\n\n");

        report.append("[Connection Pool Details]\n");
        Map<String, LlmConnectionPool.PoolStats> poolStats = connectionManager.getAllPoolStats();
        for (Map.Entry<String, LlmConnectionPool.PoolStats> entry : poolStats.entrySet()) {
            LlmConnectionPool.PoolStats stats = entry.getValue();
            report.append("  Pool: ").append(stats.getPoolId()).append("\n");
            report.append("    State: ").append(stats.getState()).append("\n");
            report.append("    Connections: ").append(stats.getActiveConnections()).append("/").append(stats.getMaxConnections()).append("\n");
            report.append("    References: ").append(stats.getReferenceCount()).append("\n");
        }

        report.append("==============================================\n");

        return report.toString();
    }

    /**
     * 监控指标类
     */
    public static class MonitorMetrics {
        private int totalUsers;
        private int totalAgents;
        private int activeAgents;
        private int totalPools;
        private int totalConnections;
        private int activeConnections;

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

        public int getTotalAgents() { return totalAgents; }
        public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }

        public int getActiveAgents() { return activeAgents; }
        public void setActiveAgents(int activeAgents) { this.activeAgents = activeAgents; }

        public int getTotalPools() { return totalPools; }
        public void setTotalPools(int totalPools) { this.totalPools = totalPools; }

        public int getTotalConnections() { return totalConnections; }
        public void setTotalConnections(int totalConnections) { this.totalConnections = totalConnections; }

        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
    }
}
