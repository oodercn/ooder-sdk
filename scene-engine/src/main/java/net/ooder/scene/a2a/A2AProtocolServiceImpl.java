package net.ooder.scene.a2a;

import net.ooder.scene.a2a.mcp.MCPAgent;
import net.ooder.scene.a2a.router.MessageRouter;
import net.ooder.scene.a2a.router.RouteAgent;
import net.ooder.scene.agent.context.AgentContextManager;
import net.ooder.scene.message.queue.MessageEnvelope;
import net.ooder.scene.message.queue.MessageParticipant;
import net.ooder.scene.message.queue.MessagePriority;
import net.ooder.scene.message.queue.MessageQueueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A2A 协议服务实现
 *
 * <p>整合 MCPAgent、RouteAgent 和消息队列，提供完整的 A2A 通信能力。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(A2AProtocolService.class)
public class A2AProtocolServiceImpl implements A2AProtocolService {

    private static final Logger log = LoggerFactory.getLogger(A2AProtocolServiceImpl.class);

    private final Map<String, A2AMessageHandler> handlers = new ConcurrentHashMap<>();
    private final Map<String, A2AConversation> conversations = new ConcurrentHashMap<>();
    private final Map<String, MCPAgent> mcpAgents = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<A2AResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final AtomicInteger messageCounter = new AtomicInteger(0);
    
    private RouteAgent routeAgent;
    private AgentContextManager agentContextManager;
    private MessageQueueService messageQueueService;

    public A2AProtocolServiceImpl() {
        this.routeAgent = new RouteAgent();
    }

    public A2AProtocolServiceImpl(AgentContextManager agentContextManager, MessageQueueService messageQueueService) {
        this();
        this.agentContextManager = agentContextManager;
        this.messageQueueService = messageQueueService;
        
        if (agentContextManager != null) {
            initHandlersFromAgentManager();
        }
    }

    private void initHandlersFromAgentManager() {
        log.info("Initializing A2A handlers from AgentContextManager");
    }

    @Override
    public String sendMessage(A2AMessage message) {
        if (message == null || message.getToAgentId() == null) {
            throw new IllegalArgumentException("Message and target agent are required");
        }

        String messageId = message.getMessageId();
        
        if (messageQueueService != null) {
            toMessageQueue(message);
        }
        
        A2AMessageHandler handler = handlers.get(message.getToAgentId());
        if (handler != null) {
            try {
                if (handler.canHandle(message)) {
                    handler.handle(message);
                    log.debug("A2A message handled: messageId={}, to={}", messageId, message.getToAgentId());
                }
            } catch (Exception e) {
                log.error("Handler error: agentId={}, error={}", message.getToAgentId(), e.getMessage());
            }
        }
        
        if (routeAgent != null) {
            routeAgent.dispatch(message);
        }
        
        messageCounter.incrementAndGet();
        
        return messageId;
    }

    @Override
    public CompletableFuture<A2AResponse> sendRequest(A2ARequest request) {
        if (request == null || request.getToAgentId() == null) {
            return CompletableFuture.completedFuture(A2AResponse.failure(null, null, "INVALID_REQUEST", "Request is null or target is null"));
        }

        A2AMessage a2aMessage = request.toA2AMessage();
        
        CompletableFuture<A2AResponse> future = new CompletableFuture<>();
        pendingRequests.put(request.getRequestId(), future);
        
        try {
            sendMessage(a2aMessage);
            
            if (request.getTimeout() > 0) {
                CompletableFuture.delayedExecutor(request.getTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS)
                        .execute(() -> {
                            CompletableFuture<A2AResponse> pending = pendingRequests.remove(request.getRequestId());
                            if (pending != null && !pending.isDone()) {
                                pending.complete(A2AResponse.failure(
                                        request.getRequestId(),
                                        request.getFromAgentId(),
                                        "TIMEOUT",
                                        "Request timeout"
                                ));
                            }
                        });
            }
        } catch (Exception e) {
            pendingRequests.remove(request.getRequestId());
            future.complete(A2AResponse.failure(
                    request.getRequestId(),
                    request.getFromAgentId(),
                    "ERROR",
                    e.getMessage()
            ));
        }
        
        return future;
    }

    @Override
    public void registerHandler(String agentId, A2AMessageHandler handler) {
        if (agentId == null || handler == null) {
            return;
        }
        
        handlers.put(agentId, handler);
        
        if (routeAgent != null) {
            routeAgent.registerAgent(agentId, null, handler);
        }
        
        log.info("A2A handler registered: agentId={}", agentId);
    }

    @Override
    public void unregisterHandler(String agentId) {
        if (agentId == null) {
            return;
        }
        
        handlers.remove(agentId);
        
        if (routeAgent != null) {
            routeAgent.unregisterAgent(agentId);
        }
        
        log.info("A2A handler unregistered: agentId={}", agentId);
    }

    @Override
    public A2AConversation createConversation(String sceneGroupId, List<String> agentIds) {
        String conversationId = "conv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        
        A2AConversation conversation = new A2AConversation(conversationId, sceneGroupId);
        if (agentIds != null) {
            agentIds.forEach(conversation::addParticipant);
        }
        
        conversations.put(conversationId, conversation);
        
        log.info("A2A conversation created: conversationId={}, sceneGroupId={}, participants={}", 
                conversationId, sceneGroupId, agentIds);
        
        return conversation;
    }

    @Override
    public A2AConversation getConversation(String conversationId) {
        if (conversationId == null) {
            return null;
        }
        return conversations.get(conversationId);
    }

    @Override
    public void addToConversation(String conversationId, String agentId) {
        A2AConversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.addParticipant(agentId);
            log.debug("Agent added to conversation: conversationId={}, agentId={}", conversationId, agentId);
        }
    }

    @Override
    public void removeFromConversation(String conversationId, String agentId) {
        A2AConversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.removeParticipant(agentId);
            log.debug("Agent removed from conversation: conversationId={}, agentId={}", conversationId, agentId);
        }
    }

    @Override
    public void sendToConversation(String conversationId, A2AMessage message) {
        A2AConversation conversation = getConversation(conversationId);
        if (conversation == null) {
            log.warn("Conversation not found: {}", conversationId);
            return;
        }
        
        message.setConversationId(conversationId);
        
        conversation.addMessage(message);
        
        for (String participantId : conversation.getParticipantIds()) {
            if (!participantId.equals(message.getFromAgentId())) {
                A2AMessage copy = copyMessage(message, participantId);
                sendMessage(copy);
            }
        }
        
        log.debug("Message sent to conversation: conversationId={}, participants={}", 
                conversationId, conversation.getParticipantIds().size());
    }

    private A2AMessage copyMessage(A2AMessage original, String newToAgentId) {
        return A2AMessage.builder()
                .messageId(UUID.randomUUID().toString().replace("-", ""))
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
    public List<A2AMessage> getConversationHistory(String conversationId) {
        A2AConversation conversation = getConversation(conversationId);
        return conversation != null ? conversation.getHistory() : new ArrayList<>();
    }

    @Override
    public void addRoutingRule(A2ARoutingRule rule) {
        if (routeAgent != null) {
            routeAgent.addRoutingRule(rule);
        }
    }

    @Override
    public void removeRoutingRule(String ruleId) {
        if (routeAgent != null) {
            routeAgent.removeRoutingRule(ruleId);
        }
    }

    @Override
    public List<A2ARoutingRule> getRoutingRules() {
        return routeAgent != null ? routeAgent.getRoutingRules() : new ArrayList<>();
    }

    @Override
    public String route(A2AMessage message) {
        return routeAgent != null ? routeAgent.route(message) : null;
    }

    @Override
    public void broadcast(String sceneGroupId, A2AMessage message) {
        if (routeAgent != null) {
            routeAgent.broadcast(sceneGroupId, message);
        }
    }

    @Override
    public List<String> getActiveConversations(String sceneGroupId) {
        return conversations.values().stream()
                .filter(c -> c.isActive())
                .filter(c -> sceneGroupId == null || sceneGroupId.equals(c.getSceneGroupId()))
                .map(A2AConversation::getConversationId)
                .collect(Collectors.toList());
    }

    @Override
    public void endConversation(String conversationId) {
        A2AConversation conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.end();
            log.info("Conversation ended: conversationId={}", conversationId);
        }
    }

    @Override
    public A2AStats getStats() {
        A2AStats stats = new A2AStats();
        stats.setTotalMessages(messageCounter.get());
        stats.setActiveConversations((int) conversations.values().stream().filter(A2AConversation::isActive).count());
        stats.setRegisteredAgents(handlers.size());
        stats.setRoutingRules(getRoutingRules().size());
        return stats;
    }

    private void toMessageQueue(A2AMessage a2aMessage) {
        if (messageQueueService == null || a2aMessage == null) {
            return;
        }
        
        MessageEnvelope envelope = MessageEnvelope.builder()
                .messageId(a2aMessage.getMessageId())
                .conversationId(a2aMessage.getConversationId())
                .sceneGroupId(a2aMessage.getSceneGroupId())
                .from(MessageParticipant.virtualAgent(a2aMessage.getFromAgentId()))
                .to(MessageParticipant.virtualAgent(a2aMessage.getToAgentId()))
                .messageType(a2aMessage.getMessageType().getCode())
                .content(a2aMessage.getPayload())
                .priority(MessagePriority.NORMAL)
                .build();
        
        messageQueueService.sendMessage(envelope);
    }

    public void registerMCPAgent(String agentId) {
        MCPAgent mcpAgent = new MCPAgent(agentId);
        mcpAgents.put(agentId, mcpAgent);
        registerHandler(agentId, mcpAgent);
        log.info("MCP Agent registered: agentId={}", agentId);
    }

    public MCPAgent getMCPAgent(String agentId) {
        return mcpAgents.get(agentId);
    }

    public void setAgentContextManager(AgentContextManager agentContextManager) {
        this.agentContextManager = agentContextManager;
        initHandlersFromAgentManager();
    }

    public void setMessageQueueService(MessageQueueService messageQueueService) {
        this.messageQueueService = messageQueueService;
    }
}
