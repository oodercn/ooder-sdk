package net.ooder.scene.core;

/**
 * CAP 路由策略枚举
 *
 * <p>定义能力路由的选择策略，当一个能力绑定多个 Skill 时使用。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public enum RoutingStrategy {

    /**
     * 按优先级选择
     * 
     * <p>选择优先级最高的可用 Skill。优先级通过 {@link SkillBinding#getPriority()} 获取。</p>
     */
    PRIORITY("priority", "按优先级选择"),

    /**
     * 轮询选择
     * 
     * <p>依次选择每个可用的 Skill，实现负载均衡。</p>
     */
    ROUND_ROBIN("round-robin", "轮询选择"),

    /**
     * 随机选择
     * 
     * <p>从可用的 Skill 中随机选择一个。</p>
     */
    RANDOM("random", "随机选择"),

    /**
     * 最小负载选择
     * 
     * <p>选择当前负载最低的 Skill。负载通过 {@link SkillBinding#getLoad()} 获取。</p>
     */
    LEAST_LOAD("least-load", "最小负载选择");

    private final String code;
    private final String description;

    RoutingStrategy(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RoutingStrategy fromCode(String code) {
        for (RoutingStrategy strategy : values()) {
            if (strategy.code.equalsIgnoreCase(code)) {
                return strategy;
            }
        }
        return PRIORITY;
    }
}
