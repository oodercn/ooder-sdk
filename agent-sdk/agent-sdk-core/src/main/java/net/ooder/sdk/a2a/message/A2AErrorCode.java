package net.ooder.sdk.a2a.message;

/**
 * Ooder-A2A 错误码枚举
 *
 * <p>根据Ooder-A2A规范v1.0定义的错误码</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public enum A2AErrorCode {

    // ==================== 客户端错误 (4xxx) ====================

    /**
     * Skill不存在
     */
    SKILL_NOT_FOUND(4001, "Skill不存在", "检查skillId是否正确"),

    /**
     * 状态转换非法
     */
    INVALID_STATE_TRANSITION(4002, "状态转换非法", "检查当前状态是否允许该操作"),

    /**
     * 配置验证失败
     */
    CONFIG_VALIDATION_FAILED(4003, "配置验证失败", "检查配置格式是否正确"),

    /**
     * 参数错误
     */
    INVALID_PARAMETER(4004, "参数错误", "检查请求参数"),

    /**
     * 未授权
     */
    UNAUTHORIZED(4005, "未授权", "检查认证信息"),

    /**
     * 任务不存在
     */
    TASK_NOT_FOUND(4006, "任务不存在", "检查taskId是否正确"),

    /**
     * 消息格式错误
     */
    INVALID_MESSAGE_FORMAT(4007, "消息格式错误", "检查消息格式是否符合规范"),

    // ==================== 服务端错误 (5xxx) ====================

    /**
     * 内部错误
     */
    INTERNAL_ERROR(5001, "内部错误", "查看服务端日志"),

    /**
     * 依赖未满足
     */
    DEPENDENCY_NOT_SATISFIED(5002, "依赖未满足", "检查依赖Skill状态"),

    /**
     * 资源不足
     */
    RESOURCE_EXHAUSTED(5003, "资源不足", "检查系统资源使用情况"),

    /**
     * 超时
     */
    TIMEOUT(5004, "操作超时", "稍后重试或增加超时时间"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(5005, "服务不可用", "稍后重试"),

    /**
     * 数据库错误
     */
    DATABASE_ERROR(5006, "数据库错误", "联系管理员");

    private final int code;
    private final String message;
    private final String suggestion;

    A2AErrorCode(int code, String message, String suggestion) {
        this.code = code;
        this.message = message;
        this.suggestion = suggestion;
    }

    /**
     * 获取错误码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取处理建议
     */
    public String getSuggestion() {
        return suggestion;
    }

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 错误码枚举，如果不存在返回null
     */
    public static A2AErrorCode fromCode(int code) {
        for (A2AErrorCode errorCode : values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return null;
    }

    /**
     * 判断是否为客户端错误
     */
    public boolean isClientError() {
        return code >= 4000 && code < 5000;
    }

    /**
     * 判断是否为服务端错误
     */
    public boolean isServerError() {
        return code >= 5000 && code < 6000;
    }

    @Override
    public String toString() {
        return "A2AErrorCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", suggestion='" + suggestion + '\'' +
                '}';
    }
}
