package net.ooder.scene.skill.vector;

import java.util.List;

/**
 * 嵌入服务接口
 * <p>SE 业务层嵌入服务，区别于 SDK 的 EmbeddingService</p>
 *
 * <p>实现类需要提供文本到向量的转换能力</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneEmbeddingService {
    
    /**
     * 嵌入单个文本
     * @param text 待嵌入文本
     * @return 嵌入向量
     */
    float[] embed(String text);
    
    /**
     * 批量嵌入文本
     * @param texts 待嵌入文本列表
     * @return 嵌入向量列表
     */
    List<float[]> embedBatch(List<String> texts);
    
    /**
     * 获取向量维度
     * @return 向量维度
     */
    int getDimension();
    
    /**
     * 获取嵌入模型名称
     * @return 模型名称
     */
    String getModel();
    
    /**
     * 计算余弦相似度
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度分数 (0-1)
     */
    default float cosineSimilarity(float[] vector1, float[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        float dotProduct = 0;
        float norm1 = 0;
        float norm2 = 0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }
        
        return dotProduct / (float) (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
