package net.ooder.sdk.deployment;

import java.time.LocalDateTime;

/**
 * 部署结果
 */
public class DeploymentResult {
    private boolean success;
    private String skillId;
    private String version;
    private Environment deployedEnvironment;
    private Environment previousEnvironment;
    private DeploymentStatus status;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Throwable error;

    public enum DeploymentStatus {
        PENDING,
        DEPLOYING,
        HEALTH_CHECKING,
        TRAFFIC_SHIFTING,
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Environment getDeployedEnvironment() {
        return deployedEnvironment;
    }

    public void setDeployedEnvironment(Environment deployedEnvironment) {
        this.deployedEnvironment = deployedEnvironment;
    }

    public Environment getPreviousEnvironment() {
        return previousEnvironment;
    }

    public void setPreviousEnvironment(Environment previousEnvironment) {
        this.previousEnvironment = previousEnvironment;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public static DeploymentResult success(String skillId, String version, Environment env) {
        DeploymentResult result = new DeploymentResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        result.setVersion(version);
        result.setDeployedEnvironment(env);
        result.setStatus(DeploymentStatus.COMPLETED);
        result.setEndTime(LocalDateTime.now());
        result.setMessage("Deployment completed successfully");
        return result;
    }

    public static DeploymentResult failure(String skillId, String message, Throwable error) {
        DeploymentResult result = new DeploymentResult();
        result.setSuccess(false);
        result.setSkillId(skillId);
        result.setMessage(message);
        result.setError(error);
        result.setStatus(DeploymentStatus.FAILED);
        result.setEndTime(LocalDateTime.now());
        return result;
    }
}
