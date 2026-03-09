package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NLP 组件上下文
 * 
 * <p>封装单个 NLP 组件的上下文信息。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class NlpComponentContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String componentId;
    private String componentType;
    private String moduleViewType;
    
    private Map<String, Object> properties;
    private Map<String, Object> bindings;
    private List<String> eventHandlers;
    
    private boolean active;
    private long lastModified;

    public NlpComponentContext() {
        this.properties = new HashMap<>();
        this.bindings = new HashMap<>();
        this.eventHandlers = new ArrayList<>();
        this.lastModified = System.currentTimeMillis();
    }
    
    public NlpComponentContext(String componentId, String componentType) {
        this();
        this.componentId = componentId;
        this.componentType = componentType;
    }
    
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }
    
    public void setProperty(String key, Object value) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(key, value);
        this.lastModified = System.currentTimeMillis();
    }
    
    public void removeProperty(String key) {
        if (properties != null) {
            properties.remove(key);
            this.lastModified = System.currentTimeMillis();
        }
    }
    
    public Object getBinding(String key) {
        return bindings != null ? bindings.get(key) : null;
    }
    
    public void setBinding(String key, Object value) {
        if (bindings == null) {
            bindings = new HashMap<>();
        }
        bindings.put(key, value);
    }
    
    public void addEventHandler(String handler) {
        if (eventHandlers == null) {
            eventHandlers = new ArrayList<>();
        }
        if (!eventHandlers.contains(handler)) {
            eventHandlers.add(handler);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getComponentId() { return componentId; }
    public void setComponentId(String componentId) { this.componentId = componentId; }
    
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    
    public String getModuleViewType() { return moduleViewType; }
    public void setModuleViewType(String moduleViewType) { this.moduleViewType = moduleViewType; }
    
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    
    public Map<String, Object> getBindings() { return bindings; }
    public void setBindings(Map<String, Object> bindings) { this.bindings = bindings; }
    
    public List<String> getEventHandlers() { return eventHandlers; }
    public void setEventHandlers(List<String> eventHandlers) { this.eventHandlers = eventHandlers; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    
    public static class Builder {
        private NlpComponentContext context = new NlpComponentContext();
        
        public Builder componentId(String componentId) {
            context.setComponentId(componentId);
            return this;
        }
        
        public Builder componentType(String componentType) {
            context.setComponentType(componentType);
            return this;
        }
        
        public Builder moduleViewType(String moduleViewType) {
            context.setModuleViewType(moduleViewType);
            return this;
        }
        
        public Builder property(String key, Object value) {
            context.setProperty(key, value);
            return this;
        }
        
        public Builder binding(String key, Object value) {
            context.setBinding(key, value);
            return this;
        }
        
        public Builder eventHandler(String handler) {
            context.addEventHandler(handler);
            return this;
        }
        
        public Builder active(boolean active) {
            context.setActive(active);
            return this;
        }
        
        public NlpComponentContext build() {
            return context;
        }
    }
}
