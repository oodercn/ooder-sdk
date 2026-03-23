package net.ooder.scene.agent;

import net.ooder.scene.agent.persistence.MessagePersistence;
import net.ooder.scene.agent.security.MessageSecurityService;
import net.ooder.scene.agent.security.SecureMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class SecureAgentMessageBus implements AgentMessageBus {

    private static final Logger log = LoggerFactory.getLogger(SecureAgentMessageBus.class);

    private static final int MAX_QUEUE_SIZE = 1000;
    private static final long DEFAULT_MESSAGE_TTL = 60 * 60 * 1000L;

    private final Map<String, PriorityBlockingQueue<AgentMessage>> messageQueues = new ConcurrentHashMap<>();
    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> acknowledgedMessages = new ConcurrentHashMap<>();

    private MessagePersistence persistence;
    private MessageSecurityService securityService;

    @Value("${scene.engine.message.persistence.enabled:true}")
    private boolean persistenceEnabled;

    @Value("${scene.engine.message.security.enabled:true}")
    private boolean securityEnabled;

    public SecureAgentMessageBus() {
    }

    @Autowired(required = false)
    public void setPersistence(MessagePersistence persistence) {
        this.persistence = persistence;
    }

    @Autowired(required = false)
    public void setSecurityService(MessageSecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public String send(AgentMessage message) {
        if (message == null || message.getToAgent() == null) {
            throw new IllegalArgumentException("Message and toAgent are required");
        }

        String toAgent = message.getToAgent();

        if (message.getExpireTime() <= 0) {
            message.setExpireTime(System.currentTimeMillis() + DEFAULT_MESSAGE_TTL);
        }

        if (securityEnabled && securityService != null) {
            SecureMessage secureMessage = securityService.sign(message);
            log.debug("Message signed: messageId={}", message.getMessageId());
        }

        if (persistenceEnabled && persistence != null) {
            persistence.persist(message);
            log.debug("Message persisted: messageId={}", message.getMessageId());
        }

        PriorityBlockingQueue<AgentMessage> queue = messageQueues.computeIfAbsent(
                toAgent,
                k -> new PriorityBlockingQueue<>(100, this::compareMessages)
        );

        if (queue.size() >= MAX_QUEUE_SIZE) {
            log.warn("Message queue full for agent: {}, dropping oldest messages", toAgent);
            queue.poll();
        }

        queue.offer(message);

        log.info("Message sent: messageId={}, from={}, to={}, type={}",
                message.getMessageId(), message.getFromAgent(), toAgent, message.getType());

        notifyHandler(toAgent, message);

        return message.getMessageId();
    }

    @Override
    public List<AgentMessage> receive(String agentId) {
        if (agentId == null) {
            return Collections.emptyList();
        }

        List<AgentMessage> result = new ArrayList<>();

        if (persistenceEnabled && persistence != null) {
            List<AgentMessage> persistedMessages = persistence.loadPendingByAgent(agentId);
            result.addAll(persistedMessages);
        }

        PriorityBlockingQueue<AgentMessage> queue = messageQueues.get(agentId);
        if (queue != null && !queue.isEmpty()) {
            Set<String> acknowledged = acknowledgedMessages.computeIfAbsent(agentId, k -> ConcurrentHashMap.newKeySet());

            while (!queue.isEmpty() && result.size() < 100) {
                AgentMessage message = queue.poll();
                if (message != null) {
                    if (message.isExpired()) {
                        log.debug("Dropping expired message: messageId={}", message.getMessageId());
                        continue;
                    }
                    if (!acknowledged.contains(message.getMessageId())) {
                        result.add(message);
                    }
                }
            }
        }

        log.debug("Messages received: agentId={}, count={}", agentId, result.size());

        return result;
    }

    @Override
    public void subscribe(String agentId, MessageHandler handler) {
        if (agentId == null || handler == null) {
            return;
        }

        handlers.put(agentId, handler);
        log.info("Handler subscribed: agentId={}", agentId);
    }

    @Override
    public void unsubscribe(String agentId) {
        if (agentId == null) {
            return;
        }

        handlers.remove(agentId);
        log.info("Handler unsubscribed: agentId={}", agentId);
    }

    @Override
    public void acknowledge(String agentId, String messageId) {
        if (agentId == null || messageId == null) {
            return;
        }

        Set<String> acknowledged = acknowledgedMessages.computeIfAbsent(agentId, k -> ConcurrentHashMap.newKeySet());
        acknowledged.add(messageId);

        PriorityBlockingQueue<AgentMessage> queue = messageQueues.get(agentId);
        if (queue != null) {
            queue.removeIf(msg -> messageId.equals(msg.getMessageId()));
        }

        if (persistenceEnabled && persistence != null) {
            persistence.markAcknowledged(messageId);
        }

        log.debug("Message acknowledged: agentId={}, messageId={}", agentId, messageId);
    }

    @Override
    public int getPendingCount(String agentId) {
        if (agentId == null) {
            return 0;
        }

        int count = 0;

        if (persistenceEnabled && persistence != null) {
            count += persistence.loadPendingByAgent(agentId).size();
        }

        PriorityBlockingQueue<AgentMessage> queue = messageQueues.get(agentId);
        if (queue != null) {
            Set<String> acknowledged = acknowledgedMessages.get(agentId);
            if (acknowledged == null) {
                count += queue.size();
            } else {
                count += (int) queue.stream()
                        .filter(msg -> !acknowledged.contains(msg.getMessageId()) && !msg.isExpired())
                        .count();
            }
        }

        return count;
    }

    @Override
    public void clearMessages(String agentId) {
        if (agentId == null) {
            return;
        }

        messageQueues.remove(agentId);
        acknowledgedMessages.remove(agentId);

        if (persistenceEnabled && persistence != null) {
            persistence.deleteByAgent(agentId);
        }

        log.info("Messages cleared: agentId={}", agentId);
    }

    public boolean verifyMessage(AgentMessage message, String signature, long timestamp) {
        if (!securityEnabled || securityService == null) {
            return true;
        }

        return securityService.verify(message, signature, timestamp);
    }

    private void notifyHandler(String agentId, AgentMessage message) {
        MessageHandler handler = handlers.get(agentId);
        if (handler != null) {
            try {
                if (handler.canHandle(message.getType())) {
                    handler.onMessage(message);
                }
            } catch (Exception e) {
                log.warn("Handler error: agentId={}, messageId={}, error={}",
                        agentId, message.getMessageId(), e.getMessage());
            }
        }
    }

    private int compareMessages(AgentMessage m1, AgentMessage m2) {
        int priorityCompare = Integer.compare(m2.getPriority(), m1.getPriority());
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        return Long.compare(m1.getCreateTime(), m2.getCreateTime());
    }

    public void cleanupExpiredMessages() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, PriorityBlockingQueue<AgentMessage>> entry : messageQueues.entrySet()) {
            entry.getValue().removeIf(msg -> {
                if (msg.isExpired()) {
                    log.debug("Cleaned up expired message: messageId={}", msg.getMessageId());
                    return true;
                }
                return false;
            });
        }

        if (persistenceEnabled && persistence != null) {
            persistence.cleanupExpired();
        }
    }

    public int getTotalMessageCount() {
        return messageQueues.values().stream()
                .mapToInt(Queue::size)
                .sum();
    }

    public Set<String> getActiveAgents() {
        return new HashSet<>(messageQueues.keySet());
    }

    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }

    public void setPersistenceEnabled(boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }
}
