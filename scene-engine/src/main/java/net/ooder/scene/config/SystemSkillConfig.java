package net.ooder.scene.config;

import java.util.Map;

/**
 * 系统 Skill 配置
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SystemSkillConfig {
    
    private String skillId;
    private String name;
    private String description;
    private String category;
    private String status;
    private boolean autoStart;
    private boolean enabled;
    private Map<String, Object> config;
    private Map<String, Object> configSchema;
    private long createTime;
    private long updateTime;
    
    public SystemSkillConfig() {}
    
    public SystemSkillConfig(String skillId, String name) {
        this.skillId = skillId;
        this.name = name;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isAutoStart() { return autoStart; }
    public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public Map<String, Object> getConfigSchema() { return configSchema; }
    public void setConfigSchema(Map<String, Object> configSchema) { this.configSchema = configSchema; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
}
