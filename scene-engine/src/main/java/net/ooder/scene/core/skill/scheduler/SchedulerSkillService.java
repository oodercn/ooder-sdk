package net.ooder.scene.core.skill.scheduler;

import net.ooder.scene.core.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务调度服务技能
 *
 * <p>包装SDK任务调度能力，添加安全检查和审计日志</p>
 */
public class SchedulerSkillService extends SecureSkillService {

    private SchedulerSdkWrapper schedulerSdkWrapper;

    @Override
    protected Object doExecute(SkillRequest request) {
        String operation = request.getOperation();
        switch (operation) {
            case "schedule":
                return scheduleTask(request);
            case "cancel":
                return cancelTask(request);
            case "get":
                return getTask(request);
            case "list":
                return listTasks(request);
            case "pause":
                return pauseTask(request);
            case "resume":
                return resumeTask(request);
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + operation);
        }
    }

    @Override
    protected String getResourceType() {
        return "scheduler";
    }

    private Object scheduleTask(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String taskName = (String) params.get("taskName");
        String cronExpression = (String) params.get("cronExpression");
        Object taskData = params.get("taskData");
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        String taskId = schedulerSdkWrapper.schedule(taskName, cronExpression, taskData, options);
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskId);
        result.put("taskName", taskName);
        result.put("cronExpression", cronExpression);
        return result;
    }

    private Object cancelTask(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String taskId = (String) params.get("taskId");
        
        boolean success = schedulerSdkWrapper.cancel(taskId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("taskId", taskId);
        return result;
    }

    private Object getTask(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String taskId = (String) params.get("taskId");
        
        TaskInfo taskInfo = schedulerSdkWrapper.getTask(taskId);
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", taskInfo.getTaskId());
        result.put("taskName", taskInfo.getTaskName());
        result.put("cronExpression", taskInfo.getCronExpression());
        result.put("status", taskInfo.getStatus());
        result.put("lastExecutionTime", taskInfo.getLastExecutionTime());
        result.put("nextExecutionTime", taskInfo.getNextExecutionTime());
        return result;
    }

    private Object listTasks(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String status = params.containsKey("status") ? (String) params.get("status") : null;
        int page = params.containsKey("page") ? ((Number) params.get("page")).intValue() : 1;
        int pageSize = params.containsKey("pageSize") ? ((Number) params.get("pageSize")).intValue() : 20;
        
        TaskListResult result = schedulerSdkWrapper.listTasks(status, page, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("tasks", result.getTasks());
        resultMap.put("total", result.getTotal());
        resultMap.put("page", page);
        resultMap.put("pageSize", pageSize);
        return resultMap;
    }

    private Object pauseTask(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String taskId = (String) params.get("taskId");
        
        boolean success = schedulerSdkWrapper.pause(taskId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("taskId", taskId);
        return result;
    }

    private Object resumeTask(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String taskId = (String) params.get("taskId");
        
        boolean success = schedulerSdkWrapper.resume(taskId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("taskId", taskId);
        return result;
    }

    @Override
    protected String getSkillId() {
        return "skill-scheduler";
    }
}

/**
 * 任务调度SDK包装器
 *
 * <p>包装底层SDK任务调度能力，提供统一接口</p>
 */
class SchedulerSdkWrapper {

    public String schedule(String taskName, String cronExpression, Object taskData, Map<String, Object> options) {
        return "task-" + System.currentTimeMillis();
    }

    public boolean cancel(String taskId) {
        return true;
    }

    public TaskInfo getTask(String taskId) {
        return new TaskInfo(
            taskId,
            "Sample Task",
            "0 0/5 * * * ?",
            "ACTIVE",
            System.currentTimeMillis() - 300000,
            System.currentTimeMillis() + 300000
        );
    }

    public TaskListResult listTasks(String status, int page, int pageSize) {
        List<TaskInfo> tasks = new ArrayList<>();
        tasks.add(new TaskInfo(
            "task-1",
            "Task 1",
            "0 0/5 * * * ?",
            "ACTIVE",
            System.currentTimeMillis() - 300000,
            System.currentTimeMillis() + 300000
        ));
        tasks.add(new TaskInfo(
            "task-2",
            "Task 2",
            "0 0/10 * * * ?",
            "PAUSED",
            System.currentTimeMillis() - 600000,
            System.currentTimeMillis() + 600000
        ));
        return new TaskListResult(tasks, 2);
    }

    public boolean pause(String taskId) {
        return true;
    }

    public boolean resume(String taskId) {
        return true;
    }
}

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
    
    public TaskInfo(String taskId, String taskName, String cronExpression, String status, long lastExecutionTime, long nextExecutionTime) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.cronExpression = cronExpression;
        this.status = status;
        this.lastExecutionTime = lastExecutionTime;
        this.nextExecutionTime = nextExecutionTime;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public String getCronExpression() {
        return cronExpression;
    }
    
    public String getStatus() {
        return status;
    }
    
    public long getLastExecutionTime() {
        return lastExecutionTime;
    }
    
    public long getNextExecutionTime() {
        return nextExecutionTime;
    }
}

/**
 * 任务列表结果
 */
class TaskListResult {
    private List<TaskInfo> tasks;
    private int total;
    
    public TaskListResult(List<TaskInfo> tasks, int total) {
        this.tasks = tasks;
        this.total = total;
    }
    
    public List<TaskInfo> getTasks() {
        return tasks;
    }
    
    public int getTotal() {
        return total;
    }
}
