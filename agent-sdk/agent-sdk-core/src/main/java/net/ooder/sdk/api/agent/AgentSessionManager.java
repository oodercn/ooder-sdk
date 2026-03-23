package net.ooder.sdk.api.agent;

import java.util.Optional;

import net.ooder.skills.sync.AgentCapabilities;

public interface AgentSessionManager {

    AgentSession register(AgentSessionRegistration registration);

    AgentSession authenticate(String agentId, String credentials);

    void invalidate(String agentId);

    AgentSession getSession(String agentId);

    boolean isValid(String sessionToken);

    void heartbeat(String agentId);

    Optional<AgentSession> findByToken(String sessionToken);

    void updateCapabilities(String agentId, AgentCapabilities capabilities);

    long getSessionTimeout();

    void setSessionTimeout(long timeoutMs);
}
