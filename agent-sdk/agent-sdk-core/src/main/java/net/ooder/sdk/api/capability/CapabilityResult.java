package net.ooder.sdk.api.capability;

import java.util.HashMap;
import java.util.Map;

/**
 * Capability 调用结果封装
 *
 * @author Ooder Team
 * @version 2.3
 */
public class CapabilityResult {

    private ResultStatus status;

    private Object data;

    private String errorCode;

    private String errorMessage;

    private long executionTimeMs;

    private Map<String, Object> metadata;

    private String llmFriendlyDescription;

    private String suggestedNextAction;

    private double confidenceScore;

    public CapabilityResult() {
        this.status = ResultStatus.PENDING;
        this.metadata = new HashMap<>();
        this.confidenceScore = 1.0;
    }

    public static CapabilityResult success(Object data) {
        CapabilityResult result = new CapabilityResult();
        result.status = ResultStatus.SUCCESS;
        result.data = data;
        return result;
    }

    public static CapabilityResult failure(String errorCode, String errorMessage) {
        CapabilityResult result = new CapabilityResult();
        result.status = ResultStatus.FAILED;
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.confidenceScore = 0.0;
        return result;
    }

    public static CapabilityResult partial(Object data, String message) {
        CapabilityResult result = new CapabilityResult();
        result.status = ResultStatus.PARTIAL;
        result.data = data;
        result.llmFriendlyDescription = message;
        result.confidenceScore = 0.5;
        return result;
    }

    public CapabilityResult withData(Object data) {
        this.data = data;
        return this;
    }

    public CapabilityResult withMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    public CapabilityResult withExecutionTime(long ms) {
        this.executionTimeMs = ms;
        return this;
    }

    public CapabilityResult withLlmDescription(String description) {
        this.llmFriendlyDescription = description;
        return this;
    }

    public CapabilityResult withSuggestion(String suggestion) {
        this.suggestedNextAction = suggestion;
        return this;
    }

    public CapabilityResult withConfidence(double score) {
        this.confidenceScore = Math.max(0.0, Math.min(1.0, score));
        return this;
    }

    public boolean isSuccess() {
        return status == ResultStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == ResultStatus.FAILED;
    }

    public boolean isPartial() {
        return status == ResultStatus.PARTIAL;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getLlmFriendlyDescription() {
        return llmFriendlyDescription;
    }

    public void setLlmFriendlyDescription(String llmFriendlyDescription) {
        this.llmFriendlyDescription = llmFriendlyDescription;
    }

    public String getSuggestedNextAction() {
        return suggestedNextAction;
    }

    public void setSuggestedNextAction(String suggestedNextAction) {
        this.suggestedNextAction = suggestedNextAction;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    @Override
    public String toString() {
        return "CapabilityResult{" +
                "status=" + status +
                ", errorCode='" + errorCode + '\'' +
                ", executionTimeMs=" + executionTimeMs +
                ", confidenceScore=" + confidenceScore +
                '}';
    }

    public enum ResultStatus {
        PENDING,
        SUCCESS,
        PARTIAL,
        FAILED,
        CANCELLED,
        TIMEOUT
    }
}
