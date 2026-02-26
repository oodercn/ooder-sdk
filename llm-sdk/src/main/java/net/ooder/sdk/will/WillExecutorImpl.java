package net.ooder.sdk.will;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class WillExecutorImpl implements WillExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(WillExecutorImpl.class);
    
    private final Map<String, ExecutionStatus> executionStatuses = new ConcurrentHashMap<>();
    private final Map<String, List<Task>> willTasks = new ConcurrentHashMap<>();
    private final Map<String, StepExecutor> stepExecutors = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public WillExecutorImpl() {
        // 注册默认执行器
        registerDefaultExecutors();
    }
    
    /**
     * 注册步骤执行器
     * @param executor 执行器
     */
    public void registerStepExecutor(StepExecutor executor) {
        if (executor != null && executor.getActionType() != null) {
            stepExecutors.put(executor.getActionType().toLowerCase(), executor);
            log.debug("Registered step executor for action: {}", executor.getActionType());
        }
    }
    
    /**
     * 注销步骤执行器
     * @param actionType 操作类型
     */
    public void unregisterStepExecutor(String actionType) {
        if (actionType != null) {
            stepExecutors.remove(actionType.toLowerCase());
            log.debug("Unregistered step executor for action: {}", actionType);
        }
    }
    
    private void registerDefaultExecutors() {
        // 注册初始化执行器
        registerStepExecutor(new StepExecutor() {
            @Override
            public String getActionType() {
                return "initialize";
            }
            
            @Override
            public boolean execute(WillExpression will, WillTransformer.ExecutionStep step, ExecutionContext context) {
                log.debug("Initializing execution environment for will: {}", will.getWillId());
                // 设置初始参数
                context.setParameter("startTime", System.currentTimeMillis());
                context.setParameter("willType", will.getType());
                context.setParameter("priority", will.getPriority());
                return true;
            }
        });
        
        // 注册验证执行器
        registerStepExecutor(new StepExecutor() {
            @Override
            public String getActionType() {
                return "verify";
            }
            
            @Override
            public boolean execute(WillExpression will, WillTransformer.ExecutionStep step, ExecutionContext context) {
                log.debug("Verifying execution result for will: {}", will.getWillId());
                // 验证执行结果
                Object result = context.getResult("executionResult");
                if (result == null) {
                    log.warn("No execution result found for verification");
                    return false;
                }
                return true;
            }
        });
        
        // 注册默认执行器（处理未知操作）
        registerStepExecutor(new StepExecutor() {
            @Override
            public String getActionType() {
                return "default";
            }
            
            @Override
            public boolean execute(WillExpression will, WillTransformer.ExecutionStep step, ExecutionContext context) {
                log.debug("Executing default action for step: {}", step.getName());
                // 默认执行成功
                context.setResult("defaultExecuted", true);
                return true;
            }
        });
    }
    
    @Override
    public CompletableFuture<WillExecutionResult> execute(WillExpression will, WillTransformer.ExecutionPlan plan) {
        if (will == null || plan == null) {
            return CompletableFuture.completedFuture(
                WillExecutionResult.failure(will != null ? will.getWillId() : "unknown", 
                    "Will and plan cannot be null"));
        }
        
        String executionId = "exec-" + System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        
        ExecutionStatus status = new ExecutionStatus();
        status.setWillId(will.getWillId());
        status.setStatus("RUNNING");
        status.setProgress(0.0);
        status.setCompletedSteps(new ArrayList<>());
        status.setPendingSteps(new ArrayList<>());
        status.setMetrics(new HashMap<>());
        executionStatuses.put(will.getWillId(), status);
        
        will.setStatus(WillExpression.WillStatus.EXECUTING);
        
        StepExecutor.ExecutionContext context = new StepExecutor.ExecutionContext(executionId, will.getWillId());
        
        return CompletableFuture.supplyAsync(() -> {
            WillExecutionResult result = new WillExecutionResult();
            result.setWillId(will.getWillId());
            result.setExecutionId(executionId);
            result.setCompletedSteps(new ArrayList<>());
            result.setFailedSteps(new ArrayList<>());
            result.setData(new HashMap<>());
            
            try {
                List<WillTransformer.ExecutionStep> steps = plan.getSteps();
                if (steps == null || steps.isEmpty()) {
                    result.setSuccess(true);
                    result.setMessage("No steps to execute");
                    return result;
                }
                
                status.setPendingSteps(new ArrayList<>());
                for (WillTransformer.ExecutionStep step : steps) {
                    status.getPendingSteps().add(step.getStepId());
                }
                
                for (WillTransformer.ExecutionStep step : steps) {
                    status.setCurrentStep(step.getStepId());
                    context.setCurrentStepId(step.getStepId());
                    
                    boolean stepSuccess = executeStep(will, step, context, status);
                    
                    if (stepSuccess) {
                        result.getCompletedSteps().add(step.getStepId());
                        status.getCompletedSteps().add(step.getStepId());
                    } else {
                        result.getFailedSteps().add(step.getStepId());
                        if (isCriticalStep(step)) {
                            throw new RuntimeException("Critical step failed: " + step.getName());
                        }
                    }
                    
                    status.getPendingSteps().remove(step.getStepId());
                    status.setProgress((double) status.getCompletedSteps().size() / steps.size());
                }
                
                status.setStatus("COMPLETED");
                status.setProgress(1.0);
                will.setStatus(WillExpression.WillStatus.COMPLETED);
                
                result.setSuccess(true);
                result.setMessage("Execution completed successfully");
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                result.setData(context.getResults());
                
                log.info("Will execution completed: {} in {}ms", will.getWillId(), result.getExecutionTime());
                
            } catch (Exception e) {
                log.error("Will execution failed: {}", will.getWillId(), e);
                
                status.setStatus("FAILED");
                will.setStatus(WillExpression.WillStatus.FAILED);
                
                result.setSuccess(false);
                result.setMessage("Execution failed: " + e.getMessage());
                result.setExecutionTime(System.currentTimeMillis() - startTime);
            }
            
            return result;
        }, executor);
    }
    
    private boolean executeStep(WillExpression will, WillTransformer.ExecutionStep step, 
                               StepExecutor.ExecutionContext context, ExecutionStatus status) {
        log.debug("Executing step: {} for will: {}", step.getName(), will.getWillId());
        
        try {
            String action = step.getAction();
            if (action == null) {
                log.warn("Step {} has no action defined", step.getStepId());
                return true;
            }
            
            StepExecutor stepExecutor = stepExecutors.get(action.toLowerCase());
            if (stepExecutor == null) {
                stepExecutor = stepExecutors.get("default");
            }
            
            if (stepExecutor == null) {
                log.error("No executor found for action: {}", action);
                return false;
            }
            
            // 验证步骤
            if (!stepExecutor.validate(step)) {
                log.error("Step validation failed: {}", step.getStepId());
                return false;
            }
            
            // 执行步骤
            boolean success = stepExecutor.execute(will, step, context);
            
            if (success) {
                log.debug("Step {} executed successfully", step.getStepId());
            } else {
                log.warn("Step {} execution failed", step.getStepId());
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("Step execution failed: {}", step.getStepId(), e);
            return false;
        }
    }
    
    private boolean isCriticalStep(WillTransformer.ExecutionStep step) {
        return step.getName() != null && 
            (step.getName().contains("初始化") || step.getName().contains("核心"));
    }
    
    @Override
    public void assignTasks(WillExpression will, List<Task> tasks) {
        if (will == null || tasks == null) return;
        
        for (Task task : tasks) {
            task.setWillId(will.getWillId());
            task.setTaskId("task-" + UUID.randomUUID().toString().substring(0, 8));
            task.setCreatedAt(System.currentTimeMillis());
            task.setStatus("ASSIGNED");
        }
        
        willTasks.put(will.getWillId(), tasks);
        log.info("Assigned {} tasks for will: {}", tasks.size(), will.getWillId());
    }
    
    @Override
    public CompletableFuture<ExecutionStatus> monitorExecution(String willId) {
        return CompletableFuture.supplyAsync(() -> {
            ExecutionStatus status = executionStatuses.get(willId);
            if (status == null) {
                status = new ExecutionStatus();
                status.setWillId(willId);
                status.setStatus("NOT_FOUND");
            }
            return status;
        });
    }
    
    @Override
    public CompletableFuture<EffectEvaluation> evaluateEffect(String willId) {
        return CompletableFuture.supplyAsync(() -> {
            EffectEvaluation evaluation = new EffectEvaluation();
            evaluation.setWillId(willId);
            
            ExecutionStatus status = executionStatuses.get(willId);
            if (status == null) {
                evaluation.setGoalAchieved(false);
                evaluation.setAchievementRate(0.0);
                evaluation.setRecommendation("Execution not found");
                return evaluation;
            }
            
            boolean completed = "COMPLETED".equals(status.getStatus());
            evaluation.setGoalAchieved(completed);
            evaluation.setAchievementRate(completed ? 1.0 : status.getProgress());
            
            List<String> achievements = new ArrayList<>();
            List<String> shortcomings = new ArrayList<>();
            
            if (completed) {
                achievements.add("所有步骤已完成");
                achievements.add("目标已达成");
            } else {
                shortcomings.add("执行未完成");
                if (status.getCompletedSteps() != null && !status.getCompletedSteps().isEmpty()) {
                    achievements.add("已完成 " + status.getCompletedSteps().size() + " 个步骤");
                }
            }
            
            evaluation.setAchievements(achievements);
            evaluation.setShortcomings(shortcomings);
            evaluation.setRecommendation(completed ? "执行成功，可以进行后续优化" : "需要继续执行或调整策略");
            evaluation.setMetrics(new HashMap<>());
            
            return evaluation;
        });
    }
    
    @Override
    public void adjustStrategy(String willId, Feedback feedback) {
        log.info("Adjusting strategy for will: {} based on feedback: {}", willId, feedback.getType());
        
        ExecutionStatus status = executionStatuses.get(willId);
        if (status != null) {
            status.getMetrics().put("lastFeedback", feedback.getContent());
            status.getMetrics().put("lastFeedbackTime", feedback.getTimestamp());
            status.getMetrics().put("feedbackType", feedback.getType());
            
            // 根据反馈类型调整策略
            String feedbackType = feedback.getType();
            if ("PRIORITY_CHANGE".equals(feedbackType)) {
                // 调整优先级
                log.info("Adjusting priority based on feedback");
            } else if ("RESOURCE_CHANGE".equals(feedbackType)) {
                // 调整资源
                log.info("Adjusting resources based on feedback");
            } else if ("TIMELINE_CHANGE".equals(feedbackType)) {
                // 调整时间线
                log.info("Adjusting timeline based on feedback");
            }
        }
    }
    
    @Override
    public void cancel(String willId) {
        ExecutionStatus status = executionStatuses.get(willId);
        if (status != null && "RUNNING".equals(status.getStatus())) {
            status.setStatus("CANCELLED");
            log.info("Will execution cancelled: {}", willId);
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
