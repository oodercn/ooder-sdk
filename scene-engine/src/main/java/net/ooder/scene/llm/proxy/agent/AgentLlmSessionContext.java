package net.ooder.scene.llm.proxy.agent;

import net.ooder.scene.llm.config.SceneLlmConfig;
import net.ooder.scene.llm.proxy.common.AgentState;
import net.ooder.scene.llm.proxy.connection.LlmConnectionPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent LLM 会话上下文
 * 核心隔离单元，每个Agent独立维护自己的LLM配置和对话
 *
 * 对应 JDSServer 的 ConnectInfo，但扩展为Agent级
 */
public class AgentLlmSessionContext {

    private final String agentId;
    private final String userId;
    private final String agentType;

    // LLM配置引用
    private final String llmConfigId;
    private final SceneLlmConfig llmConfig;

    // 连接池引用（关键：多个Agent可共享同一连接池）
    private final String connectionPoolId;
    private final LlmConnectionPool connectionPool;

    // 对话历史（Agent级隔离）
    private final String conversationMemoryId;
    private final List<ChatMessage> conversationHistory;

    // Agent级配额
    private final AgentLlmQuota quota;

    // 状态
    private volatile AgentState state;

    // 时间戳
    private final long createdAt;
    private volatile long lastActiveAt;
    private final long idleTimeout;

    public AgentLlmSessionContext(String agentId, String userId, String agentType,
                                   String llmConfigId, SceneLlmConfig llmConfig,
                                   String connectionPoolId, LlmConnectionPool connectionPool,
                                   String conversationMemoryId,
                                   AgentLlmQuota quota, long idleTimeout) {
        this.agentId = agentId;
        this.userId = userId;
        this.agentType = agentType;
        this.llmConfigId = llmConfigId;
        this.llmConfig = llmConfig;
        this.connectionPoolId = connectionPoolId;
        this.connectionPool = connectionPool;
        this.conversationMemoryId = conversationMemoryId;
        this.conversationHistory = new ArrayList<>();
        this.quota = quota;
        this.idleTimeout = idleTimeout;
        this.state = AgentState.ACTIVE;
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = createdAt;
    }

    /**
     * 更新活跃时间
     */
    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }

    /**
     * 检查是否空闲超时
     */
    public boolean isIdleTimeout() {
        return System.currentTimeMillis() - lastActiveAt > idleTimeout;
    }

    /**
     * 检查是否活跃
     */
    public boolean isActive() {
        return state == AgentState.ACTIVE && !isIdleTimeout();
    }

    /**
     * 添加对话消息
     */
    public void addMessage(String role, String content) {
        conversationHistory.add(new ChatMessage(role, content));
        touch();
    }

    /**
     * 获取对话历史
     */
    public List<ChatMessage> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    /**
     * 清空对话历史
     */
    public void clearConversationHistory() {
        conversationHistory.clear();
    }

    // Getters
    public String getAgentId() {
        return agentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAgentType() {
        return agentType;
    }

    public String getLlmConfigId() {
        return llmConfigId;
    }

    public SceneLlmConfig getLlmConfig() {
        return llmConfig;
    }

    public String getConnectionPoolId() {
        return connectionPoolId;
    }

    public LlmConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public String getConversationMemoryId() {
        return conversationMemoryId;
    }

    public AgentLlmQuota getQuota() {
        return quota;
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastActiveAt() {
        return lastActiveAt;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * 对话消息内部类
     */
    public static class ChatMessage {
        private final String role;
        private final String content;
        private final long timestamp;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
