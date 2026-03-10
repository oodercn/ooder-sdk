
package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

public class SkillManifest {
    
    private String skillId;
    private String name;
    private String description;
    private String version;
    private String sceneId;
    private String mainClass;
    private String skillType;
    private List<Capability> capabilities;
    private List<Dependency> dependencies;
    /**
     * @deprecated 使用 {@link #collaborativeCapabilities} 替代
     */
    @Deprecated
    private List<String> collaborativeScenes;
    
    /**
     * 协作能力ID列表（新术语）
     */
    private List<String> collaborativeCapabilities;
    
    /**
     * @deprecated 使用 {@link #collaborativeCapabilityDependencies} 替代
     */
    @Deprecated
    private List<SceneDependency> collaborativeSceneDependencies;
    
    /**
     * 协作能力依赖（新术语）
     */
    private List<SceneDependency> collaborativeCapabilityDependencies;
    
    /**
     * @deprecated 使用 {@link #mainFirstScene} 替代
     */
    @Deprecated
    private SceneConfig primaryScene;
    
    /**
     * 自驱入口场景配置（mainFirst）
     */
    private SceneConfig mainFirstScene;
    
    /**
     * 场景能力定义列表
     */
    private List<SceneCapabilityDef> sceneCapabilities;
    private Map<String, Parameter> parameters;
    private Map<String, Object> config;
    private String author;
    private String license;
    private String homepage;
    private String category;
    private String subCategory;
    private List<String> tags;
    private List<String> providedInterfaces;
    private List<String> requiredInterfaces;
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
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
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public String getSkillType() {
        return skillType;
    }
    
    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }
    
    public List<Capability> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }
    
    public List<Dependency> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    
    /**
     * @deprecated 使用 {@link #getCollaborativeCapabilities()} 替代
     */
    @Deprecated
    public List<String> getCollaborativeScenes() {
        return collaborativeScenes;
    }
    
    /**
     * @deprecated 使用 {@link #setCollaborativeCapabilities(List)} 替代
     */
    @Deprecated
    public void setCollaborativeScenes(List<String> collaborativeScenes) {
        this.collaborativeScenes = collaborativeScenes;
    }
    
    /**
     * 获取协作能力ID列表
     * @return 协作能力ID列表
     */
    public List<String> getCollaborativeCapabilities() {
        return collaborativeCapabilities != null ? collaborativeCapabilities : collaborativeScenes;
    }
    
    /**
     * 设置协作能力ID列表
     * @param collaborativeCapabilities 协作能力ID列表
     */
    public void setCollaborativeCapabilities(List<String> collaborativeCapabilities) {
        this.collaborativeCapabilities = collaborativeCapabilities;
    }
    
    /**
     * @deprecated 使用 {@link #getCollaborativeCapabilityDependencies()} 替代
     */
    @Deprecated
    public List<SceneDependency> getCollaborativeSceneDependencies() {
        return collaborativeSceneDependencies;
    }
    
    /**
     * @deprecated 使用 {@link #setCollaborativeCapabilityDependencies(List)} 替代
     */
    @Deprecated
    public void setCollaborativeSceneDependencies(List<SceneDependency> collaborativeSceneDependencies) {
        this.collaborativeSceneDependencies = collaborativeSceneDependencies;
    }
    
    /**
     * 获取协作能力依赖
     * @return 协作能力依赖列表
     */
    public List<SceneDependency> getCollaborativeCapabilityDependencies() {
        return collaborativeCapabilityDependencies != null ? 
            collaborativeCapabilityDependencies : collaborativeSceneDependencies;
    }
    
    /**
     * 设置协作能力依赖
     * @param collaborativeCapabilityDependencies 协作能力依赖列表
     */
    public void setCollaborativeCapabilityDependencies(List<SceneDependency> collaborativeCapabilityDependencies) {
        this.collaborativeCapabilityDependencies = collaborativeCapabilityDependencies;
    }
    
    /**
     * @deprecated 使用 {@link #getMainFirstScene()} 替代
     */
    @Deprecated
    public SceneConfig getPrimaryScene() {
        return primaryScene;
    }
    
    /**
     * @deprecated 使用 {@link #setMainFirstScene(SceneConfig)} 替代
     */
    @Deprecated
    public void setPrimaryScene(SceneConfig primaryScene) {
        this.primaryScene = primaryScene;
    }
    
    /**
     * 获取自驱入口场景配置（mainFirst）
     * @return 自驱入口场景配置
     */
    public SceneConfig getMainFirstScene() {
        return mainFirstScene != null ? mainFirstScene : primaryScene;
    }
    
    /**
     * 设置自驱入口场景配置（mainFirst）
     * @param mainFirstScene 自驱入口场景配置
     */
    public void setMainFirstScene(SceneConfig mainFirstScene) {
        this.mainFirstScene = mainFirstScene;
    }
    
    /**
     * 获取场景能力定义列表
     * @return 场景能力定义列表
     */
    public List<SceneCapabilityDef> getSceneCapabilities() {
        return sceneCapabilities;
    }
    
    /**
     * 设置场景能力定义列表
     * @param sceneCapabilities 场景能力定义列表
     */
    public void setSceneCapabilities(List<SceneCapabilityDef> sceneCapabilities) {
        this.sceneCapabilities = sceneCapabilities;
    }
    
    public List<CapabilityInfo> getProvidedCapabilities() {
        return new java.util.ArrayList<>();
    }
    
    public Map<String, Parameter> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getLicense() {
        return license;
    }
    
    public void setLicense(String license) {
        this.license = license;
    }
    
    public String getHomepage() {
        return homepage;
    }
    
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public List<String> getProvidedInterfaces() {
        return providedInterfaces;
    }
    
    public void setProvidedInterfaces(List<String> providedInterfaces) {
        this.providedInterfaces = providedInterfaces;
    }
    
    public List<String> getRequiredInterfaces() {
        return requiredInterfaces;
    }
    
    public void setRequiredInterfaces(List<String> requiredInterfaces) {
        this.requiredInterfaces = requiredInterfaces;
    }
    
    public static class Dependency {
        private String skillId;
        private String versionRange;
        private boolean required;
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersionRange() { return versionRange; }
        public void setVersionRange(String versionRange) { this.versionRange = versionRange; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
    }
    
    /**
     * 场景能力定义
     * 用于定义场景能力的配置信息
     */
    public static class SceneCapabilityDef {
        private String capabilityId;
        private boolean mainFirst;
        private net.ooder.skills.config.MainFirstConfiguration mainFirstConfig;
        private List<String> capabilities;
        private List<CollaborativeCapabilityRef> collaborativeCapabilities;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public boolean isMainFirst() { return mainFirst; }
        public void setMainFirst(boolean mainFirst) { this.mainFirst = mainFirst; }
        public net.ooder.skills.config.MainFirstConfiguration getMainFirstConfig() { return mainFirstConfig; }
        public void setMainFirstConfig(net.ooder.skills.config.MainFirstConfiguration mainFirstConfig) { this.mainFirstConfig = mainFirstConfig; }
        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
        public List<CollaborativeCapabilityRef> getCollaborativeCapabilities() { return collaborativeCapabilities; }
        public void setCollaborativeCapabilities(List<CollaborativeCapabilityRef> collaborativeCapabilities) { 
            this.collaborativeCapabilities = collaborativeCapabilities; 
        }
    }
    
    /**
     * 自检配置
     */
    public static class SelfCheck {
        private String checkType;
        private Map<String, Object> params;
        
        public String getCheckType() { return checkType; }
        public void setCheckType(String checkType) { this.checkType = checkType; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 自启配置
     */
    public static class SelfStart {
        private String startType;
        private Map<String, Object> params;
        
        public String getStartType() { return startType; }
        public void setStartType(String startType) { this.startType = startType; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 自驱配置
     */
    public static class SelfDriveConfig {
        private String driveMode;
        private long interval;
        private Map<String, Object> params;
        
        public String getDriveMode() { return driveMode; }
        public void setDriveMode(String driveMode) { this.driveMode = driveMode; }
        public long getInterval() { return interval; }
        public void setInterval(long interval) { this.interval = interval; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 协作启动配置
     */
    public static class CollaborationStart {
        private String collaborativeCapabilityId;
        private Map<String, Object> initParams;
        
        public String getCollaborativeCapabilityId() { return collaborativeCapabilityId; }
        public void setCollaborativeCapabilityId(String collaborativeCapabilityId) { 
            this.collaborativeCapabilityId = collaborativeCapabilityId; 
        }
        public Map<String, Object> getInitParams() { return initParams; }
        public void setInitParams(Map<String, Object> initParams) { this.initParams = initParams; }
    }
    
    /**
     * 协作能力引用
     */
    public static class CollaborativeCapabilityRef {
        private String capabilityId;
        private String role;
        private Map<String, Object> config;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }
}
