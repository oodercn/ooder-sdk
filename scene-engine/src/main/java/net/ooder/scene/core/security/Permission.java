package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 权限定义
 */
public class Permission {
    private String permissionId;
    private String resource;
    private String action;
    private String effect;
    private Map<String, Object> conditions;

    public Permission() {}

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
