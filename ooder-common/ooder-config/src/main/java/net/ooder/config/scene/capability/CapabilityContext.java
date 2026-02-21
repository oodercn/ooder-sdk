package net.ooder.config.scene.capability;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CapabilityContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String capabilityId;
    private String sceneId;
    private String clientId;
    private String userId;
    
    private CapabilityRequest request;
    
    private Map<String, Object> dataSources;
    private Map<String, Object> cache;
    private Map<String, Object> attributes;
    
    private long startTime;
    private String traceId;
    
    public CapabilityContext() {
        this.dataSources = new HashMap<String, Object>();
        this.cache = new HashMap<String, Object>();
        this.attributes = new HashMap<String, Object>();
        this.startTime = System.currentTimeMillis();
    }
    
    public static CapabilityContextBuilder builder() {
        return new CapabilityContextBuilder();
    }
    
    public String getCapabilityId() {
        return capabilityId;
    }
    
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public CapabilityRequest getRequest() {
        return request;
    }
    
    public void setRequest(CapabilityRequest request) {
        this.request = request;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getRequest(Class<T> type) {
        if (request != null && request.getData() != null) {
            if (type.isInstance(request.getData())) {
                return (T) request.getData();
            }
        }
        return null;
    }
    
    public Map<String, Object> getDataSources() {
        return dataSources;
    }
    
    public void setDataSources(Map<String, Object> dataSources) {
        this.dataSources = dataSources != null ? dataSources : new HashMap<String, Object>();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getDataSource(String dataSourceId) {
        Object ds = dataSources.get(dataSourceId);
        if (ds != null) {
            return (T) ds;
        }
        return null;
    }
    
    public void addDataSource(String dataSourceId, Object dataSource) {
        if (dataSourceId != null && dataSource != null) {
            dataSources.put(dataSourceId, dataSource);
        }
    }
    
    public Map<String, Object> getCache() {
        return cache;
    }
    
    public void setCache(Map<String, Object> cache) {
        this.cache = cache != null ? cache : new HashMap<String, Object>();
    }
    
    public Object getFromCache(String key) {
        return cache.get(key);
    }
    
    public void putToCache(String key, Object value) {
        cache.put(key, value);
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? attributes : new HashMap<String, Object>();
    }
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public String getTraceId() {
        return traceId;
    }
    
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    public static class CapabilityContextBuilder {
        private CapabilityContext context = new CapabilityContext();
        
        public CapabilityContextBuilder capabilityId(String capabilityId) {
            context.capabilityId = capabilityId;
            return this;
        }
        
        public CapabilityContextBuilder sceneId(String sceneId) {
            context.sceneId = sceneId;
            return this;
        }
        
        public CapabilityContextBuilder clientId(String clientId) {
            context.clientId = clientId;
            return this;
        }
        
        public CapabilityContextBuilder userId(String userId) {
            context.userId = userId;
            return this;
        }
        
        public CapabilityContextBuilder request(CapabilityRequest request) {
            context.request = request;
            return this;
        }
        
        public CapabilityContextBuilder dataSource(String dataSourceId, Object dataSource) {
            context.addDataSource(dataSourceId, dataSource);
            return this;
        }
        
        public CapabilityContextBuilder attribute(String key, Object value) {
            context.setAttribute(key, value);
            return this;
        }
        
        public CapabilityContextBuilder traceId(String traceId) {
            context.traceId = traceId;
            return this;
        }
        
        public CapabilityContext build() {
            return context;
        }
    }
}
