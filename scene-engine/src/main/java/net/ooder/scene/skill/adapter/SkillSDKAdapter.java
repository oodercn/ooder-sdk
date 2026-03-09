package net.ooder.scene.skill.adapter;

import net.ooder.scene.skill.state.SkillLifecycleState;
import net.ooder.skills.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * SDK适配器 - 封装对skills-framework的调用
 *
 * <p>将SDK的复杂调用转换为简单接口，隔离SDK细节</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
@Component
public class SkillSDKAdapter {

    private static final Logger log = LoggerFactory.getLogger(SkillSDKAdapter.class);

    @Autowired
    private SkillRegistry skillRegistry;

    @Autowired
    private SkillInstaller skillInstaller;

    @Autowired
    private SkillDiscoverer skillDiscoverer;

    // ==================== 安装管理 ====================

    /**
     * 安装Skill
     */
    public boolean installSkill(String skillId, Map<String, Object> config) {
        log.debug("Installing skill: {}", skillId);

        try {
            Object skillPackage = findSkillPackage(skillId);
            if (skillPackage == null) {
                log.error("Skill package not found: {}", skillId);
                return false;
            }

            CompletableFuture<InstallResult> future = skillInstaller.install(
                    (SkillPackage) skillPackage,
                    SkillInstaller.InstallMode.NORMAL
            );

            InstallResult result = future.get();
            return result.isSuccess();

        } catch (Exception e) {
            log.error("Failed to install skill: {}", skillId, e);
            return false;
        }
    }

    /**
     * 卸载Skill
     */
    public boolean uninstallSkill(String skillId, boolean removeData) {
        log.debug("Uninstalling skill: {}", skillId);

        try {
            // 从注册表移除
            skillRegistry.unregister(skillId);
            return true;
        } catch (Exception e) {
            log.error("Failed to uninstall skill: {}", skillId, e);
            return false;
        }
    }

    // ==================== 生命周期管理 ====================

    /**
     * 启动Skill
     */
    public boolean startSkill(String skillId) {
        log.debug("Starting skill: {}", skillId);

        try {
            SkillService skillService = findSkillService(skillId);
            if (skillService != null) {
                skillService.start();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to start skill: {}", skillId, e);
            return false;
        }
    }

    /**
     * 停止Skill
     */
    public boolean stopSkill(String skillId) {
        log.debug("Stopping skill: {}", skillId);

        try {
            SkillService skillService = findSkillService(skillId);
            if (skillService != null) {
                skillService.stop();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to stop skill: {}", skillId, e);
            return false;
        }
    }

    /**
     * 获取Skill状态
     */
    public SkillLifecycleState getSkillState(String skillId) {
        SkillService skillService = findSkillService(skillId);
        if (skillService == null) {
            return SkillLifecycleState.CREATED;
        }

        // 根据SkillService状态转换
        if (skillService.isRunning()) {
            return SkillLifecycleState.RUNNING;
        } else {
            return SkillLifecycleState.STOPPED;
        }
    }

    // ==================== 调用执行 ====================

    /**
     * 调用能力
     */
    public Object invokeCapability(String userId, String skillId, String capability, Map<String, Object> params) {
        log.debug("Invoking capability: skill={}, capability={}", skillId, capability);

        try {
            SkillService skillService = findSkillService(skillId);
            if (skillService == null) {
                throw new RuntimeException("Skill not found: " + skillId);
            }

            // 构造SDK请求 - 使用工厂方法
            SkillRequest request = SkillRequest.create(skillId, null, capability);
            request.setParams(params);

            return skillService.execute(request);

        } catch (Exception e) {
            log.error("Failed to invoke skill: {}, capability: {}", skillId, capability, e);
            throw new RuntimeException("Invoke failed: " + e.getMessage(), e);
        }
    }

    /**
     * 异步调用能力
     */
    public CompletableFuture<Object> invokeCapabilityAsync(String userId, String skillId,
                                                            String capability, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() ->
                invokeCapability(userId, skillId, capability, params));
    }

    // ==================== 查询方法 ====================

    /**
     * 查找Skill
     */
    public Object findSkillPackage(String skillId) {
        // 返回Object避免类型不匹配
        return skillRegistry.getSkill(skillId);
    }

    /**
     * 获取SkillService
     */
    public SkillService findSkillService(String skillId) {
        // 从注册表获取SkillService
        // 注意：这里需要根据实际SDK API调整
        return null; // 占位，需要根据实际情况实现
    }

    /**
     * 获取能力列表
     */
    public List<net.ooder.skills.api.Capability> listCapabilities(String skillId) {
        Object skillPackage = findSkillPackage(skillId);
        if (skillPackage != null && skillPackage instanceof SkillPackage) {
            return ((SkillPackage) skillPackage).getCapabilities();
        }
        return Collections.emptyList();
    }

    /**
     * 发现Skill
     */
    public List<Object> discoverSkills(String query) {
        try {
            // 返回类型不确定，使用Object
            CompletableFuture<?> future = skillDiscoverer.discover(query);
            Object result = future.get();
            if (result instanceof List) {
                return (List<Object>) result;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to discover skills", e);
            return Collections.emptyList();
        }
    }
}
