package net.ooder.sdk.cli;

import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.sdk.cli.config.SkillCliConfiguration;
import net.ooder.sdk.cli.core.router.DefaultCliRouter;
import net.ooder.sdk.cli.core.registry.SkillExtensionRegistry;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillInvoker;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillService;
import net.ooder.skills.api.CollaborativeSceneGroupManager;
import net.ooder.skills.api.security.PermissionEngine;

/**
 * OoderCli 构建器
 *
 * <p>提供流畅的 API 用于构建 OoderCli 实例</p>
 *
 * <p>使用示例:</p>
 * <pre>
 * OoderCli cli = OoderCli.builder()
 *     .skillRegistry(skillRegistry)
 *     .skillInvoker(skillInvoker)
 *     .sceneGroupManager(sceneManager)
 *     .build();
 * </pre>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class OoderCliBuilder {

    private SkillRegistry skillRegistry;
    private SkillInstaller skillInstaller;
    private SkillInvoker skillInvoker;
    private CollaborativeSceneGroupManager sceneGroupManager;
    private SkillService skillService;
    private PermissionEngine permissionEngine;
    private SkillExtensionRegistry extensionRegistry;
    private CliRouter cliRouter;
    private SkillCliConfiguration configuration;

    /**
     * 设置 SkillRegistry
     */
    public OoderCliBuilder skillRegistry(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
        return this;
    }

    /**
     * 设置 SkillInstaller
     */
    public OoderCliBuilder skillInstaller(SkillInstaller skillInstaller) {
        this.skillInstaller = skillInstaller;
        return this;
    }

    /**
     * 设置 SkillInvoker
     */
    public OoderCliBuilder skillInvoker(SkillInvoker skillInvoker) {
        this.skillInvoker = skillInvoker;
        return this;
    }

    /**
     * 设置 SceneGroupManager
     */
    public OoderCliBuilder sceneGroupManager(CollaborativeSceneGroupManager sceneGroupManager) {
        this.sceneGroupManager = sceneGroupManager;
        return this;
    }

    /**
     * 设置 SkillService
     */
    public OoderCliBuilder skillService(SkillService skillService) {
        this.skillService = skillService;
        return this;
    }

    /**
     * 设置 PermissionEngine
     */
    public OoderCliBuilder permissionEngine(PermissionEngine permissionEngine) {
        this.permissionEngine = permissionEngine;
        return this;
    }

    /**
     * 设置 ExtensionRegistry
     */
    public OoderCliBuilder extensionRegistry(SkillExtensionRegistry extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
        return this;
    }

    /**
     * 设置 CliRouter
     */
    public OoderCliBuilder cliRouter(CliRouter cliRouter) {
        this.cliRouter = cliRouter;
        return this;
    }

    /**
     * 设置配置
     */
    public OoderCliBuilder configuration(SkillCliConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * 构建 OoderCli 实例
     */
    public OoderCli build() {
        OoderCli cli = new OoderCli();

        // 设置依赖
        if (skillRegistry != null) {
            cli.setSkillRegistry(skillRegistry);
        }
        if (skillInstaller != null) {
            cli.setSkillInstaller(skillInstaller);
        }
        if (skillInvoker != null) {
            cli.setSkillInvoker(skillInvoker);
        }
        if (sceneGroupManager != null) {
            cli.setSceneGroupManager(sceneGroupManager);
        }
        if (skillService != null) {
            cli.setSkillService(skillService);
        }
        if (permissionEngine != null) {
            cli.setPermissionEngine(permissionEngine);
        }
        if (extensionRegistry != null) {
            cli.setExtensionRegistry(extensionRegistry);
        }
        if (cliRouter != null) {
            cli.setCliRouter(cliRouter);
        } else {
            cli.setCliRouter(new DefaultCliRouter());
        }

        // 初始化适配器
        cli.initializeAdapters();

        // 应用配置
        if (configuration != null) {
            applyConfiguration(cli, configuration);
        }

        return cli;
    }

    /**
     * 应用配置
     */
    private void applyConfiguration(OoderCli cli, SkillCliConfiguration config) {
        if (config.getSkill() == null || config.getSkill().getCli() == null) {
            return;
        }

        SkillCliConfiguration.CliConfig cliConfig = config.getSkill().getCli();

        // 加载扩展
        if (cliConfig.getExtensions() != null && extensionRegistry != null) {
            extensionRegistry.loadFromConfig(config);
        }
    }

    /**
     * 验证构建器状态
     */
    public boolean isValid() {
        // 至少需要一个核心依赖
        return skillRegistry != null || skillInvoker != null || sceneGroupManager != null;
    }

    /**
     * 获取缺失的必需依赖列表
     */
    public String getMissingDependencies() {
        StringBuilder missing = new StringBuilder();
        if (skillRegistry == null) {
            missing.append("skillRegistry, ");
        }
        if (skillInvoker == null) {
            missing.append("skillInvoker, ");
        }
        if (missing.length() > 0) {
            missing.setLength(missing.length() - 2);
        }
        return missing.toString();
    }
}
