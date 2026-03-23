package net.ooder.sdk.a2a.registry;

import net.ooder.sdk.a2a.AgentInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Agent 注册中心
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface AgentRegistry {

    /**
     * 注册 Agent
     *
     * @param agentInfo Agent 信息
     * @return 注册结果
     */
    CompletableFuture<RegistrationResult> register(AgentInfo agentInfo);

    /**
     * 注销 Agent
     *
     * @param agentId Agent ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unregister(String agentId);

    /**
     * 更新 Agent 信息
     *
     * @param agentInfo Agent 信息
     * @return 是否成功
     */
    CompletableFuture<Boolean> update(AgentInfo agentInfo);

    /**
     * 发送心跳
     *
     * @param agentId Agent ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> heartbeat(String agentId);

    /**
     * 获取 Agent 状态
     *
     * @param agentId Agent ID
     * @return Agent 状态
     */
    AgentInfo.AgentStatus getAgentStatus(String agentId);

    /**
     * 获取 Agent 信息
     *
     * @param agentId Agent ID
     * @return Agent 信息
     */
    CompletableFuture<AgentInfo> getAgent(String agentId);

    /**
     * 发现 Agent
     *
     * @param criteria 发现条件
     * @return Agent 列表
     */
    CompletableFuture<List<AgentInfo>> discoverAgents(DiscoveryCriteria criteria);

    /**
     * 获取所有在线 Agent
     *
     * @return Agent 列表
     */
    CompletableFuture<List<AgentInfo>> getOnlineAgents();

    /**
     * 注册结果
     */
    class RegistrationResult {
        private boolean success;
        private String agentId;
        private String token;
        private String errorMessage;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    /**
     * 发现条件
     */
    class DiscoveryCriteria {
        private String agentType;
        private List<String> capabilities;
        private AgentInfo.AgentStatus status;
        private long lastActiveWithin;  // 毫秒

        // Getters and Setters
        public String getAgentType() { return agentType; }
        public void setAgentType(String agentType) { this.agentType = agentType; }
        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
        public AgentInfo.AgentStatus getStatus() { return status; }
        public void setStatus(AgentInfo.AgentStatus status) { this.status = status; }
        public long getLastActiveWithin() { return lastActiveWithin; }
        public void setLastActiveWithin(long lastActiveWithin) { this.lastActiveWithin = lastActiveWithin; }
    }
}
