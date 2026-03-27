package net.ooder.scene.agent.context;

import java.util.List;
import java.util.Map;

/**
 * Agent 上下文管理器接口
 *
 * <p>提供 Agent 的注册、上下文管理和状态管理能力。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>Agent 注册 - 支持虚拟 Agent 和物理 Agent</li>
 *   <li>上下文管理 - 多级上下文管理</li>
 *   <li>状态管理 - 在线状态、心跳管理</li>
 *   <li>查询 - 场景组内 Agent 查询</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface AgentContextManager {
    
    AgentProfile registerVirtualAgent(VirtualAgentConfig config);
    
    AgentProfile registerPhysicalAgent(PhysicalAgentConfig config);
    
    void unregisterAgent(String agentId);
    
    AgentProfile getAgentProfile(String agentId);
    
    AgentContext getAgentContext(String agentId);
    
    void updateAgentContext(String agentId, Map<String, Object> context);
    
    void updateAgentContext(String agentId, String level, Map<String, Object> context);
    
    ConversationContext getConversationContext(String conversationId);
    
    ConversationContext createIsolatedContext(String agentId, String sceneGroupId);
    
    AgentStatus getAgentStatus(String agentId);
    
    void updateAgentStatus(String agentId, AgentStatus status);
    
    void heartbeat(String agentId);
    
    List<AgentProfile> getAgentsByScene(String sceneGroupId);
    
    List<AgentProfile> getOnlineAgents(String sceneGroupId);
    
    List<AgentProfile> getVirtualAgents(String sceneGroupId);
    
    List<AgentProfile> getPhysicalAgents(String sceneGroupId);
    
    boolean isAgentOnline(String agentId);
    
    int getAgentCount();
    
    int getOnlineAgentCount();
    
    void cleanupOfflineAgents(long timeoutMs);
}
