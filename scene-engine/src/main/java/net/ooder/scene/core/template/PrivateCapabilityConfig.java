package net.ooder.scene.core.template;

import java.io.Serializable;
import java.util.Map;

/**
 * 私有能力配置
 * 
 * <p>定义场景中用户可自定义的私有能力</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class PrivateCapabilityConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String capId;           // 能力ID
    private String name;            // 能力名称
    private String description;     // 能力描述
    private String category;        // 分类
    private boolean enabled;        // 是否启用
    private Map<String, Object> configSchema; // 配置Schema
    private Map<String, Object> defaultConfig; // 默认配置
    
    public PrivateCapabilityConfig() {
    }
    
    public PrivateCapabilityConfig(String capId, String name) {
        this.capId = capId;
        this.name = name;
    }
    
    public String getCapId() {
        return capId;
    }
    
    public void setCapId(String capId) {
        this.capId = capId;
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
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Map<String, Object> getConfigSchema() {
        return configSchema;
    }
    
    public void setConfigSchema(Map<String, Object> configSchema) {
        this.configSchema = configSchema;
    }
    
    public Map<String, Object> getDefaultConfig() {
        return defaultConfig;
    }
    
    public void setDefaultConfig(Map<String, Object> defaultConfig) {
        this.defaultConfig = defaultConfig;
    }
    
    @Override
    public String toString() {
        return "PrivateCapabilityConfig{" +
                "capId='" + capId + '\'' +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
