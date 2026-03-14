package net.ooder.scene.skill.llm.impl;

import net.ooder.scene.llm.SceneChatRequest;
import net.ooder.scene.skill.llm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象 LLM Provider 基类
 * 
 * <p>提供通用的 LLM Provider 实现，子类只需实现核心调用逻辑</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public abstract class AbstractLlmProvider implements EnhancedLlmProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    protected final String providerType;
    protected final Map<String, ModelConfig> modelConfigs = new ConcurrentHashMap<>();
    protected final Map<String, Integer> contextWindows = new ConcurrentHashMap<>();
    
    protected boolean streamingSupported = false;
    protected boolean functionCallingSupported = false;

    public AbstractLlmProvider(String providerType) {
        this.providerType = providerType;
        initDefaultModels();
    }

    protected void initDefaultModels() {
    }

    protected void registerModel(String modelName, int contextWindow, 
                                  boolean supportsStreaming, boolean supportsFunctionCalling) {
        ModelConfig config = new ModelConfig(modelName, supportsStreaming, supportsFunctionCalling);
        modelConfigs.put(modelName, config);
        contextWindows.put(modelName, contextWindow);
    }

    @Override
    public String getProviderType() {
        return providerType;
    }

    @Override
    public List<String> getSupportedModels() {
        return new ArrayList<>(modelConfigs.keySet());
    }

    @Override
    public boolean supportsStreaming() {
        return streamingSupported;
    }

    @Override
    public boolean supportsFunctionCalling() {
        return functionCallingSupported;
    }

    @Override
    public boolean supportsFunctionCalling(String model) {
        ModelConfig config = modelConfigs.get(model);
        return config != null && config.supportsFunctionCalling;
    }

    @Override
    public boolean supportsMultimodal(String model) {
        ModelConfig config = modelConfigs.get(model);
        return config != null && config.supportsMultimodal;
    }

    @Override
    public int getContextWindowSize(String model) {
        Integer size = contextWindows.get(model);
        return size != null ? size : 4096;
    }

    @Override
    public Map<String, Object> chatWithFunctions(String model, 
                                                  List<Map<String, Object>> messages,
                                                  List<FunctionCall> functions,
                                                  Map<String, Object> options) {
        if (!supportsFunctionCalling(model)) {
            log.warn("Model {} does not support function calling", model);
            return chat(model, messages, options);
        }

        Map<String, Object> enhancedOptions = new HashMap<>();
        if (options != null) {
            enhancedOptions.putAll(options);
        }
        
        List<Map<String, Object>> functionDefs = new ArrayList<>();
        for (FunctionCall fc : functions) {
            Map<String, Object> def = new HashMap<>();
            def.put("name", fc.getName());
            def.put("description", fc.getDescription());
            def.put("parameters", fc.getParameters());
            functionDefs.add(def);
        }
        enhancedOptions.put("functions", functionDefs);

        return chat(model, messages, enhancedOptions);
    }

    @Override
    public Map<String, Object> executeFunctionCall(String model,
                                                    List<Map<String, Object>> messages,
                                                    String functionName,
                                                    Map<String, Object> functionArgs,
                                                    Object functionResult,
                                                    Map<String, Object> options) {
        List<Map<String, Object>> newMessages = new ArrayList<>(messages);
        
        Map<String, Object> assistantMessage = new HashMap<>();
        assistantMessage.put("role", "assistant");
        Map<String, Object> functionCall = new HashMap<>();
        functionCall.put("name", functionName);
        functionCall.put("arguments", functionArgs);
        assistantMessage.put("function_call", functionCall);
        newMessages.add(assistantMessage);
        
        Map<String, Object> functionMessage = new HashMap<>();
        functionMessage.put("role", "function");
        functionMessage.put("name", functionName);
        functionMessage.put("content", toJson(functionResult));
        newMessages.add(functionMessage);

        return chat(model, newMessages, options);
    }

    @Override
    public Map<String, Object> chatMultimodal(String model,
                                               List<Map<String, Object>> messages,
                                               Map<String, Object> options) {
        if (!supportsMultimodal(model)) {
            log.warn("Model {} does not support multimodal", model);
            return chat(model, messages, options);
        }
        return chat(model, messages, options);
    }

    @Override
    public Map<String, Object> chatWithContext(String model,
                                                List<Map<String, Object>> messages,
                                                String systemPrompt,
                                                Map<String, Object> context,
                                                Map<String, Object> options) {
        List<Map<String, Object>> enhancedMessages = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildSystemContent(systemPrompt, context));
            enhancedMessages.add(systemMessage);
        }
        
        enhancedMessages.addAll(messages);

        return chat(model, enhancedMessages, options);
    }

    @Override
    public List<Map<String, Object>> batchChat(List<SceneChatRequest> requests) {
        List<Map<String, Object>> results = new ArrayList<>();
        int index = 0;
        for (SceneChatRequest request : requests) {
            try {
                Map<String, Object> result = chat(
                    request.getModel(),
                    convertToMapList(request.getMessages()),
                    request.getParameters()
                );
                results.add(result);
            } catch (Exception e) {
                log.error("Batch chat failed for request #{}: {}", index, e.getMessage(), e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", e.getMessage());
                errorResult.put("requestIndex", index);
                results.add(errorResult);
            }
            index++;
        }
        return results;
    }

    private List<Map<String, Object>> convertToMapList(List<SceneChatRequest.Message> messages) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SceneChatRequest.Message msg : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("role", msg.getRole());
            map.put("content", msg.getContent());
            if (msg.getName() != null) {
                map.put("name", msg.getName());
            }
            result.add(map);
        }
        return result;
    }

    @Override
    public int countTokens(String model, String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil(text.length() / 4.0);
    }

    protected String buildSystemContent(String systemPrompt, Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return systemPrompt;
        }

        StringBuilder sb = new StringBuilder(systemPrompt);
        sb.append("\n\nContext:\n");
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    protected String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }
        if (obj instanceof List) {
            return listToJson((List<?>) obj);
        }
        return obj.toString();
    }

    protected String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("\"").append(entry.getKey()).append("\": ");
            sb.append(toJson(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    protected String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(toJson(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Map<String, Object> chatWithTools(String model,
                                              List<Map<String, Object>> messages,
                                              List<String> toolNames,
                                              Map<String, Object> options) {
        log.info("chatWithTools called with model: {}, tools: {}", model, toolNames);
        
        // 1. 获取工具定义（这里简化处理，实际需要集成 ToolRegistry）
        List<Map<String, Object>> tools = buildToolDefinitions(toolNames);
        
        // 2. 调用带函数调用的对话
        Map<String, Object> response = chatWithFunctions(model, messages, null, options);
        
        // 3. 检查是否需要工具调用
        if (response.containsKey("function_call")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> functionCall = (Map<String, Object>) response.get("function_call");
            String functionName = (String) functionCall.get("name");
            @SuppressWarnings("unchecked")
            Map<String, Object> functionArgs = (Map<String, Object>) functionCall.get("arguments");
            
            log.info("Function call detected: {} with args: {}", functionName, functionArgs);
            
            // 4. 执行工具调用（简化实现，实际需要调用 ToolOrchestrator）
            Object functionResult = executeTool(functionName, functionArgs);
            
            // 5. 将工具结果反馈给 LLM
            Map<String, Object> resultMessage = new HashMap<>();
            resultMessage.put("role", "function");
            resultMessage.put("name", functionName);
            resultMessage.put("content", toJson(functionResult));
            
            List<Map<String, Object>> newMessages = new ArrayList<>(messages);
            newMessages.add(resultMessage);
            
            // 6. 再次调用获取最终响应
            return chat(model, newMessages, options);
        }
        
        return response;
    }
    
    /**
     * 构建工具定义列表
     */
    protected List<Map<String, Object>> buildToolDefinitions(List<String> toolNames) {
        // 简化实现，返回空列表
        // 实际实现需要从 ToolRegistry 获取工具定义
        return new ArrayList<>();
    }
    
    /**
     * 执行工具调用
     */
    protected Object executeTool(String functionName, Map<String, Object> args) {
        // 简化实现，返回模拟结果
        // 实际实现需要调用 ToolOrchestrator 执行工具
        log.info("Executing tool: {} with args: {}", functionName, args);
        
        Map<String, Object> result = new HashMap<>();
        result.put("tool", functionName);
        result.put("status", "success");
        result.put("result", "Tool execution result for " + functionName);
        return result;
    }

    protected static class ModelConfig {
        final String name;
        final boolean supportsStreaming;
        final boolean supportsFunctionCalling;
        boolean supportsMultimodal = false;

        ModelConfig(String name, boolean supportsStreaming, boolean supportsFunctionCalling) {
            this.name = name;
            this.supportsStreaming = supportsStreaming;
            this.supportsFunctionCalling = supportsFunctionCalling;
        }

        void setSupportsMultimodal(boolean supportsMultimodal) {
            this.supportsMultimodal = supportsMultimodal;
        }
    }
}
