package net.ooder.scene.service.impl;

import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.service.UnifiedSceneService;
import net.ooder.scene.skill.coordinator.InstallCoordinator;
import net.ooder.scene.skill.model.RichSkill;
import net.ooder.scene.skill.session.InstallSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 统一场景服务实现
 *
 * <p>整合发现和安装功能，提供统一的场景服务接口。</p>
 *
 * <p><b>重要说明：</b></p>
 * <ul>
 *   <li>此类不依赖Spring注解，通过构造函数传入依赖</li>
 *   <li>由配置类（如SceneEngineAutoConfiguration）负责创建实例</li>
 *   <li>避免在engine内部使用Spring注入，保持独立性</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class UnifiedSceneServiceImpl implements UnifiedSceneService {

    private DiscoveryCoordinator discoveryCoordinator;
    private InstallCoordinator installCoordinator;

    /**
     * 默认构造函数
     *
     * <p>用于反射创建，创建后必须通过setter设置依赖</p>
     */
    public UnifiedSceneServiceImpl() {
        // 默认构造函数，用于反射创建
    }

    /**
     * 完整构造函数
     *
     * <p>推荐方式：通过构造函数传入所有依赖</p>
     *
     * @param discoveryCoordinator 发现协调器
     * @param installCoordinator   安装协调器
     */
    public UnifiedSceneServiceImpl(DiscoveryCoordinator discoveryCoordinator,
                                    InstallCoordinator installCoordinator) {
        this.discoveryCoordinator = discoveryCoordinator;
        this.installCoordinator = installCoordinator;
    }

    // ==================== Getters & Setters ====================

    public DiscoveryCoordinator getDiscoveryCoordinator() {
        return discoveryCoordinator;
    }

    public void setDiscoveryCoordinator(DiscoveryCoordinator discoveryCoordinator) {
        this.discoveryCoordinator = discoveryCoordinator;
    }

    public InstallCoordinator getInstallCoordinator() {
        return installCoordinator;
    }

    public void setInstallCoordinator(InstallCoordinator installCoordinator) {
        this.installCoordinator = installCoordinator;
    }

    // ==================== UnifiedSceneService 接口实现 ====================

    @Override
    public CompletableFuture<DiscoveryResult> discoverSkills(String source) {
        if (discoveryCoordinator == null) {
            throw new IllegalStateException("DiscoveryCoordinator not initialized");
        }
        return discoveryCoordinator.discover(source)
            .thenApply(skills -> {
                DiscoveryResult result = new DiscoveryResult();
                result.setSource(source);
                result.setDiscoveredSkills(skills);
                result.setTotalCount(skills.size());
                return result;
            });
    }

    @Override
    public CompletableFuture<DiscoveryResult> discoverSkills(String owner, String repo, DiscoveryOptions options) {
        // 根据owner和repo推断来源（github/gitee）
        String source = inferSource(owner, repo);
        return discoverSkills(source);
    }

    @Override
    public CompletableFuture<List<RichSkill>> searchSkills(String keyword) {
        if (discoveryCoordinator == null) {
            throw new IllegalStateException("DiscoveryCoordinator not initialized");
        }
        return discoveryCoordinator.search(keyword);
    }

    @Override
    public CompletableFuture<RichSkill> getSkillDetail(String skillId) {
        if (discoveryCoordinator == null) {
            throw new IllegalStateException("DiscoveryCoordinator not initialized");
        }
        return discoveryCoordinator.getSkillDetail(skillId);
    }

    @Override
    public String installSkill(String skillId) {
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.install(null); // 需要根据skillId查找RichSkill
    }

    @Override
    public String installSkill(String skillId, String version) {
        // 实现带版本的安装
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.install(null);
    }

    @Override
    public int getInstallProgress(String sessionId) {
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.getProgress(sessionId);
    }

    @Override
    public InstallSession.InstallState getInstallState(String sessionId) {
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.getState(sessionId);
    }

    @Override
    public boolean pauseInstall(String sessionId) {
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.pause(sessionId);
    }

    @Override
    public boolean resumeInstall(String sessionId) {
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.resume(sessionId);
    }

    @Override
    public boolean cancelInstall(String sessionId) {
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.cancel(sessionId);
    }

    @Override
    public InstallSession.InstallReport getInstallReport(String sessionId) {
        if (installCoordinator == null) {
            throw new IllegalStateException("InstallCoordinator not initialized");
        }
        return installCoordinator.getReport(sessionId);
    }

    /**
     * 根据owner和repo推断来源
     */
    private String inferSource(String owner, String repo) {
        // 简化实现，实际应该根据配置判断
        if (repo != null && repo.toLowerCase().contains("gitee")) {
            return "gitee";
        }
        return "github";
    }
}
