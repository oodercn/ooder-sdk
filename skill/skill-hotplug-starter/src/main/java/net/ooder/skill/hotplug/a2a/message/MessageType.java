package net.ooder.skill.hotplug.a2a.message;

/**
 * A2A消息类型枚举
 * 对应Ooder-A2A规范v1.0第5.1节
 */
public enum MessageType {
    // 客户端→服务端
    TASK_SEND("task_send", "发送任务"),
    TASK_GET("task_get", "获取任务"),
    TASK_RESUBSCRIBE("task_resubscribe", "重新订阅"),
    TASK_CANCEL("task_cancel", "取消任务"),
    CONFIG_UPDATE("config_update", "配置更新"),

    // 服务端→客户端
    SKILL_CARD("skill_card", "Skill卡片"),
    STATE_CHANGE("state_change", "状态变更"),
    TASK_UPDATE("task_update", "任务更新"),
    ERROR("error", "错误消息"),

    // 双向
    HEARTBEAT("heartbeat", "心跳"),
    PING("ping", "ping"),
    PONG("pong", "pong");

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
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + code);
    }
}
