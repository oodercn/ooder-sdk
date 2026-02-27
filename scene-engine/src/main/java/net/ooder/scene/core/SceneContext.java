package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SceneContext {
    private String sceneId;
    private Map<String, Object> attributes;
    private Map<String, SceneConfig> skillConfigs;

    public SceneContext(String sceneId) {
        this.sceneId = sceneId;
        this.attributes = new ConcurrentHashMap<>();
        this.skillConfigs = new ConcurrentHashMap<>();
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public void addSkillConfig(String skillId, SceneConfig config) {
        skillConfigs.put(skillId, config);
    }

    public SceneConfig getSkillConfig(String skillId) {
        return skillConfigs.get(skillId);
    }

    public void removeSkillConfig(String skillId) {
        skillConfigs.remove(skillId);
    }

    public Map<String, SceneConfig> getSkillConfigs() {
        return skillConfigs;
    }

    public void clear() {
        attributes.clear();
        skillConfigs.clear();
    }
}
