package net.ooder.scene.event.config;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class ConfigEvent extends SceneEvent {
    
    private final String configKey;
    private final String configGroup;
    private final String oldValue;
    private final String newValue;
    private final String userId;
    private final boolean success;
    private final String errorMessage;
    
    private ConfigEvent(Object source, SceneEventType eventType, String configKey, String configGroup) {
        super(source, eventType);
        this.configKey = configKey;
        this.configGroup = configGroup;
        this.oldValue = null;
        this.newValue = null;
        this.userId = null;
        this.success = true;
        this.errorMessage = null;
    }
    
    private ConfigEvent(Object source, SceneEventType eventType, String configKey, String configGroup,
                        String oldValue, String newValue, String userId, boolean success, String errorMessage) {
        super(source, eventType);
        this.configKey = configKey;
        this.configGroup = configGroup;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.userId = userId;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public static ConfigEvent set(Object source, String configKey, String configGroup, 
                                   String oldValue, String newValue, String userId) {
        return new ConfigEvent(source, SceneEventType.CONFIG_SET, configKey, configGroup,
                oldValue, newValue, userId, true, null);
    }
    
    public static ConfigEvent deleted(Object source, String configKey, String configGroup, String userId) {
        return new ConfigEvent(source, SceneEventType.CONFIG_DELETED, configKey, configGroup,
                null, null, userId, true, null);
    }
    
    public static ConfigEvent rollback(Object source, String configKey, String configGroup, String userId) {
        return new ConfigEvent(source, SceneEventType.CONFIG_ROLLBACK, configKey, configGroup,
                null, null, userId, true, null);
    }
    
    public static ConfigEvent imported(Object source, String configGroup, int count, String userId) {
        return new ConfigEvent(source, SceneEventType.CONFIG_IMPORTED, null, configGroup,
                null, String.valueOf(count), userId, true, null);
    }
    
    public static ConfigEvent exported(Object source, String configGroup, String userId) {
        return new ConfigEvent(source, SceneEventType.CONFIG_EXPORTED, null, configGroup,
                null, null, userId, true, null);
    }
    
    public static ConfigEvent batchSet(Object source, String configGroup, int count, String userId) {
        return new ConfigEvent(source, SceneEventType.CONFIG_BATCH_SET, null, configGroup,
                null, String.valueOf(count), userId, true, null);
    }
    
    public static ConfigEvent securityConfigChanged(Object source, String userId) {
        return new ConfigEvent(source, SceneEventType.SECURITY_CONFIG_CHANGED, null, "security",
                null, null, userId, true, null);
    }
    
    public String getConfigKey() {
        return configKey;
    }
    
    public String getConfigGroup() {
        return configGroup;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    public String toString() {
        return "ConfigEvent{" +
                "eventType=" + getEventType() +
                ", configKey='" + configKey + '\'' +
                ", configGroup='" + configGroup + '\'' +
                ", userId='" + userId + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
