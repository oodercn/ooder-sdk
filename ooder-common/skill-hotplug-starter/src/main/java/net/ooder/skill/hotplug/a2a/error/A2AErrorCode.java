package net.ooder.skill.hotplug.a2a.error;

/**
 * A2A错误码
 * 对应Ooder-A2A规范v1.0第5.3节
 */
public enum A2AErrorCode {

    // 4xxx - 客户端错误
    SKILL_NOT_FOUND(4001, "Skill不存在", "检查skillId是否正确"),
    INVALID_STATE_TRANSITION(4002, "状态转换非法", "检查当前Skill状态"),
    CONFIG_VALIDATION_FAILED(4003, "配置验证失败", "检查配置格式"),
    INVALID_MESSAGE_FORMAT(4004, "消息格式无效", "检查消息格式是否符合规范"),
    MISSING_REQUIRED_FIELD(4005, "缺少必填字段", "检查消息中必填字段"),

    // 5xxx - 服务端错误
    INTERNAL_ERROR(5001, "内部错误", "查看服务端日志"),
    DEPENDENCY_NOT_SATISFIED(5002, "依赖未满足", "检查依赖Skill状态"),
    SKILL_INITIALIZATION_FAILED(5003, "Skill初始化失败", "检查Skill配置和环境"),
    MESSAGE_PROCESSING_FAILED(5004, "消息处理失败", "查看详细错误信息"),
    TIMEOUT(5005, "处理超时", "检查Skill响应时间");

    private final int code;
    private final String message;
    private final String suggestion;

    A2AErrorCode(int code, String message, String suggestion) {
        this.code = code;
        this.message = message;
        this.suggestion = suggestion;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public static A2AErrorCode fromCode(int code) {
        for (A2AErrorCode errorCode : values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }
}
