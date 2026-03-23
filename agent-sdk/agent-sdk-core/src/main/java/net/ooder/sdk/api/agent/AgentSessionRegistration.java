package net.ooder.sdk.api.agent;

import java.util.Map;
import java.util.UUID;

import net.ooder.skills.sync.AgentCapabilities;

public class AgentSessionRegistration {

    private String agentId;
    private String agentName;
    private String agentType;
    private String credentials;
    private String authType;
    private AgentCapabilities capabilities;
    private Map<String, Object> metadata;
    private long registrationTime;

    public AgentSessionRegistration() {
        this.agentId = UUID.randomUUID().toString();
        this.registrationTime = System.currentTimeMillis();
    }

    public AgentSessionRegistration(String agentId, String agentName, String credentials) {
        this();
        this.agentId = agentId;
        this.agentName = agentName;
        this.credentials = credentials;
    }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }

    public String getCredentials() { return credentials; }
    public void setCredentials(String credentials) { this.credentials = credentials; }

    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }

    public AgentCapabilities getCapabilities() { return capabilities; }
    public void setCapabilities(AgentCapabilities capabilities) { this.capabilities = capabilities; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public long getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(long registrationTime) { this.registrationTime = registrationTime; }
}
