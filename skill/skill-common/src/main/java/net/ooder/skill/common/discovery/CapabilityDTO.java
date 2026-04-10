package net.ooder.skill.common.discovery;

import java.util.List;
import java.util.Map;

public class CapabilityDTO {
    
    private String id;
    private String capabilityId;
    private String name;
    private String description;
    private String version;
    private String icon;
    
    private String type;
    private String ownership;
    private String category;
    
    private String skillForm;
    private String sceneType;
    private String visibility;
    private String capabilityCategory;
    
    private String businessCategory;
    private String subCategory;
    private List<String> tags;
    
    private boolean sceneCapability;
    private boolean hasSelfDrive;
    private boolean mainFirst;
    private Integer businessSemanticsScore;
    
    private String source;
    private String status;
    private String skillId;
    private String parentScene;
    
    private List<String> capabilities;
    private List<String> supportedSceneTypes;
    private List<String> dependencies;
    private List<Map<String, Object>> driverConditions;
    private List<Map<String, Object>> participants;
    
    private Map<String, Object> metadata;
    private Long createdAt;
    private Long updatedAt;
    
    private Boolean installed;
    private String installUrl;

    public CapabilityDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getOwnership() { return ownership; }
    public void setOwnership(String ownership) { this.ownership = ownership; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSkillForm() { return skillForm; }
    public void setSkillForm(String skillForm) { this.skillForm = skillForm; }
    
    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    
    public String getCapabilityCategory() { return capabilityCategory; }
    public void setCapabilityCategory(String capabilityCategory) { this.capabilityCategory = capabilityCategory; }
    
    public String getBusinessCategory() { return businessCategory; }
    public void setBusinessCategory(String businessCategory) { this.businessCategory = businessCategory; }
    
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public boolean isSceneCapability() { return sceneCapability; }
    public void setSceneCapability(boolean sceneCapability) { this.sceneCapability = sceneCapability; }
    
    public boolean isHasSelfDrive() { return hasSelfDrive; }
    public void setHasSelfDrive(boolean hasSelfDrive) { this.hasSelfDrive = hasSelfDrive; }
    
    public boolean isMainFirst() { return mainFirst; }
    public void setMainFirst(boolean mainFirst) { this.mainFirst = mainFirst; }
    
    public Integer getBusinessSemanticsScore() { return businessSemanticsScore; }
    public void setBusinessSemanticsScore(Integer businessSemanticsScore) { this.businessSemanticsScore = businessSemanticsScore; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getParentScene() { return parentScene; }
    public void setParentScene(String parentScene) { this.parentScene = parentScene; }
    
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    
    public List<String> getSupportedSceneTypes() { return supportedSceneTypes; }
    public void setSupportedSceneTypes(List<String> supportedSceneTypes) { this.supportedSceneTypes = supportedSceneTypes; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public List<Map<String, Object>> getDriverConditions() { return driverConditions; }
    public void setDriverConditions(List<Map<String, Object>> driverConditions) { this.driverConditions = driverConditions; }
    
    public List<Map<String, Object>> getParticipants() { return participants; }
    public void setParticipants(List<Map<String, Object>> participants) { this.participants = participants; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getInstalled() { return installed; }
    public void setInstalled(Boolean installed) { this.installed = installed; }
    
    public String getInstallUrl() { return installUrl; }
    public void setInstallUrl(String installUrl) { this.installUrl = installUrl; }
    
    public void setCapId(String capId) { this.capabilityId = capId; }
    public String getCapId() { return this.capabilityId; }
}
