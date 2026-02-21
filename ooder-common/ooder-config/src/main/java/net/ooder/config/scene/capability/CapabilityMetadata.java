package net.ooder.config.scene.capability;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapabilityMetadata implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String capabilityId;
    private String capabilityName;
    private String capabilityType;
    private String description;
    private String version;
    
    private List<String> requiredDataSources;
    private List<String> requiredCapabilities;
    
    private List<ParameterDefinition> inputParameters;
    private List<ParameterDefinition> outputParameters;
    
    private Map<String, Object> properties;
    
    public CapabilityMetadata() {
        this.requiredDataSources = new ArrayList<String>();
        this.requiredCapabilities = new ArrayList<String>();
        this.inputParameters = new ArrayList<ParameterDefinition>();
        this.outputParameters = new ArrayList<ParameterDefinition>();
        this.properties = new HashMap<String, Object>();
    }
    
    public static CapabilityMetadataBuilder builder() {
        return new CapabilityMetadataBuilder();
    }
    
    public String getCapabilityId() {
        return capabilityId;
    }
    
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }
    
    public String getCapabilityName() {
        return capabilityName;
    }
    
    public void setCapabilityName(String capabilityName) {
        this.capabilityName = capabilityName;
    }
    
    public String getCapabilityType() {
        return capabilityType;
    }
    
    public void setCapabilityType(String capabilityType) {
        this.capabilityType = capabilityType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public List<String> getRequiredDataSources() {
        return requiredDataSources;
    }
    
    public void setRequiredDataSources(List<String> requiredDataSources) {
        this.requiredDataSources = requiredDataSources != null ? 
            requiredDataSources : new ArrayList<String>();
    }
    
    public void addRequiredDataSource(String dataSourceId) {
        if (dataSourceId != null && !requiredDataSources.contains(dataSourceId)) {
            requiredDataSources.add(dataSourceId);
        }
    }
    
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }
    
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities != null ? 
            requiredCapabilities : new ArrayList<String>();
    }
    
    public List<ParameterDefinition> getInputParameters() {
        return inputParameters;
    }
    
    public void setInputParameters(List<ParameterDefinition> inputParameters) {
        this.inputParameters = inputParameters != null ? 
            inputParameters : new ArrayList<ParameterDefinition>();
    }
    
    public void addInputParameter(ParameterDefinition param) {
        if (param != null) {
            inputParameters.add(param);
        }
    }
    
    public List<ParameterDefinition> getOutputParameters() {
        return outputParameters;
    }
    
    public void setOutputParameters(List<ParameterDefinition> outputParameters) {
        this.outputParameters = outputParameters != null ? 
            outputParameters : new ArrayList<ParameterDefinition>();
    }
    
    public void addOutputParameter(ParameterDefinition param) {
        if (param != null) {
            outputParameters.add(param);
        }
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties != null ? properties : new HashMap<String, Object>();
    }
    
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public static class ParameterDefinition implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String name;
        private String type;
        private boolean required;
        private String description;
        private Object defaultValue;
        
        public ParameterDefinition() {
        }
        
        public ParameterDefinition(String name, String type, boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }
        
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
        
        public boolean isRequired() {
            return required;
        }
        
        public void setRequired(boolean required) {
            this.required = required;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Object getDefaultValue() {
            return defaultValue;
        }
        
        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
    
    public static class CapabilityMetadataBuilder {
        private CapabilityMetadata metadata = new CapabilityMetadata();
        
        public CapabilityMetadataBuilder capabilityId(String capabilityId) {
            metadata.capabilityId = capabilityId;
            return this;
        }
        
        public CapabilityMetadataBuilder capabilityName(String capabilityName) {
            metadata.capabilityName = capabilityName;
            return this;
        }
        
        public CapabilityMetadataBuilder capabilityType(String capabilityType) {
            metadata.capabilityType = capabilityType;
            return this;
        }
        
        public CapabilityMetadataBuilder description(String description) {
            metadata.description = description;
            return this;
        }
        
        public CapabilityMetadataBuilder version(String version) {
            metadata.version = version;
            return this;
        }
        
        public CapabilityMetadataBuilder requiredDataSource(String dataSourceId) {
            metadata.addRequiredDataSource(dataSourceId);
            return this;
        }
        
        public CapabilityMetadataBuilder inputParameter(String name, String type, boolean required) {
            metadata.addInputParameter(new ParameterDefinition(name, type, required));
            return this;
        }
        
        public CapabilityMetadataBuilder outputParameter(String name, String type, boolean required) {
            metadata.addOutputParameter(new ParameterDefinition(name, type, required));
            return this;
        }
        
        public CapabilityMetadataBuilder property(String key, Object value) {
            metadata.setProperty(key, value);
            return this;
        }
        
        public CapabilityMetadata build() {
            return metadata;
        }
    }
}
