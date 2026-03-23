package net.ooder.scene.agent;

import java.util.List;
import java.util.Map;

public class AgentRegistration {

    private String agentId;
    private String agentName;
    private String agentType;
    private String credentials;
    private List<String> capabilities;
    private int maxConcurrentTasks;
    private Map<String, Object> metadata;

    public AgentRegistration() {
    }

    public AgentRegistration(String agentId, String agentName, String agentType) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentType = agentType;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public int getMaxConcurrentTasks() {
        return maxConcurrentTasks;
    }

    public void setMaxConcurrentTasks(int maxConcurrentTasks) {
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "AgentRegistration{" +
                "agentId='" + agentId + '\'' +
                ", agentName='" + agentName + '\'' +
                ", agentType='" + agentType + '\'' +
                ", capabilities=" + capabilities +
                '}';
    }
}
