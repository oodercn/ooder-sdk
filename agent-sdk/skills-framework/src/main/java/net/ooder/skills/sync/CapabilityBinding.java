package net.ooder.skills.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CapabilityBinding {
    
    private String bindingId;
    private String capabilityId;
    private String sceneGroupId;
    private Map<String, Object> config;
    private String status;
    private long createTime;
    private long lastUpdateTime;
    
    public CapabilityBinding() {
        this.bindingId = UUID.randomUUID().toString();
        this.config = new HashMap<>();
        this.status = "active";
        this.createTime = System.currentTimeMillis();
        this.lastUpdateTime = this.createTime;
    }
    
    public CapabilityBinding(String capabilityId, String sceneGroupId) {
        this();
        this.capabilityId = capabilityId;
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }
    
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    
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
}
