package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapResponse {
    private String requestId;
    private String capId;
    private boolean success;
    private Object result;
    private String errorMessage;
    private Map<String, Object> metadata;

    public CapResponse(String requestId, String capId) {
        this.requestId = requestId;
        this.capId = capId;
        this.metadata = new ConcurrentHashMap<>();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCapId() {
        return capId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public static CapResponse success(String requestId, String capId, Object result) {
        CapResponse response = new CapResponse(requestId, capId);
        response.setSuccess(true);
        response.setResult(result);
        return response;
    }

    public static CapResponse failure(String requestId, String capId, String errorMessage) {
        CapResponse response = new CapResponse(requestId, capId);
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
