package net.ooder.scene.session.unified;

/**
 * 会话类型枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum SessionType {
    
    USER("user", "用户会话"),
    
    AGENT("agent", "Agent会话"),
    
    SCENE("scene", "场景会话"),
    
    CONVERSATION("conversation", "对话会话");
    
    private final String code;
    private final String description;
    
    SessionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static SessionType fromCode(String code) {
        for (SessionType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return USER;
    }
}
