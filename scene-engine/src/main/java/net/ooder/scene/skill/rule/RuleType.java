package net.ooder.scene.skill.rule;

/**
 * 规则类型枚举
 *
 * @author ooder Team
 * @since 2.3.1
 */
public enum RuleType {

    /**
     * 决策规则 - 决定下一步执行什么
     */
    DECISION("decision", "决策规则"),

    /**
     * 转换规则 - 数据转换
     */
    TRANSFORM("transform", "转换规则"),

    /**
     * 验证规则 - 输入验证
     */
    VALIDATION("validation", "验证规则"),

    /**
     * 路由规则 - 能力路由
     */
    ROUTING("routing", "路由规则"),

    /**
     * 降级规则 - 异常处理
     */
    FALLBACK("fallback", "降级规则");

    private final String code;
    private final String name;

    RuleType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RuleType fromCode(String code) {
        if (code == null) {
            return DECISION;
        }
        for (RuleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return DECISION;
    }
}
