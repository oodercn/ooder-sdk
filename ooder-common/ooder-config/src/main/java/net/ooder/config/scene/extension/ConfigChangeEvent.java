package net.ooder.config.scene.extension;

import java.io.Serializable;

/**
 * 配置变更事件
 *
 * @deprecated 自 SDK 7.3 起，请使用 {@code net.ooder.sdk.infra.observer.ConfigChangeEvent} 替代。
 *             该类将在未来版本中移除。
 *             <p>
 *             迁移示例:
 *             <pre>
 *             // 旧代码
 *             import net.ooder.config.scene.extension.ConfigChangeEvent;
 *             
 *             // 新代码
 *             import net.ooder.sdk.infra.observer.ConfigChangeEvent;
 *             </pre>
 *             </p>
 * @see net.ooder.sdk.infra.observer.ConfigChangeEvent
 */
@Deprecated
public class ConfigChangeEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private String configPath;
    private Object oldValue;
    private Object newValue;
    private long timestamp;
    private String source;
    
    @Deprecated
    public ConfigChangeEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    @Deprecated
    public ConfigChangeEvent(String sceneId, String configPath, Object oldValue, Object newValue) {
        this();
        this.sceneId = sceneId;
        this.configPath = configPath;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    @Deprecated
    public String getSceneId() {
        return sceneId;
    }
    
    @Deprecated
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    @Deprecated
    public String getConfigPath() {
        return configPath;
    }
    
    @Deprecated
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    
    @Deprecated
    public Object getOldValue() {
        return oldValue;
    }
    
    @Deprecated
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
    
    @Deprecated
    public Object getNewValue() {
        return newValue;
    }
    
    @Deprecated
    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
    
    @Deprecated
    public long getTimestamp() {
        return timestamp;
    }
    
    @Deprecated
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Deprecated
    public String getSource() {
        return source;
    }
    
    @Deprecated
    public void setSource(String source) {
        this.source = source;
    }
    
    @Deprecated
    public boolean hasChange() {
        if (oldValue == null && newValue == null) {
            return false;
        }
        if (oldValue == null || newValue == null) {
            return true;
        }
        return !oldValue.equals(newValue);
    }
    
    @Override
    public String toString() {
        return "ConfigChangeEvent{" +
            "sceneId='" + sceneId + '\'' +
            ", configPath='" + configPath + '\'' +
            ", oldValue=" + oldValue +
            ", newValue=" + newValue +
            ", timestamp=" + timestamp +
            '}';
    }
}
