package net.ooder.sdk.api.capability;

import java.util.Map;
import java.util.Set;

public class Capability {

    private String capabilityId;
    private String name;
    private CapAddress address;
    private CapabilityType type;
    private String version;
    private String skillId;
    private String specId;
    private CapabilityStatus status;
    private String description;

    private Map<String, CapParameter> inputParams;
    private Map<String, CapParameter> outputParams;
    private Set<String> tags;
    private Map<String, Object> metadata;

    private String semanticDescription;
    private String llmUsageExample;
    private Set<String> llmCategories;
    private Map<String, String> paramDescriptions;
    private String expectedOutputDescription;

    private String nodeId;
    private long registeredTime;
    private long lastActiveTime;
    private int executionCount;
    private Map<String, Object> config;

    private long createdAt;
    private long updatedAt;

    public Capability() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.status = CapabilityStatus.DISABLED;
        this.registeredTime = System.currentTimeMillis();
        this.lastActiveTime = System.currentTimeMillis();
        this.executionCount = 0;
    }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    
    public String getCapId() { return capabilityId; }
    public void setCapId(String capId) { this.capabilityId = capId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CapAddress getAddress() { return address; }
    public void setAddress(CapAddress address) { this.address = address; }

    public CapabilityType getType() { return type; }
    public void setType(CapabilityType type) { this.type = type; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }

    public String getSpecId() { return specId; }
    public void setSpecId(String specId) { this.specId = specId; }

    public CapabilityStatus getStatus() { return status; }
    public void setStatus(CapabilityStatus status) { 
        this.status = status; 
        this.updatedAt = System.currentTimeMillis();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, CapParameter> getInputParams() { return inputParams; }
    public void setInputParams(Map<String, CapParameter> inputParams) { this.inputParams = inputParams; }

    public Map<String, CapParameter> getOutputParams() { return outputParams; }
    public void setOutputParams(Map<String, CapParameter> outputParams) { this.outputParams = outputParams; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getSemanticDescription() { return semanticDescription; }
    public void setSemanticDescription(String semanticDescription) { this.semanticDescription = semanticDescription; }

    public String getLlmUsageExample() { return llmUsageExample; }
    public void setLlmUsageExample(String llmUsageExample) { this.llmUsageExample = llmUsageExample; }

    public Set<String> getLlmCategories() { return llmCategories; }
    public void setLlmCategories(Set<String> llmCategories) { this.llmCategories = llmCategories; }

    public Map<String, String> getParamDescriptions() { return paramDescriptions; }
    public void setParamDescriptions(Map<String, String> paramDescriptions) { this.paramDescriptions = paramDescriptions; }

    public String getExpectedOutputDescription() { return expectedOutputDescription; }
    public void setExpectedOutputDescription(String expectedOutputDescription) { this.expectedOutputDescription = expectedOutputDescription; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public long getRegisteredTime() { return registeredTime; }
    public void setRegisteredTime(long registeredTime) { this.registeredTime = registeredTime; }

    public long getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }

    public int getExecutionCount() { return executionCount; }
    public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public boolean isAvailable() {
        return status == CapabilityStatus.ENABLED || status == CapabilityStatus.HEALTHY;
    }

    public String getFullIdentifier() {
        return String.format("%s@%s:%s", capabilityId, version, address != null ? address.toFullString() : "N/A");
    }

    public void incrementExecutionCount() {
        this.executionCount++;
        this.lastActiveTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Capability{" +
                "capabilityId='" + capabilityId + '\'' +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", type=" + type +
                ", version='" + version + '\'' +
                ", status=" + status +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
