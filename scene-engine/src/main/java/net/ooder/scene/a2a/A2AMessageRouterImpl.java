package net.ooder.scene.a2a;

import net.ooder.scene.a2a.router.MessageRouter;
import net.ooder.scene.agent.context.AgentProfile;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A2A消息路由器实现
 *
 * <p>提供Agent间消息的智能路由能力</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(A2AMessageRouter.class)
public class A2AMessageRouterImpl implements A2AMessageRouter {

    private final MessageRouter messageRouter;

    public A2AMessageRouterImpl() {
        this.messageRouter = new MessageRouter();
    }

    public A2AMessageRouterImpl(MessageRouter messageRouter) {
        this.messageRouter = messageRouter != null ? messageRouter : new MessageRouter();
    }

    @Override
    public RouteResult route(A2AMessage message) {
        return messageRouter.routeWithResult(message);
    }

    @Override
    public List<RouteResult> routeBatch(List<A2AMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        return messages.stream()
                .map(this::route)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentProfile> findByCapability(String sceneGroupId, String capability) {
        return messageRouter.findByCapability(sceneGroupId, capability);
    }

    @Override
    public List<AgentProfile> findByRole(String sceneGroupId, String role) {
        return messageRouter.findByRole(sceneGroupId, role);
    }

    @Override
    public void registerRule(A2ARoutingRule rule) {
        messageRouter.addRule(rule);
    }

    @Override
    public void removeRule(String ruleId) {
        messageRouter.removeRule(ruleId);
    }

    @Override
    public List<A2ARoutingRule> getRules() {
        return messageRouter.getRules();
    }

    @Override
    public void registerAgent(AgentProfile profile) {
        messageRouter.registerAgent(profile);
    }

    @Override
    public void unregisterAgent(String agentId) {
        messageRouter.unregisterAgent(agentId);
    }

    @Override
    public List<String> getAgentsInScene(String sceneGroupId) {
        return messageRouter.getAgentsInScene(sceneGroupId);
    }

    @Override
    public void setDefaultTarget(String sceneGroupId, String agentId) {
        messageRouter.setDefaultTarget(sceneGroupId, agentId);
    }

    @Override
    public RoutingStats getStats() {
        MessageRouter.RoutingStats innerStats = messageRouter.getStats();
        RoutingStats stats = new RoutingStats();
        stats.setRuleCount(innerStats.getRuleCount());
        stats.setAgentCount(innerStats.getAgentCount());
        stats.setSceneCount(innerStats.getSceneCount());
        stats.setOnlineAgentCount(innerStats.getOnlineAgentCount());
        stats.setCapabilityCount(innerStats.getCapabilityCount());
        stats.setRoleCount(innerStats.getRoleCount());
        return stats;
    }
}
