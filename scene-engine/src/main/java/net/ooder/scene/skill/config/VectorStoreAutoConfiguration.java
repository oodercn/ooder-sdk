package net.ooder.scene.skill.config;

import net.ooder.sdk.llm.embedding.EmbeddingService;
import net.ooder.scene.autoconfigure.SceneEngineProperties;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.VectorStoreConfig;
import net.ooder.scene.skill.vector.impl.InMemoryVectorStore;
import net.ooder.scene.skill.vector.impl.JsonVectorStore;
import net.ooder.scene.skill.vector.impl.MilvusVectorStore;
import net.ooder.scene.skill.vector.impl.MockEmbeddingService;
import net.ooder.scene.skill.vector.LlmEmbeddingServiceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向量存储自动配置
 *
 * <p>提供向量存储和嵌入服务的自动配置，支持多种存储后端。</p>
 *
 * <p>架构层级：基础设施层 - 自动配置</p>
 *
 * <h3>支持的向量存储类型：</h3>
 * <ul>
 *   <li>memory: 内存存储（默认，仅开发测试）</li>
 *   <li>json: JSON 文件存储</li>
 *   <li>milvus: Milvus 向量数据库（生产推荐）</li>
 * </ul>
 *
 * <h3>配置示例：</h3>
 * <pre>
 * scene.engine.vector.enabled=true
 * scene.engine.knowledge.vector-store.type=milvus
 * scene.engine.knowledge.vector-store.dimension=1536
 * scene.engine.knowledge.vector-store.milvus.host=localhost
 * scene.engine.knowledge.vector-store.milvus.port=19530
 * scene.engine.knowledge.vector-store.milvus.database=default
 * scene.engine.knowledge.vector-store.milvus.collection=knowledge_vectors
 * scene.engine.embedding.mock=false
 * </pre>
 *
 * @author ooder
 * @since 2.3
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "scene.engine.vector", name = "enabled", havingValue = "true", matchIfMissing = true)
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

        if ("milvus".equalsIgnoreCase(type)) {
            return createMilvusVectorStore(vectorProps, dimension);
        } else if ("json".equalsIgnoreCase(type)) {
            String basePath = knowledgeProps.getPersistence().getBasePath();
            log.info("============================================================");
            log.info("使用JSON向量存储: {}/vectors", basePath);
            log.info("============================================================");
            return new JsonVectorStore(basePath + "/vectors", dimension, true, 5000);
        } else if ("memory".equalsIgnoreCase(type)) {
            log.warn("============================================================");
            log.warn("使用默认向量存储: InMemoryVectorStore (内存存储)");
            log.warn("注意: 此实现仅适用于开发测试，数据将在重启后丢失");
            log.warn("生产环境请配置 milvus 或其他持久化向量存储");
            log.warn("============================================================");
            return new InMemoryVectorStore(dimension);
        } else {
            log.warn("未知的向量存储类型: {}, 使用默认内存存储", type);
            return new InMemoryVectorStore(dimension);
        }
    }

    private VectorStore createMilvusVectorStore(SceneEngineProperties.VectorStoreProperties vectorProps, int dimension) {
        String host = vectorProps.getMilvusHost();
        int port = vectorProps.getMilvusPort();
        String database = vectorProps.getMilvusDatabase();
        String collection = vectorProps.getMilvusCollection();
        String token = vectorProps.getMilvusToken();
        String metricType = vectorProps.getMetricType();

        log.info("============================================================");
        log.info("使用 Milvus 向量存储: {}:{}", host, port);
        log.info("Database: {}, Collection: {}, Dimension: {}", database, collection, dimension);
        log.info("MetricType: {}", metricType);
        log.info("============================================================");

        VectorStoreConfig config = VectorStoreConfig.milvus(dimension, host, port, database, collection);
        config.setMetricType(metricType != null ? metricType : "COSINE");
        if (token != null && !token.isEmpty()) {
            config.property("token", token);
        }

        MilvusVectorStore milvusStore = new MilvusVectorStore(config);
        milvusStore.initialize();

        return milvusStore;
    }

    @Bean
    @ConditionalOnMissingBean(SceneEmbeddingService.class)
    @ConditionalOnProperty(prefix = "scene.engine.embedding", name = "mock", havingValue = "true", matchIfMissing = false)
    public SceneEmbeddingService mockEmbeddingService() {
        log.warn("============================================================");
        log.warn("使用默认嵌入服务: MockEmbeddingService (随机向量)");
        log.warn("注意: 此实现仅适用于开发测试，向量是随机生成的");
        log.warn("生产环境请配置 scene.engine.embedding.mock=false");
        log.warn("============================================================");
        return new MockEmbeddingService(DEFAULT_DIMENSION);
    }

    @Bean
    @ConditionalOnMissingBean(SceneEmbeddingService.class)
    @ConditionalOnProperty(prefix = "scene.engine.embedding", name = "mock", havingValue = "false", matchIfMissing = true)
    public SceneEmbeddingService llmEmbeddingServiceAdapter(EmbeddingService embeddingService) {
        log.info("============================================================");
        log.info("使用 LLM 嵌入服务适配器 (真实嵌入向量)");
        log.info("============================================================");
        return new LlmEmbeddingServiceAdapter(embeddingService);
    }
}
