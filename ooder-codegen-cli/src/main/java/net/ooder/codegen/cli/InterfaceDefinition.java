package net.ooder.codegen.cli;

import java.util.List;
import java.util.Map;

/**
 * 接口定义（简化版）
 * 从 YAML 解析的接口定义
 */
public class InterfaceDefinition {
    
    private String sceneId;
    private String name;
    private String description;
    private String version;
    private List<CapabilityDefinition> capabilities;
    private Map<String, Object> metadata;
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
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
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public List<CapabilityDefinition> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(List<CapabilityDefinition> capabilities) {
        this.capabilities = capabilities;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * 能力定义
     */
    public static class CapabilityDefinition {
        private String id;
        private String name;
        private String description;
        private String inputType;
        private String outputType;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
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
        
        public String getInputType() {
            return inputType;
        }
        
        public void setInputType(String inputType) {
            this.inputType = inputType;
        }
        
        public String getOutputType() {
            return outputType;
        }
        
        public void setOutputType(String outputType) {
            this.outputType = outputType;
        }
    }
}
