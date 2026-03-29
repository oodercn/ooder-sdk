package net.ooder.scene.llm.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存实现的LLM会话消息存储
 * 
 * @author ooder Team
 * @since 3.0.1
 */
public class InMemoryLlmSessionMessageRepository implements LlmSessionMessageRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryLlmSessionMessageRepository.class);

    private final Map<String, LlmSessionMessage> messageStore = new ConcurrentHashMap<>();
    private boolean initialized = false;

    @Override
    public void initialize() {
        log.info("Initializing InMemoryLlmSessionMessageRepository");
        initialized = true;
        log.info("InMemoryLlmSessionMessageRepository initialized successfully");
    }

    @Override
    public void close() {
        messageStore.clear();
        initialized = false;
        log.info("InMemoryLlmSessionMessageRepository closed");
    }

    @Override
    public LlmSessionMessage saveMessage(LlmSessionMessage message) {
        if (message == null || message.getMessageId() == null || message.getSessionId() == null) {
            throw new IllegalArgumentException("Message, messageId and sessionId must not be null");
        }
        messageStore.put(message.getMessageId(), message);
        log.debug("Saved message: {} in session: {}", message.getMessageId(), message.getSessionId());
        return message;
    }

    @Override
    public List<LlmSessionMessage> findMessagesBySessionId(String sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        return messageStore.values().stream()
            .filter(m -> sessionId.equals(m.getSessionId()))
            .sorted(Comparator.comparingLong(LlmSessionMessage::getTimestamp))
            .collect(Collectors.toList());
    }

    @Override
    public List<LlmSessionMessage> findMessagesBySessionId(String sessionId, int limit) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        return messageStore.values().stream()
            .filter(m -> sessionId.equals(m.getSessionId()))
            .sorted(Comparator.comparingLong(LlmSessionMessage::getTimestamp).reversed())
            .limit(limit)
            .sorted(Comparator.comparingLong(LlmSessionMessage::getTimestamp))
            .collect(Collectors.toList());
    }

    @Override
    public List<LlmSessionMessage> findMessagesBefore(String sessionId, long timestamp, int limit) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        return messageStore.values().stream()
            .filter(m -> sessionId.equals(m.getSessionId()) && m.getTimestamp() < timestamp)
            .sorted(Comparator.comparingLong(LlmSessionMessage::getTimestamp).reversed())
            .limit(limit)
            .sorted(Comparator.comparingLong(LlmSessionMessage::getTimestamp))
            .collect(Collectors.toList());
    }

    @Override
    public boolean deleteMessagesBySessionId(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        messageStore.entrySet().removeIf(e -> sessionId.equals(e.getValue().getSessionId()));
        log.debug("Deleted all messages for session: {}", sessionId);
        return true;
    }

    @Override
    public int countMessagesBySessionId(String sessionId) {
        if (sessionId == null) {
            return 0;
        }
        return (int) messageStore.values().stream()
            .filter(m -> sessionId.equals(m.getSessionId()))
            .count();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getTotalMessageCount() {
        return messageStore.size();
    }
}
