package net.ooder.sdk.api.agent.support;

import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.PublicAPI;
import net.ooder.sdk.common.enums.AgentType;

import java.util.UUID;

/**
 * Agent 抽象基类
 * 提供生命周期管理、状态转换等通用实现
 *
 * @version 3.0.0
 * @since 3.0.0
 */
@PublicAPI
public abstract class AbstractAgent implements Agent {

    protected final String agentId;
    protected final String agentName;
    protected final AgentType agentType;
    protected volatile AgentState state = AgentState.CREATED;

    protected AbstractAgent(String agentId, String agentName, AgentType agentType) {
        this.agentId = agentId != null ? agentId : generateAgentId(agentType);
        this.agentName = agentName;
        this.agentType = agentType;
    }

    protected AbstractAgent(String agentName, AgentType agentType) {
        this.agentId = generateAgentId(agentType);
        this.agentName = agentName;
        this.agentType = agentType;
    }

    private static String generateAgentId(AgentType type) {
        return type.name().toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public AgentType getAgentType() {
        return agentType;
    }

    @Override
    public boolean isHealthy() {
        return state == AgentState.RUNNING;
    }

    @Override
    public AgentState getState() {
        return state;
    }

    protected boolean transitionState(AgentState from, AgentState to) {
        if (state == from) {
            state = to;
            onStateChanged(from, to);
            return true;
        }
        return false;
    }

    protected void onStateChanged(AgentState from, AgentState to) {
    }

    public enum AgentState {
        CREATED,
        INITIALIZING,
        RUNNING,
        STOPPING,
        STOPPED,
        ERROR
    }
}
