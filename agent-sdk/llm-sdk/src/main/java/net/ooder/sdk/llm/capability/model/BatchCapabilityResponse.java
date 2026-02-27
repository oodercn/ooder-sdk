package net.ooder.sdk.llm.capability.model;

import java.util.List;

public class BatchCapabilityResponse {
    private List<CapabilityResponse> results;
    private int successCount;
    private int failureCount;
    private List<String> errorMessages;

    public BatchCapabilityResponse() {
    }

    public List<CapabilityResponse> getResults() {
        return results;
    }

    public void setResults(List<CapabilityResponse> results) {
        this.results = results;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
