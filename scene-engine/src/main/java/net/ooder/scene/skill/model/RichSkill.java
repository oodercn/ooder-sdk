package net.ooder.scene.skill.model;

import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.skill.SkillService;
import net.ooder.skills.api.SkillPackage;

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
        try {
            Object category = rawPackage.getMetadata().get("category");
            if (category != null) {
                return SkillCategory.fromCode(category.toString());
            }
        } catch (Exception ignored) {}
        
        return SkillCategory.OTHER;
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
        // TODO: 从 rawPackage 解析场景结构
        return Optional.empty();
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
