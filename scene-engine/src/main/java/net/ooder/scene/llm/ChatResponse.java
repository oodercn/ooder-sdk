package net.ooder.scene.llm;

import java.util.List;
import java.util.Map;

/**
 * 聊天响应
 *
 * @author ooder
 * @since 2.4
 */
public class ChatResponse {

    private String id;
    private String provider;
    private String model;
    private String content;
    private List<FunctionCall> functionCalls;
    private Map<String, Object> metadata;
    private Usage usage;
    private boolean success;
    private String errorMessage;

    public ChatResponse() {}

    public static ChatResponse success(String content) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setContent(content);
        return response;
    }

    public static ChatResponse failure(String errorMessage) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<FunctionCall> getFunctionCalls() { return functionCalls; }
    public void setFunctionCalls(List<FunctionCall> functionCalls) { this.functionCalls = functionCalls; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean hasFunctionCalls() {
        return functionCalls != null && !functionCalls.isEmpty();
    }

    /**
     * Token 使用量
     */
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;

        public Usage() {}

        public Usage(int promptTokens, int completionTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = promptTokens + completionTokens;
        }

        public int getPromptTokens() { return promptTokens; }
        public void setPromptTokens(int promptTokens) { this.promptTokens = promptTokens; }
        public int getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(int completionTokens) { this.completionTokens = completionTokens; }
        public int getTotalTokens() { return totalTokens; }
        public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    }
}
