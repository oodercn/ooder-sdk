package net.ooder.sdk.cli;

import net.ooder.sdk.cli.api.CliErrorCode;

/**
 * CLI 执行结果
 *
 * <p>用于嵌入式使用，提供结构化的执行结果</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CliExecutionResult {

    private final int exitCode;
    private final String message;
    private final Object data;
    private final CliErrorCode errorCode;

    public CliExecutionResult(int exitCode, String message, Object data, CliErrorCode errorCode) {
        this.exitCode = exitCode;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    /**
     * 获取退出码
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * 获取输出消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取结构化数据
     */
    public Object getData() {
        return data;
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
    public String getErrorCodeString() {
        return errorCode != null ? errorCode.getCode() : "CLI-001";
    }

    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return exitCode == 0;
    }

    /**
     * 是否失败
     */
    public boolean isError() {
        return exitCode != 0;
    }

    /**
     * 创建成功结果
     */
    public static CliExecutionResult success(String message) {
        return new CliExecutionResult(0, message, null, CliErrorCode.SUCCESS);
    }

    /**
     * 创建成功结果（带数据）
     */
    public static CliExecutionResult success(String message, Object data) {
        return new CliExecutionResult(0, message, data, CliErrorCode.SUCCESS);
    }

    /**
     * 创建错误结果
     */
    public static CliExecutionResult error(String message) {
        return new CliExecutionResult(1, message, null, CliErrorCode.EXECUTION_FAILED);
    }

    /**
     * 创建错误结果（带退出码）
     */
    public static CliExecutionResult error(int exitCode, String message) {
        return new CliExecutionResult(exitCode, message, null, CliErrorCode.fromExitCode(exitCode));
    }

    /**
     * 创建错误结果（带错误码）
     */
    public static CliExecutionResult error(CliErrorCode errorCode, Object... args) {
        return new CliExecutionResult(
            errorCode.getExitCode(),
            errorCode.formatMessage(args),
            null,
            errorCode
        );
    }

    @Override
    public String toString() {
        if (errorCode != null && !isSuccess()) {
            return String.format("[%s] ExitCode=%d, Message=%s", errorCode.getCode(), exitCode, message);
        }
        return String.format("ExitCode=%d, Message=%s", exitCode, message);
    }
}
