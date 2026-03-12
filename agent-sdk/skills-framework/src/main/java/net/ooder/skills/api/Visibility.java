package net.ooder.skills.api;

/**
 * 技能可见性枚举
 *
 * <p>定义技能在技能市场中的可见范围</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public enum Visibility {

    /**
     * 公开可见
     *
     * <p>所有用户可见，可在技能市场搜索和安装</p>
     */
    PUBLIC("public", "公开", "所有用户可见"),

    /**
     * 开发者可见
     *
     * <p>仅开发者可见，用于开发测试阶段</p>
     */
    DEVELOPER("developer", "开发者", "仅开发者可见"),

    /**
     * 内部使用
     *
     * <p>系统内部使用，不在技能市场显示</p>
     */
    INTERNAL("internal", "内部", "系统内部使用");

    private final String code;
    private final String name;
    private final String description;

    Visibility(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 是否公开可见
     */
    public boolean isPublic() {
        return this == PUBLIC;
    }

    /**
     * 是否开发者可见
     */
    public boolean isDeveloper() {
        return this == DEVELOPER;
    }

    /**
     * 是否内部使用
     */
    public boolean isInternal() {
        return this == INTERNAL;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举值，找不到返回 null
     */
    public static Visibility fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Visibility visibility : values()) {
            if (visibility.code.equalsIgnoreCase(code)) {
                return visibility;
            }
        }
        return null;
    }

    /**
     * 根据代码获取枚举，带默认值
     *
     * @param code 代码
     * @param defaultValue 默认值
     * @return 枚举值
     */
    public static Visibility fromCode(String code, Visibility defaultValue) {
        Visibility result = fromCode(code);
        return result != null ? result : defaultValue;
    }
}
