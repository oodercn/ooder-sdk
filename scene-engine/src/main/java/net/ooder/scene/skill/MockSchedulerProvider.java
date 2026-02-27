package net.ooder.scene.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mock Scheduler Provider
 *
 * <p>默认的 Mock 实现，用于测试和开发</p>
 */
public class MockSchedulerProvider implements SchedulerProvider {

    @Override
    public String getProviderType() {
        return "mock";
    }

    @Override
    public String schedule(String taskName, String cronExpression, Object taskData, Map<String, Object> options) {
        return "task-mock-" + System.currentTimeMillis();
    }

    @Override
    public boolean cancel(String taskId) {
        return true;
    }

    @Override
    public boolean pause(String taskId) {
        return true;
    }

    @Override
    public boolean resume(String taskId) {
        return true;
    }

    @Override
    public TaskInfo getTask(String taskId) {
        TaskInfo info = new TaskInfo();
        info.setTaskId(taskId);
        info.setTaskName("Mock Task");
        info.setCronExpression("0 0/5 * * * ?");
        info.setStatus("ACTIVE");
        info.setLastExecutionTime(System.currentTimeMillis() - 300000);
        info.setNextExecutionTime(System.currentTimeMillis() + 300000);
        return info;
    }

    @Override
    public TaskListResult listTasks(String status, int page, int pageSize) {
        List<TaskInfo> tasks = new ArrayList<TaskInfo>();
        tasks.add(getTask("task-mock-1"));
        tasks.add(getTask("task-mock-2"));
        return new TaskListResult(tasks, 2);
    }

    @Override
    public boolean triggerNow(String taskId) {
        return true;
    }

    @Override
    public boolean updateCron(String taskId, String cronExpression) {
        return true;
    }

    @Override
    public List<TaskExecution> getExecutionHistory(String taskId, int limit) {
        List<TaskExecution> history = new ArrayList<TaskExecution>();
        TaskExecution exec = new TaskExecution();
        exec.setExecutionId("exec-1");
        exec.setTaskId(taskId);
        exec.setStartTime(System.currentTimeMillis() - 300000);
        exec.setEndTime(System.currentTimeMillis() - 299000);
        exec.setStatus("SUCCESS");
        history.add(exec);
        return history;
    }
}
