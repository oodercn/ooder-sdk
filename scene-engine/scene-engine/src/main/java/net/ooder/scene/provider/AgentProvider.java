package net.ooder.scene.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.agent.EndAgent;
import net.ooder.scene.provider.model.agent.NetworkStatusData;
import net.ooder.scene.provider.model.agent.CommandStatsData;
import net.ooder.scene.provider.model.agent.TestCommandResult;

import java.util.List;
import java.util.Map;

public interface AgentProvider extends BaseProvider {
    
    Result<List<EndAgent>> getEndAgents();
    
    Result<EndAgent> addEndAgent(Map<String, Object> agentData);
    
    Result<EndAgent> editEndAgent(String agentId, Map<String, Object> agentData);
    
    Result<EndAgent> deleteEndAgent(String agentId);
    
    Result<EndAgent> getEndAgentDetails(String agentId);
    
    Result<NetworkStatusData> getNetworkStatus();
    
    Result<CommandStatsData> getCommandStats();
    
    Result<TestCommandResult> testCommand(Map<String, Object> commandData);
}
