package net.ooder.scene.agent.security;

import java.util.List;
import java.util.Optional;

public interface AgentCredentialStorage {

    String saveCredential(AgentCredential credential);

    Optional<AgentCredential> loadCredential(String credentialId);

    Optional<AgentCredential> loadCredentialByAgent(String agentId, CredentialType type);

    List<AgentCredential> loadCredentialsByAgent(String agentId);

    void updateCredential(AgentCredential credential);

    void deleteCredential(String credentialId);

    void deleteCredentialsByAgent(String agentId);

    boolean validateCredential(String agentId, String plainValue, CredentialType type);

    void recordUsage(String credentialId);

    int cleanupExpiredCredentials();
}
