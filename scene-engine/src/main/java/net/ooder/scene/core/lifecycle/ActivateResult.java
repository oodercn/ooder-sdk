package net.ooder.scene.core.lifecycle;

/**
 * 激活结果
 */
public class ActivateResult {
    private String activateId;
    private String sceneId;
    private String skillId;
    private String role;
    private boolean success;
    private String errorMessage;
    private long activateTime;

    public static ActivateResult success(String activateId, String sceneId, String skillId, String role) {
        ActivateResult result = new ActivateResult();
        result.setActivateId(activateId);
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setRole(role);
        result.setSuccess(true);
        result.setActivateTime(System.currentTimeMillis());
        return result;
    }

    public static ActivateResult failure(String sceneId, String skillId, String error) {
        ActivateResult result = new ActivateResult();
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setSuccess(false);
        result.setErrorMessage(error);
        return result;
    }

    public String getActivateId() { return activateId; }
    public void setActivateId(String activateId) { this.activateId = activateId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public long getActivateTime() { return activateTime; }
    public void setActivateTime(long activateTime) { this.activateTime = activateTime; }
}
