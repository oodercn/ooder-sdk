
package net.ooder.skills.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillManifest {
    
    /**
     * API 版本
     */
    private String apiVersion;
    
    /**
     * 资源类型 (SkillPackage, Skill)
     */
    private String kind;
    
    /**
     * 嵌套的 metadata 对象（Kubernetes 风格）
     */
    private Metadata metadata;
    
    /**
     * 嵌套的 spec 对象（Kubernetes 风格）
     */
    private Spec spec;
    
    // 兼容旧格式的根级别字段
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
    private List<String> tags;
    private List<String> providedInterfaces;
    private List<String> requiredInterfaces;
    private PromptConfig prompt;
    private LlmConfig llmConfig;
    
    // ==================== 智能 Getter 方法 ====================
    
    public String getApiVersion() {
        return apiVersion;
    }
    
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    
    public String getKind() {
        return kind;
    }
    
    public void setKind(String kind) {
        this.kind = kind;
    }
    
    public Metadata getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
    public Spec getSpec() {
        return spec;
    }
    
    public void setSpec(Spec spec) {
        this.spec = spec;
    }
    
    /**
     * 智能获取 skillId
     * 优先级: metadata.id > skillId
     */
    public String getSkillId() {
        if (metadata != null && metadata.getId() != null) {
            return metadata.getId();
        }
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    /**
     * 智能获取 name
     * 优先级: metadata.name > name
     */
    public String getName() {
        if (metadata != null && metadata.getName() != null) {
            return metadata.getName();
        }
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 智能获取 description
     * 优先级: metadata.description > description
     */
    public String getDescription() {
        if (metadata != null && metadata.getDescription() != null) {
            return metadata.getDescription();
        }
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 智能获取 version
     * 优先级: metadata.version > version
     */
    public String getVersion() {
        if (metadata != null && metadata.getVersion() != null) {
            return metadata.getVersion();
        }
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
    
    /**
     * 智能获取 skillType
     * 优先级: spec.skillForm > spec.type > skillType
     */
    public String getSkillType() {
        if (spec != null && spec.getSkillForm() != null) {
            return spec.getSkillForm();
        }
        if (spec != null && spec.getType() != null) {
            return spec.getType();
        }
        return skillType;
    }
    
    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }
    
    /**
     * 智能获取 capabilities
     * 优先级: spec.capabilities > capabilities
     */
    public List<Capability> getCapabilities() {
        if (spec != null && spec.getCapabilities() != null) {
            return spec.getCapabilities();
        }
        return capabilities;
    }
    
    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }
    
    /**
     * 智能获取 dependencies
     * 优先级: spec.dependencies > dependencies
     */
    public List<Dependency> getDependencies() {
        if (spec != null && spec.getDependencies() != null) {
            return spec.getDependencies();
        }
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
    
    /**
     * 智能获取 config
     * 优先级: spec.config > spec.configSchema > config
     */
    public Map<String, Object> getConfig() {
        if (spec != null && spec.getConfig() != null) {
            return spec.getConfig();
        }
        if (spec != null && spec.getConfigSchema() != null) {
            return spec.getConfigSchema();
        }
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    /**
     * 智能获取 author
     * 优先级: metadata.author > author
     */
    public String getAuthor() {
        if (metadata != null && metadata.getAuthor() != null) {
            return metadata.getAuthor();
        }
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
    
    /**
     * 智能获取 category（V3规范）
     * 优先级: spec.capability.category
     */
    public String getCategory() {
        if (spec != null && spec.getCapability() != null && 
            spec.getCapability().getCategory() != null) {
            return spec.getCapability().getCategory();
        }
        return null;
    }
    
    /**
     * 智能获取 subCategory（已废弃）
     * @deprecated V3规范中已移除 subCategory 概念
     */
    @Deprecated
    public String getSubCategory() {
        return null;
    }
    
    /**
     * 智能获取 tags
     * 优先级: metadata.tags > tags
     */
    public List<String> getTags() {
        if (metadata != null && metadata.getTags() != null) {
            return metadata.getTags();
        }
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
    
    public PromptConfig getPrompt() {
        return prompt;
    }
    
    public void setPrompt(PromptConfig prompt) {
        this.prompt = prompt;
    }
    
    public LlmConfig getLlmConfig() {
        return llmConfig;
    }
    
    public void setLlmConfig(LlmConfig llmConfig) {
        this.llmConfig = llmConfig;
    }
    
    // ==================== 嵌套类定义 ====================
    
    /**
     * Metadata 嵌套类（Kubernetes 风格）
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        private String id;
        private String name;
        private String version;
        private String description;
        private String author;
        private String type;
        private String icon;
        private List<String> tags;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
    
    /**
     * Spec 嵌套类（Kubernetes 风格）
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Spec {
        private String skillForm;
        private String type;
        private List<Capability> capabilities;
        private List<Dependency> dependencies;
        private Map<String, Object> config;
        private Map<String, Object> configSchema;
        private Map<String, Object> estimatedResources;
        private Integer timeout;
        private CapabilityInfo capability;
        
        public String getSkillForm() { return skillForm; }
        public void setSkillForm(String skillForm) { this.skillForm = skillForm; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public List<Capability> getCapabilities() { return capabilities; }
        public void setCapabilities(List<Capability> capabilities) { this.capabilities = capabilities; }
        
        public List<Dependency> getDependencies() { return dependencies; }
        public void setDependencies(List<Dependency> dependencies) { this.dependencies = dependencies; }
        
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        
        public Map<String, Object> getConfigSchema() { return configSchema; }
        public void setConfigSchema(Map<String, Object> configSchema) { this.configSchema = configSchema; }
        
        public Map<String, Object> getEstimatedResources() { return estimatedResources; }
        public void setEstimatedResources(Map<String, Object> estimatedResources) { this.estimatedResources = estimatedResources; }
        
        public Integer getTimeout() { return timeout; }
        public void setTimeout(Integer timeout) { this.timeout = timeout; }
        
        public CapabilityInfo getCapability() { return capability; }
        public void setCapability(CapabilityInfo capability) { this.capability = capability; }
    }
    
    /**
     * 能力信息（V3规范）
     * 包含 category 和 code 字段
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CapabilityInfo {
        private String category;
        private String code;
        private List<String> operations;
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public List<String> getOperations() { return operations; }
        public void setOperations(List<String> operations) { this.operations = operations; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromptConfig {
        private String systemPromptFile;
        private String rolePromptsDir;
        private List<String> contextFiles;
        private Map<String, String> variables;
        
        public String getSystemPromptFile() { return systemPromptFile; }
        public void setSystemPromptFile(String systemPromptFile) { this.systemPromptFile = systemPromptFile; }
        public String getRolePromptsDir() { return rolePromptsDir; }
        public void setRolePromptsDir(String rolePromptsDir) { this.rolePromptsDir = rolePromptsDir; }
        public List<String> getContextFiles() { return contextFiles; }
        public void setContextFiles(List<String> contextFiles) { this.contextFiles = contextFiles; }
        public Map<String, String> getVariables() { return variables; }
        public void setVariables(Map<String, String> variables) { this.variables = variables; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LlmConfig {
        private String systemPrompt;
        private Double temperature;
        private Integer maxTokens;
        private List<FunctionConfig> functions;
        
        public String getSystemPrompt() { return systemPrompt; }
        public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
        public List<FunctionConfig> getFunctions() { return functions; }
        public void setFunctions(List<FunctionConfig> functions) { this.functions = functions; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FunctionConfig {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private String capability;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        public String getCapability() { return capability; }
        public void setCapability(String capability) { this.capability = capability; }
    }
}
