package net.ooder.scene.message.northbound;

import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.a2a.A2AMessageType;
import net.ooder.scene.a2a.A2AProtocolService;
import net.ooder.scene.message.queue.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 北向消息队列实现
 *
 * <p>整合 MessageQueueService 和 A2AProtocolService，提供统一的北向接口。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(NorthboundMessageQueue.class)
public class NorthboundMessageQueueImpl implements NorthboundMessageQueue {

    private static final Logger log = LoggerFactory.getLogger(NorthboundMessageQueueImpl.class);

    private final MessageQueueService messageQueueService;
    private final A2AProtocolService a2aProtocolService;
    
    private final Map<String, NorthboundMessageHandler> userHandlers = new ConcurrentHashMap<>();
    private final Map<String, NorthboundMessageHandler> agentHandlers = new ConcurrentHashMap<>();
    
    private final AtomicInteger p2aCounter = new AtomicInteger(0);
    private final AtomicInteger p2pCounter = new AtomicInteger(0);

    public NorthboundMessageQueueImpl(MessageQueueService messageQueueService, A2AProtocolService a2aProtocolService) {
        this.messageQueueService = messageQueueService;
        this.a2aProtocolService = a2aProtocolService;
        
        setupInternalHandlers();
    }

    private void setupInternalHandlers() {
        if (messageQueueService != null) {
            messageQueueService.subscribe("northbound-router", message -> {
                routeInternal(message);
            });
        }
    }

    private void routeInternal(MessageEnvelope message) {
        if (message == null || message.getTo() == null) {
            return;
        }
        
        String recipientId = message.getTo().getId();
        NorthboundMessageHandler handler = null;
        
        if (message.getTo().isUser()) {
            handler = userHandlers.get(recipientId);
        } else if (message.getTo().isAgent()) {
            handler = agentHandlers.get(recipientId);
        }
        
        if (handler != null && handler.canHandle(message)) {
            try {
                handler.onMessage(message);
            } catch (Exception e) {
                log.error("Northbound handler error: recipientId={}, error={}", recipientId, e.getMessage());
            }
        }
    }

    @Override
    public String sendToAgent(String userId, String agentId, Object content) {
        return sendToAgent(userId, agentId, content, null);
    }

    @Override
    public String sendToAgent(String userId, String agentId, Object content, String conversationId) {
        if (userId == null || agentId == null) {
            throw new IllegalArgumentException("userId and agentId are required");
        }

        MessageEnvelope envelope = MessageEnvelope.builder()
                .conversationId(conversationId)
                .from(MessageParticipant.user(userId))
                .to(MessageParticipant.virtualAgent(agentId))
                .messageType("p2a")
                .contentType("application/json")
                .content(content)
                .priority(MessagePriority.NORMAL)
                .build();
        
        String messageId = messageQueueService.sendMessage(envelope);
        
        if (a2aProtocolService != null) {
            A2AMessage a2aMessage = A2AMessage.builder()
                    .messageId(messageId)
                    .conversationId(conversationId)
                    .from(userId)
                    .to(agentId)
                    .type(A2AMessageType.CHAT)
                    .payload(content)
                    .build();
            
            a2aProtocolService.sendMessage(a2aMessage);
        }
        
        p2aCounter.incrementAndGet();
        
        log.debug("P2A message sent: messageId={}, userId={}, agentId={}", messageId, userId, agentId);
        
        return messageId;
    }

    @Override
    public CompletableFuture<MessageReceipt> sendToAgentAsync(String userId, String agentId, Object content) {
        return CompletableFuture.supplyAsync(() -> {
            String messageId = sendToAgent(userId, agentId, content);
            return MessageReceipt.delivered(messageId, agentId);
        });
    }

    @Override
    public String sendToUser(String fromUserId, String toUserId, Object content) {
        return sendToUser(fromUserId, toUserId, content, null);
    }

    @Override
    public String sendToUser(String fromUserId, String toUserId, Object content, String conversationId) {
        if (fromUserId == null || toUserId == null) {
            throw new IllegalArgumentException("fromUserId and toUserId are required");
        }

        MessageEnvelope envelope = MessageEnvelope.builder()
                .conversationId(conversationId)
                .from(MessageParticipant.user(fromUserId))
                .to(MessageParticipant.user(toUserId))
                .messageType("p2p")
                .contentType("application/json")
                .content(content)
                .priority(MessagePriority.NORMAL)
                .build();
        
        String messageId = messageQueueService.sendMessage(envelope);
        
        p2pCounter.incrementAndGet();
        
        log.debug("P2P message sent: messageId={}, from={}, to={}", messageId, fromUserId, toUserId);
        
        return messageId;
    }

    @Override
    public CompletableFuture<MessageReceipt> sendToUserAsync(String fromUserId, String toUserId, Object content) {
        return CompletableFuture.supplyAsync(() -> {
            String messageId = sendToUser(fromUserId, toUserId, content);
            return MessageReceipt.delivered(messageId, toUserId);
        });
    }

    @Override
    public List<MessageEnvelope> getMessagesForUser(String userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        return messageQueueService.getOfflineMessages(userId);
    }

    @Override
    public List<MessageEnvelope> getMessagesForAgent(String agentId) {
        if (agentId == null) {
            return new ArrayList<>();
        }
        
        return messageQueueService.getOfflineMessages(agentId);
    }

    @Override
    public List<MessageEnvelope> getConversationMessages(String conversationId, int limit) {
        return messageQueueService.getConversationHistory(conversationId, limit);
    }

    @Override
    public void acknowledgeUserMessage(String userId, String messageId) {
        if (userId == null || messageId == null) {
            return;
        }
        messageQueueService.acknowledgeMessage(messageId, userId);
    }

    @Override
    public void acknowledgeAgentMessage(String agentId, String messageId) {
        if (agentId == null || messageId == null) {
            return;
        }
        messageQueueService.acknowledgeMessage(messageId, agentId);
    }

    @Override
    public void subscribeUser(String userId, NorthboundMessageHandler handler) {
        if (userId == null || handler == null) {
            return;
        }
        userHandlers.put(userId, handler);
        log.info("User subscribed: userId={}", userId);
    }

    @Override
    public void subscribeAgent(String agentId, NorthboundMessageHandler handler) {
        if (agentId == null || handler == null) {
            return;
        }
        agentHandlers.put(agentId, handler);
        log.info("Agent subscribed: agentId={}", agentId);
    }

    @Override
    public void unsubscribeUser(String userId) {
        if (userId == null) {
            return;
        }
        userHandlers.remove(userId);
        log.info("User unsubscribed: userId={}", userId);
    }

    @Override
    public void unsubscribeAgent(String agentId) {
        if (agentId == null) {
            return;
        }
        agentHandlers.remove(agentId);
        log.info("Agent unsubscribed: agentId={}", agentId);
    }

    @Override
    public NorthboundStats getStats() {
        NorthboundStats stats = new NorthboundStats();
        stats.setP2aMessages(p2aCounter.get());
        stats.setP2pMessages(p2pCounter.get());
        
        int pendingUser = 0;
        int pendingAgent = 0;
        
        for (String userId : userHandlers.keySet()) {
            pendingUser += messageQueueService.getPendingCount(userId);
        }
        
        for (String agentId : agentHandlers.keySet()) {
            pendingAgent += messageQueueService.getPendingCount(agentId);
        }
        
        stats.setPendingUserMessages(pendingUser);
        stats.setPendingAgentMessages(pendingAgent);
        
        return stats;
    }
}
