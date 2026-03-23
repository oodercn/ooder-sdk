package net.ooder.scene.discovery.config;

import net.ooder.scene.discovery.UnifiedDiscoveryService;
import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.discovery.api.DiscoveryService;
import net.ooder.scene.discovery.impl.UnifiedDiscoveryServiceImpl;
import net.ooder.scene.discovery.impl.UnifiedSkillRegistryImpl;
import net.ooder.scene.discovery.impl.DiscoveryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发现服务自动配置
 *
 * <p>自动配置发现服务相关的Bean，支持Gitee和GitHub</p>
 *
 * @author ooder
 * @since 2.3.1
 */
@Configuration
@ConditionalOnProperty(prefix = "scene.engine.discovery", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DiscoveryProperties.class)
public class DiscoveryAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryAutoConfiguration.class);

    /**
     * 统一发现服务
     */
    @Bean
    @ConditionalOnMissingBean
    public UnifiedDiscoveryService unifiedDiscoveryService(DiscoveryProperties properties) {
        logger.info("Initializing UnifiedDiscoveryService");
        
        UnifiedDiscoveryServiceImpl service = new UnifiedDiscoveryServiceImpl();
        
        if (properties.getGitee().isEnabled()) {
            logger.info("Configuring Gitee discovery: owner={}, repo={}", 
                properties.getGitee().getDefaultOwner(), 
                properties.getGitee().getDefaultRepo());
            
            service.configureGitee(
                properties.getGitee().getToken(),
                properties.getGitee().getDefaultOwner(),
                properties.getGitee().getDefaultRepo(),
                properties.getGitee().getDefaultBranch(),
                properties.getGitee().getSkillsPath()
            );
            
            service.setGiteeCacheTtl(properties.getGitee().getCacheTtlMs());
        }
        
        if (properties.getGithub().isEnabled()) {
            logger.info("Configuring GitHub discovery: owner={}, repo={}", 
                properties.getGithub().getDefaultOwner(), 
                properties.getGithub().getDefaultRepo());
            
            service.configureGithub(
                properties.getGithub().getToken(),
                properties.getGithub().getDefaultOwner(),
                properties.getGithub().getDefaultRepo()
            );
            
            service.setGithubCacheTtl(properties.getGithub().getCacheTtlMs());
        }
        
        if (properties.getCache().isEnabled()) {
            service.setCacheConfig(
                properties.getCache().getDir(),
                properties.getCache().getTtlMs(),
                properties.getCache().getMaxEntries()
            );
        }
        
        return service;
    }

    /**
     * Skill注册中心
     */
    @Bean
    @ConditionalOnMissingBean
    public UnifiedSkillRegistry unifiedSkillRegistry() {
        logger.info("Initializing UnifiedSkillRegistry");
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
        logger.info("Initializing DiscoveryService");
        return new DiscoveryServiceImpl(unifiedDiscoveryService, unifiedSkillRegistry);
    }
}
