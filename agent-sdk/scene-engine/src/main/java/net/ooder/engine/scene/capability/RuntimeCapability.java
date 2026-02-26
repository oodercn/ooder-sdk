package net.ooder.engine.scene.capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeCapability {
    
    private String capabilityId;
    private String name;
    private String description;
    private String version;
    private String providerId;
    private RuntimeCapabilityType type;
    private RuntimeCapabilityStatus status;
    private Map<String, Object> parameters;
    private Map<String, Class<?>> parameterTypes;
    private Class<?> returnType;
    private List<String> tags;
    private long createTime;
    private long lastUsedTime;
    
    public RuntimeCapability() {
        this.parameters = new HashMap<>();
        this.parameterTypes = new HashMap<>();
        this.tags = new ArrayList<>();
        this.status = RuntimeCapabilityStatus.AVAILABLE;
        this.createTime = System.currentTimeMillis();
    }
    
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    
    public RuntimeCapabilityType getType() { return type; }
    public void setType(RuntimeCapabilityType type) { this.type = type; }
    
    public RuntimeCapabilityStatus getStatus() { return status; }
    public void setStatus(RuntimeCapabilityStatus status) { this.status = status; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public void addParameter(String name, Object defaultValue, Class<?> type) {
        parameters.put(name, defaultValue);
        parameterTypes.put(name, type);
    }
    
    public Class<?> getParameterType(String name) {
        return parameterTypes.get(name);
    }
    
    public Class<?> getReturnType() { return returnType; }
    public void setReturnType(Class<?> returnType) { this.returnType = returnType; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public void addTag(String tag) { tags.add(tag); }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    
    public long getLastUsedTime() { return lastUsedTime; }
    public void setLastUsedTime(long lastUsedTime) { this.lastUsedTime = lastUsedTime; }
    
    public boolean isAvailable() {
        return status == RuntimeCapabilityStatus.AVAILABLE;
    }
    
    public String getQualifiedName() {
        return providerId + ":" + name + ":" + version;
    }
}
