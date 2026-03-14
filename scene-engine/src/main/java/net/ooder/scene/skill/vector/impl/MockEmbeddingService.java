package net.ooder.scene.skill.vector.impl;

import net.ooder.scene.skill.vector.SceneEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mock 嵌入服务实现
 *
 * <p>提供基于随机向量的 Mock 嵌入服务，用于开发测试环境。</p>
 *
 * <p>架构层级：微层（降级方案）</p>
 *
 * <p>注意：此实现仅用于开发测试，生产环境应使用 LlmEmbeddingServiceAdapter。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class MockEmbeddingService implements SceneEmbeddingService {
    
    private static final Logger log = LoggerFactory.getLogger(MockEmbeddingService.class);
    
    private final int dimension;
    private final Random random;
    
    public MockEmbeddingService() {
        this(1536);
    }
    
    public MockEmbeddingService(int dimension) {
        this.dimension = dimension;
        this.random = new Random(42);
        log.info("MockEmbeddingService initialized with dimension: {} (FOR TESTING ONLY)", dimension);
    }
    
    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            return new float[dimension];
        }
        
        float[] embedding = new float[dimension];
        
        int hash = text.hashCode();
        random.setSeed(hash);
        
        for (int i = 0; i < dimension; i++) {
            embedding[i] = (float) random.nextGaussian();
        }
        
        normalize(embedding);
        
        return embedding;
    }
    
    @Override
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> embeddings = new ArrayList<>();
        for (String text : texts) {
            embeddings.add(embed(text));
        }
        return embeddings;
    }
    
    @Override
    public int getDimension() {
        return dimension;
    }
    
    @Override
    public String getModel() {
        return "mock-embedding";
    }
    
    private void normalize(float[] vector) {
        float norm = 0.0f;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }
}
