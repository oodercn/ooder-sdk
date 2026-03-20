package net.ooder.skills.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillBinding {
    
    private String bindingId;
    private String skillId;
    private String sceneGroupId;
    private Map<String, Object> config;
    private String status;
    private long createTime;
    private long lastUpdateTime;
    
    public SkillBinding() {
        this.bindingId = UUID.randomUUID().toString();
        this.config = new HashMap<>();
        this.status = "active";
        this.createTime = System.currentTimeMillis();
        this.lastUpdateTime = this.createTime;
    }
    
    public SkillBinding(String skillId, String sceneGroupId) {
        this();
        this.skillId = skillId;
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    
    public long getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    
    public void updateConfig(String key, Object value) {
        this.config.put(key, value);
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public void activate() {
        this.status = "active";
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public void deactivate() {
        this.status = "inactive";
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public boolean isActive() {
        return "active".equals(status);
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("bindingId", bindingId);
        map.put("skillId", skillId);
        map.put("sceneGroupId", sceneGroupId);
        map.put("config", config);
        map.put("status", status);
        map.put("createTime", createTime);
        map.put("lastUpdateTime", lastUpdateTime);
        return map;
    }
}
