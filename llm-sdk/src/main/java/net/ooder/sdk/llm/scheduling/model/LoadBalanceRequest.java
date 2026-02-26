package net.ooder.sdk.llm.scheduling.model;

import java.util.List;

public class LoadBalanceRequest {
    private String requestId;
    private String strategy;
    private List<String> availableNodes;
    private Object requestPayload;

    public LoadBalanceRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<String> getAvailableNodes() {
        return availableNodes;
    }

    public void setAvailableNodes(List<String> availableNodes) {
        this.availableNodes = availableNodes;
    }

    public Object getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(Object requestPayload) {
        this.requestPayload = requestPayload;
    }
}
