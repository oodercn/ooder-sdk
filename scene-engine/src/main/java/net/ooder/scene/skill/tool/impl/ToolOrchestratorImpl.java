package net.ooder.scene.skill.tool.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.ooder.scene.skill.tool.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 工具编排服务实现
 *
 * <p>提供多工具编排执行能力实现。</p>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public class ToolOrchestratorImpl implements ToolOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(ToolOrchestratorImpl.class);
    
    private final ToolRegistry toolRegistry;
    private final ExecutorService executorService;
    
    public ToolOrchestratorImpl(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public ToolCallResult executeToolCall(ToolCall toolCall, ToolExecutionContext context) {
        String toolName = toolCall.getName();
        String callId = toolCall.getId() != null ? toolCall.getId() : UUID.randomUUID().toString();
        
        log.info("Executing tool call: {} [id={}]", toolName, callId);
        
        Optional<Tool> toolOpt = toolRegistry.getTool(toolName);
        if (!toolOpt.isPresent()) {
            return ToolCallResult.failure(callId, toolName, "Tool not found: " + toolName);
        }
        
        Tool tool = toolOpt.get();

        ToolResult validationResult = tool.validateArguments(toolCall.getArguments());
        if (!validationResult.isSuccess()) {
            return ToolCallResult.failure(callId, toolName, "Validation failed: " + validationResult.getMessage());
        }
        
        long startTime = System.currentTimeMillis();
        try {
            ToolResult result = tool.execute(toolCall.getArguments(), context);
            long executionTime = System.currentTimeMillis() - startTime;
            
            ToolCallResult tcr = new ToolCallResult(callId, toolName, result);
            tcr.setExecutionTime(executionTime);
            
            log.info("Tool call completed: {} [time={}ms, success={}]", 
                    toolName, executionTime, result.isSuccess());
            
            return tcr;
            
        } catch (Exception e) {
            log.error("Tool call failed: {}", toolName, e);
            return ToolCallResult.failure(callId, toolName, "Execution error: " + e.getMessage());
        }
    }
    
    @Override
    public List<ToolCallResult> executeToolCalls(List<ToolCall> toolCalls, ToolExecutionContext context) {
        log.info("Executing {} tool calls in parallel", toolCalls.size());
        
        List<CompletableFuture<ToolCallResult>> futures = new ArrayList<>();
        
        for (ToolCall call : toolCalls) {
            CompletableFuture<ToolCallResult> future = CompletableFuture.supplyAsync(
                    () -> executeToolCall(call, context), executorService);
            futures.add(future);
        }
        
        List<ToolCallResult> results = new ArrayList<>();
        for (CompletableFuture<ToolCallResult> future : futures) {
            try {
                results.add(future.get(30, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                ToolCallResult timeoutResult = new ToolCallResult();
                timeoutResult.setStatus(ToolCallResult.STATUS_TIMEOUT);
                timeoutResult.setToolResult(ToolResult.failure(ToolCallResult.STATUS_TIMEOUT, "Tool execution timeout"));
                results.add(timeoutResult);
            } catch (Exception e) {
                log.error("Failed to get tool call result", e);
            }
        }
        
        return results;
    }
    
    @Override
    public OrchestrationResult executePlan(OrchestrationPlan plan, ToolExecutionContext context) {
        log.info("Executing orchestration plan: {} [strategy={}]", plan.getPlanId(), plan.getStrategy());
        
        OrchestrationResult result = new OrchestrationResult();
        result.setPlanId(plan.getPlanId());
        
        long startTime = System.currentTimeMillis();
        Map<String, OrchestrationResult.StepResult> completedSteps = new ConcurrentHashMap<>();
        
        try {
            switch (plan.getStrategy()) {
                case SEQUENTIAL:
                    executeSequential(plan, context, completedSteps, result);
                    break;
                case PARALLEL:
                    executeParallel(plan, context, completedSteps, result);
                    break;
                case CONDITIONAL:
                    executeConditional(plan, context, completedSteps, result);
                    break;
                case PIPELINE:
                    executePipeline(plan, context, completedSteps, result);
                    break;
            }
            
            int successCount = result.getSuccessCount();
            int totalSteps = plan.getSteps().size();
            
            if (successCount == totalSteps) {
                result.setStatus(OrchestrationResult.STATUS_SUCCESS);
            } else if (successCount > 0) {
                result.setStatus(OrchestrationResult.STATUS_PARTIAL);
            } else {
                result.setStatus(OrchestrationResult.STATUS_FAILURE);
            }
            
        } catch (Exception e) {
            log.error("Orchestration plan failed: {}", plan.getPlanId(), e);
            result.setStatus(OrchestrationResult.STATUS_FAILURE);
            result.setErrorMessage(e.getMessage());
        }
        
        result.setTotalTime(System.currentTimeMillis() - startTime);
        log.info("Orchestration plan completed: {} [status={}, time={}ms]", 
                plan.getPlanId(), result.getStatus(), result.getTotalTime());
        
        return result;
    }
    
    @Override
    public List<ToolCall> parseToolCalls(String llmResponse) {
        List<ToolCall> toolCalls = new ArrayList<>();
        
        try {
            JSONObject response = JSON.parseObject(llmResponse);
            
            JSONArray choices = response.getJSONArray("choices");
            if (choices != null) {
                for (int i = 0; i < choices.size(); i++) {
                    JSONObject choice = choices.getJSONObject(i);
                    JSONObject message = choice.getJSONObject("message");
                    if (message != null) {
                        JSONArray toolCallsArray = message.getJSONArray("tool_calls");
                        if (toolCallsArray != null) {
                            for (int j = 0; j < toolCallsArray.size(); j++) {
                                JSONObject tc = toolCallsArray.getJSONObject(j);
                                ToolCall toolCall = parseToolCall(tc);
                                if (toolCall != null) {
                                    toolCalls.add(toolCall);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse tool calls from LLM response", e);
        }
        
        return toolCalls;
    }
    
    @Override
    public String formatToolResults(List<ToolCallResult> results) {
        try {
            List<Map<String, Object>> formattedResults = new ArrayList<>();
            
            for (ToolCallResult result : results) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("tool_call_id", result.getToolCallId());
                item.put("role", "tool");
                item.put("name", result.getToolName());
                item.put("content", result.getToolResult().asText());
                formattedResults.add(item);
            }
            
            return JSON.toJSONString(formattedResults);
            
        } catch (Exception e) {
            log.error("Failed to format tool results", e);
            return "[]";
        }
    }
    
    private StringBuilder streamingBuffer = new StringBuilder();
    
    @Override
    public void parseStreamingToolCalls(String chunk, Consumer<ToolCall> callback) {
        if (chunk == null || chunk.isEmpty()) {
            return;
        }
        
        streamingBuffer.append(chunk);
        
        String buffer = streamingBuffer.toString();
        int startIndex = 0;
        
        while (true) {
            int toolCallStart = buffer.indexOf("\"tool_calls\"", startIndex);
            if (toolCallStart == -1) {
                break;
            }
            
            try {
                int bracketStart = buffer.indexOf("[", toolCallStart);
                int bracketEnd = findMatchingBracket(buffer, bracketStart);
                
                if (bracketEnd > bracketStart) {
                    String toolCallsJson = buffer.substring(bracketStart, bracketEnd + 1);
                    JSONArray toolCallsArray = JSON.parseArray(toolCallsJson);
                    
                    for (int i = 0; i < toolCallsArray.size(); i++) {
                        JSONObject tc = toolCallsArray.getJSONObject(i);
                        ToolCall toolCall = parseToolCall(tc);
                        if (toolCall != null) {
                            callback.accept(toolCall);
                        }
                    }
                    
                    startIndex = bracketEnd + 1;
                } else {
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }
        
        if (startIndex > 0) {
            streamingBuffer = new StringBuilder(buffer.substring(startIndex));
        }
    }
    
    @Override
    public void resetStreamingParser() {
        streamingBuffer = new StringBuilder();
    }
    
    private int findMatchingBracket(String str, int start) {
        int depth = 0;
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private void executeSequential(OrchestrationPlan plan, ToolExecutionContext context,
                                   Map<String, OrchestrationResult.StepResult> completedSteps,
                                   OrchestrationResult result) {
        for (OrchestrationPlan.ExecutionStep step : plan.getSteps()) {
            if (!checkDependencies(step, completedSteps)) {
                skipStep(step, result);
                continue;
            }
            
            ToolCallResult tcr = executeToolCall(step.getToolCall(), context);
            OrchestrationResult.StepResult sr = toStepResult(step, tcr);
            completedSteps.put(step.getStepId(), sr);
            result.addStepResult(sr);
        }
    }
    
    private void executeParallel(OrchestrationPlan plan, ToolExecutionContext context,
                                 Map<String, OrchestrationResult.StepResult> completedSteps,
                                 OrchestrationResult result) {
        List<CompletableFuture<OrchestrationResult.StepResult>> futures = new ArrayList<>();
        
        for (OrchestrationPlan.ExecutionStep step : plan.getSteps()) {
            CompletableFuture<OrchestrationResult.StepResult> future = CompletableFuture.supplyAsync(() -> {
                ToolCallResult tcr = executeToolCall(step.getToolCall(), context);
                return toStepResult(step, tcr);
            }, executorService);
            futures.add(future);
        }
        
        for (CompletableFuture<OrchestrationResult.StepResult> future : futures) {
            try {
                OrchestrationResult.StepResult sr = future.get(plan.getTimeout(), TimeUnit.MILLISECONDS);
                completedSteps.put(sr.getStepId(), sr);
                result.addStepResult(sr);
            } catch (Exception e) {
                log.error("Parallel step failed", e);
            }
        }
    }
    
    private void executeConditional(OrchestrationPlan plan, ToolExecutionContext context,
                                    Map<String, OrchestrationResult.StepResult> completedSteps,
                                    OrchestrationResult result) {
        for (OrchestrationPlan.ExecutionStep step : plan.getSteps()) {
            if (!checkDependencies(step, completedSteps)) {
                skipStep(step, result);
                continue;
            }
            
            if (!evaluateCondition(step, completedSteps)) {
                skipStep(step, result);
                continue;
            }
            
            ToolCallResult tcr = executeToolCall(step.getToolCall(), context);
            OrchestrationResult.StepResult sr = toStepResult(step, tcr);
            completedSteps.put(step.getStepId(), sr);
            result.addStepResult(sr);
        }
    }
    
    private void executePipeline(OrchestrationPlan plan, ToolExecutionContext context,
                                 Map<String, OrchestrationResult.StepResult> completedSteps,
                                 OrchestrationResult result) {
        Map<String, Object> pipelineData = new HashMap<>();
        
        for (OrchestrationPlan.ExecutionStep step : plan.getSteps()) {
            if (!checkDependencies(step, completedSteps)) {
                skipStep(step, result);
                continue;
            }
            
            Map<String, Object> enrichedArgs = new HashMap<>(step.getToolCall().getArguments());
            for (Map.Entry<String, Object> entry : pipelineData.entrySet()) {
                if (!enrichedArgs.containsKey(entry.getKey())) {
                    enrichedArgs.put(entry.getKey(), entry.getValue());
                }
            }
            
            ToolCall enrichedCall = new ToolCall(
                    step.getToolCall().getId(),
                    step.getToolCall().getName(),
                    enrichedArgs
            );
            
            ToolCallResult tcr = executeToolCall(enrichedCall, context);
            OrchestrationResult.StepResult sr = toStepResult(step, tcr);
            completedSteps.put(step.getStepId(), sr);
            result.addStepResult(sr);
            
            if (tcr.isSuccess() && tcr.getToolResult().getData() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> output = (Map<String, Object>) tcr.getToolResult().getData();
                pipelineData.putAll(output);
            }
        }
        
        result.setOutputs(pipelineData);
    }
    
    private boolean checkDependencies(OrchestrationPlan.ExecutionStep step, 
                                      Map<String, OrchestrationResult.StepResult> completedSteps) {
        for (String depId : step.getDependencies()) {
            OrchestrationResult.StepResult depResult = completedSteps.get(depId);
            if (depResult == null || !depResult.isSuccess()) {
                return false;
            }
        }
        return true;
    }
    
    private boolean evaluateCondition(OrchestrationPlan.ExecutionStep step,
                                      Map<String, OrchestrationResult.StepResult> completedSteps) {
        String condition = step.getCondition();
        if (condition == null || condition.isEmpty()) {
            return true;
        }
        
        return true;
    }
    
    private void skipStep(OrchestrationPlan.ExecutionStep step, OrchestrationResult result) {
        OrchestrationResult.StepResult sr = new OrchestrationResult.StepResult();
        sr.setStepId(step.getStepId());
        sr.setToolName(step.getToolCall().getName());
        sr.setStatus(OrchestrationResult.StepResult.STATUS_SKIPPED);
        result.addStepResult(sr);
    }
    
    private OrchestrationResult.StepResult toStepResult(OrchestrationPlan.ExecutionStep step, ToolCallResult tcr) {
        OrchestrationResult.StepResult sr = new OrchestrationResult.StepResult();
        sr.setStepId(step.getStepId());
        sr.setToolName(step.getToolCall().getName());
        sr.setToolResult(tcr.getToolResult());
        sr.setExecutionTime(tcr.getExecutionTime());
        sr.setStatus(tcr.getStatus());
        return sr;
    }
    
    private ToolCall parseToolCall(JSONObject tc) {
        String id = tc.getString("id");
        String type = tc.getString("type");
        
        if ("function".equals(type)) {
            JSONObject function = tc.getJSONObject("function");
            String name = function.getString("name");
            String argsJson = function.getString("arguments");
            
            Map<String, Object> arguments = new HashMap<>();
            try {
                arguments = JSON.parseObject(argsJson, 
                    new com.alibaba.fastjson2.TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.warn("Failed to parse tool arguments: {}", argsJson);
            }
            
            return new ToolCall(id, name, arguments);
        }
        
        // 非 function 类型的 tool call，返回 null 表示解析失败
        // 调用方应检查返回值
        return null;
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
}
