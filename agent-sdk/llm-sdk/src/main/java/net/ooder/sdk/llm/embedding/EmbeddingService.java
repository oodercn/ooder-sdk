package net.ooder.sdk.llm.embedding;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 文本向量化服务接口
 * 由 LLM-SDK 实现，Engine 调用
 */
public interface EmbeddingService {

    /**
     * 单文本向量化
     *
     * @param request 向量化请求
     * @return 向量化响应
     */
    EmbeddingResponse embed(EmbeddingRequest request);

    /**
     * 批量文本向量化
     *
     * @param request 批量向量化请求
     * @return 向量化响应列表
     */
    BatchEmbeddingResponse embedBatch(BatchEmbeddingRequest request);

    /**
     * 计算相似度
     *
     * @param embedding1 向量1
     * @param embedding2 向量2
     * @return 相似度 (0-1)
     */
    double calculateSimilarity(List<Float> embedding1, List<Float> embedding2);

    /**
     * 获取支持的模型列表
     *
     * @return 模型列表
     */
    List<String> getSupportedModels();

    /**
     * 向量化请求
     */
    @Data
    @Builder
    class EmbeddingRequest {
        private String requestId;       // 请求ID
        private String model;           // 模型名称 (text-embedding-3-small, etc.)
        private String text;            // 文本内容
        private String inputType;       // 输入类型 (query/document)
    }

    /**
     * 批量向量化请求
     */
    @Data
    @Builder
    class BatchEmbeddingRequest {
        private String requestId;       // 请求ID
        private String model;           // 模型名称
        private List<String> texts;     // 文本列表
        private String inputType;       // 输入类型
    }

    /**
     * 向量化响应
     */
    @Data
    @Builder
    class EmbeddingResponse {
        private String responseId;      // 响应ID
        private String model;           // 实际使用的模型
        private List<Float> embedding;  // 向量 (1536维或其他)
        private int dimension;          // 向量维度
        private int tokenCount;         // Token 使用量
        private long latency;           // 延迟(ms)
    }

    /**
     * 批量向量化响应
     */
    @Data
    @Builder
    class BatchEmbeddingResponse {
        private String responseId;              // 响应ID
        private String model;                   // 实际使用的模型
        private List<EmbeddingResult> results;  // 结果列表
        private int totalTokenCount;            // 总 Token 使用量
        private long latency;                   // 延迟(ms)
    }

    /**
     * 向量化结果
     */
    @Data
    @Builder
    class EmbeddingResult {
        private int index;              // 索引
        private List<Float> embedding;  // 向量
        private int tokenCount;         // Token 使用量
        private String error;           // 错误信息（如果有）
    }
}
