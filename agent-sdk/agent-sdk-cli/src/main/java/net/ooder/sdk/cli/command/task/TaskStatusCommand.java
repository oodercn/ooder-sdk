package net.ooder.sdk.cli.command.task;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.adapter.TaskStatusMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 任务状态查询命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class TaskStatusCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(TaskStatusCommand.class);

    private final TaskStatusMonitor taskMonitor;

    public TaskStatusCommand(TaskStatusMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }

    @Override
    public String getName() {
        return "task:status";
    }

    @Override
    public String getDescription() {
        return "Query task status";
    }

    @Override
    public String getUsage() {
        return "ooder task:status --task-id <id> [--wait] [--timeout <seconds>]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String taskId = context.getAttribute("task-id");
        boolean wait = Boolean.parseBoolean(context.getAttribute("wait"));
        String timeoutStr = context.getAttribute("timeout");

        if (taskId == null || taskId.isEmpty()) {
            return CommandResult.invalidArgs("Task ID is required (--task-id)");
        }

        try {
            log.debug("Querying task status: {}", taskId);

            TaskStatusMonitor.TaskInfo taskInfo = taskMonitor.getTaskStatus(taskId);

            if (taskInfo == null) {
                return CommandResult.notFound("Task not found: " + taskId);
            }

            // 如果需要等待任务完成
            if (wait && !taskInfo.getStatus().isTerminal()) {
                long timeout = timeoutStr != null ? Long.parseLong(timeoutStr) : 60;
                log.info("Waiting for task: {} (timeout: {}s)", taskId, timeout);

                taskInfo = taskMonitor.waitForCompletion(taskId, timeout, TimeUnit.SECONDS);

                if (taskInfo.getStatus() == TaskStatusMonitor.TaskStatus.TIMEOUT) {
                    return CommandResult.error("Timeout waiting for task: " + taskId);
                }
            }

            Map<String, Object> result = formatTaskStatus(taskInfo);

            String message = wait && taskInfo.getStatus().isTerminal()
                    ? "Task completed"
                    : "Task status retrieved";

            return CommandResult.success(message, result);

        } catch (NumberFormatException e) {
            return CommandResult.invalidArgs("Invalid timeout value: " + timeoutStr);
        } catch (Exception e) {
            log.error("Failed to query task status: {}", taskId, e);
            return CommandResult.error("Failed to query task status: " + e.getMessage(), e);
        }
    }

    /**
     * 格式化任务状态
     */
    private Map<String, Object> formatTaskStatus(TaskStatusMonitor.TaskInfo taskInfo) {
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskInfo.getTaskId());
        result.put("skillId", taskInfo.getSkillId());
        result.put("status", taskInfo.getStatus().name());
        result.put("progress", taskInfo.getProgress());

        if (taskInfo.getProgressMessage() != null) {
            result.put("progressMessage", taskInfo.getProgressMessage());
        }

        if (taskInfo.getStartTime() > 0) {
            result.put("startTime", taskInfo.getStartTime());
            result.put("elapsedMs", System.currentTimeMillis() - taskInfo.getStartTime());
        }

        if (taskInfo.getStatus().isTerminal()) {
            result.put("completedTime", taskInfo.getCompletedTime());

            if (taskInfo.getResult() != null) {
                result.put("result", taskInfo.getResult());
            }

            if (taskInfo.getError() != null) {
                result.put("error", taskInfo.getError());
            }
        }

        return result;
    }

    @Override
    public String getCategory() {
        return "task";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"task-status", "query-task"};
    }

    @Override
    public boolean validate(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--task-id".equals(args[i]) && i + 1 < args.length) {
                return true;
            }
        }
        return false;
    }
}
