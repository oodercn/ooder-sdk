package net.ooder.sdk.llm.adapter.model;

public class FallbackResult {
    private String requestId;
    private String fallbackProviderId;
    private String fallbackModelId;
    private boolean success;
    private String message;

    public FallbackResult() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFallbackProviderId() {
        return fallbackProviderId;
    }

    public void setFallbackProviderId(String fallbackProviderId) {
        this.fallbackProviderId = fallbackProviderId;
    }

    public String getFallbackModelId() {
        return fallbackModelId;
    }

    public void setFallbackModelId(String fallbackModelId) {
        this.fallbackModelId = fallbackModelId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
