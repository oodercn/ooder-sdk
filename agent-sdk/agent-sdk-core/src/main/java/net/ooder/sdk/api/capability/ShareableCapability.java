package net.ooder.sdk.api.capability;

import java.util.Map;
import java.util.Set;

/**
 * 可分享能力接口
 * 
 * 支持能力的外部共享和授权管理
 */
public interface ShareableCapability {

    /**
     * 获取能力分享配置
     */
    ShareConfig getShareConfig();

    /**
     * 设置能力分享配置
     */
    void setShareConfig(ShareConfig config);

    /**
     * 是否可分享
     */
    default boolean isShareable() {
        ShareConfig config = getShareConfig();
        return config != null && config.isEnabled();
    }

    /**
     * 生成分享令牌
     */
    String generateShareToken(String targetAgentId, SharePermission permission);

    /**
     * 验证分享令牌
     */
    boolean validateShareToken(String token);

    /**
     * 撤销分享
     */
    void revokeShare(String token);

    /**
     * 获取当前分享列表
     */
    Set<ShareRecord> getActiveShares();

    /**
     * 分享配置
     */
    class ShareConfig {
        private boolean enabled;
        private boolean requireApproval;
        private long maxShareDuration;
        private int maxConcurrentShares;
        private Set<SharePermission> allowedPermissions;
        private Map<String, Object> metadata;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isRequireApproval() {
            return requireApproval;
        }

        public void setRequireApproval(boolean requireApproval) {
            this.requireApproval = requireApproval;
        }

        public long getMaxShareDuration() {
            return maxShareDuration;
        }

        public void setMaxShareDuration(long maxShareDuration) {
            this.maxShareDuration = maxShareDuration;
        }

        public int getMaxConcurrentShares() {
            return maxConcurrentShares;
        }

        public void setMaxConcurrentShares(int maxConcurrentShares) {
            this.maxConcurrentShares = maxConcurrentShares;
        }

        public Set<SharePermission> getAllowedPermissions() {
            return allowedPermissions;
        }

        public void setAllowedPermissions(Set<SharePermission> allowedPermissions) {
            this.allowedPermissions = allowedPermissions;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }

    /**
     * 分享权限
     */
    enum SharePermission {
        READ,
        WRITE,
        EXECUTE,
        ADMIN
    }

    /**
     * 分享记录
     */
    class ShareRecord {
        private String token;
        private String targetAgentId;
        private SharePermission permission;
        private long createdAt;
        private long expiresAt;
        private String status;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getTargetAgentId() {
            return targetAgentId;
        }

        public void setTargetAgentId(String targetAgentId) {
            this.targetAgentId = targetAgentId;
        }

        public SharePermission getPermission() {
            return permission;
        }

        public void setPermission(SharePermission permission) {
            this.permission = permission;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
