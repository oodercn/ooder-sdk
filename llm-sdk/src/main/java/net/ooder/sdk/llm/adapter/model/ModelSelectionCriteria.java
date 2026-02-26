package net.ooder.sdk.llm.adapter.model;

import net.ooder.sdk.llm.common.enums.CapabilityType;
import net.ooder.sdk.llm.common.enums.LlmType;

import java.util.List;

public class ModelSelectionCriteria {
    private LlmType preferredLlmType;
    private List<CapabilityType> requiredCapabilities;
    private Integer minMaxTokens;
    private Double maxCostPerToken;
    private Double maxLatencyMs;
    private String preferredLanguage;

    public ModelSelectionCriteria() {
    }

    public LlmType getPreferredLlmType() {
        return preferredLlmType;
    }

    public void setPreferredLlmType(LlmType preferredLlmType) {
        this.preferredLlmType = preferredLlmType;
    }

    public List<CapabilityType> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    public void setRequiredCapabilities(List<CapabilityType> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }

    public Integer getMinMaxTokens() {
        return minMaxTokens;
    }

    public void setMinMaxTokens(Integer minMaxTokens) {
        this.minMaxTokens = minMaxTokens;
    }

    public Double getMaxCostPerToken() {
        return maxCostPerToken;
    }

    public void setMaxCostPerToken(Double maxCostPerToken) {
        this.maxCostPerToken = maxCostPerToken;
    }

    public Double getMaxLatencyMs() {
        return maxLatencyMs;
    }

    public void setMaxLatencyMs(Double maxLatencyMs) {
        this.maxLatencyMs = maxLatencyMs;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
