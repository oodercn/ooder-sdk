package net.ooder.scene.llm.impl;

import net.ooder.scene.llm.LlmService;
import net.ooder.scene.llm.SceneChatRequest;
import net.ooder.scene.skill.llm.StreamHandler;
import net.ooder.sdk.llm.tool.ChatResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NoOp LLM 服务实现
 * 
 * <p>空实现，用于自动配置时提供默认 Bean。</p>
 * <p>当没有实际的 LLM 服务实现时使用此空实现。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class NoOpLlmService implements LlmService {
    
    private String activeProvider;
    private String activeModel;
    private final Map<String, FunctionConfig> registeredFunctions = new HashMap<>();
    
    @Override
    public ChatResponse chat(SceneChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setContent("NoOp LLM Service: LLM service not configured. Please configure a real LLM service.");
        return response;
    }
    
    @Override
    public void chatStream(SceneChatRequest request, StreamHandler handler) {
        // 空实现，不执行任何操作
    }
    
    @Override
    public String complete(String prompt, int maxTokens) {
        return "NoOp LLM Service: LLM service not configured.";
    }
    
    @Override
    public List<ProviderInfo> getProviders() {
        return Collections.emptyList();
    }
    
    @Override
    public List<ModelInfo> getModels(String providerId) {
        return Collections.emptyList();
    }
    
    @Override
    public void setActiveProvider(String providerId) {
        this.activeProvider = providerId;
    }
    
    @Override
    public void setActiveModel(String providerId, String modelId) {
        this.activeProvider = providerId;
        this.activeModel = modelId;
    }
    
    @Override
    public String getActiveProvider() {
        return activeProvider;
    }
    
    @Override
    public String getActiveModel() {
        return activeModel;
    }
    
    @Override
    public void registerFunction(String functionId, FunctionConfig functionConfig) {
        registeredFunctions.put(functionId, functionConfig);
    }
    
    @Override
    public void unregisterFunction(String functionId) {
        registeredFunctions.remove(functionId);
    }
    
    @Override
    public Map<String, FunctionConfig> getRegisteredFunctions() {
        return Collections.unmodifiableMap(registeredFunctions);
    }
}
