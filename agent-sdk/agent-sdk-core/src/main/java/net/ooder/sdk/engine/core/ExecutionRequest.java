package net.ooder.sdk.engine.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行请求
 */
public class ExecutionRequest {
    
    private String requestId;
    private String operation;
    private Object payload;
    private Map<String, Object> headers;
    private long timeout;
    private int priority;
    
    public ExecutionRequest() {
        this.headers = new ConcurrentHashMap<>();
        this.timeout = 30000;
        this.priority = 5;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public Object getPayload() {
        return payload;
    }
    
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    
    public Map<String, Object> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers != null ? new ConcurrentHashMap<>(headers) : new ConcurrentHashMap<>();
    }
    
    public void setHeader(String key, Object value) {
        this.headers.put(key, value);
    }
    
    public Object getHeader(String key) {
        return this.headers.get(key);
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public static ExecutionRequest of(String operation) {
        ExecutionRequest request = new ExecutionRequest();
        request.setOperation(operation);
        return request;
    }
    
    public static ExecutionRequest of(String operation, Object payload) {
        ExecutionRequest request = of(operation);
        request.setPayload(payload);
        return request;
    }
}
