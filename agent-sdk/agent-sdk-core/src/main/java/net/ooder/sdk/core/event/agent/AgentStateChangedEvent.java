package net.ooder.sdk.core.event.agent;

import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.core.event.CoreEvent;

/**
 * Agent 状态变更事件（Core 层）
 *
 * <p>当 Agent 状态发生变更时触发</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public final class AgentStateChangedEvent extends CoreEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Agent ID
     */
    private final String agentId;

    /**
     * Agent 名称
     */
    private final String agentName;

    /**
     * 旧状态
     */
    private final Agent.AgentState oldState;

    /**
     * 新状态
     */
    private final Agent.AgentState newState;

    public AgentStateChangedEvent(String agentId, String agentName, Agent.AgentState oldState, Agent.AgentState newState) {
        super("AgentLifecycle");
        this.agentId = agentId;
        this.agentName = agentName;
        this.oldState = oldState;
        this.newState = newState;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public Agent.AgentState getOldState() {
        return oldState;
    }

    public Agent.AgentState getNewState() {
        return newState;
    }

    @Override
    public String getDescription() {
        return String.format("Agent %s(%s) state changed from %s to %s",
            agentName, agentId, oldState, newState);
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.HIGH;
    }

    @Override
    public String toString() {
        return String.format("AgentStateChangedEvent[agent=%s, %s -> %s, time=%s]",
            agentId, oldState, newState, getInstant());
    }
}
