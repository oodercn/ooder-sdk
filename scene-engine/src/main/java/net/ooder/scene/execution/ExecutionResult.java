package net.ooder.scene.execution;

import java.util.HashMap;
import java.util.Map;

public class ExecutionResult {

    private String executionId;
    private boolean success;
    private Object data;
    private String message;
    private String errorCode;
    private Map<String, Object> metadata;
    private long duration;

    public ExecutionResult() {
        this.metadata = new HashMap<>();
    }

    public static ExecutionResult success(String executionId, Object data) {
        ExecutionResult result = new ExecutionResult();
        result.setExecutionId(executionId);
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static ExecutionResult success(String executionId, Object data, String message) {
        ExecutionResult result = success(executionId, data);
        result.setMessage(message);
        return result;
    }

    public static ExecutionResult failure(String executionId, String errorCode, String message) {
        ExecutionResult result = new ExecutionResult();
        result.setExecutionId(executionId);
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setMessage(message);
        return result;
    }

    public static ExecutionResult failure(String executionId, Throwable error) {
        ExecutionResult result = new ExecutionResult();
        result.setExecutionId(executionId);
        result.setSuccess(false);
        result.setErrorCode("EXECUTION_ERROR");
        result.setMessage(error.getMessage());
        return result;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "executionId='" + executionId + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", duration=" + duration + "ms" +
                '}';
    }
}
