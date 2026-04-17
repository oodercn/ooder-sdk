package net.ooder.sdk.cli.security;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.security.PermissionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 安全命令代理
 *
 * <p>复用 PermissionEngine 实现权限控制，添加注入检测和危险字符过滤</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
public class SecureCommandProxy implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SecureCommandProxy.class);

    private final CliCommand targetCommand;
    private final PermissionEngine permissionEngine;
    private final CommandAuditor auditor;

    // 命令白名单 - 可从配置注入
    private Set<String> commandWhitelist;

    // 危险字符过滤器 - 防止命令注入
    private static final Pattern DANGEROUS_CHARS = Pattern.compile(
            "[;|&$`\\{}\\[\\]\\(\\)\\*\\?<>]"
    );

    // SQL注入检测模式
    private static final Pattern SQL_INJECTION = Pattern.compile(
            "(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION)\\b)|(--|#|/\\*)",
            Pattern.CASE_INSENSITIVE
    );

    // 脚本注入检测模式
    private static final Pattern SCRIPT_INJECTION = Pattern.compile(
            "<script|javascript:|on\\w+\\s*=",
            Pattern.CASE_INSENSITIVE
    );

    // 路径遍历检测模式
    private static final Pattern PATH_TRAVERSAL = Pattern.compile(
            "\\.\\./|\\.\\\\|%2e%2e%2f",
            Pattern.CASE_INSENSITIVE
    );

    // 敏感字段列表 - 可从配置注入
    private List<String> sensitiveKeys = Arrays.asList(
            "password", "secret", "token", "key", "api-key", "credential", "auth"
    );

    public SecureCommandProxy(CliCommand targetCommand, PermissionEngine permissionEngine,
                              CommandAuditor auditor, Set<String> commandWhitelist) {
        this.targetCommand = targetCommand;
        this.permissionEngine = permissionEngine;
        this.auditor = auditor;
        this.commandWhitelist = commandWhitelist != null ? commandWhitelist : new HashSet<>();
    }

    @PostConstruct
    public void init() {
        log.info("SecureCommandProxy initialized for command: {}", targetCommand.getName());
    }

    @Override
    public String getName() {
        return targetCommand.getName();
    }

    @Override
    public String getDescription() {
        return targetCommand.getDescription();
    }

    @Override
    public String getUsage() {
        return targetCommand.getUsage();
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String userId = context.getCurrentUser();
        String commandName = getName();

        // 审计记录
        auditor.recordCommandAttempt(userId, commandName, context.getAttributes());

        try {
            // 1. 白名单校验
            if (!isCommandAllowed(commandName)) {
                log.warn("Command not in whitelist: {} by user: {}", commandName, userId);
                auditor.recordCommandDenied(userId, commandName, "Not in whitelist");
                return CommandResult.permissionDenied("Command not allowed: " + commandName);
            }

            // 2. 参数安全校验
            ValidationResult validation = validateParameters(context);
            if (!validation.isValid()) {
                log.warn("Parameter validation failed for command: {} - {}", commandName, validation.getError());
                auditor.recordCommandDenied(userId, commandName, validation.getError());
                return CommandResult.invalidArgs("Security validation failed: " + validation.getError());
            }

            // 3. 权限检查
            if (!hasPermission(userId, commandName)) {
                log.warn("Permission denied: {} for user: {}", commandName, userId);
                auditor.recordCommandDenied(userId, commandName, "Permission denied");
                return CommandResult.permissionDenied("Permission denied for command: " + commandName);
            }

            // 4. 过滤敏感参数（用于审计）
            filterSensitiveParams(context);

            // 5. 执行目标命令
            CommandResult result = targetCommand.execute(context);

            // 6. 记录成功
            auditor.recordCommandSuccess(userId, commandName, result);

            return result;

        } catch (Exception e) {
            // 记录失败
            auditor.recordCommandFailure(userId, commandName, e);
            log.error("Command execution failed: {}", commandName, e);
            return CommandResult.error("Command execution failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isInteractive() {
        return targetCommand.isInteractive();
    }

    @Override
    public String getCategory() {
        return targetCommand.getCategory();
    }

    @Override
    public String[] getAliases() {
        return targetCommand.getAliases();
    }

    @Override
    public boolean validate(String[] args) {
        return targetCommand.validate(args);
    }

    /**
     * 检查命令是否允许执行
     *
     * @param commandName 命令名称
     * @return 是否允许
     */
    private boolean isCommandAllowed(String commandName) {
        if (commandWhitelist == null || commandWhitelist.isEmpty()) {
            return true; // 白名单为空时允许所有（开发模式）
        }
        return commandWhitelist.contains(commandName);
    }

    /**
     * 参数安全校验
     *
     * @param context 命令上下文
     * @return 校验结果
     */
    private ValidationResult validateParameters(CommandContext context) {
        Map<String, Object> attributes = context.getAttributes();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 1. 校验键名
            if (DANGEROUS_CHARS.matcher(key).find()) {
                return ValidationResult.error("Dangerous characters in parameter key: " + key);
            }

            // 2. 校验字符串值
            if (value instanceof String) {
                String strValue = (String) value;

                // SQL注入检测
                if (SQL_INJECTION.matcher(strValue).find()) {
                    return ValidationResult.error("SQL injection detected in parameter: " + key);
                }

                // 脚本注入检测
                if (SCRIPT_INJECTION.matcher(strValue).find()) {
                    return ValidationResult.error("Script injection detected in parameter: " + key);
                }

                // 路径遍历检测
                if (PATH_TRAVERSAL.matcher(strValue).find()) {
                    return ValidationResult.error("Path traversal detected in parameter: " + key);
                }

                // 危险字符检测
                if (DANGEROUS_CHARS.matcher(strValue).find()) {
                    return ValidationResult.error("Dangerous characters in parameter value: " + key);
                }
            }
        }

        return ValidationResult.success();
    }

    /**
     * 检查用户是否有权限执行命令
     *
     * @param userId 用户ID
     * @param commandName 命令名称
     * @return 是否有权限
     */
    private boolean hasPermission(String userId, String commandName) {
        if (permissionEngine == null) {
            return true; // 无权限引擎时跳过检查（开发模式）
        }
        return permissionEngine.hasPermission(userId, "cli", commandName);
    }

    /**
     * 过滤敏感参数
     *
     * @param context 命令上下文
     */
    private void filterSensitiveParams(CommandContext context) {
        for (String sensitiveKey : sensitiveKeys) {
            Object value = context.getAttribute(sensitiveKey);
            if (value != null) {
                context.setAttribute(sensitiveKey, "***REDACTED***");
            }

            // 检查包含敏感词的关键字
            for (String key : context.getAttributes().keySet()) {
                if (key.toLowerCase().contains(sensitiveKey)) {
                    context.setAttribute(key, "***REDACTED***");
                }
            }
        }
    }

    // Getters and Setters for configuration
    public void setCommandWhitelist(Set<String> whitelist) {
        this.commandWhitelist = whitelist;
    }

    public void setSensitiveKeys(List<String> keys) {
        this.sensitiveKeys = keys;
    }

    /**
     * 校验结果
     */
    private static class ValidationResult {
        private final boolean valid;
        private final String error;

        private ValidationResult(boolean valid, String error) {
            this.valid = valid;
            this.error = error;
        }

        static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        boolean isValid() { return valid; }
        String getError() { return error; }
    }
}
