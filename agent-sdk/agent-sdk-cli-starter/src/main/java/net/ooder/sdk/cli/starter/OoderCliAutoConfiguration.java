package net.ooder.sdk.cli.starter;

import net.ooder.sdk.cli.OoderCli;
import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.sdk.cli.config.SkillCliConfigLoader;
import net.ooder.sdk.cli.config.SkillCliConfiguration;
import net.ooder.sdk.cli.core.router.DefaultCliRouter;
import net.ooder.sdk.cli.core.registry.SkillExtensionRegistry;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillInvoker;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillService;
import net.ooder.skills.api.CollaborativeSceneGroupManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ooder CLI 自动配置类
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Configuration
@ConditionalOnClass(OoderCli.class)
@EnableConfigurationProperties(OoderCliProperties.class)
@ConditionalOnProperty(prefix = "ooder.cli", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OoderCliAutoConfiguration {

    private final OoderCliProperties properties;

    public OoderCliAutoConfiguration(OoderCliProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillExtensionRegistry skillExtensionRegistry() {
        return new SkillExtensionRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillCliConfigLoader skillCliConfigLoader() {
        return new SkillCliConfigLoader();
    }

    @Bean
    @ConditionalOnMissingBean
    public CliRouter cliRouter(SkillExtensionRegistry extensionRegistry) {
        DefaultCliRouter router = new DefaultCliRouter();
        // 注册从配置加载的扩展命令
        return router;
    }

    @Bean
    @ConditionalOnMissingBean
    public OoderCli ooderCli(
            SkillRegistry skillRegistry,
            SkillInstaller skillInstaller,
            SkillInvoker skillInvoker,
            CollaborativeSceneGroupManager sceneGroupManager,
            SkillService skillService,
            SkillExtensionRegistry extensionRegistry,
            CliRouter cliRouter) {

        OoderCli cli = new OoderCli();
        cli.setSkillRegistry(skillRegistry);
        cli.setSkillInstaller(skillInstaller);
        cli.setSkillInvoker(skillInvoker);
        cli.setSceneGroupManager(sceneGroupManager);
        cli.setSkillService(skillService);
        cli.setExtensionRegistry(extensionRegistry);
        cli.setCliRouter(cliRouter);
        cli.initializeAdapters();

        // 加载配置
        loadConfiguration(cli);

        return cli;
    }

    private void loadConfiguration(OoderCli cli) {
        try {
            SkillCliConfigLoader configLoader = new SkillCliConfigLoader();
            SkillCliConfiguration config = configLoader.load();

            if (config != null && config.getSkill() != null && config.getSkill().getCli() != null) {
                SkillCliConfiguration.CliConfig cliConfig = config.getSkill().getCli();

                // 加载扩展
                if (cliConfig.getExtensions() != null) {
                    for (SkillCliConfiguration.ExtensionConfig extConfig : cliConfig.getExtensions()) {
                        if (extConfig.isEnabled()) {
                            // 从配置加载扩展
                            loadExtensionFromConfig(cli, extConfig);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 配置加载失败不影响 CLI 启动
        }
    }

    private void loadExtensionFromConfig(OoderCli cli, SkillCliConfiguration.ExtensionConfig config) {
        // 实现从配置加载扩展的逻辑
        // 这里可以根据 handler 类名加载并注册扩展
    }
}
