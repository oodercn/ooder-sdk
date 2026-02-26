package net.ooder.sdk.engine.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行上下文
 */
public class ExecutionContext {
    
    private final String contextId;
    private final Map<String, Object> properties;
    private final long createTime;
    private volatile long startTime;
    private volatile long endTime;
    
    public ExecutionContext(String contextId) {
        this.contextId = contextId;
        this.properties = new ConcurrentHashMap<>();
        this.createTime = System.currentTimeMillis();
    }
    
    public String getContextId() {
        return contextId;
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public <T> T getProperty(String key, Class<T> type) {
        Object value = properties.get(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new ClassCastException("Property " + key + " is not of type " + type.getName());
    }
    
    public Map<String, Object> getProperties() {
        return new ConcurrentHashMap<>(properties);
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void markStarted() {
        this.startTime = System.currentTimeMillis();
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void markEnded() {
        this.endTime = System.currentTimeMillis();
    }
    
    public long getDuration() {
        if (endTime > 0 && startTime > 0) {
            return endTime - startTime;
        }
        if (startTime > 0) {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }
    
    public ExecutionContext copy() {
        ExecutionContext copy = new ExecutionContext(this.contextId + "-copy");
        copy.properties.putAll(this.properties);
        return copy;
    }
}
