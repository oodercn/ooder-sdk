package net.ooder.scene.session.unified;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.session.SessionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一会话管理器实现
 *
 * <p>整合现有的 SessionManager 和 AgentSessionManager 能力。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class UnifiedSessionManagerImpl implements UnifiedSessionManager {

    private static final Logger log = LoggerFactory.getLogger(UnifiedSessionManagerImpl.class);

    private static final long DEFAULT_TTL = 24 * 60 * 60 * 1000L;

    private final SessionStorage storage;
    private final Map<String, OnlineStatus> onlineStatusMap = new ConcurrentHashMap<>();
    private SceneEventPublisher eventPublisher;
    private long defaultTtl = DEFAULT_TTL;

    public UnifiedSessionManagerImpl() {
        this.storage = new JsonSessionStorage();
        ((JsonSessionStorage) this.storage).init();
    }

    public UnifiedSessionManagerImpl(SessionStorage storage) {
        this.storage = storage;
    }

    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UnifiedSession createSession(SessionType type, String ownerId, Map<String, Object> metadata) {
        return createSession(type, ownerId, null, metadata);
    }

    @Override
    public UnifiedSession createSession(SessionType type, String ownerId, String sceneGroupId, Map<String, Object> metadata) {
        String sessionId = generateSessionId(type, ownerId);
        
        UnifiedSession session = UnifiedSession.builder()
                .sessionId(sessionId)
                .type(type)
                .ownerId(ownerId)
                .sceneGroupId(sceneGroupId)
                .status(OnlineStatus.ONLINE)
                .ttl(defaultTtl)
                .build();
        
        if (metadata != null) {
            metadata.forEach(session::setMetadata);
        }
        
        configureSessionByType(session, type, metadata);
        
        storage.save(session);
        onlineStatusMap.put(ownerId, OnlineStatus.ONLINE);
        
        publishSessionEvent(SessionEvent.created(this, sessionId, ownerId));
        
        log.info("Session created: sessionId={}, type={}, ownerId={}", sessionId, type, ownerId);
        return session;
    }

    private void configureSessionByType(UnifiedSession session, SessionType type, Map<String, Object> metadata) {
        switch (type) {
            case AGENT:
                boolean isVirtual = metadata != null && Boolean.TRUE.equals(metadata.get("isVirtual"));
                session.setVirtual(isVirtual);
                session.setRequireHeartbeat(!isVirtual);
                break;
            case SCENE:
                session.setRequireHeartbeat(false);
                break;
            case CONVERSATION:
                session.setRequireHeartbeat(false);
                break;
            default:
                session.setRequireHeartbeat(true);
        }
    }

    private String generateSessionId(SessionType type, String ownerId) {
        return type.getCode() + "_" + ownerId + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public UnifiedSession getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        
        Optional<UnifiedSession> session = storage.load(sessionId);
        if (session.isPresent()) {
            UnifiedSession s = session.get();
            if (s.isExpired()) {
                invalidateSession(sessionId);
                return null;
            }
            return s;
        }
        return null;
    }

    @Override
    public List<UnifiedSession> getSessionsByOwner(String ownerId) {
        return storage.findByOwner(ownerId);
    }

    @Override
    public void updateSession(String sessionId, Map<String, Object> updates) {
        UnifiedSession session = getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        if (updates != null) {
            updates.forEach(session::setMetadata);
        }
        
        session.touch();
        storage.update(session);
        
        log.debug("Session updated: sessionId={}", sessionId);
    }

    @Override
    public void invalidateSession(String sessionId) {
        UnifiedSession session = getSession(sessionId);
        if (session == null) {
            return;
        }
        
        session.setStatus(OnlineStatus.OFFLINE);
        storage.update(session);
        
        String ownerId = session.getOwnerId();
        boolean hasOtherActiveSessions = storage.findByOwner(ownerId).stream()
                .anyMatch(s -> !s.getSessionId().equals(sessionId) && s.isValid());
        
        if (!hasOtherActiveSessions) {
            onlineStatusMap.put(ownerId, OnlineStatus.OFFLINE);
        }
        
        storage.delete(sessionId);
        
        publishSessionEvent(SessionEvent.destroyed(this, sessionId, ownerId));
        
        log.info("Session invalidated: sessionId={}", sessionId);
    }

    @Override
    public boolean isValid(String sessionId) {
        UnifiedSession session = getSession(sessionId);
        return session != null && session.isValid();
    }

    @Override
    public void refreshSession(String sessionId) {
        refreshSession(sessionId, defaultTtl);
    }

    @Override
    public void refreshSession(String sessionId, long ttlMs) {
        UnifiedSession session = getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        
        session.refresh(ttlMs);
        storage.update(session);
        
        log.debug("Session refreshed: sessionId={}, newExpireAt={}", sessionId, session.getExpireAt());
    }

    @Override
    public OnlineStatus getOnlineStatus(String ownerId) {
        return onlineStatusMap.getOrDefault(ownerId, OnlineStatus.OFFLINE);
    }

    @Override
    public void setOnlineStatus(String ownerId, OnlineStatus status) {
        onlineStatusMap.put(ownerId, status);
        
        storage.findByOwner(ownerId).forEach(session -> {
            session.setStatus(status);
            storage.update(session);
        });
        
        log.debug("Online status updated: ownerId={}, status={}", ownerId, status);
    }

    @Override
    public List<UnifiedSession> getActiveSessionsByScene(String sceneGroupId) {
        return storage.findBySceneGroup(sceneGroupId);
    }

    @Override
    public List<UnifiedSession> getActiveSessionsByType(SessionType type) {
        return storage.findByType(type);
    }

    @Override
    public void heartbeat(String sessionId) {
        UnifiedSession session = getSession(sessionId);
        if (session == null) {
            return;
        }
        
        session.touch();
        session.setStatus(OnlineStatus.ONLINE);
        storage.update(session);
        onlineStatusMap.put(session.getOwnerId(), OnlineStatus.ONLINE);
        
        log.debug("Heartbeat received: sessionId={}", sessionId);
    }

    @Override
    public void cleanupExpired() {
        storage.cleanupExpired();
        
        onlineStatusMap.entrySet().removeIf(entry -> {
            List<UnifiedSession> sessions = storage.findByOwner(entry.getKey());
            return sessions.isEmpty();
        });
        
        log.debug("Expired sessions cleaned up");
    }

    @Override
    public long getSessionCount() {
        return storage.count();
    }

    @Override
    public long getActiveSessionCount() {
        return storage.findActiveSessions().size();
    }

    @Override
    public void setDefaultTtl(long ttlMs) {
        this.defaultTtl = ttlMs;
    }

    @Override
    public long getDefaultTtl() {
        return defaultTtl;
    }

    private void publishSessionEvent(SessionEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publishSessionEvent(event);
        }
    }
}
