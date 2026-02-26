package net.ooder.sdk.a2a.capability;

import java.util.*;

/**
 * Skill卡片
 *
 * <p>根据Ooder-A2A规范v1.0定义的Skill能力声明卡片</p>
 *
 * <p>Skill卡片是Skill的元数据和能力声明，用于:</p>
 * <ul>
 *   <li>AI理解Skill的功能</li>
 *   <li>动态发现和调用Skill</li>
 *   <li>用户了解Skill的能力</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class SkillCard {

    /**
     * Skill唯一标识
     */
    private String skillId;

    /**
     * 显示名称
     */
    private Map<String, String> name;

    /**
     * 描述
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

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 能力列表
     */
    private List<Capability> capabilities;

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

    /**
     * UI配置
     */
    private UIConfig uiConfig;

    /**
     * 端点信息
     */
    private EndpointInfo endpoint;

    public SkillCard() {
        this.name = new HashMap<>();
        this.description = new HashMap<>();
        this.tags = new ArrayList<>();
        this.capabilities = new ArrayList<>();
        this.inputFormats = new ArrayList<>();
        this.outputFormats = new ArrayList<>();
        this.authMethods = new ArrayList<>();
    }

    // ==================== 便捷方法 ====================

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    @Override
    public String toString() {
        return "SkillCard{" +
                "skillId='" + skillId + '\'' +
                ", name=" + name +
                ", version='" + version + '\'' +
                ", category='" + category + '\'' +
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
