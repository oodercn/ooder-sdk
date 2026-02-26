package net.ooder.sdk.migration;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * 迁移脚本
 */
public class MigrationScript {
    private String id;
    private String skillId;
    private String fromVersion;
    private String toVersion;
    private ScriptType type;
    private Path scriptPath;
    private String checksum;
    private LocalDateTime createdAt;
    private String description;
    private boolean backupRequired;

    public enum ScriptType {
        SQL, JS, GROOVY, JSON, JAVA
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public String getToVersion() {
        return toVersion;
    }

    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }

    public ScriptType getType() {
        return type;
    }

    public void setType(ScriptType type) {
        this.type = type;
    }

    public Path getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(Path scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isBackupRequired() {
        return backupRequired;
    }

    public void setBackupRequired(boolean backupRequired) {
        this.backupRequired = backupRequired;
    }
}
