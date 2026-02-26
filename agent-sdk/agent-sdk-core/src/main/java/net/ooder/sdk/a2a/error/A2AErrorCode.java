package net.ooder.sdk.a2a.error;

/**
 * A2A协议错误码枚举
 *
 * <p>定义了A2A通信中可能发生的错误类型及其处理建议</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public enum A2AErrorCode {

    // ========== 客户端错误 (4xxx) ==========

    /**
     * Skill不存在
     */
    SKILL_NOT_FOUND(4001, "Skill不存在", "检查skillId是否正确"),

    /**
     * 状态转换非法
     */
    INVALID_STATE_TRANSITION(4002, "状态转换非法", "检查当前Skill状态"),

    /**
     * 配置验证失败
     */
    CONFIG_VALIDATION_FAILED(4003, "配置验证失败", "检查配置格式"),

    /**
     * 消息格式无效
     */
    INVALID_MESSAGE_FORMAT(4004, "消息格式无效", "检查消息格式是否符合规范"),

    /**
     * 缺少必填字段
     */
    MISSING_REQUIRED_FIELD(4005, "缺少必填字段", "检查消息中必填字段"),

    /**
     * 无效的能力声明
     */
    INVALID_CAPABILITY(4006, "无效的能力声明", "检查能力声明格式"),

    /**
     * 版本不兼容
     */
    VERSION_INCOMPATIBLE(4007, "版本不兼容", "检查Skill版本兼容性"),

    // ========== 服务端错误 (5xxx) ==========

    /**
     * 内部错误
     */
    INTERNAL_ERROR(5001, "内部错误", "查看服务端日志"),

    /**
     * 依赖未满足
     */
    DEPENDENCY_NOT_SATISFIED(5002, "依赖未满足", "检查依赖Skill状态"),

    /**
     * Skill初始化失败
     */
    SKILL_INITIALIZATION_FAILED(5003, "Skill初始化失败", "检查Skill配置和环境"),

    /**
     * 消息处理失败
     */
    MESSAGE_PROCESSING_FAILED(5004, "消息处理失败", "查看详细错误信息"),

    /**
     * 处理超时
     */
    TIMEOUT(5005, "处理超时", "检查Skill响应时间"),

    /**
     * 资源不足
     */
    RESOURCE_EXHAUSTED(5006, "资源不足", "检查系统资源使用情况"),

    /**
     * 循环依赖
     */
    CIRCULAR_DEPENDENCY(5007, "循环依赖", "检查Skill依赖关系");

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
     * 根据错误码获取枚举值
     *
     * @param code 错误码
     * @return 对应的A2AErrorCode，如果找不到则返回INTERNAL_ERROR
     */
    public static A2AErrorCode fromCode(int code) {
        for (A2AErrorCode errorCode : values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
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
}
