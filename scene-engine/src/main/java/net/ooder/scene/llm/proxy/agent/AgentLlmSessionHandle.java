package net.ooder.scene.llm.proxy.agent;

import net.ooder.scene.llm.proxy.common.AgentState;

/**
 * Agent 会话句柄
 * 对应 JDSServer 的 JDSSessionHandle
 */
public class AgentLlmSessionHandle {
    
    private final String agentSessionId;
    private final String agentId;
    private final String userId;
    private final long createdTime;
    
    public AgentLlmSessionHandle(String agentSessionId, String agentId, String userId) {
        this.agentSessionId = agentSessionId;
        this.agentId = agentId;
        this.userId = userId;
        this.createdTime = System.currentTimeMillis();
    }
    
    /**
     * 检查会话是否有效
     */
    public boolean isValid(AgentSessionManager manager) {
        AgentLlmSessionContext context = manager.getAgentContext(agentId);
        if (context == null) {
            return false;
        }
        return context.getState() == AgentState.ACTIVE && !context.isIdleTimeout();
    }
    
    public String getAgentSessionId() {
        return agentSessionId;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
    
    @Override
    public String toString() {
        return "AgentLlmSessionHandle{" +
                "agentSessionId='" + agentSessionId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", userId='" + userId + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
