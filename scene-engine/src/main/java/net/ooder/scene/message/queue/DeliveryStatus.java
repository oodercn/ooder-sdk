package net.ooder.scene.message.queue;

/**
 * 投递状态枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum DeliveryStatus {
    
    CREATED("created", "已创建"),
    
    SENDING("sending", "发送中"),
    
    PENDING("pending", "待投递"),
    
    DELIVERED("delivered", "已投递"),
    
    READ("read", "已读"),
    
    FAILED("failed", "投递失败"),
    
    RETRYING("retrying", "重试中"),
    
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
    
    public boolean isTerminal() {
        return this == DELIVERED || this == READ || this == ACKNOWLEDGED || this == EXPIRED || this == FAILED;
    }
    
    public boolean canRetry() {
        return this == FAILED || this == RETRYING;
    }
    
    public boolean isSuccess() {
        return this == DELIVERED || this == READ || this == ACKNOWLEDGED;
    }
}
