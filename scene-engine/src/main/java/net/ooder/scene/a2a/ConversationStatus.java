package net.ooder.scene.a2a;

/**
 * 对话状态枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum ConversationStatus {
    
    ACTIVE("active", "活跃"),
    
    PAUSED("paused", "暂停"),
    
    ENDED("ended", "已结束");
    
    private final String code;
    private final String description;
    
    ConversationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ConversationStatus fromCode(String code) {
        for (ConversationStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
