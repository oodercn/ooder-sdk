package net.ooder.sdk.orchestration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CapabilityRouter {
    
    CompletableFuture<RouteResult> route(String capabilityId, Map<String, Object> params);
    
    CompletableFuture<RouteResult> route(String capabilityId, Map<String, Object> params, RouteOptions options);
    
    boolean canRoute(String capabilityId);
    
    List<String> findCapabilities(String domain, String intent);
    
    CapabilityInfo getCapabilityInfo(String capabilityId);
    
    void registerExecutor(String capabilityId, CapabilityExecutor executor);
    
    void unregisterExecutor(String capabilityId);
    
    class RouteResult {
        private String capabilityId;
        private boolean success;
        private Object data;
        private String message;
        private long executionTime;
        private String executorId;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public String getExecutorId() { return executorId; }
        public void setExecutorId(String executorId) { this.executorId = executorId; }
        
        public static RouteResult success(String capabilityId, Object data) {
            RouteResult r = new RouteResult();
            r.setCapabilityId(capabilityId);
            r.setSuccess(true);
            r.setData(data);
            return r;
        }
        
        public static RouteResult failure(String capabilityId, String message) {
            RouteResult r = new RouteResult();
            r.setCapabilityId(capabilityId);
            r.setSuccess(false);
            r.setMessage(message);
            return r;
        }
    }
    
    class RouteOptions {
        private int timeout = 30000;
        private int retries = 3;
        private boolean async = true;
        private Map<String, Object> metadata;
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public int getRetries() { return retries; }
        public void setRetries(int retries) { this.retries = retries; }
        
        public boolean isAsync() { return async; }
        public void setAsync(boolean async) { this.async = async; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    class CapabilityInfo {
        private String capabilityId;
        private String name;
        private String description;
        private String domain;
        private String version;
        private List<String> tags;
        private Map<String, Object> schema;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public Map<String, Object> getSchema() { return schema; }
        public void setSchema(Map<String, Object> schema) { this.schema = schema; }
    }
    
    interface CapabilityExecutor {
        
        CompletableFuture<RouteResult> execute(Map<String, Object> params);
        
        String getExecutorId();
        
        boolean isAvailable();
    }
}
