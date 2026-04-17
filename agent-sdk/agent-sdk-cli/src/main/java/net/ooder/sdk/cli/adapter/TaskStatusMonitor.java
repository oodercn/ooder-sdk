package net.ooder.sdk.cli.adapter;

import net.ooder.skills.api.SkillCallback;
import net.ooder.skills.api.SkillRequest;
import net.ooder.skills.api.SkillResponse;
import net.ooder.skills.api.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 任务状态监控器
 *
 * <p>复用 SkillService.executeAsync() 实现异步任务监控</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class TaskStatusMonitor {

    private static final Logger log = LoggerFactory.getLogger(TaskStatusMonitor.class);

    private final Map<String, TaskInfo> taskRegistry = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * 提交异步任务
     *
     * @param service Skill服务
     * @param request 请求
     * @return 任务ID
     */
    public String submitTask(SkillService service, SkillRequest request) {
        String taskId = generateTaskId();

        TaskInfo taskInfo = new TaskInfo(taskId, request.getSkillId(), TaskStatus.PENDING);
        taskRegistry.put(taskId, taskInfo);

        service.executeAsync(request, new SkillCallback() {
            @Override
            public void onSuccess(SkillResponse response) {
                taskInfo.setStatus(TaskStatus.COMPLETED);
                taskInfo.setResult(response.getResult());
                taskInfo.setCompletedTime(System.currentTimeMillis());
                log.debug("Task completed: {}", taskId);
            }

            @Override
            public void onError(SkillResponse response) {
                taskInfo.setStatus(TaskStatus.FAILED);
                taskInfo.setError(response.getErrorMessage());
                taskInfo.setCompletedTime(System.currentTimeMillis());
                log.error("Task failed: {} - {}", taskId, response.getErrorMessage());
            }

            @Override
            public void onTimeout(SkillRequest request) {
                taskInfo.setStatus(TaskStatus.TIMEOUT);
                taskInfo.setCompletedTime(System.currentTimeMillis());
                log.warn("Task timeout: {}", taskId);
            }
        });

        taskInfo.setStatus(TaskStatus.RUNNING);
        taskInfo.setStartTime(System.currentTimeMillis());

        return taskId;
    }

    /**
     * 获取任务状态
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    public TaskInfo getTaskStatus(String taskId) {
        return taskRegistry.get(taskId);
    }

    /**
     * 等待任务完成
     *
     * @param taskId 任务ID
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 任务信息
     */
    public TaskInfo waitForCompletion(String taskId, long timeout, TimeUnit unit) {
        TaskInfo taskInfo = taskRegistry.get(taskId);
        if (taskInfo == null) {
            return null;
        }

        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        while (System.currentTimeMillis() < deadline) {
            if (taskInfo.getStatus().isTerminal()) {
                return taskInfo;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return taskInfo;
            }
        }

        taskInfo.setStatus(TaskStatus.TIMEOUT);
        return taskInfo;
    }

    /**
     * 列出所有任务
     *
     * @return 任务列表
     */
    public Map<String, TaskInfo> listTasks() {
        return new ConcurrentHashMap<>(taskRegistry);
    }

    /**
     * 清理已完成任务
     */
    public void cleanupCompletedTasks() {
        long cutoff = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);
        taskRegistry.entrySet().removeIf(entry -> {
            TaskInfo task = entry.getValue();
            return task.getStatus().isTerminal() && task.getCompletedTime() < cutoff;
        });
    }

    private String generateTaskId() {
        return "task-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(10000);
    }

    /**
     * 任务信息
     */
    public static class TaskInfo {
        private final String taskId;
        private final String skillId;
        private TaskStatus status;
        private int progress;
        private String progressMessage;
        private Object result;
        private String error;
        private long startTime;
        private long completedTime;

        public TaskInfo(String taskId, String skillId, TaskStatus status) {
            this.taskId = taskId;
            this.skillId = skillId;
            this.status = status;
        }

        public String getTaskId() { return taskId; }
        public String getSkillId() { return skillId; }
        public TaskStatus getStatus() { return status; }
        public void setStatus(TaskStatus status) { this.status = status; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public String getProgressMessage() { return progressMessage; }
        public void setProgressMessage(String progressMessage) { this.progressMessage = progressMessage; }
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getCompletedTime() { return completedTime; }
        public void setCompletedTime(long completedTime) { this.completedTime = completedTime; }
    }

    /**
     * 任务状态
     */
    public enum TaskStatus {
        PENDING(false),
        RUNNING(false),
        COMPLETED(true),
        FAILED(true),
        TIMEOUT(true),
        CANCELLED(true);

        private final boolean terminal;

        TaskStatus(boolean terminal) {
            this.terminal = terminal;
        }

        public boolean isTerminal() {
            return terminal;
        }
    }

    private static class ThreadLocalRandom {
        private static final java.util.concurrent.ThreadLocalRandom RANDOM =
                java.util.concurrent.ThreadLocalRandom.current();

        public static java.util.concurrent.ThreadLocalRandom current() {
            return RANDOM;
        }
    }
}
