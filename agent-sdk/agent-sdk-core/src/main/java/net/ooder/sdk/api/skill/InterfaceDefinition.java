package net.ooder.sdk.api.skill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InterfaceDefinition implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final String TYPE_INTERFACE_SKILL = "interface-skill";
    
    private String interfaceId;
    private String name;
    private String description;
    private String version;
    private String type = TYPE_INTERFACE_SKILL;
    
    private List<MethodDefinition> methods = new ArrayList<>();
    private List<EventDefinition> events = new ArrayList<>();
    
    private DriverConfig driver = new DriverConfig();
    private List<String> implementations = new ArrayList<>();
    
    private Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    public InterfaceDefinition() {}
    
    public InterfaceDefinition(String interfaceId, String name) {
        this.interfaceId = interfaceId;
        this.name = name;
    }
    
    public static InterfaceDefinition create(String interfaceId, String name) {
        return new InterfaceDefinition(interfaceId, name);
    }
    
    public String getInterfaceId() { return interfaceId; }
    public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<MethodDefinition> getMethods() { return methods; }
    public void setMethods(List<MethodDefinition> methods) { 
        this.methods = methods != null ? methods : new ArrayList<>(); 
    }
    
    public void addMethod(MethodDefinition method) {
        this.methods.add(method);
    }
    
    public MethodDefinition findMethod(String name) {
        for (MethodDefinition m : methods) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }
    
    public List<EventDefinition> getEvents() { return events; }
    public void setEvents(List<EventDefinition> events) { 
        this.events = events != null ? events : new ArrayList<>(); 
    }
    
    public void addEvent(EventDefinition event) {
        this.events.add(event);
    }
    
    public DriverConfig getDriver() { return driver; }
    public void setDriver(DriverConfig driver) { this.driver = driver; }
    
    public List<String> getImplementations() { return implementations; }
    public void setImplementations(List<String> implementations) { 
        this.implementations = implementations != null ? implementations : new ArrayList<>(); 
    }
    
    public void addImplementation(String skillId) {
        if (!this.implementations.contains(skillId)) {
            this.implementations.add(skillId);
        }
    }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new ConcurrentHashMap<>(); 
    }
    
    public InterfaceDefinition name(String name) {
        this.name = name;
        return this;
    }
    
    public InterfaceDefinition description(String description) {
        this.description = description;
        return this;
    }
    
    public InterfaceDefinition version(String version) {
        this.version = version;
        return this;
    }
    
    public boolean isInterfaceSkill() {
        return TYPE_INTERFACE_SKILL.equals(type);
    }
    
    public String getDefaultImplementation() {
        return driver != null ? driver.getFallback() : null;
    }
    
    @Override
    public String toString() {
        return "InterfaceDefinition{" +
                "interfaceId='" + interfaceId + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", methods=" + methods.size() +
                ", implementations=" + implementations.size() +
                '}';
    }
    
    public static class MethodDefinition implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String name;
        private String description;
        private List<ParameterDefinition> parameters = new ArrayList<>();
        private String returnType;
        private boolean async;
        
        public MethodDefinition() {}
        
        public MethodDefinition(String name, String returnType) {
            this.name = name;
            this.returnType = returnType;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<ParameterDefinition> getParameters() { return parameters; }
        public void setParameters(List<ParameterDefinition> parameters) { 
            this.parameters = parameters != null ? parameters : new ArrayList<>(); 
        }
        
        public void addParameter(ParameterDefinition param) {
            this.parameters.add(param);
        }
        
        public String getReturnType() { return returnType; }
        public void setReturnType(String returnType) { this.returnType = returnType; }
        
        public boolean isAsync() { return async; }
        public void setAsync(boolean async) { this.async = async; }
        
        @Override
        public String toString() {
            return "MethodDefinition{name='" + name + "', returnType='" + returnType + "'}";
        }
    }
    
    public static class ParameterDefinition implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String name;
        private String type;
        private boolean required;
        private Object defaultValue;
        private String description;
        
        public ParameterDefinition() {}
        
        public ParameterDefinition(String name, String type, boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class EventDefinition implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String name;
        private String description;
        private String payloadType;
        
        public EventDefinition() {}
        
        public EventDefinition(String name, String payloadType) {
            this.name = name;
            this.payloadType = payloadType;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPayloadType() { return payloadType; }
        public void setPayloadType(String payloadType) { this.payloadType = payloadType; }
    }
    
    public static class DriverConfig implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private boolean singleton = true;
        private boolean autoDiscover = true;
        private String fallback;
        private Map<String, Object> config = new ConcurrentHashMap<>();
        
        public boolean isSingleton() { return singleton; }
        public void setSingleton(boolean singleton) { this.singleton = singleton; }
        
        public boolean isAutoDiscover() { return autoDiscover; }
        public void setAutoDiscover(boolean autoDiscover) { this.autoDiscover = autoDiscover; }
        
        public String getFallback() { return fallback; }
        public void setFallback(String fallback) { this.fallback = fallback; }
        
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { 
            this.config = config != null ? config : new ConcurrentHashMap<>(); 
        }
    }
}
