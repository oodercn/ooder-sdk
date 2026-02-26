package net.ooder.sdk.a2a.message;

/**
 * Ooder-A2A 消息类型枚举
 *
 * <p>根据Ooder-A2A规范v1.0定义的消息类型</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public enum A2AMessageType {

    /**
     * 发送任务 (Client → Server)
     */
    TASK_SEND("task_send", "发送任务", MessageDirection.CLIENT_TO_SERVER),

    /**
     * 获取任务 (Client → Server)
     */
    TASK_GET("task_get", "获取任务", MessageDirection.CLIENT_TO_SERVER),

    /**
     * 重新订阅 (Client → Server)
     */
    TASK_RESUBSCRIBE("task_resubscribe", "重新订阅", MessageDirection.CLIENT_TO_SERVER),

    /**
     * 取消任务 (Client → Server)
     */
    TASK_CANCEL("task_cancel", "取消任务", MessageDirection.CLIENT_TO_SERVER),

    /**
     * Skill卡片 (Server → Client)
     */
    SKILL_CARD("skill_card", "Skill卡片", MessageDirection.SERVER_TO_CLIENT),

    /**
     * 状态变更 (Server → Client)
     */
    STATE_CHANGE("state_change", "状态变更", MessageDirection.SERVER_TO_CLIENT),

    /**
     * 配置更新 (Client → Server)
     */
    CONFIG_UPDATE("config_update", "配置更新", MessageDirection.CLIENT_TO_SERVER),

    /**
     * 错误响应 (Server → Client)
     */
    ERROR("error", "错误", MessageDirection.SERVER_TO_CLIENT),

    /**
     * 心跳 (双向)
     */
    HEARTBEAT("heartbeat", "心跳", MessageDirection.BIDIRECTIONAL),

    /**
     * 确认 (双向)
     */
    ACK("ack", "确认", MessageDirection.BIDIRECTIONAL);

    private final String code;
    private final String displayName;
    private final MessageDirection direction;

    A2AMessageType(String code, String displayName, MessageDirection direction) {
        this.code = code;
        this.displayName = displayName;
        this.direction = direction;
    }

    /**
     * 获取消息类型代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取消息方向
     */
    public MessageDirection getDirection() {
        return direction;
    }

    /**
     * 根据代码获取消息类型
     *
     * @param code 消息类型代码
     * @return 消息类型，如果不存在返回null
     */
    public static A2AMessageType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (A2AMessageType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 消息方向
     */
    public enum MessageDirection {
        CLIENT_TO_SERVER("客户端→服务端"),
        SERVER_TO_CLIENT("服务端→客户端"),
        BIDIRECTIONAL("双向");

        private final String displayName;

        MessageDirection(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
