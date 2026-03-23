package net.ooder.scene.agent.persistence;

public enum MessageStatus {

    PENDING("pending", "待投递"),

    DELIVERED("delivered", "已投递"),

    ACKNOWLEDGED("acknowledged", "已确认"),

    FAILED("failed", "投递失败"),

    EXPIRED("expired", "已过期");

    private final String code;
    private final String description;

    MessageStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MessageStatus fromCode(String code) {
        for (MessageStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
