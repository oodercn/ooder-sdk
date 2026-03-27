package net.ooder.scene.agent.context;

/**
 * Agent 类型枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum AgentType {
    
    VIRTUAL("virtual", "虚拟Agent (LLM驱动)"),
    
    PHYSICAL("physical", "物理Agent (外部服务)");
    
    private final String code;
    private final String description;
    
    AgentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AgentType fromCode(String code) {
        for (AgentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return VIRTUAL;
    }
}
