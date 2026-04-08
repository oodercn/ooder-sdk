package net.ooder.scene.discovery.config;

import net.ooder.scene.discovery.GiteeDiscoveryConfig;
import net.ooder.scene.discovery.GithubDiscoveryConfig;
import net.ooder.scene.discovery.UnifiedDiscoveryService;
import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.discovery.adapter.GiteeSkillDiscovererAdapter;
import net.ooder.scene.discovery.adapter.GitHubSkillDiscovererAdapter;
import net.ooder.scene.discovery.adapter.LocalSkillDiscovererAdapter;
import net.ooder.scene.discovery.adapter.SkillDiscovererAdapter;
import net.ooder.scene.discovery.api.DiscoveryService;
import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.discovery.impl.DiscoveryServiceImpl;
import net.ooder.scene.discovery.impl.UnifiedDiscoveryServiceImpl;
import net.ooder.scene.discovery.impl.UnifiedSkillRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 发现服务自动配置
 *
 * <p>自动配置发现服务相关的 Bean，支持：</p>
 * <ul>
 *   <li>本地发现</li>
 *   <li>Gitee 发现</li>
 *   <li>GitHub 发现</li>
 *   <li>P2P 发现（UDP/mDNS）</li>
 * </ul>
 *
 * <h3>配置示例：</h3>
 * <pre>
 * scene:
 *   engine:
 *     discovery:
 *       enabled: true
 *       gitee:
 *         enabled: true
 *         token: ${GITEE_TOKEN:}
 *         default-owner: ooderCN
 *         default-repo: skills
 *       github:
 *         enabled: false
 *         token: ${GITHUB_TOKEN:}
 * </pre>
 *
 * @author ooder
 * @since 2.3.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "scene.engine.discovery", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DiscoveryProperties.class)
public class DiscoveryAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryAutoConfiguration.class);

    private final DiscoveryProperties properties;

    public DiscoveryAutoConfiguration(DiscoveryProperties properties) {
        this.properties = properties;
    }

    /**
     * 统一发现服务
     */
    @Bean
    @ConditionalOnMissingBean
    public UnifiedDiscoveryService unifiedDiscoveryService() {
        logger.info("[DiscoveryAutoConfiguration] Initializing UnifiedDiscoveryService");
        
        UnifiedDiscoveryServiceImpl service = new UnifiedDiscoveryServiceImpl();
        
        if (properties.getGitee().isEnabled()) {
            logger.info("[DiscoveryAutoConfiguration] Configuring Gitee discovery: owner={}, repo={}", 
                properties.getGitee().getDefaultOwner(), 
                properties.getGitee().getDefaultRepo());
            
            GiteeDiscoveryConfig giteeConfig = new GiteeDiscoveryConfig(
                properties.getGitee().getToken(),
                properties.getGitee().getDefaultOwner(),
                properties.getGitee().getDefaultRepo(),
                properties.getGitee().getDefaultBranch(),
                properties.getGitee().getSkillsPath()
            );
            giteeConfig.setCacheTtl(properties.getGitee().getCacheTtlMs());
            service.configureGitee(giteeConfig);
        }
        
        if (properties.getGithub().isEnabled()) {
            logger.info("[DiscoveryAutoConfiguration] Configuring GitHub discovery: owner={}, repo={}", 
                properties.getGithub().getDefaultOwner(), 
                properties.getGithub().getDefaultRepo());
            
            GithubDiscoveryConfig githubConfig = new GithubDiscoveryConfig(
                properties.getGithub().getToken(),
                properties.getGithub().getDefaultOwner(),
                properties.getGithub().getDefaultRepo()
            );
            githubConfig.setCacheTtl(properties.getGithub().getCacheTtlMs());
            service.configureGithub(githubConfig);
        }
        
        if (properties.getCache().isEnabled()) {
            UnifiedDiscoveryService.CacheConfig cacheConfig = new UnifiedDiscoveryService.CacheConfig();
            cacheConfig.setCacheDir(properties.getCache().getDir());
            cacheConfig.setCacheTtlMs(properties.getCache().getTtlMs());
            cacheConfig.setMaxCacheEntries(properties.getCache().getMaxEntries());
            service.setCacheConfig(cacheConfig);
        }
        
        return service;
    }

    /**
     * Skill 注册中心
     */
    @Bean
    @ConditionalOnMissingBean
    public UnifiedSkillRegistry unifiedSkillRegistry() {
        logger.info("[DiscoveryAutoConfiguration] Initializing UnifiedSkillRegistry");
        return new UnifiedSkillRegistryImpl();
    }

    /**
     * 发现服务（高级接口）
     */
    @Bean
    @ConditionalOnMissingBean
    public DiscoveryService discoveryService(
            UnifiedDiscoveryService unifiedDiscoveryService,
            UnifiedSkillRegistry unifiedSkillRegistry) {
        logger.info("[DiscoveryAutoConfiguration] Initializing DiscoveryService");
        return new DiscoveryServiceImpl(unifiedDiscoveryService, unifiedSkillRegistry);
    }

    /**
     * 发现协调器
     */
    @Bean
    @ConditionalOnMissingBean
    public DiscoveryCoordinator discoveryCoordinator(CacheManager cacheManager) {
        logger.info("[DiscoveryAutoConfiguration] Initializing DiscoveryCoordinator");
        return new DiscoveryCoordinator(cacheManager);
    }

    /**
     * 本地发现器适配器
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalSkillDiscovererAdapter localSkillDiscovererAdapter() {
        logger.info("[DiscoveryAutoConfiguration] Initializing LocalSkillDiscovererAdapter");
        return new LocalSkillDiscovererAdapter();
    }

    /**
     * Gitee 发现器适配器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scene.engine.discovery.gitee", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GiteeSkillDiscovererAdapter giteeSkillDiscovererAdapter(UnifiedDiscoveryService unifiedDiscoveryService) {
        logger.info("[DiscoveryAutoConfiguration] Initializing GiteeSkillDiscovererAdapter");
        return new GiteeSkillDiscovererAdapter(unifiedDiscoveryService);
    }

    /**
     * GitHub 发现器适配器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scene.engine.discovery.github", name = "enabled", havingValue = "true")
    public GitHubSkillDiscovererAdapter gitHubSkillDiscovererAdapter(UnifiedDiscoveryService unifiedDiscoveryService) {
        logger.info("[DiscoveryAutoConfiguration] Initializing GitHubSkillDiscovererAdapter");
        return new GitHubSkillDiscovererAdapter(unifiedDiscoveryService);
    }

    /**
     * 发现协调器初始化器
     *
     * <p>自动注册所有可用的发现器适配器到协调器。</p>
     */
    @Bean
    public DiscoveryCoordinatorInitializer discoveryCoordinatorInitializer(
            DiscoveryCoordinator coordinator,
            @Autowired(required = false) List<SkillDiscovererAdapter> adapters) {
        logger.info("[DiscoveryAutoConfiguration] Initializing DiscoveryCoordinatorInitializer");
        return new DiscoveryCoordinatorInitializer(coordinator, adapters);
    }

    /**
     * 发现协调器初始化器
     */
    public static class DiscoveryCoordinatorInitializer {
        
        private static final Logger initLogger = LoggerFactory.getLogger(DiscoveryCoordinatorInitializer.class);
        
        private final DiscoveryCoordinator coordinator;
        private final List<SkillDiscovererAdapter> adapters;

        public DiscoveryCoordinatorInitializer(DiscoveryCoordinator coordinator, 
                                                List<SkillDiscovererAdapter> adapters) {
            this.coordinator = coordinator;
            this.adapters = adapters;
        }

        @PostConstruct
        public void initialize() {
            initLogger.info("[DiscoveryCoordinatorInitializer] Starting auto-registration of adapters");
            
            if (adapters != null && !adapters.isEmpty()) {
                coordinator.autoRegisterAvailableAdapters(adapters);
                initLogger.info("[DiscoveryCoordinatorInitializer] Registered {} adapters", 
                    coordinator.getAdapterDiscoverers().size());
            } else {
                initLogger.warn("[DiscoveryCoordinatorInitializer] No adapters found for auto-registration");
            }

            List<String> availableMethods = coordinator.getAvailableMethods().stream()
                .map(m -> m.getCode())
                .toList();
            initLogger.info("[DiscoveryCoordinatorInitializer] Available discovery methods: {}", availableMethods);
        }
    }
}
