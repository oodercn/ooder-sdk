package net.ooder.scene.a2a.router;

import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.a2a.A2AMessageHandler;
import net.ooder.scene.a2a.A2ARoutingRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Route Agent
 *
 * <p>负责内部消息路由的 Agent。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class RouteAgent implements A2AMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RouteAgent.class);

    private static final String ROUTER_AGENT_ID = "router-agent";

    private final String agentId;
    private final MessageRouter router;
    private final Map<String, A2AMessageHandler> agentHandlers = new ConcurrentHashMap<>();

    public RouteAgent() {
        this.agentId = ROUTER_AGENT_ID;
        this.router = new MessageRouter();
    }

    public RouteAgent(String agentId) {
        this.agentId = agentId;
        this.router = new MessageRouter();
    }

    public String getAgentId() {
        return agentId;
    }

    public void registerAgent(String agentId, String sceneGroupId, A2AMessageHandler handler) {
        router.registerAgent(agentId, sceneGroupId);
        if (handler != null) {
            agentHandlers.put(agentId, handler);
        }
        log.info("Agent registered with router: agentId={}, sceneGroupId={}", agentId, sceneGroupId);
    }

    public void unregisterAgent(String agentId) {
        router.unregisterAgent(agentId);
        agentHandlers.remove(agentId);
        log.info("Agent unregistered from router: agentId={}", agentId);
    }

    public void addRoutingRule(A2ARoutingRule rule) {
        router.addRule(rule);
    }

    public void removeRoutingRule(String ruleId) {
        router.removeRule(ruleId);
    }

    public List<A2ARoutingRule> getRoutingRules() {
        return router.getRules();
    }

    public void setDefaultTarget(String sceneGroupId, String agentId) {
        router.setDefaultTarget(sceneGroupId, agentId);
    }

    public String route(A2AMessage message) {
        return router.route(message);
    }

    public void dispatch(A2AMessage message) {
        if (message == null) {
            return;
        }
        
        String targetAgentId = route(message);
        if (targetAgentId == null) {
            log.warn("No route found for message: messageId={}", message.getMessageId());
            return;
        }
        
        A2AMessageHandler handler = agentHandlers.get(targetAgentId);
        if (handler != null) {
            try {
                if (handler.canHandle(message)) {
                    handler.handle(message);
                    log.debug("Message dispatched: messageId={}, target={}", 
                            message.getMessageId(), targetAgentId);
                }
            } catch (Exception e) {
                log.error("Handler error: agentId={}, error={}", targetAgentId, e.getMessage());
            }
        } else {
            log.warn("No handler found for agent: agentId={}", targetAgentId);
        }
    }

    public void broadcast(String sceneGroupId, A2AMessage message) {
        if (sceneGroupId == null || message == null) {
            return;
        }
        
        List<String> agents = router.getAgentsInScene(sceneGroupId);
        for (String agentId : agents) {
            if (!agentId.equals(message.getFromAgentId())) {
                A2AMessage copy = copyMessage(message, agentId);
                dispatch(copy);
            }
        }
        
        log.debug("Message broadcast to {} agents in scene: {}", agents.size(), sceneGroupId);
    }

    private A2AMessage copyMessage(A2AMessage original, String newToAgentId) {
        return net.ooder.scene.a2a.A2AMessage.builder()
                .messageId(java.util.UUID.randomUUID().toString().replace("-", ""))
                .conversationId(original.getConversationId())
                .sceneGroupId(original.getSceneGroupId())
                .from(original.getFromAgentId())
                .to(newToAgentId)
                .type(original.getMessageType())
                .payload(original.getPayload())
                .priority(original.getPriority())
                .build();
    }

    @Override
    public void handle(A2AMessage message) {
        dispatch(message);
    }

    @Override
    public boolean canHandle(A2AMessage message) {
        return message != null;
    }

    @Override
    public String getHandlerId() {
        return "RouteAgent-" + agentId;
    }

    public MessageRouter.RoutingStats getStats() {
        return router.getStats();
    }

    public int getAgentCount() {
        return agentHandlers.size();
    }
}
