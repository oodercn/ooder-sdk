package net.ooder.scene.workflow.impl;

import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.agent.WorkerAgent;
import net.ooder.scene.workflow.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class WorkflowEngineImpl implements WorkflowEngine {
    
    private static final Logger log = LoggerFactory.getLogger(WorkflowEngineImpl.class);
    
    private final Map<String, WorkflowDefinition> workflows = new ConcurrentHashMap<>();
    private final Map<String, WorkflowExecution> executions = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final AgentResolver agentResolver;
    
    public WorkflowEngineImpl() {
        this.executorService = Executors.newCachedThreadPool();
        this.agentResolver = new DefaultAgentResolver();
    }
    
    public WorkflowEngineImpl(AgentResolver agentResolver) {
        this.executorService = Executors.newCachedThreadPool();
        this.agentResolver = agentResolver != null ? agentResolver : new DefaultAgentResolver();
    }
    
    @Override
    public WorkflowResult execute(String workflowId, WorkflowContext context) {
        WorkflowDefinition definition = workflows.get(workflowId);
        if (definition == null) {
            throw new IllegalArgumentException("Workflow not found: " + workflowId);
        }
        return execute(definition, context);
    }
    
    @Override
    public CompletableFuture<WorkflowResult> executeAsync(String workflowId, WorkflowContext context) {
        return CompletableFuture.supplyAsync(() -> execute(workflowId, context), executorService);
    }
    
    @Override
    public WorkflowResult execute(WorkflowDefinition definition, WorkflowContext context) {
        String executionId = generateExecutionId(definition.getWorkflowId());
        WorkflowExecution execution = new WorkflowExecution(executionId, definition.getWorkflowId());
        executions.put(executionId, execution);
        
        long startTime = System.currentTimeMillis();
        log.info("Starting workflow execution: {} [{}]", definition.getName(), executionId);
        
        try {
            context.setVariables(definition.getVariables());
            
            List<WorkflowStep> sortedSteps = topologicalSort(definition.getSteps());
            
            for (WorkflowStep step : sortedSteps) {
                if (!context.canExecuteStep(step)) {
                    log.warn("Step {} dependencies not satisfied, skipping", step.getStepId());
                    long stepStart = System.currentTimeMillis();
                    context.setStepResult(step.getStepId(), 
                        WorkflowContext.StepResult.skipped(step.getStepId(), 
                            "Dependencies not satisfied", stepStart, System.currentTimeMillis()));
                    continue;
                }
                
                if (!evaluateCondition(step, context)) {
                    log.debug("Step {} condition not met, skipping", step.getStepId());
                    long stepStart = System.currentTimeMillis();
                    context.setStepResult(step.getStepId(),
                        WorkflowContext.StepResult.skipped(step.getStepId(),
                            "Condition not met", stepStart, System.currentTimeMillis()));
                    continue;
                }
                
                execution.setCurrentStepId(step.getStepId());
                WorkflowContext.StepResult stepResult = executeStep(step, context);
                context.setStepResult(step.getStepId(), stepResult);
                
                if (stepResult.getStatus() == WorkflowContext.StepStatus.FAILED) {
                    if (definition.getConfig().isFailFast()) {
                        log.error("Step {} failed, stopping workflow: {}", 
                            step.getStepId(), stepResult.getErrorMessage());
                        execution.setEndTime(System.currentTimeMillis());
                        return WorkflowEngine.WorkflowResult.failure(executionId, definition.getWorkflowId(),
                            stepResult.getErrorMessage(), context.getStepResults(),
                            startTime, System.currentTimeMillis());
                    }
                }
                
                if (step.getOutput() != null && stepResult.getOutput() != null) {
                    context.setVariable(step.getOutput(), stepResult.getOutput());
                }
            }
            
            execution.setEndTime(System.currentTimeMillis());
            Map<String, Object> outputs = extractOutputs(definition, context);
            
            log.info("Workflow execution completed: {} [{}]", definition.getName(), executionId);
            return WorkflowEngine.WorkflowResult.success(executionId, definition.getWorkflowId(),
                context.getStepResults(), outputs, startTime, System.currentTimeMillis());
                
        } catch (Exception e) {
            log.error("Workflow execution failed: {} [{}]", definition.getName(), executionId, e);
            execution.setEndTime(System.currentTimeMillis());
            return WorkflowEngine.WorkflowResult.failure(executionId, definition.getWorkflowId(),
                e.getMessage(), context.getStepResults(), startTime, System.currentTimeMillis());
        }
    }
    
    @Override
    public CompletableFuture<WorkflowResult> executeAsync(WorkflowDefinition definition, WorkflowContext context) {
        return CompletableFuture.supplyAsync(() -> execute(definition, context), executorService);
    }
    
    private WorkflowContext.StepResult executeStep(WorkflowStep step, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        log.debug("Executing step: {} [{}]", step.getName(), step.getStepId());
        
        try {
            Agent agent = agentResolver.resolve(step.getAgentId(), context.getSceneId());
            if (agent == null) {
                return WorkflowContext.StepResult.failure(step.getStepId(),
                    "Agent not found: " + step.getAgentId(), startTime, System.currentTimeMillis());
            }
            
            Map<String, Object> params = buildInputParams(step, context);
            
            CompletableFuture<Object> future;
            if (agent instanceof SceneAgent) {
                future = ((SceneAgent) agent).invokeCapabilityAsync(step.getCapId(), params);
            } else if (agent instanceof WorkerAgent) {
                future = ((WorkerAgent) agent).execute(step.getCapId(), params);
            } else {
                return WorkflowContext.StepResult.failure(step.getStepId(),
                    "Unsupported agent type: " + agent.getClass().getName(), 
                    startTime, System.currentTimeMillis());
            }
            
            Object result = future.get(
                step.getTimeout() != null ? step.getTimeout().getDuration() : 30000,
                TimeUnit.MILLISECONDS);
            
            log.debug("Step {} completed successfully", step.getStepId());
            return WorkflowContext.StepResult.success(step.getStepId(), result, 
                startTime, System.currentTimeMillis());
                
        } catch (TimeoutException e) {
            log.error("Step {} timed out", step.getStepId());
            return WorkflowContext.StepResult.failure(step.getStepId(),
                "Step timed out", startTime, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Step {} failed: {}", step.getStepId(), e.getMessage());
            return WorkflowContext.StepResult.failure(step.getStepId(),
                e.getMessage(), startTime, System.currentTimeMillis());
        }
    }
    
    private Map<String, Object> buildInputParams(WorkflowStep step, WorkflowContext context) {
        Map<String, Object> params = new HashMap<>();
        
        if (step.getInputs() != null) {
            for (Map.Entry<String, Object> entry : step.getInputs().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String) {
                    value = resolveVariable((String) value, context);
                }
                params.put(entry.getKey(), value);
            }
        }
        
        if (step.getConfig().getParams() != null) {
            params.putAll(step.getConfig().getParams());
        }
        
        return params;
    }
    
    private Object resolveVariable(String value, WorkflowContext context) {
        if (value.startsWith("${") && value.endsWith("}")) {
            String varName = value.substring(2, value.length() - 1);
            if (varName.contains(".")) {
                String[] parts = varName.split("\\.");
                Object obj = context.getVariable(parts[0]);
                if (obj instanceof Map && parts.length > 1) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) obj;
                    return map.get(parts[1]);
                }
            }
            return context.getVariable(varName);
        }
        return value;
    }
    
    private boolean evaluateCondition(WorkflowStep step, WorkflowContext context) {
        if (step.getCondition() == null) {
            return true;
        }
        
        String expression = step.getCondition();
        if (expression == null || expression.isEmpty()) {
            return true;
        }
        
        Object value = resolveVariable(expression, context);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        
        return value != null;
    }
    
    private List<WorkflowStep> topologicalSort(List<WorkflowStep> steps) {
        Map<String, WorkflowStep> stepMap = steps.stream()
            .collect(Collectors.toMap(WorkflowStep::getStepId, s -> s));
        
        Map<String, Integer> inDegree = new HashMap<>();
        for (WorkflowStep step : steps) {
            inDegree.putIfAbsent(step.getStepId(), 0);
            for (String dep : step.getDependsOn()) {
                inDegree.merge(step.getStepId(), 1, Integer::sum);
            }
        }
        
        Queue<String> queue = steps.stream()
            .filter(s -> inDegree.get(s.getStepId()) == 0)
            .map(WorkflowStep::getStepId)
            .collect(Collectors.toCollection(LinkedList::new));
        
        List<WorkflowStep> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String stepId = queue.poll();
            result.add(stepMap.get(stepId));
            
            for (WorkflowStep step : steps) {
                if (step.getDependsOn().contains(stepId)) {
                    int newDegree = inDegree.get(step.getStepId()) - 1;
                    inDegree.put(step.getStepId(), newDegree);
                    if (newDegree == 0) {
                        queue.add(step.getStepId());
                    }
                }
            }
        }
        
        return result;
    }
    
    private Map<String, Object> extractOutputs(WorkflowDefinition definition, WorkflowContext context) {
        Map<String, Object> outputs = new HashMap<>();
        for (WorkflowStep step : definition.getSteps()) {
            if (step.getOutput() != null) {
                Object output = context.getStepOutput(step.getStepId());
                if (output != null) {
                    outputs.put(step.getOutput(), output);
                }
            }
        }
        return outputs;
    }
    
    private String generateExecutionId(String workflowId) {
        return workflowId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public void registerWorkflow(WorkflowDefinition definition) {
        workflows.put(definition.getWorkflowId(), definition);
        log.info("Workflow registered: {}", definition.getWorkflowId());
    }
    
    @Override
    public void unregisterWorkflow(String workflowId) {
        workflows.remove(workflowId);
        log.info("Workflow unregistered: {}", workflowId);
    }
    
    @Override
    public WorkflowDefinition getWorkflow(String workflowId) {
        return workflows.get(workflowId);
    }
    
    @Override
    public List<WorkflowDefinition> getAllWorkflows() {
        return new ArrayList<>(workflows.values());
    }
    
    @Override
    public void pause(String executionId) {
        log.info("Pausing workflow execution: {}", executionId);
    }
    
    @Override
    public void resume(String executionId) {
        log.info("Resuming workflow execution: {}", executionId);
    }
    
    @Override
    public void cancel(String executionId) {
        log.info("Cancelling workflow execution: {}", executionId);
        executions.remove(executionId);
    }
    
    @Override
    public WorkflowStatus getStatus(String executionId) {
        WorkflowExecution execution = executions.get(executionId);
        return execution != null ? execution.getStatus() : null;
    }
    
    @Override
    public List<WorkflowExecution> getActiveExecutions() {
        return new ArrayList<>(executions.values());
    }
    
    @Override
    public WorkflowExecution getExecution(String executionId) {
        return executions.get(executionId);
    }
    
    public interface AgentResolver {
        Agent resolve(String agentId, String sceneId);
    }
    
    private static class DefaultAgentResolver implements AgentResolver {
        @Override
        public Agent resolve(String agentId, String sceneId) {
            return null;
        }
    }
    
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
