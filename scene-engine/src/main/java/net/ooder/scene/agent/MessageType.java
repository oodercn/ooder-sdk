package net.ooder.scene.agent;

public enum MessageType {

    TASK_DELEGATE("task_delegate", "任务委派"),

    TASK_RESULT("task_result", "任务结果"),

    COLLAB_REQUEST("collab_request", "协作请求"),

    DATA_SHARE("data_share", "数据共享"),

    STATUS_UPDATE("status_update", "状态更新"),

    HEARTBEAT("heartbeat", "心跳");

    private final String code;
    private final String description;

    MessageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MessageType fromCode(String code) {
        for (MessageType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return STATUS_UPDATE;
    }
}
