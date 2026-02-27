package net.ooder.sdk.llm.model;

/**
 * 智能体回复
 */
public class AgentResponse {
    private String agentId;
    private String agentName;
    private String content;
    private long responseTime;
    private ResponseStatus status;
    private String errorMessage;

    public enum ResponseStatus {
        SUCCESS,
        TIMEOUT,
        ERROR,
        REJECTED
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
