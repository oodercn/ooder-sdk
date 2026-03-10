package net.ooder.skills.spi;

import net.ooder.skills.api.SkillCategory;
import net.ooder.skills.api.SkillForm;
import net.ooder.skills.api.SceneType;

import java.util.List;
import java.util.Map;

/**
 * LLM Provider SPI 接口
 * 
 * <p>用于扩展 LLM 服务提供者，支持自定义 LLM 实现</p>
 * 
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>接入第三方 LLM 服务（OpenAI、Claude、文心一言等）</li>
 *   <li>自定义本地模型部署</li>
 *   <li>实现模型路由和负载均衡</li>
 * </ul>
 * 
 * <h3>实现示例：</h3>
 * <pre>
 * public class OpenAiProvider implements LlmProviderSpi {
 *     @Override
 *     public String getProviderName() {
 *         return "openai";
 *     }
 *     
 *     @Override
 *     public LlmResponse chat(LlmRequest request) {
 *         // 调用 OpenAI API
 *     }
 * }
 * </pre>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface LlmProviderSpi {
    
    /**
     * 获取提供者名称
     * 
     * @return 唯一标识，如：openai、claude、qianwen
     */
    String getProviderName();
    
    /**
     * 获取提供者显示名称
     * 
     * @return 人类可读的名称
     */
    default String getDisplayName() {
        return getProviderName();
    }
    
    /**
     * 是否支持当前配置
     * 
     * @param config 配置参数
     * @return 是否支持
     */
    boolean supports(Map<String, Object> config);
    
    /**
     * 执行对话
     * 
     * @param request 对话请求
     * @return 对话响应
     */
    LlmResponse chat(LlmRequest request);
    
    /**
     * 执行流式对话
     * 
     * @param request 对话请求
     * @param callback 流式回调
     */
    void chatStream(LlmRequest request, LlmStreamCallback callback);
    
    /**
     * 获取支持的模型列表
     * 
     * @return 模型列表
     */
    List<String> getSupportedModels();
    
    /**
     * 获取默认模型
     * 
     * @return 默认模型名称
     */
    String getDefaultModel();
    
    /**
     * 获取模型信息
     * 
     * @param modelName 模型名称
     * @return 模型信息
     */
    ModelInfo getModelInfo(String modelName);
    
    /**
     * 验证配置
     * 
     * @param config 配置参数
     * @return 验证结果
     */
    ValidationResult validateConfig(Map<String, Object> config);
    
    /**
     * 健康检查
     * 
     * @return 健康状态
     */
    HealthStatus healthCheck();
    
    // ========== 内部类定义 ==========
    
    /**
     * LLM 请求
     */
    class LlmRequest {
        private String model;
        private List<Message> messages;
        private List<FunctionDef> functions;
        private Map<String, Object> parameters;
        private String sessionId;
        private Map<String, Object> context;
        
        // Getters and Setters
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
        
        public List<FunctionDef> getFunctions() { return functions; }
        public void setFunctions(List<FunctionDef> functions) { this.functions = functions; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }
    
    /**
     * 消息
     */
    class Message {
        private String role;      // system, user, assistant, function
        private String content;
        private String functionCall;
        private String name;      // function name
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getFunctionCall() { return functionCall; }
        public void setFunctionCall(String functionCall) { this.functionCall = functionCall; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    /**
     * 函数定义
     */
    class FunctionDef {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    /**
     * LLM 响应
     */
    class LlmResponse {
        private String content;
        private String finishReason;
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
        private FunctionCall functionCall;
        private Map<String, Object> metadata;
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
        
        public int getPromptTokens() { return promptTokens; }
        public void setPromptTokens(int promptTokens) { this.promptTokens = promptTokens; }
        
        public int getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(int completionTokens) { this.completionTokens = completionTokens; }
        
        public int getTotalTokens() { return totalTokens; }
        public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
        
        public FunctionCall getFunctionCall() { return functionCall; }
        public void setFunctionCall(FunctionCall functionCall) { this.functionCall = functionCall; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    /**
     * 函数调用
     */
    class FunctionCall {
        private String name;
        private String arguments;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getArguments() { return arguments; }
        public void setArguments(String arguments) { this.arguments = arguments; }
    }
    
    /**
     * 流式回调
     */
    interface LlmStreamCallback {
        void onStart();
        void onChunk(String chunk);
        void onComplete(LlmResponse response);
        void onError(Throwable error);
    }
    
    /**
     * 模型信息
     */
    class ModelInfo {
        private String name;
        private String displayName;
        private String description;
        private int maxTokens;
        private boolean supportsFunctions;
        private boolean supportsStreaming;
        private Map<String, Object> capabilities;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        
        public boolean isSupportsFunctions() { return supportsFunctions; }
        public void setSupportsFunctions(boolean supportsFunctions) { this.supportsFunctions = supportsFunctions; }
        
        public boolean isSupportsStreaming() { return supportsStreaming; }
        public void setSupportsStreaming(boolean supportsStreaming) { this.supportsStreaming = supportsStreaming; }
        
        public Map<String, Object> getCapabilities() { return capabilities; }
        public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
    }
    
    /**
     * 验证结果
     */
    class ValidationResult {
        private boolean valid;
        private String message;
        private List<String> errors;
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        
        public static ValidationResult success() {
            ValidationResult result = new ValidationResult();
            result.setValid(true);
            return result;
        }
        
        public static ValidationResult failure(String message) {
            ValidationResult result = new ValidationResult();
            result.setValid(false);
            result.setMessage(message);
            return result;
        }
    }
    
    /**
     * 健康状态
     */
    class HealthStatus {
        private boolean healthy;
        private String status;
        private String message;
        private long responseTime;
        
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
        
        public static HealthStatus up() {
            HealthStatus status = new HealthStatus();
            status.setHealthy(true);
            status.setStatus("UP");
            return status;
        }
        
        public static HealthStatus down(String message) {
            HealthStatus status = new HealthStatus();
            status.setHealthy(false);
            status.setStatus("DOWN");
            status.setMessage(message);
            return status;
        }
    }
}
