package net.ooder.skills.api;

/**
 * 依赖
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class Dependency {
    private String id;
    private String type;        // skill | capability | service
    private String version;
    private boolean required;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
