package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.ActivationStepRef;
import net.ooder.sdk.api.procedure.ProcedureRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规范角色定义实体实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class ProcedureRoleEntity implements ProcedureRole {

    private static final long serialVersionUID = 1L;

    private String roleId;
    private String name;
    private String description;
    private int priority;
    private boolean required = true;
    private int minCount = 1;
    private int maxCount = 1;
    private List<String> positionIds = new ArrayList<>();
    private List<String> permissionIds = new ArrayList<>();
    private List<String> requiredCapabilities = new ArrayList<>();
    private List<ActivationStepRef> activationSteps = new ArrayList<>();
    private List<String> menuIds = new ArrayList<>();
    private Map<String, Object> extensions = new HashMap<>();

    public ProcedureRoleEntity() {
    }

    public ProcedureRoleEntity(String roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    @Override
    public String getRoleId() {
        return roleId;
    }

    @Override
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public int getMinCount() {
        return minCount;
    }

    @Override
    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    @Override
    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public List<String> getPositionIds() {
        return positionIds;
    }

    @Override
    public void setPositionIds(List<String> positionIds) {
        this.positionIds = positionIds != null ? positionIds : new ArrayList<>();
    }

    @Override
    public List<String> getPermissionIds() {
        return permissionIds;
    }

    @Override
    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds != null ? permissionIds : new ArrayList<>();
    }

    @Override
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    @Override
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities != null ? requiredCapabilities : new ArrayList<>();
    }

    @Override
    public List<ActivationStepRef> getActivationSteps() {
        return activationSteps;
    }

    @Override
    public void setActivationSteps(List<ActivationStepRef> activationSteps) {
        this.activationSteps = activationSteps != null ? activationSteps : new ArrayList<>();
    }

    @Override
    public List<String> getMenuIds() {
        return menuIds;
    }

    @Override
    public void setMenuIds(List<String> menuIds) {
        this.menuIds = menuIds != null ? menuIds : new ArrayList<>();
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public String toString() {
        return "ProcedureRoleEntity{" +
                "roleId='" + roleId + '\'' +
                ", name='" + name + '\'' +
                ", required=" + required +
                '}';
    }
}
