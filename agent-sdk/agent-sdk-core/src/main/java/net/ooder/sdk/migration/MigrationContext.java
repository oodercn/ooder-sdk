package net.ooder.sdk.migration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 迁移上下文
 */
public class MigrationContext {
    private String skillId;
    private String fromVersion;
    private String toVersion;
    private Path dataDir;
    private Path backupDir;
    private boolean dryRun;
    private boolean autoRollback;
    private Map<String, Object> parameters = new HashMap<>();

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

    public Path getDataDir() {
        return dataDir;
    }

    public void setDataDir(Path dataDir) {
        this.dataDir = dataDir;
    }

    public Path getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(Path backupDir) {
        this.backupDir = backupDir;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isAutoRollback() {
        return autoRollback;
    }

    public void setAutoRollback(boolean autoRollback) {
        this.autoRollback = autoRollback;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }
}
