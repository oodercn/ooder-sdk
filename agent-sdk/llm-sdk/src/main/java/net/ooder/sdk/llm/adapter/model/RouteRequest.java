package net.ooder.sdk.llm.adapter.model;

import java.util.List;

public class RouteRequest {
    private String requestId;
    private String prompt;
    private List<String> preferredProviders;
    private String routingStrategy;
    private Boolean enableFallback;

    public RouteRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<String> getPreferredProviders() {
        return preferredProviders;
    }

    public void setPreferredProviders(List<String> preferredProviders) {
        this.preferredProviders = preferredProviders;
    }

    public String getRoutingStrategy() {
        return routingStrategy;
    }

    public void setRoutingStrategy(String routingStrategy) {
        this.routingStrategy = routingStrategy;
    }

    public Boolean getEnableFallback() {
        return enableFallback;
    }

    public void setEnableFallback(Boolean enableFallback) {
        this.enableFallback = enableFallback;
    }
}
