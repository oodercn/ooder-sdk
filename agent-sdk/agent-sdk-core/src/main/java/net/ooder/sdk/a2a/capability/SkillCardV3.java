package net.ooder.sdk.a2a.capability;

import net.ooder.skills.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SkillCard v3.0
 * 
 * <p>重构后的技能卡片定义</p>
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class SkillCardV3 {
    
    // ========== 基础信息 ==========
    private String skillId;
    private String name;
    private String version;
    private String description;
    
    // ========== v3.0 核心属性 ==========
    private SkillForm form;
    private SkillCategory category;
    private Set<ServicePurpose> purposes;
    private SceneType sceneType;
    
    // ========== 能力端点 ==========
    private List<CapabilityEndpoint> capabilities;
    
    // ========== Agent 信息 ==========
    private String agentId;
    private String agentEndpoint;
    
    // ========== 状态 ==========
    private SkillStatus status;
    private long lastHeartbeat;
    
    // ========== 元数据 ==========
    private Map<String, Object> metadata;
    
    // ========== Getters/Setters ==========
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public SkillForm getForm() { return form; }
    public void setForm(SkillForm form) { this.form = form; }
    
    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }
    
    public Set<ServicePurpose> getPurposes() { return purposes; }
    public void setPurposes(Set<ServicePurpose> purposes) { this.purposes = purposes; }
    
    public SceneType getSceneType() { return sceneType; }
    public void setSceneType(SceneType sceneType) { this.sceneType = sceneType; }
    
    public List<CapabilityEndpoint> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityEndpoint> capabilities) { this.capabilities = capabilities; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getAgentEndpoint() { return agentEndpoint; }
    public void setAgentEndpoint(String agentEndpoint) { this.agentEndpoint = agentEndpoint; }
    
    public SkillStatus getStatus() { return status; }
    public void setStatus(SkillStatus status) { this.status = status; }
    
    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    // ========== 便捷方法 ==========
    
    /**
     * 是否为场景技能
     */
    public boolean isScene() {
        return form == SkillForm.SCENE;
    }
    
    /**
     * 是否可自驱动
     */
    public boolean canSelfDrive() {
        return isScene() && sceneType != null && sceneType.canSelfDrive();
    }
}
