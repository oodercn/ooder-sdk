package net.ooder.sdk.cli.api;

public class CommandResult {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int INVALID_ARGS = 2;
    public static final int NOT_FOUND = 3;
    public static final int PERMISSION_DENIED = 4;
    public static final int TIMEOUT = 5;
    public static final int ASYNC_SUBMITTED = 10;

    private final int exitCode;
    private final String message;
    private final Object data;
    private final CliErrorCode errorCode;
    private final String taskId;

    public CommandResult(int exitCode, String message) {
        this(exitCode, message, null, null, null);
    }

    public CommandResult(int exitCode, String message, Object data) {
        this(exitCode, message, data, null, null);
    }

    public CommandResult(int exitCode, String message, Object data, CliErrorCode errorCode) {
        this(exitCode, message, data, errorCode, null);
    }

    public CommandResult(int exitCode, String message, Object data, CliErrorCode errorCode, String taskId) {
        this.exitCode = exitCode;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.taskId = taskId;
    }

    public static CommandResult success(String message) {
        return new CommandResult(SUCCESS, message, null, CliErrorCode.SUCCESS, null);
    }

    public static CommandResult success(String message, Object data) {
        return new CommandResult(SUCCESS, message, data, CliErrorCode.SUCCESS, null);
    }

    public static CommandResult error(String message) {
        return new CommandResult(ERROR, message, null, CliErrorCode.EXECUTION_FAILED, null);
    }

    public static CommandResult error(String message, Throwable cause) {
        return new CommandResult(ERROR, message + ": " + cause.getMessage(), null, CliErrorCode.EXECUTION_FAILED, null);
    }

    public static CommandResult error(String message, int exitCode) {
        return new CommandResult(exitCode, message, null, CliErrorCode.fromExitCode(exitCode), null);
    }

    public static CommandResult error(CliErrorCode errorCode, Object... args) {
        return new CommandResult(errorCode.getExitCode(), errorCode.formatMessage(args), null, errorCode, null);
    }

    public static CommandResult error(CliErrorCode errorCode, Throwable cause, Object... args) {
        return new CommandResult(errorCode.getExitCode(), errorCode.formatMessage(args), null, errorCode, null);
    }

    public static CommandResult invalidArgs(String message) {
        return new CommandResult(INVALID_ARGS, message, null, CliErrorCode.INVALID_ARGUMENT, null);
    }

    public static CommandResult notFound(String message) {
        return new CommandResult(NOT_FOUND, message, null, CliErrorCode.COMMAND_NOT_FOUND, null);
    }

    public static CommandResult permissionDenied(String message) {
        return new CommandResult(PERMISSION_DENIED, message, null, CliErrorCode.PERMISSION_DENIED, null);
    }

    public static CommandResult asyncSubmitted(String taskId, String message) {
        return new CommandResult(ASYNC_SUBMITTED, message, null, CliErrorCode.SUCCESS, taskId);
    }

    public static CommandResult asyncSubmitted(String taskId, String message, Object data) {
        return new CommandResult(ASYNC_SUBMITTED, message, data, CliErrorCode.SUCCESS, taskId);
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

    public String getTaskId() {
        return taskId;
    }

    public boolean isAsync() {
        return taskId != null;
    }

    public boolean isSuccess() {
        return exitCode == SUCCESS || exitCode == ASYNC_SUBMITTED;
    }

    @Override
    public String toString() {
        if (isAsync()) {
            return String.format("[ASYNC] TaskId: %s - %s", taskId, message);
        }
        if (errorCode != null && !isSuccess()) {
            return String.format("[%s] %s", errorCode.getCode(), message);
        }
        return message;
    }
}
