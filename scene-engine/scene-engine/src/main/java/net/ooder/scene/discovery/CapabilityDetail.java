package net.ooder.scene.discovery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityDetail {
    private String capId;
    private String name;
    private String version;
    private String category;
    private String description;
    private String status;
    private List<String> permissions;
    private List<String> dependencies;
    private List<DiscoveredItem> availableSkills;
    private Map<String, Object> interfaceSpec;
    private Map<String, Object> offlineSpec;
    private Map<String, Object> metadata;

    public CapabilityDetail(String capId, String name) {
        this.capId = capId;
        this.name = name;
        this.metadata = new ConcurrentHashMap<>();
    }

    public String getCapId() {
        return capId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<DiscoveredItem> getAvailableSkills() {
        return availableSkills;
    }

    public void setAvailableSkills(List<DiscoveredItem> availableSkills) {
        this.availableSkills = availableSkills;
    }

    public Map<String, Object> getInterfaceSpec() {
        return interfaceSpec;
    }

    public void setInterfaceSpec(Map<String, Object> interfaceSpec) {
        this.interfaceSpec = interfaceSpec;
    }

    public Map<String, Object> getOfflineSpec() {
        return offlineSpec;
    }

    public void setOfflineSpec(Map<String, Object> offlineSpec) {
        this.offlineSpec = offlineSpec;
    }

    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
