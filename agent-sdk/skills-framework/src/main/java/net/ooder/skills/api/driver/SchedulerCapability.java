package net.ooder.skills.api.driver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 时间驱动能力接口
 * 
 * 监听时间事件并触发能力调用的驱动能力
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SchedulerCapability {
    
    /**
     * 创建定时调度
     *
     * @param scheduleId 调度ID
     * @param cron Cron表达式
     * @param action 执行动作
     * @return 调度信息
     */
    CompletableFuture<ScheduleInfo> schedule(String scheduleId, String cron, ScheduledAction action);
    
    /**
     * 创建延迟调度
     *
     * @param scheduleId 调度ID
     * @param delayMillis 延迟毫秒数
     * @param action 执行动作
     * @return 调度信息
     */
    CompletableFuture<ScheduleInfo> scheduleOnce(String scheduleId, long delayMillis, ScheduledAction action);
    
    /**
     * 创建间隔调度
     *
     * @param scheduleId 调度ID
     * @param intervalMillis 间隔毫秒数
     * @param action 执行动作
     * @return 调度信息
     */
    CompletableFuture<ScheduleInfo> scheduleAtFixedRate(String scheduleId, long intervalMillis, ScheduledAction action);
    
    /**
     * 取消调度
     *
     * @param scheduleId 调度ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> cancel(String scheduleId);
    
    /**
     * 暂停调度
     *
     * @param scheduleId 调度ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> pause(String scheduleId);
    
    /**
     * 恢复调度
     *
     * @param scheduleId 调度ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> resume(String scheduleId);
    
    /**
     * 获取调度信息
     *
     * @param scheduleId 调度ID
     * @return 调度信息
     */
    CompletableFuture<ScheduleInfo> getSchedule(String scheduleId);
    
    /**
     * 列出所有调度
     *
     * @return 调度列表
     */
    CompletableFuture<List<ScheduleInfo>> listSchedules();
    
    /**
     * 列出指定能力的调度
     *
     * @param capabilityId 能力ID
     * @return 调度列表
     */
    CompletableFuture<List<ScheduleInfo>> listSchedulesByCapability(String capabilityId);
    
    /**
     * 添加调度监听器
     *
     * @param listener 监听器
     */
    void addScheduleListener(ScheduleListener listener);
    
    /**
     * 移除调度监听器
     *
     * @param listener 监听器
     */
    void removeScheduleListener(ScheduleListener listener);
    
    /**
     * 调度信息
     */
    class ScheduleInfo {
        private String scheduleId;
        private String capabilityId;
        private String type;  // CRON, ONCE, FIXED_RATE
        private String cron;
        private long delay;
        private long interval;
        private ScheduleStatus status;
        private long nextExecutionTime;
        private long lastExecutionTime;
        private int executionCount;
        private Map<String, Object> metadata;
        
        public enum ScheduleStatus {
            PENDING,    // 待执行
            RUNNING,    // 运行中
            PAUSED,     // 已暂停
            CANCELLED,  // 已取消
            COMPLETED   // 已完成
        }
        
        // Getters and Setters
        public String getScheduleId() { return scheduleId; }
        public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCron() { return cron; }
        public void setCron(String cron) { this.cron = cron; }
        public long getDelay() { return delay; }
        public void setDelay(long delay) { this.delay = delay; }
        public long getInterval() { return interval; }
        public void setInterval(long interval) { this.interval = interval; }
        public ScheduleStatus getStatus() { return status; }
        public void setStatus(ScheduleStatus status) { this.status = status; }
        public long getNextExecutionTime() { return nextExecutionTime; }
        public void setNextExecutionTime(long nextExecutionTime) { this.nextExecutionTime = nextExecutionTime; }
        public long getLastExecutionTime() { return lastExecutionTime; }
        public void setLastExecutionTime(long lastExecutionTime) { this.lastExecutionTime = lastExecutionTime; }
        public int getExecutionCount() { return executionCount; }
        public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    /**
     * 调度动作
     */
    interface ScheduledAction {
        /**
         * 执行动作
         *
         * @param context 执行上下文
         * @return 执行结果
         */
        CompletableFuture<ActionResult> execute(ScheduleContext context);
    }
    
    /**
     * 调度上下文
     */
    class ScheduleContext {
        private String scheduleId;
        private String capabilityId;
        private int executionCount;
        private long scheduledTime;
        private Map<String, Object> params;
        
        // Getters and Setters
        public String getScheduleId() { return scheduleId; }
        public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public int getExecutionCount() { return executionCount; }
        public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
        public long getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(long scheduledTime) { this.scheduledTime = scheduledTime; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 动作执行结果
     */
    class ActionResult {
        private boolean success;
        private String message;
        private Map<String, Object> result;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getResult() { return result; }
        public void setResult(Map<String, Object> result) { this.result = result; }
    }
    
    /**
     * 调度监听器
     */
    interface ScheduleListener {
        void onScheduleCreated(ScheduleInfo schedule);
        void onScheduleExecuted(ScheduleInfo schedule, ActionResult result);
        void onScheduleCancelled(ScheduleInfo schedule);
        void onSchedulePaused(ScheduleInfo schedule);
        void onScheduleResumed(ScheduleInfo schedule);
    }
}
