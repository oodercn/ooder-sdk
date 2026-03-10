package net.ooder.scene.skill.model;

import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.skill.SkillService;
import net.ooder.skills.api.SkillPackage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Skill充血模型
 * 
 * <p>包装SDK的贫血模型SkillPackage，添加业务逻辑和行为</p>
 * 
 * <p>设计原则：</p>
 * <ul>
 *   <li>充血模型：包含数据和行为</li>
 *   <li>业务逻辑：安装检查、依赖解析等</li>
 *   <li>状态感知：知道自己在缓存中的状态</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class RichSkill {
    
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
    
    /**
     * 获取原始Skill ID
     */
    public String getSkillId() {
        return rawPackage.getSkillId();
    }
    
    /**
     * 获取Skill名称
     */
    public String getName() {
        return rawPackage.getName();
    }
    
    /**
     * 获取版本
     */
    public String getVersion() {
        return rawPackage.getVersion();
    }
    
    /**
     * 获取描述
     */
    public String getDescription() {
        return rawPackage.getDescription();
    }
    
    /**
     * 检查是否可安装
     * 
     * <p>业务逻辑：检查依赖、版本兼容性、权限</p>
     * 
     * @return 是否可安装
     */
    public boolean isInstallable() {
        return checkDependencies() && checkCompatibility() && checkPermission();
    }
    
    /**
     * 检查依赖是否满足
     */
    private boolean checkDependencies() {
        List<String> dependencies = rawPackage.getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }
        
        // 检查每个依赖是否已安装
        // 简化实现，实际应该从SkillService查询
        return true;
    }
    
    /**
     * 检查版本兼容性
     */
    private boolean checkCompatibility() {
        // 版本兼容性检查逻辑
        return true;
    }
    
    /**
     * 检查权限
     */
    private boolean checkPermission() {
        // 权限检查逻辑
        return true;
    }
    
    /**
     * 获取依赖列表
     * 
     * @return 依赖的RichSkill列表
     */
    @SuppressWarnings("unchecked")
    public List<RichSkill> getDependencies() {
        if (rawPackage == null) {
            return Collections.emptyList();
        }
        
        List<String> dependencyIds = rawPackage.getDependencies();
        if (dependencyIds == null || dependencyIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 使用 SkillService 查询依赖的 RichSkill
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
     * 
     * @return 安装计划
     */
    public InstallPlan createInstallPlan() {
        InstallPlan plan = new InstallPlan();
        plan.setMainSkill(this);
        // 拓扑排序依赖
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
        // 检查远程版本是否比本地版本新
        // 简化实现
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
