package net.ooder.scene.core.decision;

/**
 * 决策模式枚举
 * 
 * <p>定义决策引擎的三种工作模式：</p>
 * <ul>
 *   <li>ONLINE_ONLY - 仅在线决策，依赖LLM</li>
 *   <li>OFFLINE_ONLY - 仅离线决策，依赖规则引擎</li>
 *   <li>ONLINE_FIRST - 优先在线，降级离线（默认）</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public enum DecisionMode {
    
    /**
     * 仅在线决策
     * 
     * <p>完全依赖LLM进行决策，LLM不可用时直接失败</p>
     * <p>适用场景：需要高质量决策、LLM必须可用</p>
     */
    ONLINE_ONLY("online_only", "仅在线", "完全依赖LLM进行决策"),
    
    /**
     * 仅离线决策
     * 
     * <p>完全依赖规则引擎进行决策，不使用LLM</p>
     * <p>适用场景：LLM不可用、数据安全要求高、成本控制</p>
     */
    OFFLINE_ONLY("offline_only", "仅离线", "完全依赖规则引擎进行决策"),
    
    /**
     * 优先在线，降级离线（默认）
     * 
     * <p>优先使用LLM进行决策，LLM不可用时降级到规则引擎</p>
     * <p>适用场景：默认模式，平衡质量和可用性</p>
     */
    ONLINE_FIRST("online_first", "优先在线", "优先使用LLM，降级到规则引擎");

    private final String code;
    private final String name;
    private final String description;

    DecisionMode(String code, String name, String description) {
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
     * 是否需要LLM
     */
    public boolean needsLlm() {
        return this == ONLINE_ONLY || this == ONLINE_FIRST;
    }

    /**
     * 是否需要规则引擎
     */
    public boolean needsRuleEngine() {
        return this == OFFLINE_ONLY || this == ONLINE_FIRST;
    }

    /**
     * 是否允许降级
     */
    public boolean allowsFallback() {
        return this == ONLINE_FIRST;
    }

    /**
     * 根据代码获取模式
     */
    public static DecisionMode fromCode(String code) {
        if (code == null) {
            return ONLINE_FIRST;
        }
        for (DecisionMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        return ONLINE_FIRST;
    }
}
