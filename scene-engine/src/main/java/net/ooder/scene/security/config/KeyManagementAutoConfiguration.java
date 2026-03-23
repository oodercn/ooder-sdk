package net.ooder.scene.security.config;

import net.ooder.scene.security.storage.JsonKeyStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 密钥管理自动配置
 *
 * <p>SDK核心包只提供存储层和配置，不提供服务实现</p>
 * <p>服务层实现由MVP项目完成</p>
 *
 * @author ooder
 * @since 2.3.1
 */
@Configuration
@EnableConfigurationProperties(KeyManagementProperties.class)
@ConditionalOnProperty(prefix = "scene.engine.key.management", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KeyManagementAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(KeyManagementAutoConfiguration.class);

    /**
     * JSON存储服务
     * 
     * <p>SDK提供存储层实现</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public JsonKeyStorageService jsonKeyStorageService(KeyManagementProperties properties) {
        logger.info("Initializing JsonKeyStorageService with root: {}", properties.getStorage().getRoot());
        return new JsonKeyStorageService();
    }
}
