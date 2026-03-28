package net.ooder.scene.a2a;

import net.ooder.scene.agent.context.AgentProfile;

import java.util.List;

/**
 * A2A 消息路由器接口
 *
 * <p>提供Agent间消息的智能路由能力</p>
 *
 * <h3>路由策略优先级：</h3>
 * <ol>
 *   <li>直接指定目标Agent (toAgentId) - 直接路由到指定Agent</li>
 *   <li>能力匹配 (targetCapability) - 查找具有指定能力的在线Agent，多Agent时选择负载最低的</li>
 *   <li>角色匹配 (targetRole) - 查找具有指定角色的在线Agent，多Agent时选择负载最低的</li>
 *   <li>规则匹配 (A2ARoutingRule) - 按优先级匹配自定义路由规则</li>
 *   <li>广播 - 广播到场景组内所有Agent</li>
 * </ol>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface A2AMessageRouter {
    
    /**
     * 路由消息到目标Agent
     * 
     * @param message A2A消息
     * @return 路由结果
     */
    RouteResult route(A2AMessage message);
    
    /**
     * 批量路由消息
     * 
     * @param messages 消息列表
     * @return 路由结果列表
     */
    List<RouteResult> routeBatch(List<A2AMessage> messages);
    
    /**
     * 根据能力查找Agent
     * 
     * @param sceneGroupId 场景组ID
     * @param capability 能力标识
     * @return 匹配的Agent列表
     */
    List<AgentProfile> findByCapability(String sceneGroupId, String capability);
    
    /**
     * 根据角色查找Agent
     * 
     * @param sceneGroupId 场景组ID
     * @param role 角色标识
     * @return 匹配的Agent列表
     */
    List<AgentProfile> findByRole(String sceneGroupId, String role);
    
    /**
     * 注册路由规则
     * 
     * @param rule 路由规则
     */
    void registerRule(A2ARoutingRule rule);
    
    /**
     * 移除路由规则
     * 
     * @param ruleId 规则ID
     */
    void removeRule(String ruleId);
    
    /**
     * 获取所有路由规则
     * 
     * @return 路由规则列表
     */
    List<A2ARoutingRule> getRules();
    
    /**
     * 注册Agent
     * 
     * @param profile Agent配置
     */
    void registerAgent(AgentProfile profile);
    
    /**
     * 注销Agent
     * 
     * @param agentId Agent ID
     */
    void unregisterAgent(String agentId);
    
    /**
     * 获取场景组内的Agent列表
     * 
     * @param sceneGroupId 场景组ID
     * @return Agent ID列表
     */
    List<String> getAgentsInScene(String sceneGroupId);
    
    /**
     * 设置默认路由目标
     * 
     * @param sceneGroupId 场景组ID
     * @param agentId 默认目标Agent ID
     */
    void setDefaultTarget(String sceneGroupId, String agentId);
    
    /**
     * 获取路由统计信息
     * 
     * @return 统计信息
     */
    RoutingStats getStats();
    
    /**
     * 路由统计信息
     */
    class RoutingStats {
        private int ruleCount;
        private int agentCount;
        private int sceneCount;
        private int onlineAgentCount;
        private int capabilityCount;
        private int roleCount;
        
        public int getRuleCount() { return ruleCount; }
        public void setRuleCount(int ruleCount) { this.ruleCount = ruleCount; }
        
        public int getAgentCount() { return agentCount; }
        public void setAgentCount(int agentCount) { this.agentCount = agentCount; }
        
        public int getSceneCount() { return sceneCount; }
        public void setSceneCount(int sceneCount) { this.sceneCount = sceneCount; }
        
        public int getOnlineAgentCount() { return onlineAgentCount; }
        public void setOnlineAgentCount(int onlineAgentCount) { this.onlineAgentCount = onlineAgentCount; }
        
        public int getCapabilityCount() { return capabilityCount; }
        public void setCapabilityCount(int capabilityCount) { this.capabilityCount = capabilityCount; }
        
        public int getRoleCount() { return roleCount; }
        public void setRoleCount(int roleCount) { this.roleCount = roleCount; }
    }
}
