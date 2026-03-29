package net.ooder.scene.llm.session;

import net.ooder.scene.session.unified.SessionType;
import net.ooder.scene.session.unified.UnifiedSession;
import net.ooder.scene.session.unified.UnifiedSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * LLM会话服务
 * 
 * <p>集成 UnifiedSessionManager 管理会话生命周期，独立管理消息存储。</p>
 * 
 * @author ooder Team
 * @since 3.0.1
 */
public class LlmSessionService {

    private static final Logger log = LoggerFactory.getLogger(LlmSessionService.class);

    private static final String SKILL_ID_KEY = "skillId";
    private static final String TITLE_KEY = "title";
    private static final String MESSAGE_COUNT_KEY = "messageCount";

    private final UnifiedSessionManager sessionManager;
    private final LlmSessionMessageRepository messageRepository;

    public LlmSessionService(UnifiedSessionManager sessionManager, LlmSessionMessageRepository messageRepository) {
        this.sessionManager = sessionManager;
        this.messageRepository = messageRepository;
    }

    public LlmSessionService(UnifiedSessionManager sessionManager) {
        this(sessionManager, new InMemoryLlmSessionMessageRepository());
    }

    public void initialize() {
        if (messageRepository instanceof InMemoryLlmSessionMessageRepository repo) {
            repo.initialize();
        }
        log.info("LlmSessionService initialized");
    }

    public void shutdown() {
        if (messageRepository instanceof InMemoryLlmSessionMessageRepository repo) {
            repo.close();
        }
        log.info("LlmSessionService shutdown");
    }

    public UnifiedSession createSession(String userId, String skillId, String title) {
        if (userId == null || skillId == null) {
            throw new IllegalArgumentException("userId and skillId must not be null");
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(SKILL_ID_KEY, skillId);
        metadata.put(TITLE_KEY, title != null ? title : "New Chat");
        metadata.put(MESSAGE_COUNT_KEY, 0);

        UnifiedSession session = sessionManager.createSession(
            SessionType.CONVERSATION, 
            userId, 
            null, 
            metadata
        );

        log.info("Created LLM session: {} for user: {}, skill: {}", 
            session.getSessionId(), userId, skillId);
        return session;
    }

    public UnifiedSession getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionManager.getSession(sessionId);
    }

    public List<UnifiedSession> getUserSessions(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return sessionManager.getSessionsByOwner(userId).stream()
            .filter(s -> s.getType() == SessionType.CONVERSATION)
            .collect(Collectors.toList());
    }

    public List<UnifiedSession> getUserSessions(String userId, String skillId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return getUserSessions(userId).stream()
            .filter(s -> skillId.equals(s.getMetadata(SKILL_ID_KEY)))
            .collect(Collectors.toList());
    }

    public UnifiedSession updateSessionTitle(String sessionId, String title) {
        if (sessionId == null) {
            return null;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put(TITLE_KEY, title);
        sessionManager.updateSession(sessionId, updates);
        return getSession(sessionId);
    }

    public void invalidateSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        sessionManager.invalidateSession(sessionId);
        messageRepository.deleteMessagesBySessionId(sessionId);
        log.info("Invalidated session: {}", sessionId);
    }

    public LlmSessionMessage addMessage(String sessionId, String role, String content) {
        return addMessage(sessionId, role, content, null);
    }

    public LlmSessionMessage addMessage(String sessionId, String role, String content, Map<String, Object> metadata) {
        if (sessionId == null || role == null || content == null) {
            throw new IllegalArgumentException("sessionId, role and content must not be null");
        }

        UnifiedSession session = getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        LlmSessionMessage message = new LlmSessionMessage();
        message.setMessageId(generateMessageId());
        message.setSessionId(sessionId);
        message.setRole(LlmSessionMessage.MessageRole.valueOf(role.toUpperCase()));
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        if (metadata != null) {
            message.setMetadata(metadata);
        }

        messageRepository.saveMessage(message);

        Integer count = session.getMetadata(MESSAGE_COUNT_KEY);
        int newCount = (count != null ? count : 0) + 1;
        Map<String, Object> updates = new HashMap<>();
        updates.put(MESSAGE_COUNT_KEY, newCount);
        sessionManager.updateSession(sessionId, updates);

        log.debug("Added message: {} to session: {}", message.getMessageId(), sessionId);
        return message;
    }

    public List<LlmSessionMessage> getSessionMessages(String sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        return messageRepository.findMessagesBySessionId(sessionId);
    }

    public List<LlmSessionMessage> getSessionMessages(String sessionId, int limit) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        return messageRepository.findMessagesBySessionId(sessionId, limit);
    }

    public List<LlmSessionMessage> getMessagesBefore(String sessionId, long timestamp, int limit) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        return messageRepository.findMessagesBefore(sessionId, timestamp, limit);
    }

    public int getMessageCount(String sessionId) {
        if (sessionId == null) {
            return 0;
        }
        return messageRepository.countMessagesBySessionId(sessionId);
    }

    public List<Map<String, Object>> getHistoryForChat(String sessionId, int limit) {
        List<LlmSessionMessage> messages = getSessionMessages(sessionId, limit);
        return messages.stream()
            .map(m -> Map.<String, Object>of(
                "role", m.getRole().name().toLowerCase(),
                "content", m.getContent()
            ))
            .collect(Collectors.toList());
    }

    public void heartbeat(String sessionId) {
        if (sessionId != null) {
            sessionManager.heartbeat(sessionId);
        }
    }

    private String generateMessageId() {
        return "msg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public UnifiedSessionManager getSessionManager() {
        return sessionManager;
    }

    public LlmSessionMessageRepository getMessageRepository() {
        return messageRepository;
    }
}
