package net.ooder.sdk.cli.api;

/**
 * CLI 异常类
 *
 * <p>封装 CLI 错误码和错误信息</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CliException extends RuntimeException {

    private final CliErrorCode errorCode;
    private final Object[] args;

    public CliException(CliErrorCode errorCode, Object... args) {
        super(errorCode.formatMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    public CliException(CliErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.formatMessage(args), cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public CliException(String message) {
        super(message);
        this.errorCode = CliErrorCode.UNKNOWN_ERROR;
        this.args = new Object[]{message};
    }

    public CliException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = CliErrorCode.UNKNOWN_ERROR;
        this.args = new Object[]{message};
    }

    /**
     * 获取错误码
     */
    public CliErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 获取错误码字符串
     */
    public String getCode() {
        return errorCode.getCode();
    }

    /**
     * 获取退出码
     */
    public int getExitCode() {
        return errorCode.getExitCode();
    }

    /**
     * 获取格式化后的错误消息
     */
    public String getFormattedMessage() {
        return errorCode.formatMessage(args);
    }

    /**
     * 获取参数
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 创建参数错误异常
     */
    public static CliException invalidArgument(String argName) {
        return new CliException(CliErrorCode.INVALID_ARGUMENT, argName);
    }

    /**
     * 创建缺少参数异常
     */
    public static CliException missingArgument(String argName) {
        return new CliException(CliErrorCode.MISSING_ARGUMENT, argName);
    }

    /**
     * 创建命令未找到异常
     */
    public static CliException commandNotFound(String command) {
        return new CliException(CliErrorCode.COMMAND_NOT_FOUND, command);
    }

    /**
     * 创建 Skill 未找到异常
     */
    public static CliException skillNotFound(String skillId) {
        return new CliException(CliErrorCode.SKILL_NOT_FOUND, skillId);
    }

    /**
     * 创建权限拒绝异常
     */
    public static CliException permissionDenied(String resource) {
        return new CliException(CliErrorCode.PERMISSION_DENIED, resource);
    }

    /**
     * 创建执行失败异常
     */
    public static CliException executionFailed(String reason) {
        return new CliException(CliErrorCode.EXECUTION_FAILED, reason);
    }

    /**
     * 创建执行失败异常（带原因）
     */
    public static CliException executionFailed(String reason, Throwable cause) {
        return new CliException(CliErrorCode.EXECUTION_FAILED, cause, reason);
    }
}
