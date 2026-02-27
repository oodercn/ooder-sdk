package net.ooder.scene.core;

public class SkillUninstallResult {
    private String skillId;
    private boolean success;
    private String message;

    public SkillUninstallResult() {}

    public static SkillUninstallResult success(String skillId) {
        SkillUninstallResult result = new SkillUninstallResult();
        result.setSkillId(skillId);
        result.setSuccess(true);
        result.setMessage("Uninstall success");
        return result;
    }

    public static SkillUninstallResult failed(String skillId, String message) {
        SkillUninstallResult result = new SkillUninstallResult();
        result.setSkillId(skillId);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
