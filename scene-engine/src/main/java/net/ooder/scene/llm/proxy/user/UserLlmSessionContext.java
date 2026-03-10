package net.ooder.scene.llm.proxy.user;

import net.ooder.scene.llm.proxy.agent.AgentLlmSessionContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户LLM会话上下文
 * 对应 JDSServer 的 ConnectInfo，但扩展为用户级
 */
public class UserLlmSessionContext {

    private final String userId;
    private String userSessionId;  // 关联 SceneEngine Session

    // Agent会话映射
    private final Map<String, AgentLlmSessionContext> agentSessions;

    // 用户级配额
    private final UserLlmQuota quota;

    // 时间戳
    private final long createdAt;
    private volatile long lastActiveAt;

    public UserLlmSessionContext(String userId) {
        this(userId, null);
    }

    public UserLlmSessionContext(String userId, String userSessionId) {
        this.userId = userId;
        this.userSessionId = userSessionId;
        this.agentSessions = new ConcurrentHashMap<>();
        this.quota = new UserLlmQuota();
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = createdAt;
    }

    /**
     * 添加Agent会话
     */
    public void addAgentSession(AgentLlmSessionContext agentContext) {
        agentSessions.put(agentContext.getAgentId(), agentContext);
        quota.incrementAgent();
        touch();
    }

    /**
     * 移除Agent会话
     */
    public void removeAgentSession(String agentId) {
        AgentLlmSessionContext removed = agentSessions.remove(agentId);
        if (removed != null) {
            quota.decrementAgent();
        }
        touch();
    }

    /**
     * 获取Agent会话
     */
    public AgentLlmSessionContext getAgentSession(String agentId) {
        touch();
        return agentSessions.get(agentId);
    }

    /**
     * 检查是否可以创建Agent
     */
    public boolean canCreateAgent() {
        return quota.canCreateAgent();
    }

    /**
     * 检查并消耗Token配额
     */
    public boolean consumeTokens(long tokens) {
        return quota.consumeTokens(tokens);
    }

    /**
     * 更新活跃时间
     */
    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public Map<String, AgentLlmSessionContext> getAgentSessions() {
        return agentSessions;
    }

    public UserLlmQuota getQuota() {
        return quota;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastActiveAt() {
        return lastActiveAt;
    }

    /**
     * 获取Agent数量
     */
    public int getAgentCount() {
        return agentSessions.size();
    }
}
