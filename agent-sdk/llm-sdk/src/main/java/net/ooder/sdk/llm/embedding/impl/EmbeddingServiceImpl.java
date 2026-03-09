package net.ooder.sdk.llm.embedding.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.embedding.EmbeddingService;

import java.util.*;

/**
 * 文本向量化服务实现
 */
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {

    private final List<String> supportedModels = Arrays.asList(
            "text-embedding-3-small",
            "text-embedding-3-large",
            "text-embedding-ada-002"
    );

    @Override
    public EmbeddingResponse embed(EmbeddingRequest request) {
        long startTime = System.currentTimeMillis();
        String requestId = request.getRequestId() != null ? request.getRequestId() : generateRequestId();

        log.info("[{}] Embedding request started, model: {}", requestId, request.getModel());

        /**
         * FIXME: 伪实现 - 需要集成真实 Embedding Provider
         *
         * 预期实现：
         * 1. 根据 request.getModel() 选择合适的 Embedding Provider
         * 2. 调用 Provider 的 embedding API
         * 3. 返回向量结果
         */
        log.warn("[STUB] embed() not fully implemented. RequestId: {}", requestId);

        // 模拟向量 (1536维)
        List<Float> mockEmbedding = generateMockEmbedding(1536);

        EmbeddingResponse response = EmbeddingResponse.builder()
                .responseId(generateRequestId())
                .model(request.getModel() != null ? request.getModel() : "text-embedding-3-small")
                .embedding(mockEmbedding)
                .dimension(1536)
                .tokenCount(request.getText() != null ? request.getText().length() / 4 : 0)
                .latency(System.currentTimeMillis() - startTime)
                .build();

        log.info("[{}] Embedding request completed in {}ms", requestId, response.getLatency());
        return response;
    }

    @Override
    public BatchEmbeddingResponse embedBatch(BatchEmbeddingRequest request) {
        long startTime = System.currentTimeMillis();
        String requestId = request.getRequestId() != null ? request.getRequestId() : generateRequestId();

        log.info("[{}] Batch embedding request started, model: {}, texts count: {}",
                requestId, request.getModel(), request.getTexts() != null ? request.getTexts().size() : 0);

        /**
         * FIXME: 伪实现 - 需要集成真实 Embedding Provider
         */
        log.warn("[STUB] embedBatch() not fully implemented. RequestId: {}", requestId);

        List<EmbeddingResult> results = new ArrayList<>();
        int totalTokenCount = 0;

        if (request.getTexts() != null) {
            for (int i = 0; i < request.getTexts().size(); i++) {
                String text = request.getTexts().get(i);
                List<Float> mockEmbedding = generateMockEmbedding(1536);
                int tokenCount = text.length() / 4;
                totalTokenCount += tokenCount;

                results.add(EmbeddingResult.builder()
                        .index(i)
                        .embedding(mockEmbedding)
                        .tokenCount(tokenCount)
                        .build());
            }
        }

        BatchEmbeddingResponse response = BatchEmbeddingResponse.builder()
                .responseId(generateRequestId())
                .model(request.getModel() != null ? request.getModel() : "text-embedding-3-small")
                .results(results)
                .totalTokenCount(totalTokenCount)
                .latency(System.currentTimeMillis() - startTime)
                .build();

        log.info("[{}] Batch embedding request completed in {}ms", requestId, response.getLatency());
        return response;
    }

    @Override
    public double calculateSimilarity(List<Float> embedding1, List<Float> embedding2) {
        if (embedding1 == null || embedding2 == null || embedding1.size() != embedding2.size()) {
            return 0.0;
        }

        // 计算余弦相似度
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < embedding1.size(); i++) {
            dotProduct += embedding1.get(i) * embedding2.get(i);
            norm1 += embedding1.get(i) * embedding1.get(i);
            norm2 += embedding2.get(i) * embedding2.get(i);
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    @Override
    public List<String> getSupportedModels() {
        return new ArrayList<>(supportedModels);
    }

    /**
     * 生成模拟向量
     */
    private List<Float> generateMockEmbedding(int dimension) {
        List<Float> embedding = new ArrayList<>(dimension);
        Random random = new Random();
        for (int i = 0; i < dimension; i++) {
            embedding.add(random.nextFloat() * 2 - 1); // -1 到 1 之间的随机数
        }
        return embedding;
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return "emb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
