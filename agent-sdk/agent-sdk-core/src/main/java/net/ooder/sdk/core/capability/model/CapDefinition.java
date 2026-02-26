package net.ooder.sdk.core.capability.model;

import java.util.List;
import java.util.Map;

public class CapDefinition {
    
    private String apiVersion;
    private String kind;
    private CapMetadata metadata;
    private CapSpec spec;
    
    public static class CapMetadata {
        private String capId;
        private String name;
        private String version;
        private String category;
        private String status;
        private String description;
        private String author;
        private long createdAt;
        private long updatedAt;
        
        public String getCapId() { return capId; }
        public void setCapId(String capId) { this.capId = capId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        public long getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    }
    
    public static class CapSpec {
        private CapInterface interface_;
        private CapOffline offline;
        private List<String> permissions;
        private CapRateLimit rateLimit;
        private List<CapDependency> dependencies;
        private List<String> connectorTypes;
        
        public CapInterface getInterface() { return interface_; }
        public void setInterface(CapInterface interface_) { this.interface_ = interface_; }
        public CapOffline getOffline() { return offline; }
        public void setOffline(CapOffline offline) { this.offline = offline; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
        public CapRateLimit getRateLimit() { return rateLimit; }
        public void setRateLimit(CapRateLimit rateLimit) { this.rateLimit = rateLimit; }
        public List<CapDependency> getDependencies() { return dependencies; }
        public void setDependencies(List<CapDependency> dependencies) { this.dependencies = dependencies; }
        public List<String> getConnectorTypes() { return connectorTypes; }
        public void setConnectorTypes(List<String> connectorTypes) { this.connectorTypes = connectorTypes; }
    }
    
    public static class CapInterface {
        private String protocol;
        private String path;
        private String method;
        private int timeout;
        private CapSchema request;
        private CapSchema response;
        private List<CapError> errors;
        
        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        public CapSchema getRequest() { return request; }
        public void setRequest(CapSchema request) { this.request = request; }
        public CapSchema getResponse() { return response; }
        public void setResponse(CapSchema response) { this.response = response; }
        public List<CapError> getErrors() { return errors; }
        public void setErrors(List<CapError> errors) { this.errors = errors; }
    }
    
    public static class CapSchema {
        private String type;
        private Map<String, Object> properties;
        private List<String> required;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
        public List<String> getRequired() { return required; }
        public void setRequired(List<String> required) { this.required = required; }
    }
    
    public static class CapError {
        private String code;
        private String message;
        private int httpStatus;
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getHttpStatus() { return httpStatus; }
        public void setHttpStatus(int httpStatus) { this.httpStatus = httpStatus; }
    }
    
    public static class CapOffline {
        private boolean required;
        private String strategy;
        private int maxQueueSize;
        private boolean syncOnReconnect;
        private String syncStrategy;
        private CapFallback fallback;
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }
        public int getMaxQueueSize() { return maxQueueSize; }
        public void setMaxQueueSize(int maxQueueSize) { this.maxQueueSize = maxQueueSize; }
        public boolean isSyncOnReconnect() { return syncOnReconnect; }
        public void setSyncOnReconnect(boolean syncOnReconnect) { this.syncOnReconnect = syncOnReconnect; }
        public String getSyncStrategy() { return syncStrategy; }
        public void setSyncStrategy(String syncStrategy) { this.syncStrategy = syncStrategy; }
        public CapFallback getFallback() { return fallback; }
        public void setFallback(CapFallback fallback) { this.fallback = fallback; }
    }
    
    public static class CapFallback {
        private String method;
        private List<CapFallbackParam> params;
        private CapFallbackReturn returns;
        
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public List<CapFallbackParam> getParams() { return params; }
        public void setParams(List<CapFallbackParam> params) { this.params = params; }
        public CapFallbackReturn getReturns() { return returns; }
        public void setReturns(CapFallbackReturn returns) { this.returns = returns; }
    }
    
    public static class CapFallbackParam {
        private String name;
        private String type;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
    
    public static class CapFallbackReturn {
        private String type;
        private Map<String, Object> properties;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
    
    public static class CapRateLimit {
        private int max;
        private int window;
        private String unit;
        private String strategy;
        
        public int getMax() { return max; }
        public void setMax(int max) { this.max = max; }
        public int getWindow() { return window; }
        public void setWindow(int window) { this.window = window; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }
    }
    
    public static class CapDependency {
        private String capId;
        private boolean required;
        private String description;
        
        public String getCapId() { return capId; }
        public void setCapId(String capId) { this.capId = capId; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public CapMetadata getMetadata() { return metadata; }
    public void setMetadata(CapMetadata metadata) { this.metadata = metadata; }
    public CapSpec getSpec() { return spec; }
    public void setSpec(CapSpec spec) { this.spec = spec; }
    
    public String getCapId() {
        return metadata != null ? metadata.getCapId() : null;
    }
    
    public String getName() {
        return metadata != null ? metadata.getName() : null;
    }
    
    public String getVersion() {
        return metadata != null ? metadata.getVersion() : null;
    }
}
