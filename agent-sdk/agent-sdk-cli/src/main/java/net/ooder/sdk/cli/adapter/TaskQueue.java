package net.ooder.sdk.cli.adapter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class TaskQueue {

    private final ExecutorService executor;
    private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<>();
    private final int maxConcurrent;
    private final long defaultTimeout;

    public TaskQueue() {
        this(10, 60);
    }

    public TaskQueue(int maxConcurrent, long defaultTimeoutSeconds) {
        this.maxConcurrent = maxConcurrent;
        this.defaultTimeout = defaultTimeoutSeconds;
        this.executor = new ThreadPoolExecutor(
            maxConcurrent, maxConcurrent * 2,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public TaskInfo submit(String name, Callable<Object> task) {
        return submit(name, task, defaultTimeout);
    }

    public TaskInfo submit(String name, Callable<Object> task, long timeoutSeconds) {
        String taskId = generateTaskId();
        TaskInfo taskInfo = new TaskInfo(taskId, name, TaskStatus.QUEUED);

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            taskInfo.setStatus(TaskStatus.RUNNING);
            taskInfo.setStartedAt(System.currentTimeMillis());
            tasks.put(taskId, taskInfo);
            try {
                Object result = task.call();
                taskInfo.setStatus(TaskStatus.COMPLETED);
                taskInfo.setResult(result);
                taskInfo.setCompletedAt(System.currentTimeMillis());
                return result;
            } catch (Exception e) {
                taskInfo.setStatus(TaskStatus.FAILED);
                taskInfo.setError(e.getMessage());
                taskInfo.setCompletedAt(System.currentTimeMillis());
                throw new CompletionException(e);
            }
        }, executor);

        taskInfo.setFuture(future);
        tasks.put(taskId, taskInfo);
        return taskInfo;
    }

    public TaskInfo getTask(String taskId) {
        return tasks.get(taskId);
    }

    public Map<String, TaskInfo> getAllTasks() {
        return new ConcurrentHashMap<>(tasks);
    }

    public boolean cancelTask(String taskId) {
        TaskInfo taskInfo = tasks.get(taskId);
        if (taskInfo != null && (taskInfo.getStatus() == TaskStatus.QUEUED || taskInfo.getStatus() == TaskStatus.RUNNING)) {
            taskInfo.getFuture().cancel(true);
            taskInfo.setStatus(TaskStatus.CANCELLED);
            taskInfo.setCompletedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public void cleanup(long olderThanSeconds) {
        long cutoff = System.currentTimeMillis() - (olderThanSeconds * 1000);
        tasks.entrySet().removeIf(entry -> {
            TaskInfo info = entry.getValue();
            return info.getCompletedAt() > 0 && info.getCompletedAt() < cutoff;
        });
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    private String generateTaskId() {
        return "task-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public enum TaskStatus {
        QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED, TIMEOUT
    }

    public static class TaskInfo {
        private final String taskId;
        private final String name;
        private volatile TaskStatus status;
        private volatile long startedAt;
        private volatile long completedAt;
        private volatile String error;
        private volatile Object result;
        private CompletableFuture<Object> future;

        public TaskInfo(String taskId, String name, TaskStatus status) {
            this.taskId = taskId;
            this.name = name;
            this.status = status;
        }

        public String getTaskId() { return taskId; }
        public String getName() { return name; }
        public TaskStatus getStatus() { return status; }
        public void setStatus(TaskStatus status) { this.status = status; }
        public long getStartedAt() { return startedAt; }
        public void setStartedAt(long startedAt) { this.startedAt = startedAt; }
        public long getCompletedAt() { return completedAt; }
        public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        public CompletableFuture<Object> getFuture() { return future; }
        public void setFuture(CompletableFuture<Object> future) { this.future = future; }
        public long getDuration() { return completedAt > 0 ? completedAt - startedAt : System.currentTimeMillis() - startedAt; }
    }
}
