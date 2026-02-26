package net.ooder.sdk.core.driver.model;

import java.util.List;
import java.util.Map;

public class InterfaceDefinition {

    private String interfaceId;
    private String interfaceName;
    private String description;
    private String sceneId;
    private String version;
    private List<MethodDefinition> methods;
    private List<CapabilityDefinition> capabilities;
    private Map<String, Object> metadata;
    private SchemaDefinition inputSchema;
    private SchemaDefinition outputSchema;
    
    public String getInterfaceId() { return interfaceId; }
    public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
    
    public String getInterfaceName() { return interfaceName; }
    public void setInterfaceName(String interfaceName) { this.interfaceName = interfaceName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public List<MethodDefinition> getMethods() { return methods; }
    public void setMethods(List<MethodDefinition> methods) { this.methods = methods; }

    public List<CapabilityDefinition> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityDefinition> capabilities) { this.capabilities = capabilities; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public SchemaDefinition getInputSchema() { return inputSchema; }
    public void setInputSchema(SchemaDefinition inputSchema) { this.inputSchema = inputSchema; }
    
    public SchemaDefinition getOutputSchema() { return outputSchema; }
    public void setOutputSchema(SchemaDefinition outputSchema) { this.outputSchema = outputSchema; }
    
    public static class MethodDefinition {
        private String methodName;
        private String name;
        private String returnType;
        private List<ParameterDefinition> parameters;
        private String description;
        private SchemaDefinition input;
        private SchemaDefinition output;

        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getReturnType() { return returnType; }
        public void setReturnType(String returnType) { this.returnType = returnType; }

        public List<ParameterDefinition> getParameters() { return parameters; }
        public void setParameters(List<ParameterDefinition> parameters) { this.parameters = parameters; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public SchemaDefinition getInput() { return input; }
        public void setInput(SchemaDefinition input) { this.input = input; }

        public SchemaDefinition getOutput() { return output; }
        public void setOutput(SchemaDefinition output) { this.output = output; }
    }

    public static class CapabilityDefinition {
        private String name;
        private String description;
        private Map<String, Object> parameters;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    public static class ParameterDefinition {
        private String name;
        private String type;
        private boolean required;
        private Object defaultValue;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
    }
}
