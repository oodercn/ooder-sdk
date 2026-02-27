package net.ooder.scene.core;

/**
 * 技能安装进度
 */
public class SkillInstallProgress {
    private String installId;
    private String skillId;
    private String stage;
    private int progress;
    private String message;
    private long startTime;
    private long estimatedEndTime;

    public SkillInstallProgress() {}

    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEstimatedEndTime() { return estimatedEndTime; }
    public void setEstimatedEndTime(long estimatedEndTime) { this.estimatedEndTime = estimatedEndTime; }

    public boolean isCompleted() {
        return "COMPLETED".equals(stage) || "FAILED".equals(stage);
    }
}
