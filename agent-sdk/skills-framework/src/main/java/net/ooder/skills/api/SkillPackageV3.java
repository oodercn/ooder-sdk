package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SkillPackage v3.0
 * 
 * <p>重构后的技能包定义，支持新的技能形态和分类体系</p>
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class SkillPackageV3 {
    
    // ========== 基础信息 ==========
    private String id;
    private String name;
    private String version;
    private String description;
    
    // ========== v3.0 核心字段 ==========
    private SkillForm form;                    // SCENE | STANDALONE
    private SkillCategory category;            // knowledge | llm | tool | ...
    private Set<ServicePurpose> purposes;      // 服务目的组合
    private SceneType sceneType;               // AUTO | TRIGGER | HYBRID（仅SCENE时）
    
    // ========== 场景特有（仅 form=SCENE 时有效） ==========
    private SceneStructure sceneStructure;     // 场景结构
    private String entryCapability;            // 入口能力ID
    
    // ========== 能力列表 ==========
    private List<Capability> capabilities;
    
    // ========== 协作配置 ==========
    private CollaborationConfig collaboration;
    
    // ========== 元数据 ==========
    private Map<String, Object> metadata;
    private List<String> tags;
    
    // ========== 其他 ==========
    private String source;
    private String downloadUrl;
    private String checksum;
    private long size;
    private List<String> dependencies;
    
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
    
    public SceneStructure getSceneStructure() { return sceneStructure; }
    public void setSceneStructure(SceneStructure sceneStructure) { this.sceneStructure = sceneStructure; }
    
    public String getEntryCapability() { return entryCapability; }
    public void setEntryCapability(String entryCapability) { this.entryCapability = entryCapability; }
    
    public List<Capability> getCapabilities() { return capabilities; }
    public void setCapabilities(List<Capability> capabilities) { this.capabilities = capabilities; }
    
    public CollaborationConfig getCollaboration() { return collaboration; }
    public void setCollaboration(CollaborationConfig collaboration) { this.collaboration = collaboration; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    // ========== 便捷方法 ==========
    
    /**
     * 是否为场景技能
     */
    public boolean isScene() {
        return form == SkillForm.SCENE;
    }
    
    /**
     * 是否为独立技能
     */
    public boolean isStandalone() {
        return form == SkillForm.STANDALONE;
    }
    
    /**
     * 是否可自驱动
     */
    public boolean canSelfDrive() {
        return isScene() && sceneType != null && sceneType.canSelfDrive();
    }
    
    /**
     * 是否可被触发
     */
    public boolean canBeTriggered() {
        return isScene() && sceneType != null && sceneType.canBeTriggered();
    }
}
