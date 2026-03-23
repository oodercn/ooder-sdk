package net.ooder.scene.skill.config;

import net.ooder.scene.autoconfigure.SceneEngineProperties;
import net.ooder.scene.skill.knowledge.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

/**
 * 知识库持久化自动配置
 *
 * <p>提供知识库持久化的自动配置，支持多种存储后端：</p>
 * <ul>
 *   <li>json - JSON文件存储（默认）</li>
 *   <li>memory - 内存存储（开发测试）</li>
 *   <li>sql - SQL数据库存储（规划中）</li>
 * </ul>
 *
 * <p>架构层级：基础设施层 - 自动配置</p>
 *
 * <p>默认禁用，需要通过配置显式启用：</p>
 * <pre>
 * scene.knowledge.persistence.enabled: true
 * </pre>
 *
 * @author ooder
 * @since 2.3
 */
@Configuration
@ConditionalOnProperty(name = "scene.knowledge.persistence.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(SceneEngineProperties.class)
public class KnowledgePersistenceAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KnowledgePersistenceAutoConfiguration.class);

    private KnowledgeRepository repository;

    @Bean
    @ConditionalOnMissingBean(KnowledgeRepository.class)
    public KnowledgeRepository knowledgeRepository(SceneEngineProperties properties) {
        SceneEngineProperties.KnowledgeProperties knowledgeProps = properties.getKnowledge();
        SceneEngineProperties.PersistenceProperties persistenceProps = knowledgeProps.getPersistence();

        String type = persistenceProps.getType();
        String basePath = persistenceProps.getBasePath();
        boolean autoSave = persistenceProps.isAutoSave();
        long saveIntervalMs = persistenceProps.getSaveIntervalMs();

        log.info("============================================================");
        log.info("初始化知识库持久化: type={}, path={}", type, basePath);
        log.info("============================================================");

        RepositoryConfig config;
        switch (type.toLowerCase()) {
            case RepositoryConfig.TYPE_JSON:
                config = RepositoryConfig.jsonFile(basePath);
                break;
            case RepositoryConfig.TYPE_MEMORY:
                config = RepositoryConfig.inMemory();
                log.warn("注意: 使用内存存储，数据将在重启后丢失");
                break;
            case RepositoryConfig.TYPE_SQL:
                config = RepositoryConfig.sql(basePath);
                break;
            default:
                log.warn("未知的持久化类型: {}, 使用默认JSON存储", type);
                config = RepositoryConfig.jsonFile(basePath);
        }

        config.setAutoSave(autoSave);
        config.setSaveIntervalMs(saveIntervalMs);

        KnowledgeRepositoryFactory.switchStorageType(config);
        this.repository = KnowledgeRepositoryFactory.getRepository();

        return this.repository;
    }

    @PreDestroy
    public void destroy() {
        if (repository != null) {
            log.info("Closing knowledge repository...");
            repository.close();
        }
    }
}
