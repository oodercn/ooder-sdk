package net.ooder.skill.hotplug.model;

/**
 * 插件卸载结果
 */
public class PluginUninstallResult {

    private final boolean success;
    private final String skillId;
    private final String message;

    public PluginUninstallResult(boolean success, String skillId, String message) {
        this.success = success;
        this.skillId = skillId;
        this.message = message;
    }

    public static PluginUninstallResult success(String skillId) {
        return new PluginUninstallResult(true, skillId, "Uninstallation successful");
    }

    public static PluginUninstallResult failure(String skillId, String message) {
        return new PluginUninstallResult(false, skillId, message);
    }

    // Getters

    public boolean isSuccess() {
        return success;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PluginUninstallResult{" +
                "success=" + success +
                ", skillId='" + skillId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
