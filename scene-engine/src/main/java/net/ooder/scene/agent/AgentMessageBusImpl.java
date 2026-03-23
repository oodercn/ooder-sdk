package net.ooder.scene.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@Component
public class AgentMessageBusImpl implements AgentMessageBus {

    private static final Logger log = LoggerFactory.getLogger(AgentMessageBusImpl.class);

    private static final int MAX_QUEUE_SIZE = 1000;
    private static final long DEFAULT_MESSAGE_TTL = 60 * 60 * 1000L;

    private final Map<String, PriorityBlockingQueue<AgentMessage>> messageQueues = new ConcurrentHashMap<>();
    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> acknowledgedMessages = new ConcurrentHashMap<>();

    @Override
    public String send(AgentMessage message) {
        if (message == null || message.getToAgent() == null) {
            throw new IllegalArgumentException("Message and toAgent are required");
        }

        String toAgent = message.getToAgent();

        if (message.getExpireTime() <= 0) {
            message.setExpireTime(System.currentTimeMillis() + DEFAULT_MESSAGE_TTL);
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

        PriorityBlockingQueue<AgentMessage> queue = messageQueues.get(agentId);
        if (queue == null || queue.isEmpty()) {
            return Collections.emptyList();
        }

        List<AgentMessage> messages = new ArrayList<>();
        Set<String> acknowledged = acknowledgedMessages.computeIfAbsent(agentId, k -> ConcurrentHashMap.newKeySet());

        while (!queue.isEmpty() && messages.size() < 100) {
            AgentMessage message = queue.poll();
            if (message != null) {
                if (message.isExpired()) {
                    log.debug("Dropping expired message: messageId={}", message.getMessageId());
                    continue;
                }
                if (!acknowledged.contains(message.getMessageId())) {
                    messages.add(message);
                }
            }
        }

        log.debug("Messages received: agentId={}, count={}", agentId, messages.size());

        return messages;
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

        log.debug("Message acknowledged: agentId={}, messageId={}", agentId, messageId);
    }

    @Override
    public int getPendingCount(String agentId) {
        if (agentId == null) {
            return 0;
        }

        PriorityBlockingQueue<AgentMessage> queue = messageQueues.get(agentId);
        if (queue == null) {
            return 0;
        }

        Set<String> acknowledged = acknowledgedMessages.get(agentId);
        if (acknowledged == null) {
            return queue.size();
        }

        return (int) queue.stream()
                .filter(msg -> !acknowledged.contains(msg.getMessageId()) && !msg.isExpired())
                .count();
    }

    @Override
    public void clearMessages(String agentId) {
        if (agentId == null) {
            return;
        }

        messageQueues.remove(agentId);
        acknowledgedMessages.remove(agentId);

        log.info("Messages cleared: agentId={}", agentId);
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
    }

    public int getTotalMessageCount() {
        return messageQueues.values().stream()
                .mapToInt(Queue::size)
                .sum();
    }

    public Set<String> getActiveAgents() {
        return new HashSet<>(messageQueues.keySet());
    }
}
