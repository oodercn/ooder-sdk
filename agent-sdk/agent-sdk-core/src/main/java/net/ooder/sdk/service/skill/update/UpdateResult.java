package net.ooder.sdk.service.skill.update;

import java.time.LocalDateTime;

/**
 * 更新结果
 */
public class UpdateResult {
    private boolean success;
    private String skillId;
    private String oldVersion;
    private String newVersion;
    private String message;
    private UpdateStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Throwable error;

    public enum UpdateStatus {
        PENDING, DOWNLOADING, INSTALLING, COMPLETED, FAILED, ROLLED_BACK
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

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpdateStatus getStatus() {
        return status;
    }

    public void setStatus(UpdateStatus status) {
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

    public static UpdateResult success(String skillId, String oldVersion, String newVersion) {
        UpdateResult result = new UpdateResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        result.setOldVersion(oldVersion);
        result.setNewVersion(newVersion);
        result.setStatus(UpdateStatus.COMPLETED);
        result.setEndTime(LocalDateTime.now());
        return result;
    }

    public static UpdateResult failure(String skillId, String message, Throwable error) {
        UpdateResult result = new UpdateResult();
        result.setSuccess(false);
        result.setSkillId(skillId);
        result.setMessage(message);
        result.setError(error);
        result.setStatus(UpdateStatus.FAILED);
        result.setEndTime(LocalDateTime.now());
        return result;
    }
}
