package net.ooder.scene.core.lifecycle;

/**
 * 停用结果
 */
public class DeactivateResult {
    private String sceneId;
    private String skillId;
    private boolean success;
    private String errorMessage;
    private long deactivateTime;

    public static DeactivateResult success(String sceneId, String skillId) {
        DeactivateResult result = new DeactivateResult();
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setSuccess(true);
        result.setDeactivateTime(System.currentTimeMillis());
        return result;
    }

    public static DeactivateResult failure(String sceneId, String skillId, String error) {
        DeactivateResult result = new DeactivateResult();
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setSuccess(false);
        result.setErrorMessage(error);
        return result;
    }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public long getDeactivateTime() { return deactivateTime; }
    public void setDeactivateTime(long deactivateTime) { this.deactivateTime = deactivateTime; }
}
