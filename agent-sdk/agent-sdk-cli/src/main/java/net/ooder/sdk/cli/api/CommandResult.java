package net.ooder.sdk.cli.api;

/**
 * 命令执行结果
 *
 * <p>支持统一错误码体系</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CommandResult {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int INVALID_ARGS = 2;
    public static final int NOT_FOUND = 3;
    public static final int PERMISSION_DENIED = 4;
    public static final int TIMEOUT = 5;

    private final int exitCode;
    private final String message;
    private final Object data;
    private final CliErrorCode errorCode;

    public CommandResult(int exitCode, String message) {
        this(exitCode, message, null, null);
    }

    public CommandResult(int exitCode, String message, Object data) {
        this(exitCode, message, data, null);
    }

    public CommandResult(int exitCode, String message, Object data, CliErrorCode errorCode) {
        this.exitCode = exitCode;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static CommandResult success(String message) {
        return new CommandResult(SUCCESS, message, null, CliErrorCode.SUCCESS);
    }

    public static CommandResult success(String message, Object data) {
        return new CommandResult(SUCCESS, message, data, CliErrorCode.SUCCESS);
    }

    public static CommandResult error(String message) {
        return new CommandResult(ERROR, message, null, CliErrorCode.EXECUTION_FAILED);
    }

    public static CommandResult error(String message, Throwable cause) {
        return new CommandResult(ERROR, message + ": " + cause.getMessage(), null, CliErrorCode.EXECUTION_FAILED);
    }

    public static CommandResult error(String message, int exitCode) {
        return new CommandResult(exitCode, message, null, CliErrorCode.fromExitCode(exitCode));
    }

    public static CommandResult error(CliErrorCode errorCode, Object... args) {
        return new CommandResult(errorCode.getExitCode(), errorCode.formatMessage(args), null, errorCode);
    }

    public static CommandResult error(CliErrorCode errorCode, Throwable cause, Object... args) {
        return new CommandResult(errorCode.getExitCode(), errorCode.formatMessage(args), null, errorCode);
    }

    public static CommandResult invalidArgs(String message) {
        return new CommandResult(INVALID_ARGS, message, null, CliErrorCode.INVALID_ARGUMENT);
    }

    public static CommandResult notFound(String message) {
        return new CommandResult(NOT_FOUND, message, null, CliErrorCode.COMMAND_NOT_FOUND);
    }

    public static CommandResult permissionDenied(String message) {
        return new CommandResult(PERMISSION_DENIED, message, null, CliErrorCode.PERMISSION_DENIED);
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public CliErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorCodeString() {
        return errorCode != null ? errorCode.getCode() : "CLI-001";
    }

    public boolean isSuccess() {
        return exitCode == SUCCESS;
    }

    @Override
    public String toString() {
        if (errorCode != null && !isSuccess()) {
            return String.format("[%s] %s", errorCode.getCode(), message);
        }
        return message;
    }
}
