package net.ooder.scene.message.queue;

/**
 * 参与者类型枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum ParticipantType {
    
    USER("user", "用户"),
    
    VIRTUAL_AGENT("virtual_agent", "虚拟Agent (LLM驱动)"),
    
    PHYSICAL_AGENT("physical_agent", "物理Agent (外部服务)"),
    
    SYSTEM("system", "系统");
    
    private final String code;
    private final String description;
    
    ParticipantType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isAgent() {
        return this == VIRTUAL_AGENT || this == PHYSICAL_AGENT;
    }
    
    public boolean isVirtual() {
        return this == VIRTUAL_AGENT;
    }
    
    public static ParticipantType fromCode(String code) {
        for (ParticipantType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return USER;
    }
}
