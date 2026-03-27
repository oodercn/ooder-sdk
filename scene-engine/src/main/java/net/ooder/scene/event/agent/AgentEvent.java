package net.ooder.scene.event.agent;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

/**
 * Agent 事件
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class AgentEvent extends SceneEvent {
    
    private final String agentId;
    private final String agentName;
    private final String sceneGroupId;
    
    private AgentEvent(Object source, SceneEventType eventType, String agentId, String agentName) {
        super(source, eventType);
        this.agentId = agentId;
        this.agentName = agentName;
        this.sceneGroupId = null;
    }
    
    private AgentEvent(Object source, SceneEventType eventType, String agentId, String agentName, String sceneGroupId) {
        super(source, eventType);
        this.agentId = agentId;
        this.agentName = agentName;
        this.sceneGroupId = sceneGroupId;
    }
    
    public static AgentEvent registered(Object source, String agentId, String agentName) {
        return new AgentEvent(source, SceneEventType.AGENT_REGISTERED, agentId, agentName);
    }
    
    public static AgentEvent unregistered(Object source, String agentId, String agentName) {
        return new AgentEvent(source, SceneEventType.AGENT_UNREGISTERED, agentId, agentName);
    }
    
    public static AgentEvent online(Object source, String agentId, String sceneGroupId) {
        return new AgentEvent(source, SceneEventType.AGENT_ONLINE, agentId, null, sceneGroupId);
    }
    
    public static AgentEvent offline(Object source, String agentId, String sceneGroupId) {
        return new AgentEvent(source, SceneEventType.AGENT_OFFLINE, agentId, null, sceneGroupId);
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    @Override
    public String toString() {
        return "AgentEvent{" +
                "eventType=" + getEventType() +
                ", agentId='" + agentId + '\'' +
                ", agentName='" + agentName + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                '}';
    }
}
