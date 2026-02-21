package net.ooder.config.scene.capability;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CapabilityRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String capabilityId;
    private String operation;
    private Object data;
    private Map<String, Object> parameters;
    private Map<String, String> headers;
    
    public CapabilityRequest() {
        this.parameters = new HashMap<String, Object>();
        this.headers = new HashMap<String, String>();
    }
    
    public static CapabilityRequestBuilder builder() {
        return new CapabilityRequestBuilder();
    }
    
    public String getCapabilityId() {
        return capabilityId;
    }
    
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> type) {
        if (data != null && type.isInstance(data)) {
            return (T) data;
        }
        return null;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<String, Object>();
    }
    
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    public String getParameterAsString(String key) {
        Object value = parameters.get(key);
        return value != null ? value.toString() : null;
    }
    
    public int getParameterAsInt(String key, int defaultValue) {
        Object value = parameters.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers != null ? headers : new HashMap<String, String>();
    }
    
    public String getHeader(String key) {
        return headers.get(key);
    }
    
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
    
    public static class CapabilityRequestBuilder {
        private CapabilityRequest request = new CapabilityRequest();
        
        public CapabilityRequestBuilder capabilityId(String capabilityId) {
            request.capabilityId = capabilityId;
            return this;
        }
        
        public CapabilityRequestBuilder operation(String operation) {
            request.operation = operation;
            return this;
        }
        
        public CapabilityRequestBuilder data(Object data) {
            request.data = data;
            return this;
        }
        
        public CapabilityRequestBuilder parameter(String key, Object value) {
            request.setParameter(key, value);
            return this;
        }
        
        public CapabilityRequestBuilder header(String key, String value) {
            request.setHeader(key, value);
            return this;
        }
        
        public CapabilityRequest build() {
            return request;
        }
    }
}
