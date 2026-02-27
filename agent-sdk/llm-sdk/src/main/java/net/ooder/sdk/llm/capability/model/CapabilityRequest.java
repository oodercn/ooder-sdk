package net.ooder.sdk.llm.capability.model;

import net.ooder.sdk.llm.common.enums.CapabilityType;
import net.ooder.sdk.llm.common.enums.Priority;

import java.time.Duration;
import java.util.Map;

public class CapabilityRequest {
    private String agentId;
    private CapabilityType capabilityType;
    private ResourceRequirement resourceRequirement;
    private Priority priority;
    private Duration timeRequirement;
    private Map<String, Object> parameters;

    public CapabilityRequest() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public CapabilityType getCapabilityType() {
        return capabilityType;
    }

    public void setCapabilityType(CapabilityType capabilityType) {
        this.capabilityType = capabilityType;
    }

    public ResourceRequirement getResourceRequirement() {
        return resourceRequirement;
    }

    public void setResourceRequirement(ResourceRequirement resourceRequirement) {
        this.resourceRequirement = resourceRequirement;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Duration getTimeRequirement() {
        return timeRequirement;
    }

    public void setTimeRequirement(Duration timeRequirement) {
        this.timeRequirement = timeRequirement;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
