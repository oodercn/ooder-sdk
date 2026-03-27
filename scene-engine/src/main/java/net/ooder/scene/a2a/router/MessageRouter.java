package net.ooder.scene.a2a.router;

import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.a2a.A2ARoutingRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息路由器
 *
 * <p>负责 A2A 消息的路由决策。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MessageRouter {

    private static final Logger log = LoggerFactory.getLogger(MessageRouter.class);

    private final Map<String, A2ARoutingRule> rules = new ConcurrentHashMap<>();
    private final RoutingTable routingTable;

    public MessageRouter() {
        this.routingTable = new RoutingTable();
    }

    public void addRule(A2ARoutingRule rule) {
        if (rule == null || rule.getRuleId() == null) {
            return;
        }
        rules.put(rule.getRuleId(), rule);
        log.info("Routing rule added: ruleId={}, pattern={}, target={}", 
                rule.getRuleId(), rule.getPattern(), rule.getTargetAgentId());
    }

    public void removeRule(String ruleId) {
        if (ruleId == null) {
            return;
        }
        A2ARoutingRule removed = rules.remove(ruleId);
        if (removed != null) {
            log.info("Routing rule removed: ruleId={}", ruleId);
        }
    }

    public List<A2ARoutingRule> getRules() {
        return new ArrayList<>(rules.values());
    }

    public String route(A2AMessage message) {
        if (message == null) {
            return null;
        }

        if (message.getToAgentId() != null && !message.getToAgentId().isEmpty()) {
            return message.getToAgentId();
        }

        List<A2ARoutingRule> sortedRules = rules.values().stream()
                .filter(A2ARoutingRule::isEnabled)
                .sorted(Comparator.comparingInt(A2ARoutingRule::getPriority).reversed())
                .toList();

        for (A2ARoutingRule rule : sortedRules) {
            if (rule.matches(message)) {
                log.debug("Message routed by rule: messageId={}, ruleId={}, target={}", 
                        message.getMessageId(), rule.getRuleId(), rule.getTargetAgentId());
                return rule.getTargetAgentId();
            }
        }

        String defaultTarget = routingTable.getDefaultTarget(message.getSceneGroupId());
        if (defaultTarget != null) {
            log.debug("Message routed to default: messageId={}, target={}", 
                    message.getMessageId(), defaultTarget);
            return defaultTarget;
        }

        log.warn("No route found for message: messageId={}, type={}", 
                message.getMessageId(), message.getMessageType());
        return null;
    }

    public void registerAgent(String agentId, String sceneGroupId) {
        routingTable.register(agentId, sceneGroupId);
        log.info("Agent registered in routing table: agentId={}, sceneGroupId={}", agentId, sceneGroupId);
    }

    public void unregisterAgent(String agentId) {
        routingTable.unregister(agentId);
        log.info("Agent unregistered from routing table: agentId={}", agentId);
    }

    public List<String> getAgentsInScene(String sceneGroupId) {
        return routingTable.getAgentsInScene(sceneGroupId);
    }

    public void setDefaultTarget(String sceneGroupId, String agentId) {
        routingTable.setDefaultTarget(sceneGroupId, agentId);
        log.info("Default target set: sceneGroupId={}, agentId={}", sceneGroupId, agentId);
    }

    public RoutingStats getStats() {
        RoutingStats stats = new RoutingStats();
        stats.setRuleCount(rules.size());
        stats.setAgentCount(routingTable.getAgentCount());
        stats.setSceneCount(routingTable.getSceneCount());
        return stats;
    }

    public static class RoutingStats {
        private int ruleCount;
        private int agentCount;
        private int sceneCount;
        
        public int getRuleCount() { return ruleCount; }
        public void setRuleCount(int ruleCount) { this.ruleCount = ruleCount; }
        
        public int getAgentCount() { return agentCount; }
        public void setAgentCount(int agentCount) { this.agentCount = agentCount; }
        
        public int getSceneCount() { return sceneCount; }
        public void setSceneCount(int sceneCount) { this.sceneCount = sceneCount; }
    }
}
