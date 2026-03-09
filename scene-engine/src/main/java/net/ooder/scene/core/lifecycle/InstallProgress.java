package net.ooder.scene.core.lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * 安装进度
 */
public class InstallProgress {
    private String installId;
    private String sceneId;
    private String skillId;
    private InstallPhase phase;
    private int totalSteps;
    private int completedSteps;
    private String currentStep;
    private double progress;
    private List<StepProgress> steps = new ArrayList<>();
    private long startTime;
    private long estimatedEndTime;
    private String errorMessage;

    public enum InstallPhase {
        INITIALIZING("初始化"),
        DOWNLOADING("下载中"),
        INSTALLING("安装中"),
        CONFIGURING("配置中"),
        ACTIVATING("激活中"),
        COMPLETED("已完成"),
        FAILED("失败"),
        CANCELLED("已取消");

        private final String description;

        InstallPhase(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    public static class StepProgress {
        private String stepId;
        private String stepName;
        private StepStatus status;
        private long startTime;
        private long endTime;
        private String message;

        public enum StepStatus {
            PENDING, RUNNING, COMPLETED, FAILED, SKIPPED
        }

        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public StepStatus getStatus() { return status; }
        public void setStatus(StepStatus status) { this.status = status; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public InstallPhase getPhase() { return phase; }
    public void setPhase(InstallPhase phase) { this.phase = phase; }
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    public int getCompletedSteps() { return completedSteps; }
    public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
    public List<StepProgress> getSteps() { return steps; }
    public void setSteps(List<StepProgress> steps) { this.steps = steps; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEstimatedEndTime() { return estimatedEndTime; }
    public void setEstimatedEndTime(long estimatedEndTime) { this.estimatedEndTime = estimatedEndTime; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean isCompleted() {
        return phase == InstallPhase.COMPLETED;
    }

    public boolean isFailed() {
        return phase == InstallPhase.FAILED;
    }

    public void updateProgress() {
        if (totalSteps > 0) {
            this.progress = (double) completedSteps / totalSteps * 100;
        }
    }
}
