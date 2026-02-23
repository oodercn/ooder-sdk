package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapRequest {
    private String requestId;
    private String capId;
    private Map<String, Object> parameters;

    public CapRequest(String requestId, String capId) {
        this.requestId = requestId;
        this.capId = capId;
        this.parameters = new ConcurrentHashMap<>();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCapId() {
        return capId;
    }

    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return parameters.get(key);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
