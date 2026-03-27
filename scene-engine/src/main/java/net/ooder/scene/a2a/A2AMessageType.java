package net.ooder.scene.a2a;

/**
 * A2A 消息类型枚举
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public enum A2AMessageType {
    
    TASK_REQUEST("task_request", "任务请求"),
    
    TASK_RESPONSE("task_response", "任务响应"),
    
    TASK_STATUS("task_status", "任务状态更新"),
    
    COLLABORATION_INVITE("collaboration_invite", "协作邀请"),
    
    COLLABORATION_ACCEPT("collaboration_accept", "接受协作"),
    
    COLLABORATION_REJECT("collaboration_reject", "拒绝协作"),
    
    DATA_SHARE("data_share", "数据共享"),
    
    DATA_REQUEST("data_request", "数据请求"),
    
    NOTIFICATION("notification", "通知"),
    
    ALERT("alert", "警报"),
    
    CHAT("chat", "聊天"),
    
    COMMAND("command", "命令"),
    
    QUERY("query", "查询"),
    
    HEARTBEAT("heartbeat", "心跳"),
    
    HANDSHAKE("handshake", "握手"),
    
    DISCONNECT("disconnect", "断开连接");
    
    private final String code;
    private final String description;
    
    A2AMessageType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static A2AMessageType fromCode(String code) {
        for (A2AMessageType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return CHAT;
    }
}
