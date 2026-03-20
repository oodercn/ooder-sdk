package net.ooder.scene.llm.audit;

import java.util.Map;

/**
 * LLM 调用结果
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class LlmCallResult {
    
    private String providerId;
    private String providerName;
    private String model;
    private String requestType;
    private int inputTokens;
    private int outputTokens;
    private int totalTokens;
    private double cost;
    private long latency;
    private String status;
    private String errorMessage;
    private Map<String, Object> metadata;
    
    public LlmCallResult() {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public int getInputTokens() { return inputTokens; }
    public void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }
    public int getOutputTokens() { return outputTokens; }
    public void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }
    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public long getLatency() { return latency; }
    public void setLatency(long latency) { this.latency = latency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    public static class Builder {
        private final LlmCallResult result = new LlmCallResult();
        
        public Builder providerId(String providerId) { result.setProviderId(providerId); return this; }
        public Builder providerName(String providerName) { result.setProviderName(providerName); return this; }
        public Builder model(String model) { result.setModel(model); return this; }
        public Builder requestType(String requestType) { result.setRequestType(requestType); return this; }
        public Builder inputTokens(int inputTokens) { result.setInputTokens(inputTokens); return this; }
        public Builder outputTokens(int outputTokens) { result.setOutputTokens(outputTokens); return this; }
        public Builder totalTokens(int totalTokens) { result.setTotalTokens(totalTokens); return this; }
        public Builder cost(double cost) { result.setCost(cost); return this; }
        public Builder latency(long latency) { result.setLatency(latency); return this; }
        public Builder status(String status) { result.setStatus(status); return this; }
        public Builder errorMessage(String errorMessage) { result.setErrorMessage(errorMessage); return this; }
        public Builder metadata(Map<String, Object> metadata) { result.setMetadata(metadata); return this; }
        
        public LlmCallResult build() { return result; }
    }
}
