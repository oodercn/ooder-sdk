package net.ooder.sdk.migration;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 迁移结果
 */
public class MigrationResult {
    private boolean success;
    private String skillId;
    private String fromVersion;
    private String toVersion;
    private List<String> executedScripts;
    private String message;
    private MigrationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Throwable error;
    private String backupLocation;

    public enum MigrationStatus {
        PENDING,
        BACKING_UP,
        EXECUTING,
        COMPLETED,
        FAILED,
        ROLLED_BACK
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public List<String> getExecutedScripts() {
        return executedScripts;
    }

    public void setExecutedScripts(List<String> executedScripts) {
        this.executedScripts = executedScripts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MigrationStatus getStatus() {
        return status;
    }

    public void setStatus(MigrationStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public void setBackupLocation(String backupLocation) {
        this.backupLocation = backupLocation;
    }

    public static MigrationResult success(String skillId, String fromVersion, String toVersion, List<String> scripts) {
        MigrationResult result = new MigrationResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        result.setFromVersion(fromVersion);
        result.setToVersion(toVersion);
        result.setExecutedScripts(scripts);
        result.setStatus(MigrationStatus.COMPLETED);
        result.setEndTime(LocalDateTime.now());
        result.setMessage("Migration completed successfully");
        return result;
    }

    public static MigrationResult failure(String skillId, String message, Throwable error) {
        MigrationResult result = new MigrationResult();
        result.setSuccess(false);
        result.setSkillId(skillId);
        result.setMessage(message);
        result.setError(error);
        result.setStatus(MigrationStatus.FAILED);
        result.setEndTime(LocalDateTime.now());
        return result;
    }
}
