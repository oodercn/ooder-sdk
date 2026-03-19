package net.ooder.scene.skill.config;

import net.ooder.scene.autoconfigure.SceneEngineProperties;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.impl.InMemoryVectorStore;
import net.ooder.scene.skill.vector.impl.JsonVectorStore;
import net.ooder.scene.skill.vector.impl.MockEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向量存储自动配置
 *
 * <p>提供微层（降级）实现的自动配置，当没有外部实现时自动启用。</p>
 *
 * <p>架构层级：基础设施层 - 自动配置</p>
 *
 * @author ooder
 * @since 2.3
 */
@Configuration
public class VectorStoreAutoConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(VectorStoreAutoConfiguration.class);
    
    private static final int DEFAULT_DIMENSION = 1536;
    
    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(SceneEngineProperties properties) {
        SceneEngineProperties.KnowledgeProperties knowledgeProps = properties.getKnowledge();
        SceneEngineProperties.VectorStoreProperties vectorProps = knowledgeProps.getVectorStore();
        String type = vectorProps.getType();
        int dimension = vectorProps.getDimension();
        
        if ("json".equalsIgnoreCase(type)) {
            String basePath = knowledgeProps.getPersistence().getBasePath();
            log.info("============================================================");
            log.info("使用JSON向量存储: {}/vectors", basePath);
            log.info("============================================================");
            return new JsonVectorStore(basePath + "/vectors", dimension, true, 5000);
        } else if ("memory".equalsIgnoreCase(type)) {
            log.warn("============================================================");
            log.warn("使用默认向量存储: InMemoryVectorStore (内存存储)");
            log.warn("注意: 此实现仅适用于开发测试，数据将在重启后丢失");
            log.warn("生产环境请配置外部向量存储 Skill (skill-vector-sqlite 或 skill-vector-milvus)");
            log.warn("============================================================");
            return new InMemoryVectorStore(dimension);
        } else {
            log.warn("未知的向量存储类型: {}, 使用默认内存存储", type);
            return new InMemoryVectorStore(dimension);
        }
    }
    
    @Bean
    @ConditionalOnMissingBean(SceneEmbeddingService.class)
    public SceneEmbeddingService embeddingService() {
        log.warn("============================================================");
        log.warn("使用默认嵌入服务: MockEmbeddingService (随机向量)");
        log.warn("注意: 此实现仅适用于开发测试，向量是随机生成的");
        log.warn("生产环境请配置 LlmEmbeddingServiceAdapter 或外部嵌入服务");
        log.warn("============================================================");
        return new MockEmbeddingService(DEFAULT_DIMENSION);
    }
}
