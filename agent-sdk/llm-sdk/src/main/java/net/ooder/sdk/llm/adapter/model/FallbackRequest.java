package net.ooder.sdk.llm.adapter.model;

import java.util.List;

public class FallbackRequest {
    private String requestId;
    private String currentProviderId;
    private String currentModelId;
    private String fallbackReason;
    private List<String> excludeProviders;

    public FallbackRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCurrentProviderId() {
        return currentProviderId;
    }

    public void setCurrentProviderId(String currentProviderId) {
        this.currentProviderId = currentProviderId;
    }

    public String getCurrentModelId() {
        return currentModelId;
    }

    public void setCurrentModelId(String currentModelId) {
        this.currentModelId = currentModelId;
    }

    public String getFallbackReason() {
        return fallbackReason;
    }

    public void setFallbackReason(String fallbackReason) {
        this.fallbackReason = fallbackReason;
    }

    public List<String> getExcludeProviders() {
        return excludeProviders;
    }

    public void setExcludeProviders(List<String> excludeProviders) {
        this.excludeProviders = excludeProviders;
    }
}
