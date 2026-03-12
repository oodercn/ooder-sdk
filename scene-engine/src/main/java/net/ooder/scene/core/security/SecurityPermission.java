package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 安全权限定义
 *
 * <p>原 Permission 类重命名，避免与 skill/permission/Permission 枚举冲突</p>
 *
 * @author Ooder Team
 * @since 2.3
 */
public class SecurityPermission {
    private String permissionId;
    private String resource;
    private String action;
    private String effect;
    private Map<String, Object> conditions;

    public SecurityPermission() {}

    public String getPermissionId() { return permissionId; }
    public void setPermissionId(String permissionId) { this.permissionId = permissionId; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }
    public Map<String, Object> getConditions() { return conditions; }
    public void setConditions(Map<String, Object> conditions) { this.conditions = conditions; }
}
