package net.ooder.sdk.api.execution;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TaskExecutionResult {

    private String executionId;
    private ExecutionStatus status;
    private Object output;
    private Map<String, Object> metadata;
    private Map<String, Object> metrics;
    private String errorMessage;
    private String errorCode;
    private Throwable error;
    private Instant completedAt;
    private long durationMs;

    public enum ExecutionStatus {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILURE,
        TIMEOUT,
        CANCELLED,
        SKIPPED
    }

    public TaskExecutionResult() {
        this.metadata = new HashMap<>();
        this.metrics = new HashMap<>();
        this.completedAt = Instant.now();
    }

    public TaskExecutionResult(String executionId) {
        this();
        this.executionId = executionId;
    }

    public TaskExecutionResult(String executionId, ExecutionStatus status) {
        this(executionId);
        this.status = status;
    }

    public TaskExecutionResult(String executionId, ExecutionStatus status, Object output) {
        this(executionId, status);
        this.output = output;
    }

    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }

    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }

    public Object getOutput() { return output; }
    public void setOutput(Object output) { this.output = output; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata != null ? metadata : new HashMap<>(); }

    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics != null ? metrics : new HashMap<>(); }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public Throwable getError() { return error; }
    public void setError(Throwable error) { 
        this.error = error; 
        if (error != null) {
            this.errorMessage = error.getMessage();
        }
    }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

    public boolean isSuccess() {
        return status == ExecutionStatus.SUCCESS || status == ExecutionStatus.PARTIAL_SUCCESS;
    }

    public boolean isFailure() {
        return status == ExecutionStatus.FAILURE;
    }

    public boolean isTimeout() {
        return status == ExecutionStatus.TIMEOUT;
    }

    public boolean isCancelled() {
        return status == ExecutionStatus.CANCELLED;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key) {
        return (T) this.metadata.get(key);
    }

    public void addMetric(String key, Object value) {
        this.metrics.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMetric(String key) {
        return (T) this.metrics.get(key);
    }

    public static TaskExecutionResult success(String executionId, Object output) {
        TaskExecutionResult result = new TaskExecutionResult(executionId, ExecutionStatus.SUCCESS);
        result.setOutput(output);
        return result;
    }

    public static TaskExecutionResult failure(String executionId, Throwable error) {
        TaskExecutionResult result = new TaskExecutionResult(executionId, ExecutionStatus.FAILURE);
        result.setError(error);
        return result;
    }

    public static TaskExecutionResult failure(String executionId, String errorCode, String errorMessage) {
        TaskExecutionResult result = new TaskExecutionResult(executionId, ExecutionStatus.FAILURE);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public static TaskExecutionResult timeout(String executionId) {
        return new TaskExecutionResult(executionId, ExecutionStatus.TIMEOUT);
    }

    public static TaskExecutionResult cancelled(String executionId) {
        return new TaskExecutionResult(executionId, ExecutionStatus.CANCELLED);
    }

    public static TaskExecutionResult partialSuccess(String executionId, Object output) {
        TaskExecutionResult result = new TaskExecutionResult(executionId, ExecutionStatus.PARTIAL_SUCCESS);
        result.setOutput(output);
        return result;
    }
}
