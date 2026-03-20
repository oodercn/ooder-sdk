package net.ooder.scene.config;

import java.util.Map;

/**
 * 配置历史记录
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ConfigHistory {
    
    private String id;
    private String skillId;
    private String action;
    private Map<String, Map<String, Object>> changes;
    private String operator;
    private long timestamp;
    private String description;
    
    public ConfigHistory() {}
    
    public ConfigHistory(String id, String skillId, String action) {
        this.id = id;
        this.skillId = skillId;
        this.action = action;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Map<String, Map<String, Object>> getChanges() { return changes; }
    public void setChanges(Map<String, Map<String, Object>> changes) { this.changes = changes; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
