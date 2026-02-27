package net.ooder.sdk.llm.capability.model;

import net.ooder.sdk.llm.common.enums.LlmType;

public class LlmEndpoint {
    private String endpointId;
    private String endpointUrl;
    private LlmType llmType;
    private String modelName;
    private Integer maxTokens;
    private Double temperature;

    public LlmEndpoint() {
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public LlmType getLlmType() {
        return llmType;
    }

    public void setLlmType(LlmType llmType) {
        this.llmType = llmType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
