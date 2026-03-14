package net.ooder.scene.skill.config;

import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.impl.InMemoryVectorStore;
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
    
    /**
     * 向量存储 - 默认使用内存存储（微层/降级方案）
     *
     * <p>当容器中没有其他 VectorStore 实现时，自动创建 InMemoryVectorStore。</p>
     * <p>适用于开发测试环境，生产环境建议使用外部 Skill 实现（SQLite/Milvus）。</p>
     */
    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore() {
        log.warn("============================================================");
        log.warn("使用默认向量存储: InMemoryVectorStore (内存存储)");
        log.warn("注意: 此实现仅适用于开发测试，数据将在重启后丢失");
        log.warn("生产环境请配置外部向量存储 Skill (skill-vector-sqlite 或 skill-vector-milvus)");
        log.warn("============================================================");
        return new InMemoryVectorStore(DEFAULT_DIMENSION);
    }
    
    /**
     * 嵌入服务 - 默认使用 Mock 实现（微层/降级方案）
     *
     * <p>当容器中没有其他 SceneEmbeddingService 实现时，自动创建 MockEmbeddingService。</p>
     * <p>适用于开发测试环境，生产环境建议使用 LlmEmbeddingServiceAdapter 或外部实现。</p>
     */
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
