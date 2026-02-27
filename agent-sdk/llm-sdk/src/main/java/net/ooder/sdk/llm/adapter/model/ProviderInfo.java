package net.ooder.sdk.llm.adapter.model;

import net.ooder.sdk.llm.common.enums.LlmType;

import java.util.List;
import java.util.Map;

public class ProviderInfo {
    private String providerId;
    private String providerName;
    private LlmType llmType;
    private String endpoint;
    private String apiKey;
    private List<ModelInfo> supportedModels;
    private Map<String, Object> config;

    public ProviderInfo() {
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public LlmType getLlmType() {
        return llmType;
    }

    public void setLlmType(LlmType llmType) {
        this.llmType = llmType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<ModelInfo> getSupportedModels() {
        return supportedModels;
    }

    public void setSupportedModels(List<ModelInfo> supportedModels) {
        this.supportedModels = supportedModels;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
