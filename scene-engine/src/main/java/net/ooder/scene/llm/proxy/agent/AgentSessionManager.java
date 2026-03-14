package net.ooder.scene.llm.proxy.agent;

import net.ooder.scene.llm.config.SceneLlmConfig;
import net.ooder.scene.llm.proxy.common.AgentState;
import net.ooder.scene.llm.proxy.common.LlmProxyException;
import net.ooder.scene.llm.proxy.connection.LlmConnectionManager;
import net.ooder.scene.llm.proxy.connection.LlmConnectionPool;
import net.ooder.scene.llm.proxy.lifecycle.AgentLifecycleListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Agent 会话管理器
 * 对应 JDSServer 的 SessionManagerImpl
 *
 * 四级缓存设计（参考 SessionCacheManagerImpl）：
 * 1. agentContextCache: agentId -> AgentLlmSessionContext
 * 2. agentHandleCache: agentId -> AgentLlmSessionHandle
 * 3. userAgentMappingCache: userId -> List<agentId>
 * 4. agentActiveTimeCache: agentId -> lastActiveTime
 */
public class AgentSessionManager {

    private static final Logger log = LoggerFactory.getLogger(AgentSessionManager.class);

    // 四级缓存
    private final Map<String, AgentLlmSessionContext> agentContextCache;
    private final Map<String, AgentLlmSessionHandle> agentHandleCache;
    private final Map<String, List<String>> userAgentMappingCache;
    private final Map<String, Long> agentActiveTimeCache;

    private final LlmConnectionManager connectionManager;
    private final List<AgentLifecycleListener> lifecycleListeners;
    private final Object lock = new Object();

    public AgentSessionManager(LlmConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.agentContextCache = new ConcurrentHashMap<>();
        this.agentHandleCache = new ConcurrentHashMap<>();
        this.userAgentMappingCache = new ConcurrentHashMap<>();
        this.agentActiveTimeCache = new ConcurrentHashMap<>();
        this.lifecycleListeners = new CopyOnWriteArrayList<>();
    }

    /**
     * 创建 Agent 会话
     * 对应 JDSServer.createSession(ConnectInfo)
     */
    public AgentLlmSessionHandle createAgentSession(
            String userId,
            String agentType,
            SceneLlmConfig llmConfig,
            AgentCreationOptions options) throws LlmProxyException {

        if (userId == null || agentType == null || llmConfig == null) {
            throw new LlmProxyException("INVALID_PARAM", "userId, agentType and llmConfig are required");
        }

        // 1. 生成Agent ID
        String agentId = generateAgentId();
        String agentSessionId = generateAgentSessionId();

        log.info("Creating agent session: agentId={}, userId={}, agentType={}", agentId, userId, agentType);

        // 2. 获取或创建连接池（关键：相同配置共享）
        LlmConnectionPool pool = connectionManager.getOrCreatePool(llmConfig);

        // 3. 创建配额
        AgentLlmQuota quota = new AgentLlmQuota(
            options.getDailyTokenLimit(),
            options.getMaxConversations()
        );

        // 4. 创建Agent上下文
        AgentLlmSessionContext agentContext = new AgentLlmSessionContext(
            agentId,
            userId,
            agentType,
            llmConfig.hashCode() + "", // 使用config hash作为configId
            llmConfig,
            pool.getPoolId(),
            pool,
            agentId + ":memory",
            quota,
            options.getIdleTimeout()
        );

        // 5. 创建句柄
        AgentLlmSessionHandle handle = new AgentLlmSessionHandle(agentSessionId, agentId, userId);

        // 6. 缓存
        cacheAgentSession(handle, agentContext);

        // 7. 增加连接池引用
        pool.incrementReference();

        // 8. 触发生命周期事件
        fireAgentCreated(handle, agentContext);

        log.info("Agent session created successfully: agentId={}, poolId={}", agentId, pool.getPoolId());

        return handle;
    }

    /**
     * 获取Agent上下文
     */
    public AgentLlmSessionContext getAgentContext(String agentId) {
        AgentLlmSessionContext context = agentContextCache.get(agentId);
        if (context != null) {
            context.touch();
            agentActiveTimeCache.put(agentId, System.currentTimeMillis());
        }
        return context;
    }

    /**
     * 获取Agent句柄
     */
    public AgentLlmSessionHandle getAgentHandle(String agentId) {
        return agentHandleCache.get(agentId);
    }

    /**
     * 销毁 Agent 会话
     * 对应 JDSServer.invalidateSession
     */
    public void destroyAgentSession(String agentId) {
        AgentLlmSessionContext context = agentContextCache.get(agentId);
        if (context == null) {
            log.warn("Agent session not found: agentId={}", agentId);
            return;
        }

        log.info("Destroying agent session: agentId={}", agentId);

        // 1. 触发销毁事件
        AgentLlmSessionHandle handle = agentHandleCache.get(agentId);
        fireAgentDestroyed(handle, context);

        // 2. 减少连接池引用
        LlmConnectionPool pool = context.getConnectionPool();
        if (pool != null) {
            pool.decrementReference();
        }

        // 3. 从缓存移除
        removeFromCache(agentId);

        log.info("Agent session destroyed: agentId={}", agentId);
    }

    /**
     * 清理用户所有 Agent
     * 对应 JDSServer 按用户清理会话
     */
    public void destroyUserAgents(String userId) {
        List<String> agentIds = getUserAgentIds(userId);
        log.info("Destroying all agents for user: userId={}, count={}", userId, agentIds.size());

        for (String agentId : agentIds) {
            destroyAgentSession(agentId);
        }
    }

    /**
     * 获取用户的所有Agent ID
     */
    public List<String> getUserAgentIds(String userId) {
        List<String> agentIds = userAgentMappingCache.get(userId);
        return agentIds != null ? new ArrayList<>(agentIds) : Collections.emptyList();
    }

    /**
     * 获取用户的所有Agent上下文
     */
    public List<AgentLlmSessionContext> getUserAgentContexts(String userId) {
        List<String> agentIds = getUserAgentIds(userId);
        List<AgentLlmSessionContext> contexts = new ArrayList<>();
        for (String agentId : agentIds) {
            AgentLlmSessionContext context = agentContextCache.get(agentId);
            if (context != null) {
                contexts.add(context);
            }
        }
        return contexts;
    }

    /**
     * 检查Agent是否存在
     */
    public boolean containsAgent(String agentId) {
        return agentContextCache.containsKey(agentId);
    }

    /**
     * 获取所有Agent ID
     */
    public Set<String> getAllAgentIds() {
        return new HashSet<>(agentContextCache.keySet());
    }

    /**
     * 获取Agent数量
     */
    public int getAgentCount() {
        return agentContextCache.size();
    }

    /**
     * 获取用户的Agent数量
     */
    public int getUserAgentCount(String userId) {
        List<String> agentIds = userAgentMappingCache.get(userId);
        return agentIds != null ? agentIds.size() : 0;
    }

    /**
     * 缓存Agent会话
     */
    private void cacheAgentSession(AgentLlmSessionHandle handle, AgentLlmSessionContext context) {
        String agentId = handle.getAgentId();
        String userId = handle.getUserId();

        synchronized (lock) {
            agentContextCache.put(agentId, context);
            agentHandleCache.put(agentId, handle);
            agentActiveTimeCache.put(agentId, System.currentTimeMillis());

            userAgentMappingCache.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(agentId);
        }
    }

    /**
     * 从缓存移除
     */
    private void removeFromCache(String agentId) {
        synchronized (lock) {
            AgentLlmSessionContext context = agentContextCache.remove(agentId);
            agentHandleCache.remove(agentId);
            agentActiveTimeCache.remove(agentId);

            if (context != null) {
                String userId = context.getUserId();
                List<String> agentIds = userAgentMappingCache.get(userId);
                if (agentIds != null) {
                    agentIds.remove(agentId);
                    if (agentIds.isEmpty()) {
                        userAgentMappingCache.remove(userId);
                    }
                }
            }
        }
    }

    /**
     * 生成Agent ID
     */
    private String generateAgentId() {
        return "agent-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 生成Agent会话ID
     */
    private String generateAgentSessionId() {
        return "as-" + UUID.randomUUID().toString().replace("-", "");
    }

    // ==================== 生命周期监听 ====================

    public void registerLifecycleListener(AgentLifecycleListener listener) {
        if (listener != null) {
            lifecycleListeners.add(listener);
        }
    }

    public void unregisterLifecycleListener(AgentLifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    private void fireAgentCreated(AgentLlmSessionHandle handle, AgentLlmSessionContext context) {
        for (AgentLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onAgentCreated(handle, context);
            } catch (Exception e) {
                log.warn("AgentLifecycleListener error onCreated: {}", e.getMessage());
            }
        }
    }

    private void fireAgentDestroyed(AgentLlmSessionHandle handle, AgentLlmSessionContext context) {
        for (AgentLifecycleListener listener : lifecycleListeners) {
            try {
                listener.onAgentDestroyed(handle, context);
            } catch (Exception e) {
                log.warn("AgentLifecycleListener error onDestroyed: {}", e.getMessage());
            }
        }
    }

    // ==================== 统计信息 ====================

    public AgentSessionStats getStats() {
        AgentSessionStats stats = new AgentSessionStats();
        stats.setTotalAgents(agentContextCache.size());
        stats.setTotalUsers(userAgentMappingCache.size());

        int activeAgents = 0;
        for (AgentLlmSessionContext context : agentContextCache.values()) {
            if (context.isActive()) {
                activeAgents++;
            }
        }
        stats.setActiveAgents(activeAgents);

        return stats;
    }

    /**
     * Agent会话统计
     */
    public static class AgentSessionStats {
        private int totalAgents;
        private int activeAgents;
        private int totalUsers;

        public int getTotalAgents() { return totalAgents; }
        public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }

        public int getActiveAgents() { return activeAgents; }
        public void setActiveAgents(int activeAgents) { this.activeAgents = activeAgents; }

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

        @Override
        public String toString() {
            return "AgentSessionStats{" +
                    "totalAgents=" + totalAgents +
                    ", activeAgents=" + activeAgents +
                    ", totalUsers=" + totalUsers +
                    '}';
        }
    }
}
