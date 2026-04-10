package net.ooder.skill.hotplug.model;

/**
 * 插件更新结果
 */
public class PluginUpdateResult {

    private final boolean success;
    private final String skillId;
    private final String oldVersion;
    private final String newVersion;
    private final String message;

    public PluginUpdateResult(boolean success, String skillId, String oldVersion, 
                              String newVersion, String message) {
        this.success = success;
        this.skillId = skillId;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.message = message;
    }

    public static PluginUpdateResult success(String skillId, String oldVersion, String newVersion) {
        return new PluginUpdateResult(true, skillId, oldVersion, newVersion, "Update successful");
    }

    public static PluginUpdateResult success(String skillId) {
        return new PluginUpdateResult(true, skillId, null, null, "Update successful");
    }

    public static PluginUpdateResult failure(String skillId, String message) {
        return new PluginUpdateResult(false, skillId, null, null, message);
    }

    // Getters

    public boolean isSuccess() {
        return success;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PluginUpdateResult{" +
                "success=" + success +
                ", skillId='" + skillId + '\'' +
                ", oldVersion='" + oldVersion + '\'' +
                ", newVersion='" + newVersion + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
