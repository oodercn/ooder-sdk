package net.ooder.skill.hotplug.ui;

import net.ooder.skill.hotplug.model.SkillMetadata;
import net.ooder.skill.hotplug.model.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 页面版本管理器
 * 管理Skill页面的版本控制
 */
public class PageVersionManager {

    private static final Logger logger = LoggerFactory.getLogger(PageVersionManager.class);

    private final Map<String, SkillVersionInfo> versionRegistry = new ConcurrentHashMap<>();
    private final Map<String, Map<String, PageVersionInfo>> pageVersions = new ConcurrentHashMap<>();

    public void registerSkill(String skillId, SkillPackage skillPackage) {
        SkillMetadata metadata = skillPackage.getMetadata();
        if (metadata == null) {
            logger.warn("[PageVersionManager] No metadata found for skill: {}", skillId);
            return;
        }

        SkillVersionInfo versionInfo = new SkillVersionInfo(
                skillId,
                metadata.getVersion(),
                System.currentTimeMillis(),
                metadata.getAuthor()
        );
        versionRegistry.put(skillId, versionInfo);

        logger.info("[PageVersionManager] Registered skill version: {}={}", skillId, metadata.getVersion());
    }

    public void unregisterSkill(String skillId) {
        versionRegistry.remove(skillId);
        pageVersions.remove(skillId);
        logger.info("[PageVersionManager] Unregistered skill: {}", skillId);
    }

    public String getSkillVersion(String skillId) {
        SkillVersionInfo info = versionRegistry.get(skillId);
        return info != null ? info.getVersion() : null;
    }

    public Map<String, Object> getSkillVersionInfo(String skillId) {
        SkillVersionInfo info = versionRegistry.get(skillId);
        if (info == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("skillId", info.getSkillId());
        result.put("version", info.getVersion());
        result.put("registeredAt", info.getRegisteredAt());
        result.put("revision", info.getRevision());
        return result;
    }

    public void recordPageVersion(String skillId, String pagePath, String contentHash) {
        Map<String, PageVersionInfo> versions = pageVersions.computeIfAbsent(skillId, k -> new ConcurrentHashMap<>());
        
        PageVersionInfo existing = versions.get(pagePath);
        if (existing != null && existing.getContentHash().equals(contentHash)) {
            existing.updateAccessTime();
            return;
        }

        PageVersionInfo versionInfo = new PageVersionInfo(
                pagePath,
                contentHash,
                System.currentTimeMillis(),
                getSkillVersion(skillId)
        );
        versions.put(pagePath, versionInfo);

        logger.debug("[PageVersionManager] Recorded page version: {}:{} @ {}", skillId, pagePath, contentHash.substring(0, 8));
    }

    public boolean isPageModified(String skillId, String pagePath, String currentHash) {
        Map<String, PageVersionInfo> versions = pageVersions.get(skillId);
        if (versions == null) {
            return true;
        }

        PageVersionInfo info = versions.get(pagePath);
        if (info == null) {
            return true;
        }

        return !info.getContentHash().equals(currentHash);
    }

    public Map<String, Object> getPageVersionInfo(String skillId, String pagePath) {
        Map<String, PageVersionInfo> versions = pageVersions.get(skillId);
        if (versions == null) {
            return Collections.emptyMap();
        }

        PageVersionInfo info = versions.get(pagePath);
        if (info == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("path", info.getPath());
        result.put("contentHash", info.getContentHash());
        result.put("lastModified", info.getLastModified());
        result.put("skillVersion", info.getSkillVersion());
        result.put("lastAccessed", info.getLastAccessed());
        return result;
    }

    public List<Map<String, Object>> getAllPageVersions(String skillId) {
        Map<String, PageVersionInfo> versions = pageVersions.get(skillId);
        if (versions == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (PageVersionInfo info : versions.values()) {
            Map<String, Object> pageInfo = new LinkedHashMap<>();
            pageInfo.put("path", info.getPath());
            pageInfo.put("contentHash", info.getContentHash());
            pageInfo.put("lastModified", info.getLastModified());
            pageInfo.put("skillVersion", info.getSkillVersion());
            result.add(pageInfo);
        }

        return result;
    }

    public Map<String, Object> compareVersions(String skillId, String oldVersion, String newVersion) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("skillId", skillId);
        result.put("oldVersion", oldVersion);
        result.put("newVersion", newVersion);
        result.put("comparisonTime", System.currentTimeMillis());

        SkillVersionInfo currentInfo = versionRegistry.get(skillId);
        if (currentInfo != null) {
            result.put("currentVersion", currentInfo.getVersion());
        }

        return result;
    }

    public List<String> getModifiedPages(String skillId, String sinceVersion) {
        Map<String, PageVersionInfo> versions = pageVersions.get(skillId);
        if (versions == null) {
            return Collections.emptyList();
        }

        List<String> modified = new ArrayList<>();
        for (PageVersionInfo info : versions.values()) {
            if (!sinceVersion.equals(info.getSkillVersion())) {
                modified.add(info.getPath());
            }
        }

        return modified;
    }

    public void clearPageVersions(String skillId) {
        pageVersions.remove(skillId);
        logger.info("[PageVersionManager] Cleared page versions for skill: {}", skillId);
    }

    public void clearAll() {
        versionRegistry.clear();
        pageVersions.clear();
        logger.info("[PageVersionManager] Cleared all version info");
    }

    public Map<String, Object> getVersionStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("registeredSkills", versionRegistry.size());
        stats.put("totalTrackedPages", pageVersions.values().stream().mapToInt(Map::size).sum());
        
        List<Map<String, Object>> skillVersions = new ArrayList<>();
        for (SkillVersionInfo info : versionRegistry.values()) {
            Map<String, Object> skillInfo = new LinkedHashMap<>();
            skillInfo.put("skillId", info.getSkillId());
            skillInfo.put("version", info.getVersion());
            skillInfo.put("pageCount", pageVersions.getOrDefault(info.getSkillId(), Collections.emptyMap()).size());
            skillVersions.add(skillInfo);
        }
        stats.put("skills", skillVersions);

        return stats;
    }

    private static class SkillVersionInfo {
        private final String skillId;
        private final String version;
        private final long registeredAt;
        private final String revision;

        public SkillVersionInfo(String skillId, String version, long registeredAt, String revision) {
            this.skillId = skillId;
            this.version = version;
            this.registeredAt = registeredAt;
            this.revision = revision;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getVersion() {
            return version;
        }

        public long getRegisteredAt() {
            return registeredAt;
        }

        public String getRevision() {
            return revision;
        }
    }

    private static class PageVersionInfo {
        private final String path;
        private final String contentHash;
        private final long lastModified;
        private final String skillVersion;
        private long lastAccessed;

        public PageVersionInfo(String path, String contentHash, long lastModified, String skillVersion) {
            this.path = path;
            this.contentHash = contentHash;
            this.lastModified = lastModified;
            this.skillVersion = skillVersion;
            this.lastAccessed = System.currentTimeMillis();
        }

        public String getPath() {
            return path;
        }

        public String getContentHash() {
            return contentHash;
        }

        public long getLastModified() {
            return lastModified;
        }

        public String getSkillVersion() {
            return skillVersion;
        }

        public long getLastAccessed() {
            return lastAccessed;
        }

        public void updateAccessTime() {
            this.lastAccessed = System.currentTimeMillis();
        }
    }
}
