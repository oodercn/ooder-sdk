package net.ooder.skills.api;

import java.util.List;

/**
 * 角色配置
 * 定义场景中的角色及其权限
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class RoleConfig {
    
    private String name;
    private String description;
    private boolean required;
    private int minCount;
    private int maxCount;
    private List<String> permissions;
    
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
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public int getMinCount() {
        return minCount;
    }
    
    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }
    
    public int getMaxCount() {
        return maxCount;
    }
    
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
