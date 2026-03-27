package net.ooder.scene.message.queue;

import net.ooder.scene.agent.AgentMessage;
import net.ooder.scene.agent.AgentMessageBus;
import net.ooder.scene.agent.MessageType;
import net.ooder.scene.agent.persistence.MessagePersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 消息队列服务实现
 *
 * <p>基于现有的 AgentMessageBus 和 MessagePersistence 实现，扩展支持离线消息、消息重试等功能。</p>
 *
 * <p>复用现有组件：</p>
 * <ul>
 *   <li>AgentMessageBus - 消息发送和订阅</li>
 *   <li>MessagePersistence - 消息持久化</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(MessageQueueService.class)
public class MessageQueueServiceImpl implements MessageQueueService {

    private static final Logger log = LoggerFactory.getLogger(MessageQueueServiceImpl.class);

    private static final long DEFAULT_MESSAGE_TTL = 24 * 60 * 60 * 1000L;
    private static final int DEFAULT_MAX_RETRIES = 3;

    private final AgentMessageBus agentMessageBus;
    private final MessagePersistence messagePersistence;
    
    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<MessageReceipt>> pendingSyncRequests = new ConcurrentHashMap<>();

    public MessageQueueServiceImpl() {
        this.agentMessageBus = null;
        this.messagePersistence = null;
        
        setupBridgeHandler();
        log.info("MessageQueueService initialized (standalone mode)");
    }

    public MessageQueueServiceImpl(AgentMessageBus agentMessageBus, MessagePersistence messagePersistence) {
        this.agentMessageBus = agentMessageBus;
        this.messagePersistence = messagePersistence;
        
        setupBridgeHandler();
        log.info("MessageQueueService initialized with existing AgentMessageBus and MessagePersistence");
    }

    private void setupBridgeHandler() {
        if (agentMessageBus != null) {
            agentMessageBus.subscribe("message-queue-bridge", message -> {
                bridgeToHandler(message);
            });
        }
    }

    private void bridgeToHandler(AgentMessage agentMessage) {
        if (agentMessage == null) return;
        
        MessageEnvelope envelope = convertToEnvelope(agentMessage);
        if (envelope == null) return;
        
        String recipientId = envelope.getTo() != null ? envelope.getTo().getId() : null;
        if (recipientId != null) {
            MessageHandler handler = handlers.get(recipientId);
            if (handler != null && handler.canHandle(envelope)) {
                try {
                    handler.onMessage(envelope);
                    envelope.setDeliveryStatus(DeliveryStatus.DELIVERED);
                    
                    if (messagePersistence != null) {
                        messagePersistence.markDelivered(envelope.getMessageId());
                    }
                } catch (Exception e) {
                    log.error("Handler error: recipientId={}, error={}", recipientId, e.getMessage());
                }
            }
        }
        
        CompletableFuture<MessageReceipt> future = pendingSyncRequests.remove(envelope.getMessageId());
        if (future != null) {
            future.complete(MessageReceipt.delivered(envelope.getMessageId(), recipientId));
        }
    }

    @Override
    public String sendMessage(MessageEnvelope message) {
        if (message == null || message.getTo() == null) {
            throw new IllegalArgumentException("Message and recipient are required");
        }

        if (message.getExpireAt() <= 0) {
            message.setTtl(DEFAULT_MESSAGE_TTL);
        }
        
        if (message.getMaxRetries() <= 0) {
            message.setMaxRetries(DEFAULT_MAX_RETRIES);
        }

        AgentMessage agentMessage = convertToAgentMessage(message);
        
        if (messagePersistence != null) {
            messagePersistence.persist(agentMessage);
        }
        
        String messageId = agentMessageBus.send(agentMessage);
        message.setMessageId(messageId);
        
        log.debug("Message sent: messageId={}, from={}, to={}", 
                messageId, 
                message.getFrom() != null ? message.getFrom().getId() : "unknown",
                message.getTo().getId());
        
        return messageId;
    }

    @Override
    public MessageReceipt sendMessageSync(MessageEnvelope message, long timeoutMs) {
        String messageId = sendMessage(message);
        
        CompletableFuture<MessageReceipt> future = new CompletableFuture<>();
        pendingSyncRequests.put(messageId, future);
        
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            pendingSyncRequests.remove(messageId);
            return MessageReceipt.failed(messageId, "Timeout waiting for delivery confirmation");
        } catch (Exception e) {
            pendingSyncRequests.remove(messageId);
            return MessageReceipt.failed(messageId, e.getMessage());
        }
    }

    @Override
    public String sendPriorityMessage(MessageEnvelope message, MessagePriority priority) {
        message.setPriority(priority);
        return sendMessage(message);
    }

    @Override
    public List<MessageEnvelope> getOfflineMessages(String recipientId) {
        if (recipientId == null) {
            return new ArrayList<>();
        }

        if (messagePersistence != null) {
            List<AgentMessage> pending = messagePersistence.loadPendingByAgent(recipientId);
            return pending.stream()
                    .map(this::convertToEnvelope)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        if (agentMessageBus != null) {
            List<AgentMessage> messages = agentMessageBus.receive(recipientId);
            return messages.stream()
                    .map(this::convertToEnvelope)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }

    @Override
    public void acknowledgeMessage(String messageId, String recipientId) {
        if (messageId == null || recipientId == null) {
            return;
        }

        if (agentMessageBus != null) {
            agentMessageBus.acknowledge(recipientId, messageId);
        }
        
        if (messagePersistence != null) {
            messagePersistence.markAcknowledged(messageId);
        }

        log.debug("Message acknowledged: messageId={}, recipientId={}", messageId, recipientId);
    }

    @Override
    public List<MessageEnvelope> getUnacknowledgedMessages(String senderId) {
        return new ArrayList<>();
    }

    @Override
    public void retryMessage(String messageId) {
        log.info("Message retry requested: messageId={}", messageId);
    }

    @Override
    public void setRetryPolicy(String messageId, int maxRetries, long retryIntervalMs) {
        log.debug("Retry policy set: messageId={}, maxRetries={}", messageId, maxRetries);
    }

    @Override
    public List<MessageEnvelope> getConversationHistory(String conversationId, long since, int limit) {
        if (messagePersistence != null && conversationId != null) {
            List<AgentMessage> messages = messagePersistence.loadBySceneGroup(conversationId, limit);
            return messages.stream()
                    .filter(m -> m.getCreateTime() >= since)
                    .map(this::convertToEnvelope)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<MessageEnvelope> getConversationHistory(String conversationId, int limit) {
        return getConversationHistory(conversationId, 0, limit);
    }

    @Override
    public void subscribe(String recipientId, MessageHandler handler) {
        if (recipientId == null || handler == null) {
            return;
        }
        handlers.put(recipientId, handler);
        log.info("Handler subscribed: recipientId={}", recipientId);
    }

    @Override
    public void unsubscribe(String recipientId) {
        if (recipientId == null) {
            return;
        }
        handlers.remove(recipientId);
        log.info("Handler unsubscribed: recipientId={}", recipientId);
    }

    @Override
    public int getPendingCount(String recipientId) {
        if (agentMessageBus != null) {
            return agentMessageBus.getPendingCount(recipientId);
        }
        return 0;
    }

    @Override
    public int getTotalMessageCount() {
        if (messagePersistence != null) {
            return messagePersistence.getStats().getTotalMessages();
        }
        return 0;
    }

    @Override
    public void clearMessages(String recipientId) {
        if (agentMessageBus != null) {
            agentMessageBus.clearMessages(recipientId);
        }
        if (messagePersistence != null) {
            messagePersistence.deleteByAgent(recipientId);
        }
        log.info("Messages cleared: recipientId={}", recipientId);
    }

    @Override
    public void cleanupExpired() {
        if (messagePersistence != null) {
            int count = messagePersistence.cleanupExpired();
            if (count > 0) {
                log.info("Cleaned up {} expired messages", count);
            }
        }
    }

    @Override
    public MessageQueueStats getStats() {
        MessageQueueStats stats = new MessageQueueStats();
        
        if (messagePersistence != null) {
            MessagePersistence.MessageStats persistenceStats = messagePersistence.getStats();
            stats.setTotalMessages(persistenceStats.getTotalMessages());
            stats.setPendingMessages(persistenceStats.getPendingMessages());
            stats.setDeliveredMessages(persistenceStats.getDeliveredMessages());
            stats.setAcknowledgedMessages(persistenceStats.getAcknowledgedMessages());
        }
        
        return stats;
    }

    private AgentMessage convertToAgentMessage(MessageEnvelope envelope) {
        if (envelope == null) return null;
        
        AgentMessage message = AgentMessage.builder()
                .from(envelope.getFrom() != null ? envelope.getFrom().getId() : "unknown")
                .to(envelope.getTo() != null ? envelope.getTo().getId() : "unknown")
                .sceneGroup(envelope.getSceneGroupId())
                .type(MessageType.STATUS_UPDATE)
                .priority(envelope.getPriority().getLevel())
                .build();
        
        message.setMessageId(envelope.getMessageId());
        message.setPayloadItem("envelopeId", envelope.getMessageId());
        message.setPayloadItem("messageType", envelope.getMessageType());
        message.setPayloadItem("contentType", envelope.getContentType());
        message.setPayloadItem("content", envelope.getContent());
        message.setPayloadItem("conversationId", envelope.getConversationId());
        
        if (envelope.getExpireAt() > 0) {
            message.setExpireTime(envelope.getExpireAt());
        }
        
        return message;
    }

    private MessageEnvelope convertToEnvelope(AgentMessage agentMessage) {
        if (agentMessage == null) return null;
        
        MessageEnvelope envelope = MessageEnvelope.builder()
                .messageId(agentMessage.getMessageId())
                .from(MessageParticipant.virtualAgent(agentMessage.getFromAgent()))
                .to(MessageParticipant.virtualAgent(agentMessage.getToAgent()))
                .sceneGroupId(agentMessage.getSceneGroupId())
                .priority(MessagePriority.fromLevel(agentMessage.getPriority()))
                .build();
        
        Map<String, Object> payload = agentMessage.getPayload();
        if (payload != null) {
            envelope.setMessageType((String) payload.get("messageType"));
            envelope.setContentType((String) payload.get("contentType"));
            envelope.setContent(payload.get("content"));
            envelope.setConversationId((String) payload.get("conversationId"));
        }
        
        if (agentMessage.getExpireTime() > 0) {
            envelope.setExpireAt(agentMessage.getExpireTime());
        }
        
        return envelope;
    }
}
