package net.ooder.sdk.skill.driver;

import java.util.Map;

public class InterfaceDefinition {
    private String sceneId;
    private String version;
    private Map<String, CapabilityDefinition> capabilities;
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public Map<String, CapabilityDefinition> getCapabilities() { return capabilities; }
    public void setCapabilities(Map<String, CapabilityDefinition> capabilities) { this.capabilities = capabilities; }
    
    public static class CapabilityDefinition {
        private String name;
        private String description;
        private Map<String, MethodDefinition> methods;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, MethodDefinition> getMethods() { return methods; }
        public void setMethods(Map<String, MethodDefinition> methods) { this.methods = methods; }
    }
    
    public static class MethodDefinition {
        private String name;
        private String description;
        private SchemaDefinition input;
        private SchemaDefinition output;
        private java.util.List<ErrorDefinition> errors;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public SchemaDefinition getInput() { return input; }
        public void setInput(SchemaDefinition input) { this.input = input; }
        
        public SchemaDefinition getOutput() { return output; }
        public void setOutput(SchemaDefinition output) { this.output = output; }
        
        public java.util.List<ErrorDefinition> getErrors() { return errors; }
        public void setErrors(java.util.List<ErrorDefinition> errors) { this.errors = errors; }
    }
    
    public static class SchemaDefinition {
        private String type;
        private java.util.List<String> required;
        private Map<String, PropertyDefinition> properties;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public java.util.List<String> getRequired() { return required; }
        public void setRequired(java.util.List<String> required) { this.required = required; }
        
        public Map<String, PropertyDefinition> getProperties() { return properties; }
        public void setProperties(Map<String, PropertyDefinition> properties) { this.properties = properties; }
    }
    
    public static class PropertyDefinition {
        private String description;
        private SchemaDefinition schema;
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public SchemaDefinition getSchema() { return schema; }
        public void setSchema(SchemaDefinition schema) { this.schema = schema; }
    }
    
    public static class ErrorDefinition {
        private String code;
        private String message;
        private String description;
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}