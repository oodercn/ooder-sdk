package net.ooder.scene.a2a;

/**
 * 路由策略枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum RouteStrategy {
    
    DIRECT("direct", "直接路由"),
    CAPABILITY("capability", "能力匹配"),
    ROLE("role", "角色匹配"),
    RULE("rule", "规则匹配"),
    BROADCAST("broadcast", "广播"),
    DEFAULT("default", "默认路由");
    
    private final String code;
    private final String description;
    
    RouteStrategy(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static RouteStrategy fromCode(String code) {
        for (RouteStrategy strategy : values()) {
            if (strategy.code.equalsIgnoreCase(code)) {
                return strategy;
            }
        }
        return DEFAULT;
    }
}
