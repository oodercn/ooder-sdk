package net.ooder.skills.sync;

import java.util.List;
import java.util.Map;

import net.ooder.skills.sync.model.AgentStatusInfo;
import net.ooder.skills.sync.model.CommunicationLinkInfo;
import net.ooder.skills.sync.model.FailoverStatusInfo;
import net.ooder.skills.sync.model.HeartbeatInfo;

public interface UserSceneGroupAgentProxy {
    
    AgentStatusInfo getAgentStatus(String agentId);
    
    List<AgentStatusInfo> getAllAgentStatuses();
    
    AgentStatusInfo getPrimaryAgent();
    
    List<AgentStatusInfo> getBackupAgents();
    
    CommunicationLinkInfo getCommunicationLinks();
    
    String getAgentEndpoint(String agentId);
    
    HeartbeatInfo getHeartbeatInfo(String agentId);
    
    FailoverStatusInfo getFailoverStatus();
    
    void triggerFailover(String failedAgentId);
    
    Map<String, Object> getSharedState();
    
    void updateSharedState(String key, Object value);
    
    void syncStateToSdk();
    
    void syncStateFromSdk();
}
