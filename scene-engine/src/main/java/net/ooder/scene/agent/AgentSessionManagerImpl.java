package net.ooder.scene.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentSessionManagerImpl implements AgentSessionManager {

    private static final Logger log = LoggerFactory.getLogger(AgentSessionManagerImpl.class);

    private static final long DEFAULT_SESSION_TIMEOUT = 30 * 60 * 1000L;
    private static final int TOKEN_LENGTH = 32;

    private final Map<String, AgentSession> sessionByAgentId = new ConcurrentHashMap<>();
    private final Map<String, AgentSession> sessionByToken = new ConcurrentHashMap<>();
    private final Map<String, String> credentialsStore = new ConcurrentHashMap<>();

    private final SecureRandom secureRandom = new SecureRandom();
    private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

    @Override
    public AgentSession register(AgentRegistration registration) {
        if (registration == null || registration.getAgentId() == null) {
            throw new IllegalArgumentException("Registration and agentId are required");
        }

        String agentId = registration.getAgentId();

        if (sessionByAgentId.containsKey(agentId)) {
            log.warn("Agent already registered, invalidating previous session: agentId={}", agentId);
            invalidate(agentId);
        }

        String sessionToken = generateSessionToken();
        AgentSession session = new AgentSession(agentId, sessionToken);
        session.setExpireTime(System.currentTimeMillis() + sessionTimeout);

        if (registration.getCredentials() != null) {
            credentialsStore.put(agentId, registration.getCredentials());
        }

        if (registration.getCapabilities() != null) {
            session.setAttribute("capabilities", registration.getCapabilities());
        }

        if (registration.getMaxConcurrentTasks() > 0) {
            session.setAttribute("maxConcurrentTasks", registration.getMaxConcurrentTasks());
        }

        if (registration.getMetadata() != null) {
            session.getAttributes().putAll(registration.getMetadata());
        }

        sessionByAgentId.put(agentId, session);
        sessionByToken.put(sessionToken, session);

        log.info("Agent registered: agentId={}, token={}", agentId, maskToken(sessionToken));

        return session;
    }

    @Override
    public AgentSession authenticate(String agentId, String credentials) {
        if (agentId == null || credentials == null) {
            return null;
        }

        String storedCredentials = credentialsStore.get(agentId);
        if (storedCredentials == null || !storedCredentials.equals(credentials)) {
            log.warn("Authentication failed: agentId={}", agentId);
            return null;
        }

        AgentSession session = sessionByAgentId.get(agentId);
        if (session == null || session.isExpired()) {
            log.warn("Session not found or expired: agentId={}", agentId);
            return null;
        }

        session.touch();
        log.info("Agent authenticated: agentId={}", agentId);

        return session;
    }

    @Override
    public void invalidate(String agentId) {
        if (agentId == null) {
            return;
        }

        AgentSession session = sessionByAgentId.remove(agentId);
        if (session != null) {
            sessionByToken.remove(session.getSessionToken());
            session.setStatus(AgentStatus.OFFLINE);
            log.info("Agent session invalidated: agentId={}", agentId);
        }
    }

    @Override
    public AgentSession getSession(String agentId) {
        if (agentId == null) {
            return null;
        }

        AgentSession session = sessionByAgentId.get(agentId);
        if (session != null && session.isExpired()) {
            invalidate(agentId);
            return null;
        }

        return session;
    }

    @Override
    public boolean isValid(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        AgentSession session = sessionByToken.get(sessionToken);
        if (session == null) {
            return false;
        }

        if (session.isExpired()) {
            invalidate(session.getAgentId());
            return false;
        }

        return session.isValid();
    }

    @Override
    public void heartbeat(String agentId) {
        if (agentId == null) {
            return;
        }

        AgentSession session = sessionByAgentId.get(agentId);
        if (session != null) {
            session.touch();
            session.setExpireTime(System.currentTimeMillis() + sessionTimeout);

            if (session.getStatus() == AgentStatus.OFFLINE) {
                session.setStatus(AgentStatus.ONLINE);
            }

            log.debug("Agent heartbeat: agentId={}, lastHeartbeat={}", agentId, session.getLastHeartbeat());
        }
    }

    @Override
    public void updateStatus(String agentId, AgentStatus status) {
        if (agentId == null || status == null) {
            return;
        }

        AgentSession session = sessionByAgentId.get(agentId);
        if (session != null) {
            session.setStatus(status);
            log.info("Agent status updated: agentId={}, status={}", agentId, status);
        }
    }

    private String generateSessionToken() {
        byte[] bytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }

    public void setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getSessionCount() {
        return sessionByAgentId.size();
    }

    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        sessionByAgentId.values().removeIf(session -> {
            if (session.isExpired()) {
                sessionByToken.remove(session.getSessionToken());
                log.info("Cleaned up expired session: agentId={}", session.getAgentId());
                return true;
            }
            return false;
        });
    }
}
