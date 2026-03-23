package net.ooder.scene.core.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 角色配置
 * 
 * <p>定义场景技能中的角色信息，用于多角色协作场景</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class RoleConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String roleId;           // 角色ID，如：MANAGER, EMPLOYEE, HR
    private String roleName;         // 角色显示名称
    private String name;             // 角色名称（兼容字段）
    private String description;      // 角色描述
    private int priority;            // 优先级（用于排序）
    private boolean required;        // 是否必需角色
    private int minCount;            // 最小人数
    private int maxCount;            // 最大人数（0表示无限制）
    private List<String> permissions; // 角色权限列表
    private Map<String, Object> metadata;  // 扩展属性
    
    public RoleConfig() {
    }
    
    public RoleConfig(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.required = true;
        this.minCount = 1;
        this.maxCount = 1;
    }
    
    // Getters and Setters
    
    public String getRoleId() {
        return roleId;
    }
    
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public String getName() {
        return name != null ? name : roleName;
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
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
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
        return permissions != null ? permissions : new ArrayList<>();
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * 检查角色人数是否有效
     */
    public boolean isValidCount(int count) {
        if (count < minCount) {
            return false;
        }
        if (maxCount > 0 && count > maxCount) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "RoleConfig{" +
                "roleId='" + roleId + '\'' +
                ", roleName='" + roleName + '\'' +
                ", required=" + required +
                ", minCount=" + minCount +
                ", maxCount=" + maxCount +
                '}';
    }
}
