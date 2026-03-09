package net.ooder.scene.core.lifecycle;

/**
 * 卸载结果
 */
public class UninstallResult {
    private String sceneId;
    private String skillId;
    private boolean success;
    private String errorMessage;
    private long uninstallTime;

    public static UninstallResult success(String sceneId, String skillId) {
        UninstallResult result = new UninstallResult();
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setSuccess(true);
        result.setUninstallTime(System.currentTimeMillis());
        return result;
    }

    public static UninstallResult failure(String sceneId, String skillId, String error) {
        UninstallResult result = new UninstallResult();
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
    public long getUninstallTime() { return uninstallTime; }
    public void setUninstallTime(long uninstallTime) { this.uninstallTime = uninstallTime; }
}
