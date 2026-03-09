package net.ooder.scene.skill.llm.impl;

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
    public List<Map<String, Object>> batchChat(List<ChatRequest> requests) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (ChatRequest request : requests) {
            try {
                Map<String, Object> result = chat(
                    request.getModel(),
                    request.getMessages(),
                    request.getOptions()
                );
                results.add(result);
            } catch (Exception e) {
                log.error("Batch chat failed for request: {}", request.getRequestId(), e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", e.getMessage());
                errorResult.put("requestId", request.getRequestId());
                results.add(errorResult);
            }
        }
        return results;
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
