package net.ooder.skill.common;

import java.util.Map;
import java.util.HashMap;

public class SkillContext {
    
    private final String skillId;
    private final String skillName;
    private final Map<String, Object> properties;
    private final Map<String, Object> config;
    
    public SkillContext(String skillId, String skillName) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.properties = new HashMap<>();
        this.config = new HashMap<>();
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public Object getConfig(String key) {
        return config.get(key);
    }
    
    public void setConfig(String key, Object value) {
        config.put(key, value);
    }
}
