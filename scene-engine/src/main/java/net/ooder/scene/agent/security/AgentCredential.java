package net.ooder.scene.agent.security;

import java.time.Instant;

public class AgentCredential {

    private String credentialId;
    private String agentId;
    private CredentialType type;
    private String hashedValue;
    private String salt;
    private String hashAlgorithm;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant lastUsedAt;
    private int useCount;
    private int maxUseCount;
    private boolean active;

    public AgentCredential() {
        this.createdAt = Instant.now();
        this.useCount = 0;
        this.maxUseCount = 0;
        this.active = true;
        this.hashAlgorithm = "SHA-256";
    }

    public AgentCredential(String agentId, CredentialType type) {
        this();
        this.agentId = agentId;
        this.type = type;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public CredentialType getType() {
        return type;
    }

    public void setType(CredentialType type) {
        this.type = type;
    }

    public String getHashedValue() {
        return hashedValue;
    }

    public void setHashedValue(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public void incrementUseCount() {
        this.useCount++;
    }

    public int getMaxUseCount() {
        return maxUseCount;
    }

    public void setMaxUseCount(int maxUseCount) {
        this.maxUseCount = maxUseCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        if (!active) return false;
        if (isExpired()) return false;
        if (maxUseCount > 0 && useCount >= maxUseCount) return false;
        return true;
    }

    @Override
    public String toString() {
        return "AgentCredential{" +
                "credentialId='" + credentialId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", type=" + type +
                ", active=" + active +
                ", useCount=" + useCount +
                '}';
    }
}
