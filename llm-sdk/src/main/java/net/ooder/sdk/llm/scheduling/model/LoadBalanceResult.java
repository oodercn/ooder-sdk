package net.ooder.sdk.llm.scheduling.model;

public class LoadBalanceResult {
    private String requestId;
    private String selectedNode;
    private String strategy;
    private Integer nodeLoad;
    private String message;

    public LoadBalanceResult() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(String selectedNode) {
        this.selectedNode = selectedNode;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public Integer getNodeLoad() {
        return nodeLoad;
    }

    public void setNodeLoad(Integer nodeLoad) {
        this.nodeLoad = nodeLoad;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
