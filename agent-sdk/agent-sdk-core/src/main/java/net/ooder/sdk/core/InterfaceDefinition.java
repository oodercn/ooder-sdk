package net.ooder.sdk.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口定义
 * 从 scene-engine 迁移到 agent-sdk，作为核心抽象
 */
public class InterfaceDefinition {
    
    private String category;
    private String version;
    private String interfaceHash;
    private Map<String, CapabilityDefinition> capabilities = new HashMap<String, CapabilityDefinition>();
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getInterfaceHash() {
        return interfaceHash;
    }
    
    public void setInterfaceHash(String interfaceHash) {
        this.interfaceHash = interfaceHash;
    }
    
    public Map<String, CapabilityDefinition> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, CapabilityDefinition> capabilities) {
        this.capabilities = capabilities;
    }
    
    public void addCapability(String name, CapabilityDefinition capability) {
        this.capabilities.put(name, capability);
    }
    
    public CapabilityDefinition getCapability(String name) {
        return capabilities.get(name);
    }
    
    public boolean hasCapability(String name) {
        return capabilities.containsKey(name);
    }
    
    public MethodDefinition getMethod(String capabilityName, String methodName) {
        CapabilityDefinition cap = capabilities.get(capabilityName);
        if (cap != null) {
            return cap.getMethod(methodName);
        }
        return null;
    }
    
    public static class CapabilityDefinition {
        private String name;
        private String description;
        private Map<String, MethodDefinition> methods = new HashMap<String, MethodDefinition>();
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Map<String, MethodDefinition> getMethods() {
            return methods;
        }
        
        public void setMethods(Map<String, MethodDefinition> methods) {
            this.methods = methods;
        }
        
        public void addMethod(String name, MethodDefinition method) {
            this.methods.put(name, method);
        }
        
        public MethodDefinition getMethod(String name) {
            return methods.get(name);
        }
    }
    
    public static class MethodDefinition {
        private String name;
        private String description;
        private SchemaDefinition input;
        private SchemaDefinition output;
        private Map<String, ErrorDefinition> errors = new HashMap<String, ErrorDefinition>();
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public SchemaDefinition getInput() {
            return input;
        }
        
        public void setInput(SchemaDefinition input) {
            this.input = input;
        }
        
        public SchemaDefinition getOutput() {
            return output;
        }
        
        public void setOutput(SchemaDefinition output) {
            this.output = output;
        }
        
        public Map<String, ErrorDefinition> getErrors() {
            return errors;
        }
        
        public void setErrors(Map<String, ErrorDefinition> errors) {
            this.errors = errors;
        }
        
        public void addError(String code, ErrorDefinition error) {
            this.errors.put(code, error);
        }
    }
    
    public static class SchemaDefinition {
        private String type;
        private String description;
        private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
        private java.util.List<String> required = new java.util.ArrayList<String>();
        private String[] enumValues;
        private Object defaultValue;
        private SchemaDefinition items;
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Map<String, PropertyDefinition> getProperties() {
            return properties;
        }
        
        public void setProperties(Map<String, PropertyDefinition> properties) {
            this.properties = properties;
        }
        
        public void addProperty(String name, PropertyDefinition property) {
            this.properties.put(name, property);
        }
        
        public java.util.List<String> getRequired() {
            return required;
        }
        
        public void setRequired(java.util.List<String> required) {
            this.required = required;
        }
        
        public String[] getEnumValues() {
            return enumValues;
        }
        
        public void setEnumValues(String[] enumValues) {
            this.enumValues = enumValues;
        }
        
        public Object getDefaultValue() {
            return defaultValue;
        }
        
        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        public SchemaDefinition getItems() {
            return items;
        }
        
        public void setItems(SchemaDefinition items) {
            this.items = items;
        }
    }
    
    public static class PropertyDefinition {
        private String name;
        private String type;
        private String description;
        private boolean required;
        private String[] enumValues;
        private Object defaultValue;
        private SchemaDefinition schema;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public boolean isRequired() {
            return required;
        }
        
        public void setRequired(boolean required) {
            this.required = required;
        }
        
        public String[] getEnumValues() {
            return enumValues;
        }
        
        public void setEnumValues(String[] enumValues) {
            this.enumValues = enumValues;
        }
        
        public Object getDefaultValue() {
            return defaultValue;
        }
        
        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
        
        public SchemaDefinition getSchema() {
            return schema;
        }
        
        public void setSchema(SchemaDefinition schema) {
            this.schema = schema;
        }
    }
    
    public static class ErrorDefinition {
        private String code;
        private String message;
        private String description;
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}
