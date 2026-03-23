package net.ooder.scene.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 密钥管理配置属性
 *
 * @author ooder
 * @since 2.3.1
 */
@ConfigurationProperties(prefix = "scene.engine.key.management")
public class KeyManagementProperties {

    private boolean enabled = true;
    private boolean defaultRequireApproval = false;
    private long defaultExpiresInSeconds = 86400L;
    private int defaultMaxUseCount = 0;
    private String encryptionKey = "ooder-default-encryption-key";
    private StorageConfig storage = new StorageConfig();
    private ApprovalConfig approval = new ApprovalConfig();

    public static class StorageConfig {
        private String root = "data/keys";
        private int lockCleanupInterval = 3600;
        private int dataExpirationDays = 30;

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }

        public int getLockCleanupInterval() {
            return lockCleanupInterval;
        }

        public void setLockCleanupInterval(int lockCleanupInterval) {
            this.lockCleanupInterval = lockCleanupInterval;
        }

        public int getDataExpirationDays() {
            return dataExpirationDays;
        }

        public void setDataExpirationDays(int dataExpirationDays) {
            this.dataExpirationDays = dataExpirationDays;
        }
    }

    public static class ApprovalConfig {
        private int timeoutHours = 24;
        private boolean enableNotification = true;

        public int getTimeoutHours() {
            return timeoutHours;
        }

        public void setTimeoutHours(int timeoutHours) {
            this.timeoutHours = timeoutHours;
        }

        public boolean isEnableNotification() {
            return enableNotification;
        }

        public void setEnableNotification(boolean enableNotification) {
            this.enableNotification = enableNotification;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDefaultRequireApproval() {
        return defaultRequireApproval;
    }

    public void setDefaultRequireApproval(boolean defaultRequireApproval) {
        this.defaultRequireApproval = defaultRequireApproval;
    }

    public long getDefaultExpiresInSeconds() {
        return defaultExpiresInSeconds;
    }

    public void setDefaultExpiresInSeconds(long defaultExpiresInSeconds) {
        this.defaultExpiresInSeconds = defaultExpiresInSeconds;
    }

    public int getDefaultMaxUseCount() {
        return defaultMaxUseCount;
    }

    public void setDefaultMaxUseCount(int defaultMaxUseCount) {
        this.defaultMaxUseCount = defaultMaxUseCount;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public StorageConfig getStorage() {
        return storage;
    }

    public void setStorage(StorageConfig storage) {
        this.storage = storage;
    }

    public ApprovalConfig getApproval() {
        return approval;
    }

    public void setApproval(ApprovalConfig approval) {
        this.approval = approval;
    }
}
