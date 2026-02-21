package net.ooder.scene.core;

/**
 * 技能安装结果
 */
public class SkillInstallResult {
    private String installId;
    private String skillId;
    private boolean success;
    private String message;
    private String status;

    public SkillInstallResult() {}

    public static SkillInstallResult success(String installId, String skillId) {
        SkillInstallResult result = new SkillInstallResult();
        result.setInstallId(installId);
        result.setSkillId(skillId);
        result.setSuccess(true);
        result.setStatus("COMPLETED");
        result.setMessage("安装成功");
        return result;
    }

    public static SkillInstallResult failed(String skillId, String message) {
        SkillInstallResult result = new SkillInstallResult();
        result.setSkillId(skillId);
        result.setSuccess(false);
        result.setStatus("FAILED");
        result.setMessage(message);
        return result;
    }

    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
