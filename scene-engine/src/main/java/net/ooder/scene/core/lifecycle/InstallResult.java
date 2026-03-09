package net.ooder.scene.core.lifecycle;

import java.util.Map;

/**
 * 安装结果
 */
public class InstallResult {
    private String installId;
    private String sceneId;
    private String skillId;
    private boolean success;
    private String errorMessage;
    private Map<String, Object> installedConfig;
    private long installTime;

    public static InstallResult success(String installId, String sceneId, String skillId, Map<String, Object> config) {
        InstallResult result = new InstallResult();
        result.setInstallId(installId);
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setSuccess(true);
        result.setInstalledConfig(config);
        result.setInstallTime(System.currentTimeMillis());
        return result;
    }

    public static InstallResult failure(String sceneId, String skillId, String errorMessage) {
        InstallResult result = new InstallResult();
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Map<String, Object> getInstalledConfig() { return installedConfig; }
    public void setInstalledConfig(Map<String, Object> installedConfig) { this.installedConfig = installedConfig; }
    public long getInstallTime() { return installTime; }
    public void setInstallTime(long installTime) { this.installTime = installTime; }
}
