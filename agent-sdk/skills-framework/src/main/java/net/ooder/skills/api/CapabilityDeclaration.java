package net.ooder.skills.api;

import java.util.Map;

/**
 * 能力声明
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class CapabilityDeclaration {
    private String id;
    private String name;
    private String type;          // internal | exposed
    private String description;
    private Map<String, Object> parameters;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}
