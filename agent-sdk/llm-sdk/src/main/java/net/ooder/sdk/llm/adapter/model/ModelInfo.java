package net.ooder.sdk.llm.adapter.model;

import java.util.List;

public class ModelInfo {
    private String modelId;
    private String modelName;
    private String modelVersion;
    private Integer maxTokens;
    private List<String> capabilities;
    private Double costPerToken;
    private String description;

    public ModelInfo() {
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public Double getCostPerToken() {
        return costPerToken;
    }

    public void setCostPerToken(Double costPerToken) {
        this.costPerToken = costPerToken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
