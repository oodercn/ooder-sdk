package net.ooder.sdk.a2a.capability;

import net.ooder.skills.api.*;

import java.util.*;

/**
 * Skill卡片（统一版本）
 *
 * <p>合并自 SkillCard (v2.3) 和 SkillCardV3 (v3.0)</p>
 *
 * <p>Skill卡片是Skill的元数据和能力声明，用于:</p>
 * <ul>
 *   <li>AI理解Skill的功能</li>
 *   <li>动态发现和调用Skill</li>
 *   <li>用户了解Skill的能力</li>
 *   <li>技能形态与运行时管理</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.2
 * @since 3.0.2
 */
public class SkillCard {

    // ========== 基础信息（保留） ==========
    
    /**
     * Skill唯一标识
     */
    private String skillId;

    /**
     * 显示名称（多语言支持）
     */
    private Map<String, String> name;

    /**
     * 描述（多语言支持）
     */
    private Map<String, String> description;

    /**
     * 版本号
     */
    private String version;

    /**
     * 作者信息
     */
    private AuthorInfo author;

    /**
     * 许可证
     */
    private String license;

    private SkillCategory skillCategory;
    
    /**
     * 技能形态
     */
    private SkillForm form;
    
    /**
     * 场景类型
     */
    private SceneType sceneType;
    
    /**
     * 服务目的
     */
    private Set<ServicePurpose> purposes;

    /**
     * 标签
     */
    private List<String> tags;

    // ========== 能力声明（保留+扩展） ==========
    
    /**
     * 能力列表
     */
    private List<Capability> capabilities;
    
    /**
     * 能力端点列表
     */
    private List<CapabilityEndpoint> capabilityEndpoints;

    /**
     * 支持的输入格式
     */
    private List<String> inputFormats;

    /**
     * 支持的输出格式
     */
    private List<String> outputFormats;

    /**
     * 认证方式
     */
    private List<String> authMethods;

    // ========== UI 和端点（保留） ==========
    
    /**
     * UI配置
     */
    private UIConfig uiConfig;

    /**
     * 端点信息
     */
    private EndpointInfo endpoint;

    // ========== Agent 信息（新增） ==========
    
    /**
     * Agent ID
     */
    private String agentId;
    
    /**
     * Agent 端点地址（A2A 通信用）
     */
    private String agentEndpoint;

    // ========== 状态管理（新增） ==========
    
    /**
     * 技能状态
     */
    private SkillStatus status;
    
    /**
     * 最后心跳时间
     */
    private long lastHeartbeat;

    // ========== 元数据（保留） ==========
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    public SkillCard() {
        this.name = new HashMap<>();
        this.description = new HashMap<>();
        this.tags = new ArrayList<>();
        this.capabilities = new ArrayList<>();
        this.capabilityEndpoints = new ArrayList<>();
        this.inputFormats = new ArrayList<>();
        this.outputFormats = new ArrayList<>();
        this.authMethods = new ArrayList<>();
        this.purposes = new HashSet<>();
        this.metadata = new HashMap<>();
    }

    // ==================== 便捷方法（保留） ====================

    /**
     * 添加名称（多语言）
     */
    public void addName(String language, String name) {
        this.name.put(language, name);
    }

    /**
     * 获取默认名称
     */
    public String getDefaultName() {
        return name.getOrDefault("zh_CN", name.getOrDefault("en_US", skillId));
    }

    /**
     * 添加描述（多语言）
     */
    public void addDescription(String language, String description) {
        this.description.put(language, description);
    }

    /**
     * 获取默认描述
     */
    public String getDefaultDescription() {
        return description.getOrDefault("zh_CN", description.getOrDefault("en_US", ""));
    }

    /**
     * 添加能力
     */
    public void addCapability(Capability capability) {
        this.capabilities.add(capability);
    }
    
    /**
     * 添加能力端点
     */
    public void addCapabilityEndpoint(CapabilityEndpoint endpoint) {
        this.capabilityEndpoints.add(endpoint);
    }

    // ==================== 便捷方法（新增） ====================
    
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

    // ==================== Getters and Setters ====================

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public AuthorInfo getAuthor() {
        return author;
    }

    public void setAuthor(AuthorInfo author) {
        this.author = author;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public SkillCategory getSkillCategory() {
        return skillCategory;
    }
    
    public void setSkillCategory(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
    }
    
    public SkillForm getForm() {
        return form;
    }
    
    public void setForm(SkillForm form) {
        this.form = form;
    }
    
    public SceneType getSceneType() {
        return sceneType;
    }
    
    public void setSceneType(SceneType sceneType) {
        this.sceneType = sceneType;
    }
    
    public Set<ServicePurpose> getPurposes() {
        return purposes;
    }
    
    public void setPurposes(Set<ServicePurpose> purposes) {
        this.purposes = purposes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }
    
    public List<CapabilityEndpoint> getCapabilityEndpoints() {
        return capabilityEndpoints;
    }
    
    public void setCapabilityEndpoints(List<CapabilityEndpoint> capabilityEndpoints) {
        this.capabilityEndpoints = capabilityEndpoints;
    }

    public List<String> getInputFormats() {
        return inputFormats;
    }

    public void setInputFormats(List<String> inputFormats) {
        this.inputFormats = inputFormats;
    }

    public List<String> getOutputFormats() {
        return outputFormats;
    }

    public void setOutputFormats(List<String> outputFormats) {
        this.outputFormats = outputFormats;
    }

    public List<String> getAuthMethods() {
        return authMethods;
    }

    public void setAuthMethods(List<String> authMethods) {
        this.authMethods = authMethods;
    }

    public UIConfig getUiConfig() {
        return uiConfig;
    }

    public void setUiConfig(UIConfig uiConfig) {
        this.uiConfig = uiConfig;
    }

    public EndpointInfo getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointInfo endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public String getAgentEndpoint() {
        return agentEndpoint;
    }
    
    public void setAgentEndpoint(String agentEndpoint) {
        this.agentEndpoint = agentEndpoint;
    }
    
    public SkillStatus getStatus() {
        return status;
    }
    
    public void setStatus(SkillStatus status) {
        this.status = status;
    }
    
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "SkillCard{" +
                "skillId='" + skillId + '\'' +
                ", name=" + name +
                ", version='" + version + '\'' +
                ", form=" + form +
                ", skillCategory=" + skillCategory +
                ", capabilities=" + capabilities.size() +
                '}';
    }

    // ==================== 内部类 ====================

    /**
     * 作者信息
     */
    public static class AuthorInfo {
        private String name;
        private String email;
        private String url;

        public AuthorInfo() {}

        public AuthorInfo(String name, String email, String url) {
            this.name = name;
            this.email = email;
            this.url = url;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    /**
     * 能力定义
     */
    public static class Capability {
        private String id;
        private String name;
        private String description;
        private List<String> parameters;
        private String returnType;

        public Capability() {}

        public Capability(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.parameters = new ArrayList<>();
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getParameters() { return parameters; }
        public void setParameters(List<String> parameters) { this.parameters = parameters; }
        public String getReturnType() { return returnType; }
        public void setReturnType(String returnType) { this.returnType = returnType; }
    }

    /**
     * UI配置
     */
    public static class UIConfig {
        private boolean enabled;
        private String entry;
        private String tagName;
        private String defaultWidth;
        private String defaultHeight;
        private boolean supportsTheming;
        private boolean supportsDarkMode;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getEntry() { return entry; }
        public void setEntry(String entry) { this.entry = entry; }
        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
        public String getDefaultWidth() { return defaultWidth; }
        public void setDefaultWidth(String defaultWidth) { this.defaultWidth = defaultWidth; }
        public String getDefaultHeight() { return defaultHeight; }
        public void setDefaultHeight(String defaultHeight) { this.defaultHeight = defaultHeight; }
        public boolean isSupportsTheming() { return supportsTheming; }
        public void setSupportsTheming(boolean supportsTheming) { this.supportsTheming = supportsTheming; }
        public boolean isSupportsDarkMode() { return supportsDarkMode; }
        public void setSupportsDarkMode(boolean supportsDarkMode) { this.supportsDarkMode = supportsDarkMode; }
    }

    /**
     * 端点信息
     */
    public static class EndpointInfo {
        private String basePath;
        private List<Endpoint> endpoints;

        public EndpointInfo() {
            this.endpoints = new ArrayList<>();
        }

        public String getBasePath() { return basePath; }
        public void setBasePath(String basePath) { this.basePath = basePath; }
        public List<Endpoint> getEndpoints() { return endpoints; }
        public void setEndpoints(List<Endpoint> endpoints) { this.endpoints = endpoints; }
        public void addEndpoint(String path, String method, String handler) {
            endpoints.add(new Endpoint(path, method, handler));
        }
    }

    /**
     * 端点定义
     */
    public static class Endpoint {
        private String path;
        private String method;
        private String handler;

        public Endpoint() {}

        public Endpoint(String path, String method, String handler) {
            this.path = path;
            this.method = method;
            this.handler = handler;
        }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getHandler() { return handler; }
        public void setHandler(String handler) { this.handler = handler; }
    }
}
