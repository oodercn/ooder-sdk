package net.ooder.skills.api;

/**
 * 外部依赖
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class ExternalDependency {
    
    private String skillId;         // 技能ID
    private String capabilityId;    // 能力ID
    private String version;         // 版本要求
    private boolean required;       // 是否必需
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
