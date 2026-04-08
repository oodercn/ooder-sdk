package net.ooder.scene.workflow;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 工作流执行引擎接口
 *
 * <p>用于实时执行工作流定义，支持同步和异步执行。</p>
 *
 * <p>注意：此接口用于轻量级内存执行，与 SceneWorkflowManager 的持久化管理不同。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface WorkflowEngine {
    
    /**
     * 同步执行工作流
     *
     * @param workflowId 工作流ID
     * @param context 执行上下文
     * @return 执行结果
     */
    EngineResult execute(String workflowId, WorkflowContext context);
    
    /**
     * 异步执行工作流
     *
     * @param workflowId 工作流ID
     * @param context 执行上下文
     * @return 执行结果Future
     */
    CompletableFuture<EngineResult> executeAsync(String workflowId, WorkflowContext context);
    
    /**
     * 同步执行工作流定义
     *
     * @param definition 工作流定义
     * @param context 执行上下文
     * @return 执行结果
     */
    EngineResult execute(WorkflowDefinition definition, WorkflowContext context);
    
    /**
     * 异步执行工作流定义
     *
     * @param definition 工作流定义
     * @param context 执行上下文
     * @return 执行结果Future
     */
    CompletableFuture<EngineResult> executeAsync(WorkflowDefinition definition, WorkflowContext context);
    
    /**
     * 注册工作流定义
     *
     * @param definition 工作流定义
     */
    void registerWorkflow(WorkflowDefinition definition);
    
    /**
     * 注销工作流定义
     *
     * @param workflowId 工作流ID
     */
    void unregisterWorkflow(String workflowId);
    
    /**
     * 获取工作流定义
     *
     * @param workflowId 工作流ID
     * @return 工作流定义
     */
    WorkflowDefinition getWorkflow(String workflowId);
    
    /**
     * 获取所有工作流定义
     *
     * @return 工作流定义列表
     */
    List<WorkflowDefinition> getAllWorkflows();
    
    /**
     * 暂停执行
     *
     * @param executionId 执行ID
     */
    void pause(String executionId);
    
    /**
     * 恢复执行
     *
     * @param executionId 执行ID
     */
    void resume(String executionId);
    
    /**
     * 取消执行
     *
     * @param executionId 执行ID
     */
    void cancel(String executionId);
    
    /**
     * 获取执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    EngineStatus getStatus(String executionId);
    
    /**
     * 获取活动执行列表
     *
     * @return 活动执行列表
     */
    List<EngineExecution> getActiveExecutions();
    
    /**
     * 获取执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    EngineExecution getExecution(String executionId);
    
    /**
     * 引擎执行结果
     *
     * <p>表示工作流引擎执行的结果，与持久化的 WorkflowResult 不同。</p>
     */
    public static class EngineResult {
        private final String executionId;
        private final String workflowId;
        private final EngineStatus status;
        private final Map<String, WorkflowContext.StepResult> stepResults;
        private final Map<String, Object> outputs;
        private final String errorMessage;
        private final long startTime;
        private final long endTime;
        
        public EngineResult(String executionId, String workflowId, EngineStatus status,
                           Map<String, WorkflowContext.StepResult> stepResults,
                           Map<String, Object> outputs, String errorMessage,
                           long startTime, long endTime) {
            this.executionId = executionId;
            this.workflowId = workflowId;
            this.status = status;
            this.stepResults = stepResults;
            this.outputs = outputs;
            this.errorMessage = errorMessage;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public static EngineResult success(String executionId, String workflowId,
                                           Map<String, WorkflowContext.StepResult> stepResults,
                                           Map<String, Object> outputs,
                                           long startTime, long endTime) {
            return new EngineResult(executionId, workflowId, EngineStatus.COMPLETED,
                stepResults, outputs, null, startTime, endTime);
        }
        
        public static EngineResult failure(String executionId, String workflowId,
                                           String errorMessage,
                                           Map<String, WorkflowContext.StepResult> stepResults,
                                           long startTime, long endTime) {
            return new EngineResult(executionId, workflowId, EngineStatus.FAILED,
                stepResults, null, errorMessage, startTime, endTime);
        }
        
        public String getExecutionId() { return executionId; }
        public String getWorkflowId() { return workflowId; }
        public EngineStatus getStatus() { return status; }
        public Map<String, WorkflowContext.StepResult> getStepResults() { return stepResults; }
        public Map<String, Object> getOutputs() { return outputs; }
        public String getErrorMessage() { return errorMessage; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public long getDuration() { return endTime - startTime; }
        public boolean isSuccess() { return status == EngineStatus.COMPLETED; }
    }
    
    /**
     * 引擎执行记录
     *
     * <p>表示工作流引擎的执行状态跟踪，与持久化的 WorkflowExecution 不同。</p>
     */
    public static class EngineExecution {
        private final String executionId;
        private final String workflowId;
        private final EngineStatus status;
        private final long startTime;
        private volatile long endTime;
        private volatile String currentStepId;
        
        public EngineExecution(String executionId, String workflowId) {
            this.executionId = executionId;
            this.workflowId = workflowId;
            this.status = EngineStatus.RUNNING;
            this.startTime = System.currentTimeMillis();
        }
        
        public String getExecutionId() { return executionId; }
        public String getWorkflowId() { return workflowId; }
        public EngineStatus getStatus() { return status; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getCurrentStepId() { return currentStepId; }
        public void setCurrentStepId(String stepId) { this.currentStepId = stepId; }
    }
    
    /**
     * 引擎执行状态
     *
     * <p>表示工作流引擎的执行状态，与持久化的 WorkflowStatus 不同。</p>
     *
     * <ul>
     *   <li>PENDING - 等待执行</li>
     *   <li>RUNNING - 执行中</li>
     *   <li>PAUSED - 已暂停</li>
     *   <li>COMPLETED - 已完成</li>
     *   <li>FAILED - 已失败</li>
     *   <li>CANCELLED - 已取消</li>
     *   <li>TIMEOUT - 已超时</li>
     * </ul>
     */
    public enum EngineStatus {
        PENDING,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }
}
