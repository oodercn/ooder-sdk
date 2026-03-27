package net.ooder.scene.message.queue;

/**
 * 投递状态枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum DeliveryStatus {
    
    PENDING("pending", "待投递"),
    
    DELIVERED("delivered", "已投递"),
    
    FAILED("failed", "投递失败"),
    
    EXPIRED("expired", "已过期"),
    
    ACKNOWLEDGED("acknowledged", "已确认");
    
    private final String code;
    private final String description;
    
    DeliveryStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static DeliveryStatus fromCode(String code) {
        for (DeliveryStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
