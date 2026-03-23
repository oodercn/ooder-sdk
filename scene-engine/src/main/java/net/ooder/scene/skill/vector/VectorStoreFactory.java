package net.ooder.scene.skill.vector;

import net.ooder.scene.skill.vector.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VectorStoreFactory {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreFactory.class);

    public static VectorStore create(VectorStoreConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("VectorStoreConfig cannot be null");
        }

        String type = config.getType();
        log.info("Creating VectorStore of type: {}", type);

        VectorStore store;

        switch (type.toLowerCase()) {
            case VectorStoreConfig.TYPE_MEMORY:
                store = new InMemoryVectorStore(config.getDimension());
                break;

            case VectorStoreConfig.TYPE_JSON:
                String basePath = config.getProperty("basePath", "./data/vectors");
                boolean persist = Boolean.parseBoolean(config.getProperty("persist", "true"));
                int flushInterval = config.getProperty("flushInterval", 5000);
                store = new JsonVectorStore(basePath, config.getDimension(), persist, flushInterval);
                break;

            case VectorStoreConfig.TYPE_MILVUS:
                store = new MilvusVectorStore(config);
                break;

            case VectorStoreConfig.TYPE_CHROMA:
                store = new ChromaVectorStore(config);
                break;

            case VectorStoreConfig.TYPE_PGVECTOR:
                store = new PgVectorStore(config);
                break;

            default:
                log.warn("Unknown vector store type: {}, using InMemoryVectorStore", type);
                store = new InMemoryVectorStore(config.getDimension());
        }

        if (store instanceof AbstractVectorStore) {
            ((AbstractVectorStore) store).initialize();
        }

        return store;
    }

    public static VectorStore createInMemory(int dimension) {
        return create(VectorStoreConfig.memory(dimension));
    }

    public static VectorStore createJson(int dimension, String basePath) {
        return create(VectorStoreConfig.json(dimension, basePath));
    }

    public static VectorStore createMilvus(int dimension, String host, int port, String database, String collection) {
        return create(VectorStoreConfig.milvus(dimension, host, port, database, collection));
    }

    public static VectorStore createChroma(int dimension, String host, int port, String collection) {
        return create(VectorStoreConfig.chroma(dimension, host, port, collection));
    }

    public static VectorStore createPgVector(int dimension, String url, String table) {
        return create(VectorStoreConfig.pgvector(dimension, url, table));
    }

    public static void shutdown(VectorStore store) {
        if (store instanceof AbstractVectorStore) {
            ((AbstractVectorStore) store).shutdown();
        }
        log.info("VectorStore shutdown completed");
    }
}
