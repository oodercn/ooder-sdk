package net.ooder.sdk.common.config;

import java.util.Map;

/**
 * 共享统一配置类
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ShareConfiguration {

    private String shareId;
    private String resourceId;
    private String resourceType;
    private String ownerId;
    private Map<String, String> permissions;
    private Map<String, Object> metadata;

    public String getShareId() { return shareId; }
    public void setShareId(String shareId) { this.shareId = shareId; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public Map<String, String> getPermissions() { return permissions; }
    public void setPermissions(Map<String, String> permissions) { this.permissions = permissions; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
