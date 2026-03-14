package net.ooder.scene.llm.impl;

import net.ooder.scene.llm.LlmService;
import net.ooder.scene.llm.SceneChatRequest;
import net.ooder.scene.skill.llm.StreamHandler;
import net.ooder.sdk.llm.tool.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM 服务默认实现（占位符）
 * <p>提供默认实现，允许 SE 在没有 LLM 插件的情况下启动</p>
 * <p>实际 LLM 功能由 skill-llm 插件提供</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class DefaultLlmService implements LlmService {

    private static final Logger log = LoggerFactory.getLogger(DefaultLlmService.class);

    private final Map<String, FunctionConfig> functions = new HashMap<>();

    @Override
    public ChatResponse chat(SceneChatRequest request) {
        log.warn("DefaultLlmService.chat() called but no LLM provider configured. " +
                "Please install skill-llm plugin.");

        ChatResponse response = new ChatResponse();
        response.setContent("[系统提示：LLM 服务未配置，请安装 skill-llm 插件]");
        return response;
    }

    @Override
    public void chatStream(SceneChatRequest request, StreamHandler handler) {
        log.warn("DefaultLlmService.chatStream() called but no LLM provider configured. " +
                "Please install skill-llm plugin.");

        handler.onContent("[系统提示：LLM 服务未配置，请安装 skill-llm 插件]");
        handler.onComplete(new HashMap<>());
    }

    @Override
    public String complete(String prompt, int maxTokens) {
        log.warn("DefaultLlmService.complete() called but no LLM provider configured. " +
                "Please install skill-llm plugin.");
        return "[系统提示：LLM 服务未配置，请安装 skill-llm 插件]";
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
        log.warn("Cannot set active provider: no LLM provider configured");
    }

    @Override
    public void setActiveModel(String providerId, String modelId) {
        log.warn("Cannot set active model: no LLM provider configured");
    }

    @Override
    public String getActiveProvider() {
        return null;
    }

    @Override
    public String getActiveModel() {
        return null;
    }

    @Override
    public void registerFunction(String functionId, FunctionConfig functionConfig) {
        functions.put(functionId, functionConfig);
    }

    @Override
    public void unregisterFunction(String functionId) {
        functions.remove(functionId);
    }

    @Override
    public Map<String, FunctionConfig> getRegisteredFunctions() {
        return new HashMap<>(functions);
    }
}
