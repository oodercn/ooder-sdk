package net.ooder.skill.hotplug.model;

/**
 * 插件安装结果
 */
public class PluginInstallResult {

    private final boolean success;
    private final String skillId;
    private final String message;
    private final long duration;

    public PluginInstallResult(boolean success, String skillId, String message, long duration) {
        this.success = success;
        this.skillId = skillId;
        this.message = message;
        this.duration = duration;
    }

    public static PluginInstallResult success(String skillId) {
        return new PluginInstallResult(true, skillId, "Installation successful", 0);
    }

    public static PluginInstallResult success(String skillId, long duration) {
        return new PluginInstallResult(true, skillId, "Installation successful", duration);
    }

    public static PluginInstallResult failure(String skillId, String message) {
        return new PluginInstallResult(false, skillId, message, 0);
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

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "PluginInstallResult{" +
                "success=" + success +
                ", skillId='" + skillId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
