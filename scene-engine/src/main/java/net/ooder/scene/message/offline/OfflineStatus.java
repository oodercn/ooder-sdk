package net.ooder.scene.message.offline;

/**
 * 离线消息状态枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum OfflineStatus {
    
    PENDING("pending", "待投递"),
    DELIVERED("delivered", "已投递"),
    ACKNOWLEDGED("acknowledged", "已确认"),
    EXPIRED("expired", "已过期"),
    FAILED("failed", "投递失败");
    
    private final String code;
    private final String description;
    
    OfflineStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OfflineStatus fromCode(String code) {
        for (OfflineStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
