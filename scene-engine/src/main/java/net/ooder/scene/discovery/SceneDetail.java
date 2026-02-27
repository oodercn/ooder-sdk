package net.ooder.scene.discovery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SceneDetail {
    private String sceneId;
    private String name;
    private String description;
    private String category;
    private List<String> requiredCapabilities;
    private List<String> optionalCapabilities;
    private List<DiscoveredItem> availableSkills;
    private Map<String, Object> metadata;

    public SceneDetail(String sceneId, String name) {
        this.sceneId = sceneId;
        this.name = name;
        this.metadata = new ConcurrentHashMap<>();
    }

    public String getSceneId() {
        return sceneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }

    public List<String> getOptionalCapabilities() {
        return optionalCapabilities;
    }

    public void setOptionalCapabilities(List<String> optionalCapabilities) {
        this.optionalCapabilities = optionalCapabilities;
    }

    public List<DiscoveredItem> getAvailableSkills() {
        return availableSkills;
    }

    public void setAvailableSkills(List<DiscoveredItem> availableSkills) {
        this.availableSkills = availableSkills;
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
