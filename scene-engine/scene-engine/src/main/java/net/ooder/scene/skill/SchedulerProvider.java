package net.ooder.scene.skill;

import java.util.List;
import java.util.Map;

/**
 * Scheduler Provider 接口
 *
 * <p>定义任务调度能力接口，由 Skills Team 实现</p>
 * <p>实现类通过 ServiceLoader 注册</p>
 */
public interface SchedulerProvider {

    /**
     * 获取提供者类型
     * @return 如 "quartz", "spring", "redis", "mock"
     */
    String getProviderType();

    /**
     * 调度任务
     * @param taskName 任务名称
     * @param cronExpression Cron 表达式
     * @param taskData 任务数据
     * @param options 可选参数
     * @return 任务ID
     */
    String schedule(String taskName, String cronExpression, Object taskData, Map<String, Object> options);

    /**
     * 取消任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean cancel(String taskId);

    /**
     * 暂停任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean pause(String taskId);

    /**
     * 恢复任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean resume(String taskId);

    /**
     * 获取任务信息
     * @param taskId 任务ID
     * @return 任务信息
     */
    TaskInfo getTask(String taskId);

    /**
     * 列出任务
     * @param status 状态过滤（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 任务列表
     */
    TaskListResult listTasks(String status, int page, int pageSize);

    /**
     * 触发任务立即执行
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean triggerNow(String taskId);

    /**
     * 更新任务 Cron 表达式
     * @param taskId 任务ID
     * @param cronExpression 新的 Cron 表达式
     * @return 是否成功
     */
    boolean updateCron(String taskId, String cronExpression);

    /**
     * 获取任务执行历史
     * @param taskId 任务ID
     * @param limit 限制数量
     * @return 执行历史列表
     */
    List<TaskExecution> getExecutionHistory(String taskId, int limit);

    /**
     * 任务信息
     */
    class TaskInfo {
        private String taskId;
        private String taskName;
        private String cronExpression;
        private String status;
        private long lastExecutionTime;
        private long nextExecutionTime;
        private int executionCount;
        private Map<String, Object> taskData;

        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getTaskName() { return taskName; }
        public void setTaskName(String taskName) { this.taskName = taskName; }
        public String getCronExpression() { return cronExpression; }
        public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getLastExecutionTime() { return lastExecutionTime; }
        public void setLastExecutionTime(long lastExecutionTime) { this.lastExecutionTime = lastExecutionTime; }
        public long getNextExecutionTime() { return nextExecutionTime; }
        public void setNextExecutionTime(long nextExecutionTime) { this.nextExecutionTime = nextExecutionTime; }
        public int getExecutionCount() { return executionCount; }
        public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
        public Map<String, Object> getTaskData() { return taskData; }
        public void setTaskData(Map<String, Object> taskData) { this.taskData = taskData; }
    }

    /**
     * 任务列表结果
     */
    class TaskListResult {
        private List<TaskInfo> tasks;
        private int total;
        private int page;
        private int pageSize;

        public TaskListResult(List<TaskInfo> tasks, int total) {
            this.tasks = tasks;
            this.total = total;
        }

        public List<TaskInfo> getTasks() { return tasks; }
        public void setTasks(List<TaskInfo> tasks) { this.tasks = tasks; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    }

    /**
     * 任务执行记录
     */
    class TaskExecution {
        private String executionId;
        private String taskId;
        private long startTime;
        private long endTime;
        private String status;
        private String result;
        private String errorMessage;

        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
