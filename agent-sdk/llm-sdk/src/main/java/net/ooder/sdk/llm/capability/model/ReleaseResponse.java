package net.ooder.sdk.llm.capability.model;

public class ReleaseResponse {
    private String capabilityId;
    private boolean success;
    private String message;
    private Long releasedQuota;

    public ReleaseResponse() {
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
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

    public Long getReleasedQuota() {
        return releasedQuota;
    }

    public void setReleasedQuota(Long releasedQuota) {
        this.releasedQuota = releasedQuota;
    }
}
