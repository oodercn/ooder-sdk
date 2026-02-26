package net.ooder.sdk.version;

import net.ooder.sdk.plugin.SkillMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 版本管理器
 *
 * <p>管理Skill的版本信息，提供版本检查、兼容性验证等功能。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class VersionManager {

    private static final Logger log = LoggerFactory.getLogger(VersionManager.class);

    /**
     * Skill版本信息缓存
     */
    private final Map<String, VersionInfo> versionCache;

    public VersionManager() {
        this.versionCache = new ConcurrentHashMap<>();
    }

    // ==================== 版本注册 ====================

    /**
     * 注册Skill版本
     *
     * @param skillId Skill标识
     * @param version 版本号
     */
    public void registerVersion(String skillId, String version) {
        try {
            SemanticVersion semver = SemanticVersion.parse(version);
            VersionInfo info = new VersionInfo(skillId, semver, System.currentTimeMillis());
            versionCache.put(skillId, info);
            log.debug("Registered version for skill {}: {}", skillId, version);
        } catch (VersionParseException e) {
            log.error("Failed to register version for skill {}: {}", skillId, version, e);
            throw new IllegalArgumentException("Invalid version format: " + version, e);
        }
    }

    /**
     * 从元数据注册版本
     *
     * @param metadata Skill元数据
     */
    public void registerVersion(SkillMetadata metadata) {
        if (metadata == null || metadata.getId() == null) {
            throw new IllegalArgumentException("Metadata and id cannot be null");
        }
        registerVersion(metadata.getId(), metadata.getVersion());
    }

    /**
     * 注销Skill版本
     *
     * @param skillId Skill标识
     */
    public void unregisterVersion(String skillId) {
        versionCache.remove(skillId);
        log.debug("Unregistered version for skill: {}", skillId);
    }

    // ==================== 版本查询 ====================

    /**
     * 获取Skill版本
     *
     * @param skillId Skill标识
     * @return 版本号，如果不存在返回null
     */
    public String getVersion(String skillId) {
        VersionInfo info = versionCache.get(skillId);
        return info != null ? info.getVersion().toString() : null;
    }

    /**
     * 获取解析后的版本
     *
     * @param skillId Skill标识
     * @return SemanticVersion，如果不存在返回null
     */
    public SemanticVersion getSemanticVersion(String skillId) {
        VersionInfo info = versionCache.get(skillId);
        return info != null ? info.getVersion() : null;
    }

    /**
     * 检查Skill是否已注册版本
     *
     * @param skillId Skill标识
     * @return true如果已注册
     */
    public boolean hasVersion(String skillId) {
        return versionCache.containsKey(skillId);
    }

    // ==================== 版本比较 ====================

    /**
     * 比较两个Skill的版本
     *
     * @param skillId1 Skill 1
     * @param skillId2 Skill 2
     * @return 比较结果: <0 表示 skill1 < skill2, 0 表示相等, >0 表示 skill1 > skill2
     */
    public int compareVersions(String skillId1, String skillId2) {
        SemanticVersion v1 = getSemanticVersion(skillId1);
        SemanticVersion v2 = getSemanticVersion(skillId2);

        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return -1;
        if (v2 == null) return 1;

        return v1.compareTo(v2);
    }

    /**
     * 比较版本号
     *
     * @param skillId Skill标识
     * @param targetVersion 目标版本
     * @return 比较结果
     */
    public int compareToVersion(String skillId, String targetVersion) {
        SemanticVersion current = getSemanticVersion(skillId);
        if (current == null) {
            throw new IllegalArgumentException("Skill not found: " + skillId);
        }

        try {
            SemanticVersion target = SemanticVersion.parse(targetVersion);
            return current.compareTo(target);
        } catch (VersionParseException e) {
            throw new IllegalArgumentException("Invalid target version: " + targetVersion, e);
        }
    }

    // ==================== 兼容性检查 ====================

    /**
     * 检查版本兼容性
     *
     * @param skillId Skill标识
     * @param requiredVersion 要求的版本
     * @return true如果兼容
     */
    public boolean isCompatible(String skillId, String requiredVersion) {
        SemanticVersion current = getSemanticVersion(skillId);
        if (current == null) {
            return false;
        }

        try {
            SemanticVersion required = SemanticVersion.parse(requiredVersion);
            return current.isCompatibleWith(required);
        } catch (VersionParseException e) {
            log.error("Invalid required version: {}", requiredVersion, e);
            return false;
        }
    }

    /**
     * 检查是否满足版本范围
     *
     * @param skillId Skill标识
     * @param versionRange 版本范围表达式
     * @return true如果满足
     */
    public boolean satisfiesRange(String skillId, String versionRange) {
        SemanticVersion current = getSemanticVersion(skillId);
        if (current == null) {
            return false;
        }

        try {
            VersionRange range = VersionRange.parse(versionRange);
            return range.includes(current);
        } catch (Exception e) {
            log.error("Invalid version range: {}", versionRange, e);
            return false;
        }
    }

    // ==================== 版本升级建议 ====================

    /**
     * 获取升级建议
     *
     * @param currentVersion 当前版本
     * @param availableVersions 可用版本列表
     * @return 升级建议
     */
    public UpgradeSuggestion getUpgradeSuggestion(String currentVersion, List<String> availableVersions) {
        if (availableVersions == null || availableVersions.isEmpty()) {
            return new UpgradeSuggestion(false, null, "No available versions");
        }

        SemanticVersion current;
        try {
            current = SemanticVersion.parse(currentVersion);
        } catch (VersionParseException e) {
            return new UpgradeSuggestion(false, null, "Invalid current version: " + currentVersion);
        }

        SemanticVersion latestCompatible = null;
        SemanticVersion latest = null;

        for (String versionStr : availableVersions) {
            try {
                SemanticVersion version = SemanticVersion.parse(versionStr);

                // 跳过比当前版本旧的
                if (version.compareTo(current) <= 0) {
                    continue;
                }

                // 记录最新版本
                if (latest == null || version.compareTo(latest) > 0) {
                    latest = version;
                }

                // 记录最新兼容版本
                if (version.isCompatibleWith(current)) {
                    if (latestCompatible == null || version.compareTo(latestCompatible) > 0) {
                        latestCompatible = version;
                    }
                }

            } catch (VersionParseException e) {
                log.warn("Skipping invalid version: {}", versionStr);
            }
        }

        if (latestCompatible != null) {
            return new UpgradeSuggestion(true, latestCompatible.toString(),
                    "Compatible upgrade available");
        } else if (latest != null) {
            return new UpgradeSuggestion(true, latest.toString(),
                    "New version available (may not be compatible)");
        } else {
            return new UpgradeSuggestion(false, null, "No newer versions available");
        }
    }

    /**
     * 检查是否需要升级
     *
     * @param skillId Skill标识
     * @param availableVersions 可用版本列表
     * @return 升级建议
     */
    public UpgradeSuggestion checkForUpgrade(String skillId, List<String> availableVersions) {
        String currentVersion = getVersion(skillId);
        if (currentVersion == null) {
            return new UpgradeSuggestion(false, null, "Skill not found: " + skillId);
        }
        return getUpgradeSuggestion(currentVersion, availableVersions);
    }

    // ==================== 批量操作 ====================

    /**
     * 获取所有已注册版本
     *
     * @return Skill ID到版本号的映射
     */
    public Map<String, String> getAllVersions() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, VersionInfo> entry : versionCache.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getVersion().toString());
        }
        return result;
    }

    /**
     * 清除所有版本信息
     */
    public void clearAll() {
        versionCache.clear();
        log.info("Cleared all version information");
    }

    // ==================== 内部类 ====================

    /**
     * 版本信息
     */
    private static class VersionInfo {
        private final String skillId;
        private final SemanticVersion version;
        private final long registeredAt;

        VersionInfo(String skillId, SemanticVersion version, long registeredAt) {
            this.skillId = skillId;
            this.version = version;
            this.registeredAt = registeredAt;
        }

        public String getSkillId() {
            return skillId;
        }

        public SemanticVersion getVersion() {
            return version;
        }

        public long getRegisteredAt() {
            return registeredAt;
        }
    }

    /**
     * 升级建议
     */
    public static class UpgradeSuggestion {
        private final boolean upgradeAvailable;
        private final String suggestedVersion;
        private final String message;

        public UpgradeSuggestion(boolean upgradeAvailable, String suggestedVersion, String message) {
            this.upgradeAvailable = upgradeAvailable;
            this.suggestedVersion = suggestedVersion;
            this.message = message;
        }

        public boolean isUpgradeAvailable() {
            return upgradeAvailable;
        }

        public String getSuggestedVersion() {
            return suggestedVersion;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "UpgradeSuggestion{" +
                    "upgradeAvailable=" + upgradeAvailable +
                    ", suggestedVersion='" + suggestedVersion + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
