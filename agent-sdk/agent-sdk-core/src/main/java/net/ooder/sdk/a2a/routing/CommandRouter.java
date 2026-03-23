package net.ooder.sdk.a2a.routing;

import net.ooder.sdk.a2a.A2ACommand;
import net.ooder.sdk.a2a.A2ACommandResponse;
import net.ooder.sdk.a2a.AgentInfo;

import java.util.List;

/**
 * 命令路由器
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface CommandRouter {

    /**
     * 路由命令
     *
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse route(A2ACommand command);

    /**
     * 路由命令并返回路由结果
     *
     * @param command 命令
     * @return 路由结果
     */
    RouteResult routeWithResult(A2ACommand command);

    /**
     * 添加路由规则
     *
     * @param rule 路由规则
     */
    void addRouteRule(RouteRule rule);

    /**
     * 移除路由规则
     *
     * @param ruleId 规则ID
     */
    void removeRouteRule(String ruleId);

    /**
     * 获取所有路由规则
     *
     * @return 路由规则列表
     */
    List<RouteRule> getRouteRules();

    /**
     * 获取目标 Agent 列表
     *
     * @param sceneId 场景ID
     * @param commandType 命令类型
     * @return Agent 列表
     */
    List<AgentInfo> getTargetAgents(String sceneId, String commandType);

    /**
     * 路由规则
     */
    class RouteRule {
        private String ruleId;
        private String commandType;           // 命令类型匹配
        private String sourceAgentId;         // 源Agent匹配
        private String targetAgentId;         // 目标Agent匹配
        private RouteStrategy strategy;       // 路由策略
        private int priority;                 // 优先级
        private boolean enabled;              // 是否启用

        // Getters and Setters
        public String getRuleId() { return ruleId; }
        public void setRuleId(String ruleId) { this.ruleId = ruleId; }
        public String getCommandType() { return commandType; }
        public void setCommandType(String commandType) { this.commandType = commandType; }
        public String getSourceAgentId() { return sourceAgentId; }
        public void setSourceAgentId(String sourceAgentId) { this.sourceAgentId = sourceAgentId; }
        public String getTargetAgentId() { return targetAgentId; }
        public void setTargetAgentId(String targetAgentId) { this.targetAgentId = targetAgentId; }
        public RouteStrategy getStrategy() { return strategy; }
        public void setStrategy(RouteStrategy strategy) { this.strategy = strategy; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    /**
     * 路由策略
     */
    enum RouteStrategy {
        DIRECT,      // 直接路由
        ROUND_ROBIN, // 轮询
        RANDOM,      // 随机
        WEIGHTED,    // 加权
        LEAST_LOAD   // 最小负载
    }

    /**
     * 路由结果
     */
    class RouteResult {
        private String routeId;
        private List<AgentInfo> selectedAgents;
        private RouteStrategy usedStrategy;
        private java.util.Map<String, Object> routingMetadata;

        // Getters and Setters
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
        public List<AgentInfo> getSelectedAgents() { return selectedAgents; }
        public void setSelectedAgents(List<AgentInfo> selectedAgents) { this.selectedAgents = selectedAgents; }
        public RouteStrategy getUsedStrategy() { return usedStrategy; }
        public void setUsedStrategy(RouteStrategy usedStrategy) { this.usedStrategy = usedStrategy; }
        public java.util.Map<String, Object> getRoutingMetadata() { return routingMetadata; }
        public void setRoutingMetadata(java.util.Map<String, Object> routingMetadata) { this.routingMetadata = routingMetadata; }
    }
}
