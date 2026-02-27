package net.ooder.llm.api;

import java.util.List;
import java.util.Map;

/**
 * Chat Request for LLM Service（泛型版本）
 *
 * @param <P> 参数类型
 * @author ooder Team
 * @since 2.3
 */
public class ChatRequest<P> {
    /** 参数类型定义 */
    public static final String PARAM_TYPE_STRING = "string";
    public static final String PARAM_TYPE_NUMBER = "number";
    public static final String PARAM_TYPE_BOOLEAN = "boolean";
    public static final String PARAM_TYPE_ARRAY = "array";
    public static final String PARAM_TYPE_OBJECT = "object";

    private String prompt;
    private String systemPrompt;
    private List<Message> messages;
    /** 请求参数 */
    private Map<String, P> parameters;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private List<FunctionDef> functions;

    public ChatRequest() {
        this.parameters = new java.util.HashMap<String, P>();
    }
    
    /**
     * 创建通用 ChatRequest（向后兼容）
     */
    public static ChatRequest<Object> createGeneric() {
        return new ChatRequest<>();
    }

    public static ChatRequest<Object> of(String prompt) {
        ChatRequest<Object> request = new ChatRequest<>();
        request.setPrompt(prompt);
        return request;
    }

    public static ChatRequest<Object> of(String prompt, String systemPrompt) {
        ChatRequest<Object> request = new ChatRequest<>();
        request.setPrompt(prompt);
        request.setSystemPrompt(systemPrompt);
        return request;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    /**
     * 获取请求参数
     * @return 参数映射
     */
    public Map<String, P> getParameters() { return parameters; }
    /**
     * 设置请求参数
     * @param parameters 参数映射
     */
    public void setParameters(Map<String, P> parameters) { this.parameters = parameters; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public List<FunctionDef> getFunctions() { return functions; }
    public void setFunctions(List<FunctionDef> functions) { this.functions = functions; }

    public ChatRequest<P> addParameter(String key, P value) {
        this.parameters.put(key, value);
        return this;
    }

    public static class Message {
        private String role;
        private String content;
        private String name;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
