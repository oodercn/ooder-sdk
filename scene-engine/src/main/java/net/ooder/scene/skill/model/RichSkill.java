package net.ooder.scene.skill.model;

import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.skill.SkillService;
import net.ooder.skills.api.SkillPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Skill充血模型
 * 
 * <p>包装SDK的贫血模型SkillPackage，添加业务逻辑和行为</p>
 * 
 * <p>v3.0 更新：</p>
 * <ul>
 *   <li>支持技能形态（SCENE/STANDALONE）</li>
 *   <li>支持场景类型（AUTO/TRIGGER/HYBRID）</li>
 *   <li>支持技能分类（knowledge/llm/tool/...）</li>
 *   <li>支持服务目的（多维度组合）</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 3.0
 * @since 2.3.0
 */
public class RichSkill implements Skill {
    
    private final SkillPackage rawPackage;
    private DiscoverySource source;
    private long discoveredTime;
    private boolean cached;
    private boolean installed;
    private String installPath;
    
    // 依赖服务（由协调器注入）
    private transient SkillService skillService;
    private transient CacheManager cacheManager;
    
    public RichSkill(SkillPackage rawPackage) {
        this.rawPackage = rawPackage;
        this.discoveredTime = System.currentTimeMillis();
    }
    
    // ========== Skill 接口实现 ==========
    
    @Override
    public String getSkillId() {
        return rawPackage.getSkillId();
    }
    
    @Override
    public String getName() {
        return rawPackage.getName();
    }
    
    @Override
    public String getVersion() {
        return rawPackage.getVersion();
    }
    
    @Override
    public String getDescription() {
        return rawPackage.getDescription();
    }
    
    @Override
    public SkillForm getForm() {
        // 从 SkillPackage 获取形态，如果不存在则根据旧字段推断
        try {
            Object form = rawPackage.getMetadata().get("form");
            if (form != null) {
                return SkillForm.valueOf(form.toString().toUpperCase());
            }
        } catch (Exception ignored) {}
        
        // 兼容旧数据：根据 sceneSkill 字段推断
        Boolean sceneSkill = (Boolean) rawPackage.getMetadata().get("sceneSkill");
        return Boolean.TRUE.equals(sceneSkill) ? SkillForm.SCENE : SkillForm.STANDALONE;
    }
    
    @Override
    public Optional<SceneType> getSceneType() {
        if (getForm() != SkillForm.SCENE) {
            return Optional.empty();
        }
        
        try {
            Object sceneType = rawPackage.getMetadata().get("sceneType");
            if (sceneType != null) {
                return Optional.of(SceneType.valueOf(sceneType.toString().toUpperCase()));
            }
        } catch (Exception ignored) {}
        
        // 兼容旧数据：根据 mainFirst 字段推断
        Boolean mainFirst = (Boolean) rawPackage.getMetadata().get("mainFirst");
        return Optional.of(Boolean.TRUE.equals(mainFirst) ? SceneType.AUTO : SceneType.TRIGGER);
    }
    
    @Override
    public SkillCategory getCategory() {
        // V3规范：优先使用 SDK 的 getCategory() 方法（已处理 spec.capability.category 优先级）
        String category = rawPackage.getCategory();
        return SkillCategory.fromCode(category);
    }
    
    @Override
    public Set<ServicePurpose> getPurposes() {
        try {
            @SuppressWarnings("unchecked")
            List<String> purposes = (List<String>) rawPackage.getMetadata().get("purposes");
            if (purposes != null) {
                return purposes.stream()
                    .map(p -> ServicePurpose.fromCode(p))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            }
        } catch (Exception ignored) {}
        
        return Collections.emptySet();
    }
    
    @Override
    public List<net.ooder.scene.skill.capability.Capability> getCapabilities() {
        // 从 SkillPackage 获取能力列表
        // 简化实现，实际需要从 rawPackage 解析
        return Collections.emptyList();
    }
    
    @Override
    public Optional<SceneStructure> getSceneStructure() {
        if (getForm() != SkillForm.SCENE) {
            return Optional.empty();
        }
        
        try {
            Map<String, Object> metadata = rawPackage.getMetadata();
            if (metadata == null) {
                return Optional.empty();
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> sceneStructureData = (Map<String, Object>) metadata.get("sceneStructure");
            if (sceneStructureData == null) {
                return Optional.empty();
            }
            
            SceneStructure structure = parseSceneStructure(sceneStructureData);
            return Optional.of(structure);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @SuppressWarnings("unchecked")
    private SceneStructure parseSceneStructure(Map<String, Object> data) {
        List<SceneStructure.InternalCapability> internalCapabilities = parseInternalCapabilities(
            (List<Map<String, Object>>) data.get("internalCapabilities"));
        
        List<Skill> childSkills = parseChildSkills(
            (List<Map<String, Object>>) data.get("childSkills"));
        
        SceneStructure.Orchestration orchestration = parseOrchestration(
            (Map<String, Object>) data.get("orchestration"));
        
        SceneStructure.CollaborationConfig collaborationConfig = parseCollaborationConfig(
            (Map<String, Object>) data.get("collaborationConfig"));
        
        String entryCapability = (String) data.get("entryCapability");
        
        SceneStructure.SceneState state = parseSceneState((String) data.get("state"));
        
        Map<String, Object> structureMetadata = (Map<String, Object>) data.get("metadata");
        
        return new SceneStructureImpl(internalCapabilities, childSkills, orchestration, 
            collaborationConfig, entryCapability, state, structureMetadata);
    }
    
    private List<SceneStructure.InternalCapability> parseInternalCapabilities(List<Map<String, Object>> data) {
        if (data == null) return Collections.emptyList();
        
        List<SceneStructure.InternalCapability> result = new ArrayList<>();
        for (Map<String, Object> capData : data) {
            String id = (String) capData.get("id");
            String name = (String) capData.get("name");
            String description = (String) capData.get("description");
            String type = (String) capData.get("type");
            Map<String, Object> config = (Map<String, Object>) capData.get("config");
            Boolean isPrivate = (Boolean) capData.get("private");
            
            result.add(new SceneStructureImpl.InternalCapabilityImpl(
                id, name, description, type, config, 
                isPrivate != null ? isPrivate : true));
        }
        return result;
    }
    
    private List<Skill> parseChildSkills(List<Map<String, Object>> data) {
        if (data == null) return Collections.emptyList();
        
        List<Skill> result = new ArrayList<>();
        for (Map<String, Object> skillData : data) {
            result.add(new ChildSkillWrapper(skillData));
        }
        return result;
    }
    
    private SceneStructure.Orchestration parseOrchestration(Map<String, Object> data) {
        if (data == null) return null;
        
        String typeStr = (String) data.get("type");
        SceneStructure.Orchestration.OrchestrationType type = SceneStructure.Orchestration.OrchestrationType.SEQUENTIAL;
        if (typeStr != null) {
            try {
                type = SceneStructure.Orchestration.OrchestrationType.valueOf(typeStr.toUpperCase());
            } catch (Exception ignored) {}
        }
        
        List<Map<String, Object>> stepsData = (List<Map<String, Object>>) data.get("steps");
        List<SceneStructure.Orchestration.Step> steps = new ArrayList<>();
        if (stepsData != null) {
            for (Map<String, Object> stepData : stepsData) {
                steps.add(new SceneStructureImpl.StepImpl(
                    (String) stepData.get("id"),
                    (String) stepData.get("capabilityId"),
                    (Map<String, String>) stepData.get("inputMapping"),
                    (Map<String, String>) stepData.get("outputMapping"),
                    (String) stepData.get("condition")
                ));
            }
        }
        
        return new SceneStructureImpl.OrchestrationImpl(type, steps);
    }
    
    private SceneStructure.CollaborationConfig parseCollaborationConfig(Map<String, Object> data) {
        if (data == null) return null;
        
        Boolean externallyAccessible = (Boolean) data.get("externallyAccessible");
        List<String> exposedCapabilities = (List<String>) data.get("exposedCapabilities");
        
        List<Map<String, Object>> depsData = (List<Map<String, Object>>) data.get("externalDependencies");
        List<SceneStructure.ExternalDependency> externalDependencies = new ArrayList<>();
        if (depsData != null) {
            for (Map<String, Object> depData : depsData) {
                externalDependencies.add(new SceneStructureImpl.ExternalDependencyImpl(
                    (String) depData.get("skillId"),
                    (String) depData.get("capabilityId"),
                    Boolean.TRUE.equals(depData.get("required")),
                    (String) depData.get("fallbackStrategy")
                ));
            }
        }
        
        Map<String, Object> a2aData = (Map<String, Object>) data.get("a2aConfig");
        SceneStructure.A2AConfig a2aConfig = null;
        if (a2aData != null) {
            a2aConfig = new SceneStructureImpl.A2AConfigImpl(
                Boolean.TRUE.equals(a2aData.get("enabled")),
                (String) a2aData.get("endpoint"),
                (Map<String, String>) a2aData.get("headers")
            );
        }
        
        return new SceneStructureImpl.CollaborationConfigImpl(
            Boolean.TRUE.equals(externallyAccessible),
            exposedCapabilities,
            externalDependencies,
            a2aConfig
        );
    }
    
    private SceneStructure.SceneState parseSceneState(String stateStr) {
        if (stateStr == null) return SceneStructure.SceneState.CREATED;
        try {
            return SceneStructure.SceneState.valueOf(stateStr.toUpperCase());
        } catch (Exception e) {
            return SceneStructure.SceneState.CREATED;
        }
    }
    
    /**
     * 子技能包装器
     */
    private static class ChildSkillWrapper implements Skill {
        private final Map<String, Object> data;
        
        public ChildSkillWrapper(Map<String, Object> data) {
            this.data = data;
        }
        
        @Override
        public String getSkillId() {
            return (String) data.get("skillId");
        }
        
        @Override
        public String getName() {
            return (String) data.get("name");
        }
        
        @Override
        public String getVersion() {
            return (String) data.get("version");
        }
        
        @Override
        public String getDescription() {
            return (String) data.get("description");
        }
        
        @Override
        public SkillForm getForm() {
            String form = (String) data.get("form");
            if (form != null) {
                try {
                    return SkillForm.valueOf(form.toUpperCase());
                } catch (Exception ignored) {}
            }
            return SkillForm.STANDALONE;
        }
        
        @Override
        public SkillCategory getCategory() {
            String category = (String) data.get("category");
            if (category != null) {
                return SkillCategory.fromCode(category);
            }
            return SkillCategory.OTHER;
        }
        
        @Override
        public Set<ServicePurpose> getPurposes() {
            List<String> purposes = (List<String>) data.get("purposes");
            if (purposes != null) {
                return purposes.stream()
                    .map(ServicePurpose::fromCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        }
        
        @Override
        public List<net.ooder.scene.skill.capability.Capability> getCapabilities() {
            return Collections.emptyList();
        }
        
        @Override
        public SkillPath getPath() {
            String skillId = getSkillId();
            return skillId != null ? SkillPath.from(skillId.replace(".", "/")) : null;
        }
    }
    
    @Override
    public SkillPath getPath() {
        String skillId = getSkillId();
        return SkillPath.from(skillId.replace(".", "/"));
    }
    
    @Override
    public Optional<String> getParentId() {
        try {
            String parentId = (String) rawPackage.getMetadata().get("parentId");
            return Optional.ofNullable(parentId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    // ========== 业务方法 ==========
    
    /**
     * 检查是否可安装
     */
    public boolean isInstallable() {
        return checkDependencies() && checkCompatibility() && checkPermission();
    }
    
    private boolean checkDependencies() {
        List<String> dependencies = rawPackage.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }
        return true;
    }
    
    private boolean checkCompatibility() {
        return true;
    }
    
    private boolean checkPermission() {
        return true;
    }
    
    /**
     * 获取依赖列表
     */
    public List<RichSkill> getDependencies() {
        if (rawPackage == null) {
            return Collections.emptyList();
        }
        
        List<String> dependencyIds = rawPackage.getDependencies();
        if (dependencyIds == null || dependencyIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        if (skillService != null) {
            return dependencyIds.stream()
                .map(skillService::findSkill)
                .filter(Objects::nonNull)
                .filter(obj -> obj instanceof RichSkill)
                .map(obj -> (RichSkill) obj)
                .collect(Collectors.toList());
        }
        
        return Collections.emptyList();
    }
    
    /**
     * 创建安装计划
     */
    public InstallPlan createInstallPlan() {
        InstallPlan plan = new InstallPlan();
        plan.setMainSkill(this);
        return plan;
    }
    
    /**
     * 检查是否在缓存中
     */
    public boolean isCached() {
        if (cacheManager != null) {
            return cacheManager.exists(getSkillId());
        }
        return cached;
    }
    
    /**
     * 检查是否需要更新
     */
    public boolean needsUpdate() {
        if (!installed) {
            return false;
        }
        return false;
    }
    
    /**
     * 获取下载URL
     */
    public String getDownloadUrl() {
        return rawPackage.getDownloadUrl();
    }
    
    /**
     * 获取原始包
     */
    public SkillPackage getRawPackage() {
        return rawPackage;
    }
    
    /**
     * 获取来源
     */
    public DiscoverySource getSource() {
        return source;
    }
    
    public void setSource(DiscoverySource source) {
        this.source = source;
    }
    
    /**
     * 获取发现时间
     */
    public long getDiscoveredTime() {
        return discoveredTime;
    }
    
    /**
     * 检查是否已安装
     */
    public boolean isInstalled() {
        return installed;
    }
    
    public void setInstalled(boolean installed) {
        this.installed = installed;
    }
    
    /**
     * 获取安装路径
     */
    public String getInstallPath() {
        return installPath;
    }
    
    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }
    
    /**
     * 设置依赖服务
     */
    public void setSkillService(SkillService skillService) {
        this.skillService = skillService;
    }
    
    /**
     * 设置缓存管理器
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    /**
     * 发现来源枚举
     */
    public enum DiscoverySource {
        LOCAL,
        GITHUB,
        GITEE,
        UDP,
        SKILL_CENTER
    }
    
    /**
     * 安装计划
     */
    public static class InstallPlan {
        private RichSkill mainSkill;
        private List<RichSkill> dependencies;
        private List<String> installOrder;
        
        public RichSkill getMainSkill() { return mainSkill; }
        public void setMainSkill(RichSkill mainSkill) { this.mainSkill = mainSkill; }
        public List<RichSkill> getDependencies() { return dependencies; }
        public void setDependencies(List<RichSkill> dependencies) { this.dependencies = dependencies; }
        public List<String> getInstallOrder() { return installOrder; }
        public void setInstallOrder(List<String> installOrder) { this.installOrder = installOrder; }
    }
}
