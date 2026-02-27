package net.ooder.scene.provider;

/**
 * 访问控制
 */
public class AccessControl {
    private String aclId;
    private String resourceType;
    private String resourceId;
    private String principalType;
    private String principalId;
    private String permission;
    private String status;
    private long grantedAt;
    private String grantedBy;

    public String getAclId() { return aclId; }
    public void setAclId(String aclId) { this.aclId = aclId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getPrincipalType() { return principalType; }
    public void setPrincipalType(String principalType) { this.principalType = principalType; }
    public String getPrincipalId() { return principalId; }
    public void setPrincipalId(String principalId) { this.principalId = principalId; }
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getGrantedAt() { return grantedAt; }
    public void setGrantedAt(long grantedAt) { this.grantedAt = grantedAt; }
    public String getGrantedBy() { return grantedBy; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
}
