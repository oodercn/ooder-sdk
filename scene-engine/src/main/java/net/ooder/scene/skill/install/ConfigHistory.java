package net.ooder.scene.skill.install;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置历史
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class ConfigHistory {

    private String skillId;
    private List<ConfigVersion> versions;
    private int currentVersion;

    public ConfigHistory() {
        this.versions = new ArrayList<>();
        this.currentVersion = 0;
    }

    public ConfigHistory(String skillId) {
        this();
        this.skillId = skillId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<ConfigVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<ConfigVersion> versions) {
        this.versions = versions != null ? versions : new ArrayList<>();
    }

    public void addVersion(ConfigVersion version) {
        this.versions.add(version);
        this.currentVersion = version.getVersion();
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public ConfigVersion getVersion(int version) {
        return versions.stream()
                .filter(v -> v.getVersion() == version)
                .findFirst()
                .orElse(null);
    }

    public ConfigVersion getLatestVersion() {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.get(versions.size() - 1);
    }

    /**
     * 配置版本
     */
    public static class ConfigVersion {
        private int version;
        private String configId;
        private long timestamp;
        private String description;
        private String checksum;

        public ConfigVersion() {
        }

        public ConfigVersion(int version, String configId) {
            this.version = version;
            this.configId = configId;
            this.timestamp = System.currentTimeMillis();
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getConfigId() {
            return configId;
        }

        public void setConfigId(String configId) {
            this.configId = configId;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }
    }
}
