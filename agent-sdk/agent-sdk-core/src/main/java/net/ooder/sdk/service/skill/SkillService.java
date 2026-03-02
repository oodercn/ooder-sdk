package net.ooder.sdk.service.skill;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ooder.skills.api.InstallRequest;
import net.ooder.skills.api.InstallResult;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillManifest;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.UninstallResult;
import net.ooder.skills.api.UpdateResult;
import net.ooder.skills.common.enums.DiscoveryMethod;

/**
 * 技能服务类
 * 提供技能的发现、安装、卸载、更新和查询功能
 * 
 * <p>作为 SkillPackageManager 的封装层，提供统一的技能管理服务。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class SkillService {
    
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(SkillService.class);
    
    /** 技能包管理器 */
    private final SkillPackageManager packageManager;
    
    /**
     * 构造函数
     * @param packageManager 技能包管理器
     */
    public SkillService(SkillPackageManager packageManager) {
        this.packageManager = packageManager;
    }
    
    /**
     * 发现技能
     * @param skillId 技能ID
     * @return 技能包
     */
    public CompletableFuture<SkillPackage> discoverSkill(String skillId) {
        log.info("Discovering skill: {}", skillId);
        return packageManager.discover(skillId, DiscoveryMethod.LOCAL_FS);
    }
    
    /**
     * 发现所有技能
     * @return 技能包列表
     */
    public CompletableFuture<List<SkillPackage>> discoverAllSkills() {
        log.info("Discovering all skills");
        return packageManager.discoverAll(DiscoveryMethod.LOCAL_FS);
    }
    
    /**
     * 安装技能
     * @param skillId 技能ID
     * @return 安装结果
     */
    public CompletableFuture<InstallResult> installSkill(String skillId) {
        log.info("Installing skill: {}", skillId);
        InstallRequest request = new InstallRequest();
        request.setSkillId(skillId);
        return packageManager.install(request);
    }
    
    /**
     * 卸载技能
     * @param skillId 技能ID
     * @return 卸载结果
     */
    public CompletableFuture<UninstallResult> uninstallSkill(String skillId) {
        log.info("Uninstalling skill: {}", skillId);
        return packageManager.uninstall(skillId);
    }
    
    /**
     * 更新技能
     * @param skillId 技能ID
     * @param version 目标版本
     * @return 更新结果
     */
    public CompletableFuture<UpdateResult> updateSkill(String skillId, String version) {
        log.info("Updating skill {} to version {}", skillId, version);
        return packageManager.update(skillId, version);
    }
    
    /**
     * 获取已安装技能列表
     * @return 已安装技能列表
     */
    public CompletableFuture<List<InstalledSkill>> listInstalledSkills() {
        return packageManager.listInstalled();
    }
    
    /**
     * 获取已安装技能
     * @param skillId 技能ID
     * @return 已安装技能
     */
    public CompletableFuture<InstalledSkill> getInstalledSkill(String skillId) {
        return packageManager.getInstalled(skillId);
    }
    
    /**
     * 检查技能是否已安装
     * @param skillId 技能ID
     * @return true表示已安装
     */
    public CompletableFuture<Boolean> isSkillInstalled(String skillId) {
        return packageManager.isInstalled(skillId);
    }
    
    /**
     * 获取技能包
     * @param skillId 技能ID
     * @return 技能包
     */
    public CompletableFuture<SkillPackage> getSkillPackage(String skillId) {
        return packageManager.getPackage(skillId);
    }
    
    /**
     * 获取技能清单
     * @param skillId 技能ID
     * @return 技能清单
     */
    public CompletableFuture<SkillManifest> getSkillManifest(String skillId) {
        return packageManager.getManifest(skillId);
    }
    
    /**
     * 搜索技能
     * @param query 搜索关键词
     * @return 技能包列表
     */
    public CompletableFuture<List<SkillPackage>> searchSkills(String query) {
        log.info("Searching skills with query: {}", query);
        return packageManager.search(query, DiscoveryMethod.LOCAL_FS);
    }
    
    /**
     * 获取技能根路径
     * @return 技能根路径
     */
    public String getSkillRootPath() {
        return packageManager.getSkillRootPath();
    }
    
    /**
     * 设置技能根路径
     * @param path 技能根路径
     */
    public void setSkillRootPath(String path) {
        packageManager.setSkillRootPath(path);
    }
}
