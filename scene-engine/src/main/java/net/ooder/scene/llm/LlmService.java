package net.ooder.scene.llm;

import net.ooder.scene.skill.llm.StreamHandler;

import java.util.List;
import java.util.Map;

/**
 * LLM 统一服务接口
 *
 * <p>提供统一的 LLM 调用入口，支持：</p>
 * <ul>
 *   <li>多 Provider 管理</li>
 *   <li>统一聊天接口</li>
 *   <li>流式输出</li>
 *   <li>Function Calling</li>
 * </ul>
 *
 * @author ooder
 * @since 2.4
 */
public interface LlmService {

    /**
     * 统一聊天接口
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 流式聊天
     *
     * @param request 聊天请求
     * @param handler 流式处理器
     */
    void chatStream(ChatRequest request, StreamHandler handler);

    /**
     * 文本补全
     *
     * @param prompt 提示词
     * @param maxTokens 最大 Token 数
     * @return 补全结果
     */
    String complete(String prompt, int maxTokens);

    /**
     * 获取可用的 Provider 列表
     *
     * @return Provider 信息列表
     */
    List<ProviderInfo> getProviders();

    /**
     * 获取指定 Provider 支持的模型
     *
     * @param providerId Provider ID
     * @return 模型信息列表
     */
    List<ModelInfo> getModels(String providerId);

    /**
     * 设置当前使用的 Provider
     *
     * @param providerId Provider ID
     */
    void setActiveProvider(String providerId);

    /**
     * 设置当前使用的模型
     *
     * @param providerId Provider ID
     * @param modelId 模型 ID
     */
    void setActiveModel(String providerId, String modelId);

    /**
     * 获取当前活动的 Provider
     *
     * @return Provider ID
     */
    String getActiveProvider();

    /**
     * 获取当前活动的模型
     *
     * @return 模型 ID
     */
    String getActiveModel();

    /**
     * 注册 Function
     *
     * @param functionId Function ID
     * @param functionConfig Function 配置
     */
    void registerFunction(String functionId, FunctionConfig functionConfig);

    /**
     * 注销 Function
     *
     * @param functionId Function ID
     */
    void unregisterFunction(String functionId);

    /**
     * 获取已注册的 Functions
     *
     * @return Function 配置 Map
     */
    Map<String, FunctionConfig> getRegisteredFunctions();

    /**
     * Provider 信息
     */
    class ProviderInfo {
        private String id;
        private String name;
        private String type;
        private List<ModelInfo> models;

        public ProviderInfo() {}

        public ProviderInfo(String id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<ModelInfo> getModels() { return models; }
        public void setModels(List<ModelInfo> models) { this.models = models; }
    }

    /**
     * 模型信息
     */
    class ModelInfo {
        private String id;
        private String name;
        private int contextLength;
        private boolean supportsFunctionCall;
        private boolean supportsVision;

        public ModelInfo() {}

        public ModelInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getContextLength() { return contextLength; }
        public void setContextLength(int contextLength) { this.contextLength = contextLength; }
        public boolean isSupportsFunctionCall() { return supportsFunctionCall; }
        public void setSupportsFunctionCall(boolean supportsFunctionCall) { this.supportsFunctionCall = supportsFunctionCall; }
        public boolean isSupportsVision() { return supportsVision; }
        public void setSupportsVision(boolean supportsVision) { this.supportsVision = supportsVision; }
    }

    /**
     * Function 配置
     */
    class FunctionConfig {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private List<String> required;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        public List<String> getRequired() { return required; }
        public void setRequired(List<String> required) { this.required = required; }
    }
}
