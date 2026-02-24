package net.ooder.sdk.api.scheduling;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SchedulingApi {
    
    String schedule(ScheduledTask task);
    
    String schedule(ScheduledTask task, ScheduleConfig config);
    
    void cancel(String taskId);
    
    void pause(String taskId);
    
    void resume(String taskId);
    
    Optional<ScheduledTask> getTask(String taskId);
    
    List<ScheduledTask> getTasksByStatus(TaskExecutionStatus status);
    
    List<ScheduledTask> getTasksByTag(String tag);
    
    List<ScheduledTask> getAllTasks();
    
    CompletableFuture<TaskExecutionResult> executeNow(String taskId);
    
    CompletableFuture<TaskExecutionResult> executeNow(ScheduledTask task);
    
    List<ScheduledTask> getDueTasks();
    
    List<ScheduledTask> getOverdueTasks();
    
    void updateSchedule(String taskId, ScheduleConfig newConfig);
    
    ScheduleStats getStats();
    
    class ScheduledTask {
        private String taskId;
        private String name;
        private String description;
        private String capabilityId;
        private Map<String, Object> params;
        private ScheduleConfig schedule;
        private TaskExecutionStatus status;
        private Instant createdAt;
        private Instant lastExecution;
        private Instant nextExecution;
        private int executionCount;
        private List<String> tags;
        
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
        
        public ScheduleConfig getSchedule() { return schedule; }
        public void setSchedule(ScheduleConfig schedule) { this.schedule = schedule; }
        
        public TaskExecutionStatus getStatus() { return status; }
        public void setStatus(TaskExecutionStatus status) { this.status = status; }
        
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        
        public Instant getLastExecution() { return lastExecution; }
        public void setLastExecution(Instant lastExecution) { this.lastExecution = lastExecution; }
        
        public Instant getNextExecution() { return nextExecution; }
        public void setNextExecution(Instant nextExecution) { this.nextExecution = nextExecution; }
        
        public int getExecutionCount() { return executionCount; }
        public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
    
    class ScheduleConfig {
        private ScheduleType type;
        private String cronExpression;
        private long fixedDelay;
        private long fixedRate;
        private Instant startTime;
        private Instant endTime;
        private int maxExecutions;
        private boolean misfireHandling;
        
        public ScheduleType getType() { return type; }
        public void setType(ScheduleType type) { this.type = type; }
        
        public String getCronExpression() { return cronExpression; }
        public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
        
        public long getFixedDelay() { return fixedDelay; }
        public void setFixedDelay(long fixedDelay) { this.fixedDelay = fixedDelay; }
        
        public long getFixedRate() { return fixedRate; }
        public void setFixedRate(long fixedRate) { this.fixedRate = fixedRate; }
        
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        
        public int getMaxExecutions() { return maxExecutions; }
        public void setMaxExecutions(int maxExecutions) { this.maxExecutions = maxExecutions; }
        
        public boolean isMisfireHandling() { return misfireHandling; }
        public void setMisfireHandling(boolean misfireHandling) { this.misfireHandling = misfireHandling; }
    }
    
    enum ScheduleType {
        CRON,
        FIXED_DELAY,
        FIXED_RATE,
        ONE_TIME,
        MANUAL
    }
    
    enum TaskExecutionStatus {
        SCHEDULED,
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED,
        PAUSED
    }
    
    class TaskExecutionResult {
        private String taskId;
        private boolean success;
        private Object result;
        private String message;
        private long executionTime;
        
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    }
    
    class ScheduleStats {
        private int totalTasks;
        private int scheduledTasks;
        private int runningTasks;
        private int completedTasks;
        private int failedTasks;
        
        public int getTotalTasks() { return totalTasks; }
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
        
        public int getScheduledTasks() { return scheduledTasks; }
        public void setScheduledTasks(int scheduledTasks) { this.scheduledTasks = scheduledTasks; }
        
        public int getRunningTasks() { return runningTasks; }
        public void setRunningTasks(int runningTasks) { this.runningTasks = runningTasks; }
        
        public int getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
        
        public int getFailedTasks() { return failedTasks; }
        public void setFailedTasks(int failedTasks) { this.failedTasks = failedTasks; }
    }
}
