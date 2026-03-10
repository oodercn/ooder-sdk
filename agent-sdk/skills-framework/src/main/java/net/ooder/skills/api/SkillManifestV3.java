package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SkillManifest v3.0
 * 
 * <p>重构后的技能清单定义</p>
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class SkillManifestV3 {
    
    // ========== 基础信息 ==========
    private String id;
    private String name;
    private String version;
    private String description;
    
    // ========== v3.0 核心字段 ==========
    private SkillForm form;
    private SkillCategory category;
    private Set<ServicePurpose> purposes;
    private SceneType sceneType;              // 可选，仅SCENE时
    
    // ========== 能力声明 ==========
    private List<CapabilityDeclaration> capabilities;
    
    // ========== 场景配置（仅 form=SCENE 时有效） ==========
    private SceneConfigV3 sceneConfig;
    
    // ========== 协作配置 ==========
    private CollaborationDeclaration collaboration;
    
    // ========== 入口点 ==========
    private String entryPoint;                // 入口能力或函数
    
    // ========== 依赖 ==========
    private List<Dependency> dependencies;
    
    // ========== 元数据 ==========
    private SkillMetadata metadata;
    
    // ========== Getters/Setters ==========
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
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
    
    public List<CapabilityDeclaration> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityDeclaration> capabilities) { this.capabilities = capabilities; }
    
    public SceneConfigV3 getSceneConfig() { return sceneConfig; }
    public void setSceneConfig(SceneConfigV3 sceneConfig) { this.sceneConfig = sceneConfig; }
    
    public CollaborationDeclaration getCollaboration() { return collaboration; }
    public void setCollaboration(CollaborationDeclaration collaboration) { this.collaboration = collaboration; }
    
    public String getEntryPoint() { return entryPoint; }
    public void setEntryPoint(String entryPoint) { this.entryPoint = entryPoint; }
    
    public List<Dependency> getDependencies() { return dependencies; }
    public void setDependencies(List<Dependency> dependencies) { this.dependencies = dependencies; }
    
    public SkillMetadata getMetadata() { return metadata; }
    public void setMetadata(SkillMetadata metadata) { this.metadata = metadata; }
}
