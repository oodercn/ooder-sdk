package net.ooder.sdk.llm.adapter.model;

public class RegisterResult {
    private String providerId;
    private boolean success;
    private String message;
    private Integer registeredModelCount;

    public RegisterResult() {
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
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

    public Integer getRegisteredModelCount() {
        return registeredModelCount;
    }

    public void setRegisteredModelCount(Integer registeredModelCount) {
        this.registeredModelCount = registeredModelCount;
    }
}
