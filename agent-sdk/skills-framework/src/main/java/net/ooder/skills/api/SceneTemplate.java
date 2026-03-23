package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

/**
 * 场景模板
 * 定义场景能力的完整配置，包括 Skills、能力绑定和协作能力
 * <p>
 * 术语变更说明（v2.3）：
 * - 场景模板 → 场景能力 (SceneTemplate 作为 SceneCapability 的配置载体)
 * - 协作场景 → 协作能力 (collaborativeScenes → collaborativeCapabilities)
 * - 主场景 → 自驱入口 (primaryScene → mainFirst)
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneTemplate {

    private String templateId;
    private String name;
    private String description;
    private String version;
    private String category;

    // Skills 列表
    private List<SkillRef> skills;

    // 能力绑定配置
    private List<CapabilityBinding> capabilityBindings;

    // 协作能力配置（原协作场景）
    private List<CollaborativeCapabilityRef> collaborativeCapabilities;

    // 场景配置
    private SceneConfig sceneConfig;

    // 元数据
    private Map<String, Object> metadata;

    // 自驱入口配置（mainFirst）
    private MainFirstConfig mainFirstConfig;

    // 能力链定义（原工作流）
    private List<CapabilityChainDef> capabilityChains;

    // 角色配置列表 (v2.3.1 新增)
    private List<RoleConfig> roles;

    // 激活步骤配置列表 (v2.3.1 新增)
    private List<ActivationStepConfig> activationSteps;

    // 菜单配置列表 (v2.3.1 新增)
    private List<MenuConfig> menus;

    // Getters and Setters
    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<SkillRef> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillRef> skills) {
        this.skills = skills;
    }

    public List<CapabilityBinding> getCapabilityBindings() {
        return capabilityBindings;
    }

    public void setCapabilityBindings(List<CapabilityBinding> capabilityBindings) {
        this.capabilityBindings = capabilityBindings;
    }

    /**
     * @deprecated 使用 {@link #getCollaborativeCapabilities()} 替代
     */
    @Deprecated
    public List<CollaborativeSceneRef> getCollaborativeScenes() {
        // 兼容旧代码，返回转换后的结果
        if (collaborativeCapabilities == null) return null;
        List<CollaborativeSceneRef> refs = new java.util.ArrayList<>();
        for (CollaborativeCapabilityRef capRef : collaborativeCapabilities) {
            CollaborativeSceneRef sceneRef = new CollaborativeSceneRef();
            sceneRef.setSceneId(capRef.getCapabilityId());
            sceneRef.setRelation(capRef.getRelation());
            sceneRef.setBidirectional(capRef.isBidirectional());
            refs.add(sceneRef);
        }
        return refs;
    }

    /**
     * @deprecated 使用 {@link #setCollaborativeCapabilities(List)} 替代
     */
    @Deprecated
    public void setCollaborativeScenes(List<CollaborativeSceneRef> collaborativeScenes) {
        // 兼容旧代码，转换为新格式
        if (collaborativeScenes == null) {
            this.collaborativeCapabilities = null;
            return;
        }
        this.collaborativeCapabilities = new java.util.ArrayList<>();
        for (CollaborativeSceneRef sceneRef : collaborativeScenes) {
            CollaborativeCapabilityRef capRef = new CollaborativeCapabilityRef();
            capRef.setCapabilityId(sceneRef.getSceneId());
            capRef.setRelation(sceneRef.getRelation());
            capRef.setBidirectional(sceneRef.isBidirectional());
            this.collaborativeCapabilities.add(capRef);
        }
    }

    /**
     * 获取协作能力列表
     * @return 协作能力引用列表
     */
    public List<CollaborativeCapabilityRef> getCollaborativeCapabilities() {
        return collaborativeCapabilities;
    }

    /**
     * 设置协作能力列表
     * @param collaborativeCapabilities 协作能力引用列表
     */
    public void setCollaborativeCapabilities(List<CollaborativeCapabilityRef> collaborativeCapabilities) {
        this.collaborativeCapabilities = collaborativeCapabilities;
    }

    public SceneConfig getSceneConfig() {
        return sceneConfig;
    }

    public void setSceneConfig(SceneConfig sceneConfig) {
        this.sceneConfig = sceneConfig;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * 获取自驱入口配置（mainFirst）
     * @return 自驱入口配置
     */
    public MainFirstConfig getMainFirstConfig() {
        return mainFirstConfig;
    }

    /**
     * 设置自驱入口配置（mainFirst）
     * @param mainFirstConfig 自驱入口配置
     */
    public void setMainFirstConfig(MainFirstConfig mainFirstConfig) {
        this.mainFirstConfig = mainFirstConfig;
    }

    /**
     * 获取能力链定义列表
     * @return 能力链定义列表
     */
    public List<CapabilityChainDef> getCapabilityChains() {
        return capabilityChains;
    }

    /**
     * 设置能力链定义列表
     * @param capabilityChains 能力链定义列表
     */
    public void setCapabilityChains(List<CapabilityChainDef> capabilityChains) {
        this.capabilityChains = capabilityChains;
    }

    public List<RoleConfig> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleConfig> roles) {
        this.roles = roles;
    }

    public List<ActivationStepConfig> getActivationSteps() {
        return activationSteps;
    }

    public void setActivationSteps(List<ActivationStepConfig> activationSteps) {
        this.activationSteps = activationSteps;
    }

    public List<MenuConfig> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuConfig> menus) {
        this.menus = menus;
    }

    /**
     * Skill 引用
     */
    public static class SkillRef {
        private String skillId;
        private String version;
        private boolean required;
        private Map<String, Object> config;

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public Map<String, Object> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }

    /**
     * 能力绑定
     */
    public static class CapabilityBinding {
        private String capabilityId;
        private String skillId;
        private String condition;
        private Map<String, Object> params;

        public String getCapabilityId() {
            return capabilityId;
        }

        public void setCapabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
        }

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }

    /**
     * 协作场景引用
     * @deprecated 使用 {@link CollaborativeCapabilityRef} 替代
     */
    @Deprecated
    public static class CollaborativeSceneRef {
        private String sceneId;
        private String relation;
        private boolean bidirectional;

        public String getSceneId() {
            return sceneId;
        }

        public void setSceneId(String sceneId) {
            this.sceneId = sceneId;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public boolean isBidirectional() {
            return bidirectional;
        }

        public void setBidirectional(boolean bidirectional) {
            this.bidirectional = bidirectional;
        }
    }

    /**
     * 协作能力引用（新术语）
     */
    public static class CollaborativeCapabilityRef {
        private String capabilityId;
        private String relation;
        private boolean bidirectional;
        private Map<String, Object> config;

        public String getCapabilityId() {
            return capabilityId;
        }

        public void setCapabilityId(String capabilityId) {
            this.capabilityId = capabilityId;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public boolean isBidirectional() {
            return bidirectional;
        }

        public void setBidirectional(boolean bidirectional) {
            this.bidirectional = bidirectional;
        }

        public Map<String, Object> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }

    /**
     * 自驱入口配置（mainFirst）
     */
    public static class MainFirstConfig {
        private boolean enabled;
        private List<SelfCheckDef> selfChecks;
        private List<SelfStartDef> selfStarts;
        private SelfDriveDef selfDrive;
        private List<CollaborationStartDef> collaborationStarts;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<SelfCheckDef> getSelfChecks() {
            return selfChecks;
        }

        public void setSelfChecks(List<SelfCheckDef> selfChecks) {
            this.selfChecks = selfChecks;
        }

        public List<SelfStartDef> getSelfStarts() {
            return selfStarts;
        }

        public void setSelfStarts(List<SelfStartDef> selfStarts) {
            this.selfStarts = selfStarts;
        }

        public SelfDriveDef getSelfDrive() {
            return selfDrive;
        }

        public void setSelfDrive(SelfDriveDef selfDrive) {
            this.selfDrive = selfDrive;
        }

        public List<CollaborationStartDef> getCollaborationStarts() {
            return collaborationStarts;
        }

        public void setCollaborationStarts(List<CollaborationStartDef> collaborationStarts) {
            this.collaborationStarts = collaborationStarts;
        }
    }

    /**
     * 自检定义
     */
    public static class SelfCheckDef {
        private String checkType;
        private Map<String, Object> params;

        public String getCheckType() {
            return checkType;
        }

        public void setCheckType(String checkType) {
            this.checkType = checkType;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }

    /**
     * 自启定义
     */
    public static class SelfStartDef {
        private String startType;
        private Map<String, Object> params;

        public String getStartType() {
            return startType;
        }

        public void setStartType(String startType) {
            this.startType = startType;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }

    /**
     * 自驱定义
     */
    public static class SelfDriveDef {
        private String driveMode;
        private long interval;
        private Map<String, Object> params;

        public String getDriveMode() {
            return driveMode;
        }

        public void setDriveMode(String driveMode) {
            this.driveMode = driveMode;
        }

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }

    /**
     * 协作启动定义
     */
    public static class CollaborationStartDef {
        private String collaborativeCapabilityId;
        private Map<String, Object> initParams;

        public String getCollaborativeCapabilityId() {
            return collaborativeCapabilityId;
        }

        public void setCollaborativeCapabilityId(String collaborativeCapabilityId) {
            this.collaborativeCapabilityId = collaborativeCapabilityId;
        }

        public Map<String, Object> getInitParams() {
            return initParams;
        }

        public void setInitParams(Map<String, Object> initParams) {
            this.initParams = initParams;
        }
    }

    /**
     * 能力链定义（原工作流）
     */
    public static class CapabilityChainDef {
        private String chainId;
        private String name;
        private String description;
        private List<String> capabilityIds;
        private Map<String, Object> config;

        public String getChainId() {
            return chainId;
        }

        public void setChainId(String chainId) {
            this.chainId = chainId;
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

        public List<String> getCapabilityIds() {
            return capabilityIds;
        }

        public void setCapabilityIds(List<String> capabilityIds) {
            this.capabilityIds = capabilityIds;
        }

        public Map<String, Object> getConfig() {
            return config;
        }

        public void setConfig(Map<String, Object> config) {
            this.config = config;
        }
    }

    /**
     * 场景配置
     */
    public static class SceneConfig {
        private String sceneType;
        private Map<String, Object> properties;
        private List<String> tags;

        public String getSceneType() {
            return sceneType;
        }

        public void setSceneType(String sceneType) {
            this.sceneType = sceneType;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}
