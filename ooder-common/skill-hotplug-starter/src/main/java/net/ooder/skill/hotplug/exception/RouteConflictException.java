package net.ooder.skill.hotplug.exception;

/**
 * 路由冲突异常
 * 当不同 Skill 尝试注册相同路由时抛出
 */
public class RouteConflictException extends PluginException {

    private final String path;
    private final String method;
    private final String existingSkillId;
    private final String newSkillId;

    public RouteConflictException(String path, String method, String existingSkillId, String newSkillId) {
        super(String.format("Route conflict: %s %s is already registered by skill '%s', cannot register for skill '%s'",
                method, path, existingSkillId, newSkillId));
        this.path = path;
        this.method = method;
        this.existingSkillId = existingSkillId;
        this.newSkillId = newSkillId;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getExistingSkillId() {
        return existingSkillId;
    }

    public String getNewSkillId() {
        return newSkillId;
    }
}
