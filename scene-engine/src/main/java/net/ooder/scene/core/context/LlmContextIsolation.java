package net.ooder.scene.core.context;

import java.util.List;
import java.util.Map;

/**
 * LLM上下文隔离管理接口
 *
 * <p>管理场景级别的LLM上下文隔离，确保不同场景、不同Agent的上下文互不干扰。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface LlmContextIsolation {

    /**
     * 创建场景上下文
     *
     * @param sceneId 场景ID
     * @param config 上下文配置
     * @return 上下文实例
     */
    SceneLlmContext createSceneContext(String sceneId, ContextConfig config);

    /**
     * 获取场景上下文
     *
     * @param sceneId 场景ID
     * @return 上下文实例
     */
    SceneLlmContext getSceneContext(String sceneId);

    /**
     * 销毁场景上下文
     *
     * @param sceneId 场景ID
     */
    void destroySceneContext(String sceneId);

    /**
     * 创建Agent上下文
     *
     * @param sceneId 场景ID
     * @param agentId Agent ID
     * @param config 上下文配置
     * @return Agent上下文
     */
    AgentLlmContext createAgentContext(String sceneId, String agentId, ContextConfig config);

    /**
     * 获取Agent上下文
     *
     * @param sceneId 场景ID
     * @param agentId Agent ID
     * @return Agent上下文
     */
    AgentLlmContext getAgentContext(String sceneId, String agentId);

    /**
     * 销毁Agent上下文
     *
     * @param sceneId 场景ID
     * @param agentId Agent ID
     */
    void destroyAgentContext(String sceneId, String agentId);

    /**
     * 获取场景下所有Agent上下文
     *
     * @param sceneId 场景ID
     * @return Agent上下文列表
     */
    List<AgentLlmContext> getSceneAgentContexts(String sceneId);

    /**
     * 切换上下文
     *
     * @param fromSceneId 源场景ID
     * @param toSceneId 目标场景ID
     * @param transferConfig 传递配置
     * @return 切换结果
     */
    ContextSwitchResult switchContext(String fromSceneId, String toSceneId, ContextTransferConfig transferConfig);

    /**
     * 合并上下文
     *
     * @param sceneIds 场景ID列表
     * @param targetSceneId 目标场景ID
     * @return 合并结果
     */
    ContextMergeResult mergeContexts(List<String> sceneIds, String targetSceneId);

    /**
     * 检查上下文隔离状态
     *
     * @param sceneId 场景ID
     * @return 隔离状态
     */
    IsolationStatus checkIsolation(String sceneId);

    /**
     * 上下文配置
     */
    class ContextConfig {
        private int maxTokens;
        private int maxMessages;
        private long ttl;
        private Map<String, Object> variables;

        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        public int getMaxMessages() { return maxMessages; }
        public void setMaxMessages(int maxMessages) { this.maxMessages = maxMessages; }
        public long getTtl() { return ttl; }
        public void setTtl(long ttl) { this.ttl = ttl; }
        public Map<String, Object> getVariables() { return variables; }
        public void setVariables(Map<String, Object> variables) { this.variables = variables; }
    }

    /**
     * 场景LLM上下文
     */
    class SceneLlmContext {
        private String sceneId;
        private String contextId;
        private List<Message> messages;
        private Map<String, Object> variables;
        private long createTime;
        private long lastAccessTime;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getContextId() { return contextId; }
        public void setContextId(String contextId) { this.contextId = contextId; }
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
        public Map<String, Object> getVariables() { return variables; }
        public void setVariables(Map<String, Object> variables) { this.variables = variables; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getLastAccessTime() { return lastAccessTime; }
        public void setLastAccessTime(long lastAccessTime) { this.lastAccessTime = lastAccessTime; }
    }

    /**
     * Agent LLM上下文
     */
    class AgentLlmContext {
        private String sceneId;
        private String agentId;
        private String contextId;
        private List<Message> messages;
        private Map<String, Object> variables;
        private long createTime;
        private long lastAccessTime;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getContextId() { return contextId; }
        public void setContextId(String contextId) { this.contextId = contextId; }
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
        public Map<String, Object> getVariables() { return variables; }
        public void setVariables(Map<String, Object> variables) { this.variables = variables; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getLastAccessTime() { return lastAccessTime; }
        public void setLastAccessTime(long lastAccessTime) { this.lastAccessTime = lastAccessTime; }
    }

    /**
     * 消息
     */
    class Message {
        private String role;
        private String content;
        private long timestamp;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 上下文传递配置
     */
    class ContextTransferConfig {
        private boolean transferHistory;
        private boolean transferVariables;
        private int maxHistoryMessages;

        public boolean isTransferHistory() { return transferHistory; }
        public void setTransferHistory(boolean transferHistory) { this.transferHistory = transferHistory; }
        public boolean isTransferVariables() { return transferVariables; }
        public void setTransferVariables(boolean transferVariables) { this.transferVariables = transferVariables; }
        public int getMaxHistoryMessages() { return maxHistoryMessages; }
        public void setMaxHistoryMessages(int maxHistoryMessages) { this.maxHistoryMessages = maxHistoryMessages; }
    }

    /**
     * 上下文切换结果
     */
    class ContextSwitchResult {
        private boolean success;
        private String fromContextId;
        private String toContextId;
        private String errorMessage;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getFromContextId() { return fromContextId; }
        public void setFromContextId(String fromContextId) { this.fromContextId = fromContextId; }
        public String getToContextId() { return toContextId; }
        public void setToContextId(String toContextId) { this.toContextId = toContextId; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 上下文合并结果
     */
    class ContextMergeResult {
        private boolean success;
        private String targetContextId;
        private int mergedMessageCount;
        private String errorMessage;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getTargetContextId() { return targetContextId; }
        public void setTargetContextId(String targetContextId) { this.targetContextId = targetContextId; }
        public int getMergedMessageCount() { return mergedMessageCount; }
        public void setMergedMessageCount(int mergedMessageCount) { this.mergedMessageCount = mergedMessageCount; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 隔离状态
     */
    class IsolationStatus {
        private String sceneId;
        private boolean isolated;
        private int contextCount;
        private int agentContextCount;
        private long totalMemoryUsage;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public boolean isIsolated() { return isolated; }
        public void setIsolated(boolean isolated) { this.isolated = isolated; }
        public int getContextCount() { return contextCount; }
        public void setContextCount(int contextCount) { this.contextCount = contextCount; }
        public int getAgentContextCount() { return agentContextCount; }
        public void setAgentContextCount(int agentContextCount) { this.agentContextCount = agentContextCount; }
        public long getTotalMemoryUsage() { return totalMemoryUsage; }
        public void setTotalMemoryUsage(long totalMemoryUsage) { this.totalMemoryUsage = totalMemoryUsage; }
    }
}
