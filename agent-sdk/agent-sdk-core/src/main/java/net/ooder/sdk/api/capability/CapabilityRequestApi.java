package net.ooder.sdk.api.capability;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CapabilityRequestApi {
    
    CapabilityRequestResult request(CapabilityRequest request);
    
    CompletableFuture<CapabilityRequestResult> requestAsync(CapabilityRequest request);
    
    CapabilityRequestResult request(String capabilityId, Map<String, Object> params);
    
    CompletableFuture<CapabilityRequestResult> requestAsync(String capabilityId, Map<String, Object> params);
    
    Optional<CapabilityRequest> getRequest(String requestId);
    
    List<CapabilityRequest> getRequestsByStatus(RequestStatus status);
    
    List<CapabilityRequest> getRequestsByCapability(String capabilityId);
    
    List<CapabilityRequest> getRequestsByUser(String userId);
    
    void cancel(String requestId);
    
    void retry(String requestId);
    
    CapabilityRequestResult waitForCompletion(String requestId, long timeoutMillis);
    
    List<CapabilityInfo> discoverCapabilities(String domain);
    
    List<CapabilityInfo> searchCapabilities(String keyword);
    
    Optional<CapabilityInfo> getCapabilityInfo(String capabilityId);
    
    boolean isAvailable(String capabilityId);
    
    CapabilityStats getStats(String capabilityId);
    
    void registerCapability(CapabilityRegistration registration);
    
    void unregisterCapability(String capabilityId);
    
    void updateCapability(String capabilityId, CapabilityRegistration registration);
    
    class CapabilityRequest {
        private String requestId;
        private String capabilityId;
        private Map<String, Object> params;
        private String userId;
        private RequestPriority priority;
        private RequestStatus status;
        private long createdAt;
        private long startedAt;
        private long completedAt;
        private int retryCount;
        private int maxRetries;
        private long timeout;
        private Map<String, Object> metadata;
        
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public RequestPriority getPriority() { return priority; }
        public void setPriority(RequestPriority priority) { this.priority = priority; }
        
        public RequestStatus getStatus() { return status; }
        public void setStatus(RequestStatus status) { this.status = status; }
        
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        
        public long getStartedAt() { return startedAt; }
        public void setStartedAt(long startedAt) { this.startedAt = startedAt; }
        
        public long getCompletedAt() { return completedAt; }
        public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
        
        public int getRetryCount() { return retryCount; }
        public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
        
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
        
        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public static CapabilityRequest create(String capabilityId, Map<String, Object> params) {
            CapabilityRequest request = new CapabilityRequest();
            request.setRequestId("req-" + System.currentTimeMillis());
            request.setCapabilityId(capabilityId);
            request.setParams(params);
            request.setPriority(RequestPriority.NORMAL);
            request.setStatus(RequestStatus.PENDING);
            request.setCreatedAt(System.currentTimeMillis());
            request.setMaxRetries(3);
            request.setTimeout(30000);
            return request;
        }
    }
    
    class CapabilityRequestResult {
        private String requestId;
        private String capabilityId;
        private boolean success;
        private Object data;
        private String message;
        private String errorCode;
        private long executionTime;
        private Map<String, Object> metadata;
        
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public static CapabilityRequestResult success(String requestId, Object data) {
            CapabilityRequestResult result = new CapabilityRequestResult();
            result.setRequestId(requestId);
            result.setSuccess(true);
            result.setData(data);
            return result;
        }
        
        public static CapabilityRequestResult failure(String requestId, String message) {
            CapabilityRequestResult result = new CapabilityRequestResult();
            result.setRequestId(requestId);
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
    
    enum RequestStatus {
        PENDING,
        QUEUED,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }
    
    enum RequestPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
    
    class CapabilityInfo {
        private String capabilityId;
        private String name;
        private String description;
        private String domain;
        private String version;
        private List<String> tags;
        private Map<String, Object> inputSchema;
        private Map<String, Object> outputSchema;
        private boolean available;
        private long avgExecutionTime;
        private double successRate;
        
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
        
        public Map<String, Object> getInputSchema() { return inputSchema; }
        public void setInputSchema(Map<String, Object> inputSchema) { this.inputSchema = inputSchema; }
        
        public Map<String, Object> getOutputSchema() { return outputSchema; }
        public void setOutputSchema(Map<String, Object> outputSchema) { this.outputSchema = outputSchema; }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        
        public long getAvgExecutionTime() { return avgExecutionTime; }
        public void setAvgExecutionTime(long avgExecutionTime) { this.avgExecutionTime = avgExecutionTime; }
        
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
    }
    
    class CapabilityStats {
        private String capabilityId;
        private long totalRequests;
        private long successfulRequests;
        private long failedRequests;
        private double successRate;
        private long avgExecutionTime;
        private long maxExecutionTime;
        private long minExecutionTime;
        private long lastRequestTime;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        
        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        
        public long getSuccessfulRequests() { return successfulRequests; }
        public void setSuccessfulRequests(long successfulRequests) { this.successfulRequests = successfulRequests; }
        
        public long getFailedRequests() { return failedRequests; }
        public void setFailedRequests(long failedRequests) { this.failedRequests = failedRequests; }
        
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        
        public long getAvgExecutionTime() { return avgExecutionTime; }
        public void setAvgExecutionTime(long avgExecutionTime) { this.avgExecutionTime = avgExecutionTime; }
        
        public long getMaxExecutionTime() { return maxExecutionTime; }
        public void setMaxExecutionTime(long maxExecutionTime) { this.maxExecutionTime = maxExecutionTime; }
        
        public long getMinExecutionTime() { return minExecutionTime; }
        public void setMinExecutionTime(long minExecutionTime) { this.minExecutionTime = minExecutionTime; }
        
        public long getLastRequestTime() { return lastRequestTime; }
        public void setLastRequestTime(long lastRequestTime) { this.lastRequestTime = lastRequestTime; }
    }
    
    class CapabilityRegistration {
        private String capabilityId;
        private String name;
        private String description;
        private String domain;
        private String version;
        private List<String> tags;
        private Map<String, Object> inputSchema;
        private Map<String, Object> outputSchema;
        private String executorClass;
        private Map<String, Object> config;
        
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
        
        public Map<String, Object> getInputSchema() { return inputSchema; }
        public void setInputSchema(Map<String, Object> inputSchema) { this.inputSchema = inputSchema; }
        
        public Map<String, Object> getOutputSchema() { return outputSchema; }
        public void setOutputSchema(Map<String, Object> outputSchema) { this.outputSchema = outputSchema; }
        
        public String getExecutorClass() { return executorClass; }
        public void setExecutorClass(String executorClass) { this.executorClass = executorClass; }
        
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }
}
