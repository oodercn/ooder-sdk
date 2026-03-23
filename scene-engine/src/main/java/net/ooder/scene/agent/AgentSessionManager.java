package net.ooder.scene.agent;

public interface AgentSessionManager {

    AgentSession register(AgentRegistration registration);

    AgentSession authenticate(String agentId, String credentials);

    void invalidate(String agentId);

    AgentSession getSession(String agentId);

    boolean isValid(String sessionToken);

    void heartbeat(String agentId);

    void updateStatus(String agentId, AgentStatus status);
}
