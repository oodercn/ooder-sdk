package net.ooder.scene.llm.proxy.user;

import net.ooder.scene.llm.config.SceneLlmConfig;
import net.ooder.scene.llm.proxy.agent.AgentCreationOptions;
import net.ooder.scene.llm.proxy.agent.AgentLlmSessionContext;
import net.ooder.scene.llm.proxy.agent.AgentLlmSessionHandle;
import net.ooder.scene.llm.proxy.agent.AgentSessionManager;
import net.ooder.scene.llm.proxy.common.LlmProxyException;
import net.ooder.scene.llm.proxy.connection.LlmConnectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户LLM会话管理器
 * 管理用户级别的会话和配额
 */
public class UserLlmSessionManager {

    private static final Logger log = LoggerFactory.getLogger(UserLlmSessionManager.class);

    // 用户上下文缓存
    private final Map<String, UserLlmSessionContext> userContextCache;

    private final AgentSessionManager agentSessionManager;
    private final LlmConnectionManager connectionManager;

    public UserLlmSessionManager(AgentSessionManager agentSessionManager,
                                  LlmConnectionManager connectionManager) {
        this.agentSessionManager = agentSessionManager;
        this.connectionManager = connectionManager;
        this.userContextCache = new ConcurrentHashMap<>();
    }

    /**
     * 初始化用户上下文
     */
    public UserLlmSessionContext initializeUserContext(String userId, String userSessionId) {
        log.info("Initializing user LLM context: userId={}, sessionId={}", userId, userSessionId);

        return userContextCache.computeIfAbsent(userId, k -> {
            UserLlmSessionContext ctx = new UserLlmSessionContext(userId, userSessionId);
            log.debug("Created new user context: userId={}", userId);
            return ctx;
        });
    }

    /**
     * 获取或创建用户上下文
     */
    public UserLlmSessionContext getOrCreateUserContext(String userId) {
        return userContextCache.computeIfAbsent(userId, k -> {
            log.debug("Created new user context on demand: userId={}", userId);
            return new UserLlmSessionContext(userId);
        });
    }

    /**
     * 获取用户上下文
     */
    public UserLlmSessionContext getUserContext(String userId) {
        return userContextCache.get(userId);
    }

    /**
     * 为用户创建Agent
     */
    public AgentLlmSessionHandle createAgentForUser(
            String userId,
            String agentType,
            SceneLlmConfig llmConfig,
            AgentCreationOptions options) throws LlmProxyException {

        // 1. 获取或创建用户上下文
        UserLlmSessionContext userContext = getOrCreateUserContext(userId);

        // 2. 检查用户配额
        if (!userContext.canCreateAgent()) {
            throw new LlmProxyException("USER_AGENT_LIMIT",
                "User agent quota exceeded. Max agents: " + userContext.getQuota().getMaxAgentsPerUser());
        }

        // 3. 创建Agent会话
        AgentLlmSessionHandle handle = agentSessionManager.createAgentSession(
            userId, agentType, llmConfig, options);

        // 4. 关联到用户上下文
        AgentLlmSessionContext agentContext = agentSessionManager.getAgentContext(handle.getAgentId());
        if (agentContext != null) {
            userContext.addAgentSession(agentContext);
        }

        log.info("Agent created for user: userId={}, agentId={}", userId, handle.getAgentId());

        return handle;
    }

    /**
     * 销毁用户的Agent
     */
    public void destroyUserAgent(String userId, String agentId) {
        UserLlmSessionContext userContext = userContextCache.get(userId);
        if (userContext != null) {
            userContext.removeAgentSession(agentId);
        }

        agentSessionManager.destroyAgentSession(agentId);

        log.info("Agent destroyed for user: userId={}, agentId={}", userId, agentId);
    }

    /**
     * 清理用户的所有Agent
     */
    public void cleanupUserAgents(String userId) {
        UserLlmSessionContext userContext = userContextCache.get(userId);
        if (userContext == null) {
            return;
        }

        log.info("Cleaning up all agents for user: userId={}", userId);

        // 获取用户的所有Agent ID
        List<String> agentIds = new ArrayList<>(userContext.getAgentSessions().keySet());

        // 销毁每个Agent
        for (String agentId : agentIds) {
            destroyUserAgent(userId, agentId);
        }

        // 移除用户上下文
        userContextCache.remove(userId);

        log.info("User cleanup complete: userId={}", userId);
    }

    /**
     * 获取用户的Agent
     */
    public AgentLlmSessionContext getUserAgent(String userId, String agentId) {
        UserLlmSessionContext userContext = userContextCache.get(userId);
        if (userContext == null) {
            return null;
        }
        return userContext.getAgentSession(agentId);
    }

    /**
     * 获取用户的所有Agent
     */
    public List<AgentLlmSessionContext> getUserAgents(String userId) {
        UserLlmSessionContext userContext = userContextCache.get(userId);
        if (userContext == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(userContext.getAgentSessions().values());
    }

    /**
     * 检查用户是否存在
     */
    public boolean containsUser(String userId) {
        return userContextCache.containsKey(userId);
    }

    /**
     * 获取用户数量
     */
    public int getUserCount() {
        return userContextCache.size();
    }

    /**
     * 获取所有用户ID
     */
    public Set<String> getAllUserIds() {
        return new HashSet<>(userContextCache.keySet());
    }

    /**
     * 获取统计信息
     */
    public UserSessionStats getStats() {
        UserSessionStats stats = new UserSessionStats();
        stats.setTotalUsers(userContextCache.size());

        int totalAgents = 0;
        for (UserLlmSessionContext ctx : userContextCache.values()) {
            totalAgents += ctx.getAgentCount();
        }
        stats.setTotalAgents(totalAgents);
        stats.setAgentSessionStats(agentSessionManager.getStats());

        return stats;
    }

    /**
     * 关闭管理器
     */
    public void shutdown() {
        log.info("Shutting down UserLlmSessionManager");

        // 清理所有用户
        for (String userId : new ArrayList<>(userContextCache.keySet())) {
            cleanupUserAgents(userId);
        }

        userContextCache.clear();

        log.info("UserLlmSessionManager shutdown complete");
    }

    public AgentSessionManager getAgentSessionManager() {
        return agentSessionManager;
    }

    public LlmConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * 用户会话统计
     */
    public static class UserSessionStats {
        private int totalUsers;
        private int totalAgents;
        private AgentSessionManager.AgentSessionStats agentSessionStats;

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

        public int getTotalAgents() { return totalAgents; }
        public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }

        public AgentSessionManager.AgentSessionStats getAgentSessionStats() { return agentSessionStats; }
        public void setAgentSessionStats(AgentSessionManager.AgentSessionStats agentSessionStats) {
            this.agentSessionStats = agentSessionStats;
        }

        @Override
        public String toString() {
            return "UserSessionStats{" +
                    "totalUsers=" + totalUsers +
                    ", totalAgents=" + totalAgents +
                    ", agentSessionStats=" + agentSessionStats +
                    '}';
        }
    }
}
