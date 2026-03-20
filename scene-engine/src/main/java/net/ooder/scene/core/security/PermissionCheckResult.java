package net.ooder.scene.core.security;

import java.util.List;

/**
 * 权限检查结果
 */
public class PermissionCheckResult {
    private boolean allowed;
    private String denyReason;
    private String requiredPermission;
    private String userRole;
    private List<String> missingPermissions;

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    public String getDenyReason() { return denyReason; }
    public void setDenyReason(String denyReason) { this.denyReason = denyReason; }
    public String getRequiredPermission() { return requiredPermission; }
    public void setRequiredPermission(String requiredPermission) { this.requiredPermission = requiredPermission; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public List<String> getMissingPermissions() { return missingPermissions; }
    public void setMissingPermissions(List<String> missingPermissions) { this.missingPermissions = missingPermissions; }
}
