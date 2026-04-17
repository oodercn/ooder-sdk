package net.ooder.skill.install.dto;

public class InstallProgressDTO {
    private String installId;
    private String profile;
    private String status;
    private String currentStep;
    private String currentSkill;
    private int totalSkills;
    private int installedSkills;
    private int failedSkills;
    private long startTime;
    private long endTime;
    private String error;

    public InstallProgressDTO() {}

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getCurrentSkill() {
        return currentSkill;
    }

    public void setCurrentSkill(String currentSkill) {
        this.currentSkill = currentSkill;
    }

    public int getTotalSkills() {
        return totalSkills;
    }

    public void setTotalSkills(int totalSkills) {
        this.totalSkills = totalSkills;
    }

    public int getInstalledSkills() {
        return installedSkills;
    }

    public void setInstalledSkills(int installedSkills) {
        this.installedSkills = installedSkills;
    }

    public int getFailedSkills() {
        return failedSkills;
    }

    public void setFailedSkills(int failedSkills) {
        this.failedSkills = failedSkills;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
