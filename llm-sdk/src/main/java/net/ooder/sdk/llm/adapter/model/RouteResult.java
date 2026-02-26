package net.ooder.sdk.llm.adapter.model;

public class RouteResult {
    private String requestId;
    private String selectedProviderId;
    private String selectedModelId;
    private String routingStrategy;
    private String status;
    private String message;

    public RouteResult() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSelectedProviderId() {
        return selectedProviderId;
    }

    public void setSelectedProviderId(String selectedProviderId) {
        this.selectedProviderId = selectedProviderId;
    }

    public String getSelectedModelId() {
        return selectedModelId;
    }

    public void setSelectedModelId(String selectedModelId) {
        this.selectedModelId = selectedModelId;
    }

    public String getRoutingStrategy() {
        return routingStrategy;
    }

    public void setRoutingStrategy(String routingStrategy) {
        this.routingStrategy = routingStrategy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
