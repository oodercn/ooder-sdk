package net.ooder.scene.skill.knowledge.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 知识库仓库工厂
 *
 * <p>提供知识库仓库的创建和切换能力。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeRepositoryFactory {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeRepositoryFactory.class);

    private static final AtomicReference<KnowledgeRepository> instance = new AtomicReference<>();
    private static final AtomicReference<RepositoryConfig> config = new AtomicReference<>();

    static {
        config.set(new RepositoryConfig());
    }

    /**
     * 获取仓库实例
     *
     * @return 知识库仓库
     */
    public static KnowledgeRepository getRepository() {
        KnowledgeRepository repo = instance.get();
        if (repo == null) {
            synchronized (KnowledgeRepositoryFactory.class) {
                repo = instance.get();
                if (repo == null) {
                    repo = createRepository(config.get());
                    repo.initialize();
                    instance.set(repo);
                }
            }
        }
        return repo;
    }

    /**
     * 切换存储类型
     *
     * @param newConfig 新配置
     */
    public static synchronized void switchStorageType(RepositoryConfig newConfig) {
        if (newConfig == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        KnowledgeRepository oldRepo = instance.get();
        if (oldRepo != null) {
            oldRepo.close();
        }

        config.set(newConfig);
        KnowledgeRepository newRepo = createRepository(newConfig);
        newRepo.initialize();
        instance.set(newRepo);

        log.info("Switched to {} storage at {}", newConfig.getType(), newConfig.getBasePath());
    }

    /**
     * 重置工厂状态
     */
    public static synchronized void reset() {
        KnowledgeRepository repo = instance.get();
        if (repo != null) {
            repo.close();
        }
        instance.set(null);
        config.set(new RepositoryConfig());
        log.info("Repository factory reset");
    }

    /**
     * 获取当前配置
     *
     * @return 当前配置
     */
    public static RepositoryConfig getCurrentConfig() {
        return config.get();
    }

    private static KnowledgeRepository createRepository(RepositoryConfig repoConfig) {
        String type = repoConfig.getType();
        log.info("Creating repository of type: {}", type);

        switch (type) {
            case RepositoryConfig.TYPE_JSON:
                return new JsonKnowledgeRepository(
                        repoConfig.getBasePath(),
                        repoConfig.isAutoSave(),
                        repoConfig.getSaveIntervalMs()
                );
            case RepositoryConfig.TYPE_MEMORY:
                return new InMemoryKnowledgeRepository();
            case RepositoryConfig.TYPE_SQL:
                return new SqlKnowledgeRepository(repoConfig.getBasePath());
            default:
                log.warn("Unknown repository type: {}, defaulting to json", type);
                return new JsonKnowledgeRepository(repoConfig.getBasePath());
        }
    }
}
