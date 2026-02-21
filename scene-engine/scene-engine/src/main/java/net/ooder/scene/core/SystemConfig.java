package net.ooder.scene.core;

import java.util.Map;

/**
 * 系统配置
 */
public class SystemConfig {
    private String configId;
    private Map<String, Object> settings;
    private long updatedAt;
    private String updatedBy;

    public SystemConfig() {}

    public String getConfigId() { return configId; }
    public void setConfigId(String configId) { this.configId = configId; }
    public Map<String, Object> getSettings() { return settings; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(String key, T defaultValue) {
        if (settings == null) {
            return defaultValue;
        }
        Object value = settings.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }
}
