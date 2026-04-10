package net.ooder.skill.common.sdk.capability;

import net.ooder.sdk.api.capability.Capability;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 可分享的能力封装
 * 
 * 扩展 SDK 2.3 的 Capability，添加分享相关属性
 * 
 * @author Skills Team
 * @version 2.3.0
 * @since 2026-02-24
 */
public class ShareableCapability {

    private Capability capability;
    private boolean shareable = false;
    private ShareScope shareScope = ShareScope.PRIVATE;
    private String shareToken;
    private Instant shareExpireAt;
    private int shareLimit = -1;
    private int sharedCount = 0;
    private List<String> allowedUsers;
    private List<String> allowedDomains;
    private Map<String, String> shareMetadata;

    public enum ShareScope {
        PRIVATE,
        PUBLIC,
        DOMAIN,
        USER_LIST,
        TOKEN_ONLY
    }

    public ShareableCapability() {
    }

    private ShareableCapability(Builder builder) {
        this.capability = builder.capability;
        this.shareable = builder.shareable;
        this.shareScope = builder.shareScope;
        this.shareToken = builder.shareToken;
        this.shareExpireAt = builder.shareExpireAt;
        this.shareLimit = builder.shareLimit;
        this.sharedCount = builder.sharedCount;
        this.allowedUsers = builder.allowedUsers;
        this.allowedDomains = builder.allowedDomains;
        this.shareMetadata = builder.shareMetadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean canShare() {
        if (!shareable) {
            return false;
        }
        if (shareExpireAt != null && Instant.now().isAfter(shareExpireAt)) {
            return false;
        }
        if (shareLimit > 0 && sharedCount >= shareLimit) {
            return false;
        }
        return true;
    }

    public boolean hasAccess(String userId, String domain) {
        if (!shareable) {
            return false;
        }

        switch (shareScope) {
            case PUBLIC:
                return true;
            case DOMAIN:
                return allowedDomains != null && allowedDomains.contains(domain);
            case USER_LIST:
                return allowedUsers != null && allowedUsers.contains(userId);
            case TOKEN_ONLY:
                return true;
            case PRIVATE:
            default:
                return false;
        }
    }

    public void recordShare() {
        sharedCount++;
    }

    public String generateShareLink(String baseUrl) {
        if (!canShare()) {
            return null;
        }
        String capabilityId = capability != null ? capability.getCapabilityId() : "unknown";
        return baseUrl + "/share/capability/" + capabilityId + "?token=" + shareToken;
    }

    // Getters and Setters
    public Capability getCapability() {
        return capability;
    }

    public void setCapability(Capability capability) {
        this.capability = capability;
    }

    public boolean isShareable() {
        return shareable;
    }

    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    public ShareScope getShareScope() {
        return shareScope;
    }

    public void setShareScope(ShareScope shareScope) {
        this.shareScope = shareScope;
    }

    public String getShareToken() {
        return shareToken;
    }

    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }

    public Instant getShareExpireAt() {
        return shareExpireAt;
    }

    public void setShareExpireAt(Instant shareExpireAt) {
        this.shareExpireAt = shareExpireAt;
    }

    public int getShareLimit() {
        return shareLimit;
    }

    public void setShareLimit(int shareLimit) {
        this.shareLimit = shareLimit;
    }

    public int getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(int sharedCount) {
        this.sharedCount = sharedCount;
    }

    public List<String> getAllowedUsers() {
        return allowedUsers;
    }

    public void setAllowedUsers(List<String> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public List<String> getAllowedDomains() {
        return allowedDomains;
    }

    public void setAllowedDomains(List<String> allowedDomains) {
        this.allowedDomains = allowedDomains;
    }

    public Map<String, String> getShareMetadata() {
        return shareMetadata;
    }

    public void setShareMetadata(Map<String, String> shareMetadata) {
        this.shareMetadata = shareMetadata;
    }

    public static class Builder {
        private Capability capability;
        private boolean shareable = false;
        private ShareScope shareScope = ShareScope.PRIVATE;
        private String shareToken;
        private Instant shareExpireAt;
        private int shareLimit = -1;
        private int sharedCount = 0;
        private List<String> allowedUsers;
        private List<String> allowedDomains;
        private Map<String, String> shareMetadata;

        public Builder capability(Capability capability) {
            this.capability = capability;
            return this;
        }

        public Builder shareable(boolean shareable) {
            this.shareable = shareable;
            return this;
        }

        public Builder shareScope(ShareScope shareScope) {
            this.shareScope = shareScope;
            return this;
        }

        public Builder shareToken(String shareToken) {
            this.shareToken = shareToken;
            return this;
        }

        public Builder shareExpireAt(Instant shareExpireAt) {
            this.shareExpireAt = shareExpireAt;
            return this;
        }

        public Builder shareLimit(int shareLimit) {
            this.shareLimit = shareLimit;
            return this;
        }

        public Builder sharedCount(int sharedCount) {
            this.sharedCount = sharedCount;
            return this;
        }

        public Builder allowedUsers(List<String> allowedUsers) {
            this.allowedUsers = allowedUsers;
            return this;
        }

        public Builder allowedDomains(List<String> allowedDomains) {
            this.allowedDomains = allowedDomains;
            return this;
        }

        public Builder shareMetadata(Map<String, String> shareMetadata) {
            this.shareMetadata = shareMetadata;
            return this;
        }

        public ShareableCapability build() {
            return new ShareableCapability(this);
        }
    }
}
