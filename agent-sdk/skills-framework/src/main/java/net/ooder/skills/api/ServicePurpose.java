package net.ooder.skills.api;

/**
 * 服务目的
 *
 * <p>定义技能的服务属性和使用场景，可多选组合</p>
 *
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>目的描述技能的"使用场景"和"服务对象"</li>
 *   <li>与形态、分类是正交维度</li>
 *   <li>支持多选组合，如：PERSONAL + INSTANT + REACTIVE</li>
 * </ul>
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public enum ServicePurpose {

    // ========== 服务范围维度 ==========

    PERSONAL("personal", "个人服务", Scope.PERSONAL, null, null),
    TEAM("team", "团队服务", Scope.TEAM, null, null),
    ORGANIZATION("organization", "组织服务", Scope.ORGANIZATION, null, null),
    PUBLIC("public", "公共服务", Scope.PUBLIC, null, null),

    // ========== 服务时效维度 ==========

    INSTANT("instant", "即时服务", null, Duration.INSTANT, null),
    PERSISTENT("persistent", "持续服务", null, Duration.PERSISTENT, null),
    SCHEDULED("scheduled", "定时服务", null, Duration.SCHEDULED, null),

    // ========== 服务主动性维度 ==========

    PROACTIVE("proactive", "主动服务", null, null, Initiative.PROACTIVE),
    REACTIVE("reactive", "被动服务", null, null, Initiative.REACTIVE);

    private final String code;
    private final String name;
    private final Scope scope;
    private final Duration duration;
    private final Initiative initiative;

    ServicePurpose(String code, String name, Scope scope, Duration duration, Initiative initiative) {
        this.code = code;
        this.name = name;
        this.scope = scope;
        this.duration = duration;
        this.initiative = initiative;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public Scope getScope() { return scope; }
    public Duration getDuration() { return duration; }
    public Initiative getInitiative() { return initiative; }

    public boolean isScope() { return scope != null; }
    public boolean isDuration() { return duration != null; }
    public boolean isInitiative() { return initiative != null; }

    public static ServicePurpose fromCode(String code) {
        if (code == null) return null;
        for (ServicePurpose purpose : values()) {
            if (purpose.code.equalsIgnoreCase(code)) {
                return purpose;
            }
        }
        return null;
    }

    public enum Scope { PERSONAL, TEAM, ORGANIZATION, PUBLIC }
    public enum Duration { INSTANT, PERSISTENT, SCHEDULED }
    public enum Initiative { PROACTIVE, REACTIVE }
}
