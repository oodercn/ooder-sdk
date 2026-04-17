package net.ooder.sdk.cli.command.task;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.adapter.TaskStatusMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务列表命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class TaskListCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(TaskListCommand.class);

    private final TaskStatusMonitor taskMonitor;

    public TaskListCommand(TaskStatusMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }

    @Override
    public String getName() {
        return "task:list";
    }

    @Override
    public String getDescription() {
        return "List all tasks";
    }

    @Override
    public String getUsage() {
        return "ooder task:list [--status <status>] [--skill-id <id>] [--active-only]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String statusFilter = context.getAttribute("status");
        String skillIdFilter = context.getAttribute("skill-id");
        boolean activeOnly = Boolean.parseBoolean(context.getAttribute("active-only"));

        try {
            log.debug("Listing tasks with filters - status: {}, skillId: {}, activeOnly: {}",
                    statusFilter, skillIdFilter, activeOnly);

            Map<String, TaskStatusMonitor.TaskInfo> allTasks = taskMonitor.listTasks();

            // 应用过滤器
            List<TaskStatusMonitor.TaskInfo> filteredTasks = allTasks.values().stream()
                    .filter(task -> statusFilter == null || task.getStatus().name().equalsIgnoreCase(statusFilter))
                    .filter(task -> skillIdFilter == null || skillIdFilter.equals(task.getSkillId()))
                    .filter(task -> !activeOnly || !task.getStatus().isTerminal())
                    .sorted(Comparator.comparing(TaskStatusMonitor.TaskInfo::getStartTime).reversed())
                    .collect(Collectors.toList());

            // 构建结果
            List<Map<String, Object>> taskList = new ArrayList<>();
            for (TaskStatusMonitor.TaskInfo task : filteredTasks) {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("taskId", task.getTaskId());
                taskInfo.put("skillId", task.getSkillId());
                taskInfo.put("status", task.getStatus().name());
                taskInfo.put("progress", task.getProgress());

                if (task.getStartTime() > 0) {
                    taskInfo.put("elapsedMs", System.currentTimeMillis() - task.getStartTime());
                }

                taskList.add(taskInfo);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", allTasks.size());
            result.put("filtered", taskList.size());
            result.put("tasks", taskList);

            // 统计各状态数量
            Map<String, Long> statusCounts = allTasks.values().stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getStatus().name(),
                            Collectors.counting()
                    ));
            result.put("statusCounts", statusCounts);

            return CommandResult.success(
                    String.format("Listed %d tasks (%d total)", taskList.size(), allTasks.size()),
                    result
            );

        } catch (Exception e) {
            log.error("Failed to list tasks", e);
            return CommandResult.error("Failed to list tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCategory() {
        return "task";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"tasks", "list-tasks"};
    }
}
