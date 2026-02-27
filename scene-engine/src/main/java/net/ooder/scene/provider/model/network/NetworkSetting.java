package net.ooder.scene.provider.model.network;

import java.util.Map;

public class NetworkSetting {
    
    private String settingId;
    private String settingType;
    private Map<String, Object> config;
    private long updatedAt;
    private String updatedBy;
    
    public String getSettingId() {
        return settingId;
    }
    
    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }
    
    public String getSettingType() {
        return settingType;
    }
    
    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
