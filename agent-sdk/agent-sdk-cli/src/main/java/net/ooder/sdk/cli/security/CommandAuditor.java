package net.ooder.sdk.cli.security;

import net.ooder.sdk.cli.api.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 命令审计器
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CommandAuditor {

    private static final Logger log = LoggerFactory.getLogger(CommandAuditor.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    private final BlockingQueue<AuditRecord> auditQueue = new LinkedBlockingQueue<>(10000);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 记录命令尝试
     *
     * @param userId 用户ID
     * @param commandName 命令名称
     * @param params 参数
     */
    public void recordCommandAttempt(String userId, String commandName, Map<String, Object> params) {
        AuditRecord record = new AuditRecord(
                LocalDateTime.now(),
                userId,
                commandName,
                "ATTEMPT",
                params.toString(),
                null
        );

        auditQueue.offer(record);
        auditLog.info("[ATTEMPT] User: {}, Command: {}, Params: {}", userId, commandName, params);
    }

    /**
     * 记录命令拒绝
     *
     * @param userId 用户ID
     * @param commandName 命令名称
     * @param reason 原因
     */
    public void recordCommandDenied(String userId, String commandName, String reason) {
        AuditRecord record = new AuditRecord(
                LocalDateTime.now(),
                userId,
                commandName,
                "DENIED",
                null,
                reason
        );

        auditQueue.offer(record);
        auditLog.warn("[DENIED] User: {}, Command: {}, Reason: {}", userId, commandName, reason);
    }

    /**
     * 记录命令成功
     *
     * @param userId 用户ID
     * @param commandName 命令名称
     * @param result 结果
     */
    public void recordCommandSuccess(String userId, String commandName, CommandResult result) {
        AuditRecord record = new AuditRecord(
                LocalDateTime.now(),
                userId,
                commandName,
                "SUCCESS",
                null,
                result.getMessage()
        );

        auditQueue.offer(record);
        auditLog.info("[SUCCESS] User: {}, Command: {}, Result: {}",
                userId, commandName, result.getMessage());
    }

    /**
     * 记录命令失败
     *
     * @param userId 用户ID
     * @param commandName 命令名称
     * @param error 错误
     */
    public void recordCommandFailure(String userId, String commandName, Exception error) {
        AuditRecord record = new AuditRecord(
                LocalDateTime.now(),
                userId,
                commandName,
                "FAILURE",
                null,
                error.getMessage()
        );

        auditQueue.offer(record);
        auditLog.error("[FAILURE] User: {}, Command: {}, Error: {}",
                userId, commandName, error.getMessage());
    }

    /**
     * 获取审计记录队列
     *
     * @return 审计记录队列
     */
    public BlockingQueue<AuditRecord> getAuditQueue() {
        return auditQueue;
    }

    /**
     * 审计记录
     */
    public static class AuditRecord {
        private final LocalDateTime timestamp;
        private final String userId;
        private final String commandName;
        private final String action;
        private final String params;
        private final String result;

        public AuditRecord(LocalDateTime timestamp, String userId, String commandName,
                          String action, String params, String result) {
            this.timestamp = timestamp;
            this.userId = userId;
            this.commandName = commandName;
            this.action = action;
            this.params = params;
            this.result = result;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getUserId() { return userId; }
        public String getCommandName() { return commandName; }
        public String getAction() { return action; }
        public String getParams() { return params; }
        public String getResult() { return result; }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s - %s - %s",
                    timestamp, userId, commandName, action, result);
        }
    }
}
