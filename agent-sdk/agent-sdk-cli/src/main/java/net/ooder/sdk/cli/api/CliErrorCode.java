package net.ooder.sdk.cli.api;

/**
 * CLI 错误码枚举
 *
 * <p>统一错误码体系，格式: CLI-XXX</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public enum CliErrorCode {

    // 成功 (0)
    SUCCESS("CLI-000", "Success", 0),

    // 通用错误 (1-99)
    UNKNOWN_ERROR("CLI-001", "Unknown error", 1),
    INVALID_ARGUMENT("CLI-002", "Invalid argument: %s", 2),
    MISSING_ARGUMENT("CLI-003", "Missing required argument: %s", 3),
    INVALID_COMMAND("CLI-004", "Invalid command: %s", 4),
    COMMAND_NOT_FOUND("CLI-005", "Command not found: %s", 5),
    EXECUTION_FAILED("CLI-006", "Command execution failed: %s", 6),
    TIMEOUT("CLI-007", "Command execution timeout", 7),
    INTERRUPTED("CLI-008", "Command execution interrupted", 8),

    // Skill 相关错误 (100-199)
    SKILL_NOT_FOUND("CLI-100", "Skill not found: %s", 100),
    SKILL_NOT_INITIALIZED("CLI-101", "Skill not initialized: %s", 101),
    SKILL_ALREADY_EXISTS("CLI-102", "Skill already exists: %s", 102),
    SKILL_EXECUTION_FAILED("CLI-103", "Skill execution failed: %s", 103),
    SKILL_INSTALL_FAILED("CLI-104", "Skill installation failed: %s", 104),
    SKILL_UNINSTALL_FAILED("CLI-105", "Skill uninstallation failed: %s", 105),
    SKILL_UPDATE_FAILED("CLI-106", "Skill update failed: %s", 106),
    SKILL_DISABLED("CLI-107", "Skill is disabled: %s", 107),
    SKILL_VERSION_MISMATCH("CLI-108", "Skill version mismatch: %s", 108),

    // 场景相关错误 (200-299)
    SCENE_NOT_FOUND("CLI-200", "Scene not found: %s", 200),
    SCENE_ALREADY_EXISTS("CLI-201", "Scene already exists: %s", 201),
    SCENE_CREATION_FAILED("CLI-202", "Scene creation failed: %s", 202),
    SCENE_INVOCATION_FAILED("CLI-203", "Scene invocation failed: %s", 203),
    SCENE_EVENT_FAILED("CLI-204", "Scene event handling failed: %s", 204),
    SCENE_INVALID_STATE("CLI-205", "Scene is in invalid state: %s", 205),

    // 任务相关错误 (300-399)
    TASK_NOT_FOUND("CLI-300", "Task not found: %s", 300),
    TASK_EXECUTION_FAILED("CLI-301", "Task execution failed: %s", 301),
    TASK_CANCELLED("CLI-302", "Task was cancelled: %s", 302),
    TASK_TIMEOUT("CLI-303", "Task execution timeout: %s", 303),

    // 安全相关错误 (400-499)
    PERMISSION_DENIED("CLI-400", "Permission denied: %s", 400),
    AUTHENTICATION_FAILED("CLI-401", "Authentication failed: %s", 401),
    UNAUTHORIZED("CLI-402", "Unauthorized access: %s", 402),
    INJECTION_DETECTED("CLI-403", "Potential injection detected: %s", 403),
    DANGEROUS_COMMAND("CLI-404", "Dangerous command blocked: %s", 404),

    // 配置相关错误 (500-599)
    CONFIG_NOT_FOUND("CLI-500", "Configuration not found: %s", 500),
    CONFIG_INVALID("CLI-501", "Invalid configuration: %s", 501),
    CONFIG_LOAD_FAILED("CLI-502", "Failed to load configuration: %s", 502),

    // 扩展相关错误 (600-699)
    EXTENSION_NOT_FOUND("CLI-600", "Extension not found: %s", 600),
    EXTENSION_LOAD_FAILED("CLI-601", "Failed to load extension: %s", 601),
    EXTENSION_INIT_FAILED("CLI-602", "Extension initialization failed: %s", 602),
    EXTENSION_INCOMPATIBLE("CLI-603", "Extension incompatible: %s", 603),

    // 系统相关错误 (900-999)
    SYSTEM_ERROR("CLI-900", "System error: %s", 900),
    RESOURCE_NOT_FOUND("CLI-901", "Resource not found: %s", 901),
    IO_ERROR("CLI-902", "IO error: %s", 902),
    NETWORK_ERROR("CLI-903", "Network error: %s", 903);

    private final String code;
    private final String messageTemplate;
    private final int exitCode;

    CliErrorCode(String code, String messageTemplate, int exitCode) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.exitCode = exitCode;
    }

    /**
     * 获取错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取消息模板
     */
    public String getMessageTemplate() {
        return messageTemplate;
    }

    /**
     * 获取退出码
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * 格式化错误消息
     */
    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return messageTemplate;
        }
        try {
            return String.format(messageTemplate, args);
        } catch (Exception e) {
            return messageTemplate;
        }
    }

    /**
     * 根据退出码查找错误码
     */
    public static CliErrorCode fromExitCode(int exitCode) {
        for (CliErrorCode errorCode : values()) {
            if (errorCode.exitCode == exitCode) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 根据错误码字符串查找
     */
    public static CliErrorCode fromCode(String code) {
        for (CliErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }
}
