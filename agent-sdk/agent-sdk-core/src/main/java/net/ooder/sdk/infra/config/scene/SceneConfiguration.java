package net.ooder.sdk.infra.config.scene;

import java.util.*;

/**
 * 场景配置
 */
public class SceneConfiguration {
    private String sceneId;
    private String name;
    private String description;
    private List<String> skills = new ArrayList<>();
    private Map<String, Object> properties = new HashMap<>();
    private boolean enabled;
    private int priority;

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
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

    public List<String> getSkills() {
        return new ArrayList<>(skills);
    }

    public void setSkills(List<String> skills) {
        this.skills = new ArrayList<>(skills);
    }

    public void addSkill(String skillId) {
        if (!skills.contains(skillId)) {
            skills.add(skillId);
        }
    }

    public void removeSkill(String skillId) {
        skills.remove(skillId);
    }

    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = new HashMap<>(properties);
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
