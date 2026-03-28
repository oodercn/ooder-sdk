package net.ooder.scene.a2a.router;

import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.a2a.A2ARoutingRule;
import net.ooder.scene.a2a.RouteResult;
import net.ooder.scene.a2a.RouteStrategy;
import net.ooder.scene.agent.context.AgentProfile;
import net.ooder.scene.agent.context.AgentStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 消息路由器
 *
 * <p>负责 A2A 消息的路由决策，支持多种路由策略：</p>
 * <ul>
 *   <li>直接路由 - 指定目标Agent</li>
 *   <li>能力路由 - 按能力查找Agent</li>
 *   <li>角色路由 - 按角色查找Agent</li>
 *   <li>规则路由 - 按自定义规则路由</li>
 *   <li>广播 - 广播到场景组内所有Agent</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MessageRouter {

    private static final Logger log = LoggerFactory.getLogger(MessageRouter.class);

    private final Map<String, A2ARoutingRule> rules = new ConcurrentHashMap<>();
    private final RoutingTable routingTable;
    private final Map<String, AgentProfile> agentProfiles = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> capabilityIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> roleIndex = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> agentLoad = new ConcurrentHashMap<>();

    public MessageRouter() {
        this.routingTable = new RoutingTable();
    }

    public void addRule(A2ARoutingRule rule) {
        if (rule == null || rule.getRuleId() == null) {
            return;
        }
        rules.put(rule.getRuleId(), rule);
        log.info("Routing rule added: ruleId={}, pattern={}, target={}", 
                rule.getRuleId(), rule.getMessageTypePattern(), rule.getTargetAgentId());
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
        RouteResult result = routeWithResult(message);
        return result != null ? result.getTargetAgentId() : null;
    }

    public RouteResult routeWithResult(A2AMessage message) {
        if (message == null) {
            return null;
        }

        if (message.getToAgentId() != null && !message.getToAgentId().isEmpty()) {
            return RouteResult.success(message.getMessageId(), message.getToAgentId(), RouteStrategy.DIRECT);
        }

        List<A2ARoutingRule> sortedRules = rules.values().stream()
                .filter(A2ARoutingRule::isEnabled)
                .sorted(Comparator.comparingInt(A2ARoutingRule::getPriority).reversed())
                .toList();

        for (A2ARoutingRule rule : sortedRules) {
            if (rule.matches(message)) {
                RouteResult result = routeByRule(message, rule);
                if (result != null && result.isSuccess()) {
                    return result;
                }
            }
        }

        String defaultTarget = routingTable.getDefaultTarget(message.getSceneGroupId());
        if (defaultTarget != null) {
            log.debug("Message routed to default: messageId={}, target={}", 
                    message.getMessageId(), defaultTarget);
            return RouteResult.success(message.getMessageId(), defaultTarget, RouteStrategy.DEFAULT);
        }

        log.warn("No route found for message: messageId={}, type={}", 
                message.getMessageId(), message.getMessageType());
        return RouteResult.noRoute(message.getMessageId());
    }

    private RouteResult routeByRule(A2AMessage message, A2ARoutingRule rule) {
        if (rule.hasTargetAgentId()) {
            log.debug("Message routed by rule to agent: messageId={}, ruleId={}, target={}", 
                    message.getMessageId(), rule.getRuleId(), rule.getTargetAgentId());
            return RouteResult.success(message.getMessageId(), rule.getTargetAgentId(), RouteStrategy.RULE);
        }

        if (rule.hasTargetCapability()) {
            List<AgentProfile> agents = findByCapability(message.getSceneGroupId(), rule.getTargetCapability());
            if (!agents.isEmpty()) {
                AgentProfile selected = selectByLoad(agents);
                log.debug("Message routed by rule to capability: messageId={}, capability={}, target={}", 
                        message.getMessageId(), rule.getTargetCapability(), selected.getAgentId());
                return RouteResult.success(message.getMessageId(), selected.getAgentId(), RouteStrategy.CAPABILITY);
            }
            return RouteResult.noAgentFound(message.getMessageId(), rule.getTargetCapability());
        }

        if (rule.hasTargetRole()) {
            List<AgentProfile> agents = findByRole(message.getSceneGroupId(), rule.getTargetRole());
            if (!agents.isEmpty()) {
                AgentProfile selected = selectByLoad(agents);
                log.debug("Message routed by rule to role: messageId={}, role={}, target={}", 
                        message.getMessageId(), rule.getTargetRole(), selected.getAgentId());
                return RouteResult.success(message.getMessageId(), selected.getAgentId(), RouteStrategy.ROLE);
            }
            return RouteResult.noAgentFoundForRole(message.getMessageId(), rule.getTargetRole());
        }

        return null;
    }

    public List<AgentProfile> findByCapability(String sceneGroupId, String capability) {
        if (capability == null || capability.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> agentIds = capabilityIndex.get(capability);
        if (agentIds == null || agentIds.isEmpty()) {
            return new ArrayList<>();
        }

        return agentIds.stream()
                .map(agentProfiles::get)
                .filter(Objects::nonNull)
                .filter(profile -> isAgentInScene(profile, sceneGroupId))
                .filter(this::isAgentOnline)
                .collect(Collectors.toList());
    }

    public List<AgentProfile> findByRole(String sceneGroupId, String role) {
        if (role == null || role.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> agentIds = roleIndex.get(role);
        if (agentIds == null || agentIds.isEmpty()) {
            return new ArrayList<>();
        }

        return agentIds.stream()
                .map(agentProfiles::get)
                .filter(Objects::nonNull)
                .filter(profile -> isAgentInScene(profile, sceneGroupId))
                .filter(this::isAgentOnline)
                .collect(Collectors.toList());
    }

    private boolean isAgentInScene(AgentProfile profile, String sceneGroupId) {
        if (sceneGroupId == null || sceneGroupId.isEmpty()) {
            return true;
        }
        return sceneGroupId.equals(profile.getSceneGroupId());
    }

    private boolean isAgentOnline(AgentProfile profile) {
        return profile.isOnline();
    }

    private AgentProfile selectByLoad(List<AgentProfile> agents) {
        if (agents.isEmpty()) {
            return null;
        }

        if (agents.size() == 1) {
            return agents.get(0);
        }

        AgentProfile selected = agents.get(0);
        int minLoad = getAgentLoad(selected.getAgentId());

        for (int i = 1; i < agents.size(); i++) {
            AgentProfile candidate = agents.get(i);
            int candidateLoad = getAgentLoad(candidate.getAgentId());
            if (candidateLoad < minLoad) {
                minLoad = candidateLoad;
                selected = candidate;
            }
        }

        return selected;
    }

    public void registerAgent(String agentId, String sceneGroupId) {
        routingTable.register(agentId, sceneGroupId);
        log.info("Agent registered in routing table: agentId={}, sceneGroupId={}", agentId, sceneGroupId);
    }

    public void registerAgent(AgentProfile profile) {
        if (profile == null || profile.getAgentId() == null) {
            return;
        }

        String agentId = profile.getAgentId();
        agentProfiles.put(agentId, profile);
        routingTable.register(agentId, profile.getSceneGroupId());

        if (profile.getCapabilities() != null) {
            for (String capability : profile.getCapabilities().keySet()) {
                capabilityIndex.computeIfAbsent(capability, k -> ConcurrentHashMap.newKeySet()).add(agentId);
            }
        }

        if (profile.getRole() != null) {
            roleIndex.computeIfAbsent(profile.getRole(), k -> ConcurrentHashMap.newKeySet()).add(agentId);
        }

        agentLoad.computeIfAbsent(agentId, k -> new AtomicInteger(0));

        log.info("Agent profile registered: agentId={}, sceneGroupId={}, role={}", 
                agentId, profile.getSceneGroupId(), profile.getRole());
    }

    public void unregisterAgent(String agentId) {
        if (agentId == null) {
            return;
        }

        AgentProfile profile = agentProfiles.remove(agentId);
        if (profile != null) {
            if (profile.getCapabilities() != null) {
                for (String capability : profile.getCapabilities().keySet()) {
                    Set<String> agents = capabilityIndex.get(capability);
                    if (agents != null) {
                        agents.remove(agentId);
                        if (agents.isEmpty()) {
                            capabilityIndex.remove(capability);
                        }
                    }
                }
            }

            if (profile.getRole() != null) {
                Set<String> agents = roleIndex.get(profile.getRole());
                if (agents != null) {
                    agents.remove(agentId);
                    if (agents.isEmpty()) {
                        roleIndex.remove(profile.getRole());
                    }
                }
            }
        }

        routingTable.unregister(agentId);
        agentLoad.remove(agentId);

        log.info("Agent unregistered from routing table: agentId={}", agentId);
    }

    public void updateAgentProfile(AgentProfile profile) {
        if (profile == null || profile.getAgentId() == null) {
            return;
        }

        unregisterAgent(profile.getAgentId());
        registerAgent(profile);
    }

    public AgentProfile getAgentProfile(String agentId) {
        return agentProfiles.get(agentId);
    }

    public List<String> getAgentsInScene(String sceneGroupId) {
        return routingTable.getAgentsInScene(sceneGroupId);
    }

    public List<AgentProfile> getOnlineAgentsInScene(String sceneGroupId) {
        return routingTable.getAgentsInScene(sceneGroupId).stream()
                .map(agentProfiles::get)
                .filter(Objects::nonNull)
                .filter(this::isAgentOnline)
                .collect(Collectors.toList());
    }

    public void setDefaultTarget(String sceneGroupId, String agentId) {
        routingTable.setDefaultTarget(sceneGroupId, agentId);
        log.info("Default target set: sceneGroupId={}, agentId={}", sceneGroupId, agentId);
    }

    public void incrementAgentLoad(String agentId) {
        AtomicInteger load = agentLoad.get(agentId);
        if (load != null) {
            load.incrementAndGet();
        }
    }

    public void decrementAgentLoad(String agentId) {
        AtomicInteger load = agentLoad.get(agentId);
        if (load != null) {
            load.decrementAndGet();
        }
    }

    public int getAgentLoad(String agentId) {
        AtomicInteger load = agentLoad.get(agentId);
        return load != null ? load.get() : 0;
    }

    public RoutingStats getStats() {
        RoutingStats stats = new RoutingStats();
        stats.setRuleCount(rules.size());
        stats.setAgentCount(routingTable.getAgentCount());
        stats.setSceneCount(routingTable.getSceneCount());
        stats.setOnlineAgentCount((int) agentProfiles.values().stream().filter(this::isAgentOnline).count());
        stats.setCapabilityCount(capabilityIndex.size());
        stats.setRoleCount(roleIndex.size());
        return stats;
    }

    public static class RoutingStats {
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
