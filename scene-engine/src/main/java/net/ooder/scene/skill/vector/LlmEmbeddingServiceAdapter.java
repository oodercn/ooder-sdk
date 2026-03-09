package net.ooder.scene.skill.vector;

import net.ooder.sdk.llm.embedding.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * LLM 嵌入服务适配器
 *
 * <p>适配 LLM SDK 的嵌入能力到向量服务接口。</p>
 *
 * <p>架构说明：</p>
 * <ul>
 *   <li>向量化能力由 LLM-SDK 提供（EmbeddingService）</li>
 *   <li>本类作为适配器，将 LLM-SDK 的嵌入能力适配到 SceneEngine 的 EmbeddingService 接口</li>
 *   <li>遵循分层架构原则，知识层不直接依赖 LLM 实现</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3
 */
public class LlmEmbeddingServiceAdapter implements net.ooder.scene.skill.vector.EmbeddingService {
    
    private static final Logger log = LoggerFactory.getLogger(LlmEmbeddingServiceAdapter.class);
    
    private final EmbeddingService embeddingService;
    private final String embeddingModel;
    private final ExecutorService executorService;
    
    public LlmEmbeddingServiceAdapter(EmbeddingService embeddingService) {
        this(embeddingService, null);
    }
    
    public LlmEmbeddingServiceAdapter(EmbeddingService embeddingService, String embeddingModel) {
        this.embeddingService = embeddingService;
        this.embeddingModel = embeddingModel;
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        
        log.debug("Embedding text, length: {}", text.length());
        
        try {
            // 使用 LLM-SDK 的嵌入服务
            EmbeddingService.EmbeddingRequest request = EmbeddingService.EmbeddingRequest.builder()
                    .requestId(generateRequestId())
                    .model(embeddingModel != null ? embeddingModel : getDefaultModel())
                    .text(text)
                    .inputType("document")
                    .build();
            
            EmbeddingService.EmbeddingResponse response = embeddingService.embed(request);
            
            // 将 List<Float> 转换为 float[]
            return convertToArray(response.getEmbedding());
        } catch (Exception e) {
            log.error("Failed to embed text: {}", e.getMessage(), e);
            throw new RuntimeException("Embedding failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }
        
        log.debug("Batch embedding {} texts", texts.size());
        
        try {
            // 使用 LLM-SDK 的批量嵌入服务
            EmbeddingService.BatchEmbeddingRequest request = EmbeddingService.BatchEmbeddingRequest.builder()
                    .requestId(generateRequestId())
                    .model(embeddingModel != null ? embeddingModel : getDefaultModel())
                    .texts(texts)
                    .inputType("document")
                    .build();
            
            EmbeddingService.BatchEmbeddingResponse response = embeddingService.embedBatch(request);
            
            // 转换结果
            List<float[]> results = new ArrayList<>();
            for (EmbeddingService.EmbeddingResult result : response.getResults()) {
                if (result.getError() != null) {
                    log.warn("Embedding failed for index {}: {}", result.getIndex(), result.getError());
                    // 使用零向量作为 fallback
                    results.add(new float[getDimension()]);
                } else {
                    results.add(convertToArray(result.getEmbedding()));
                }
            }
            return results;
        } catch (Exception e) {
            log.error("Failed to batch embed texts: {}", e.getMessage(), e);
            throw new RuntimeException("Batch embedding failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将 List<Float> 转换为 float[]
     */
    private float[] convertToArray(List<Float> list) {
        if (list == null) {
            return new float[0];
        }
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
    
    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return "emb-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
    
    /**
     * 获取默认模型
     */
    private String getDefaultModel() {
        // 获取支持的模型列表
        List<String> models = embeddingService.getSupportedModels();
        if (models != null && !models.isEmpty()) {
            // 优先使用较小的模型
            for (String model : models) {
                if (model.contains("small") || model.contains("ada")) {
                    return model;
                }
            }
            return models.get(0);
        }
        return "text-embedding-3-small";
    }
    
    @Override
    public int getDimension() {
        // 根据模型返回维度
        if (embeddingModel != null) {
            if (embeddingModel.contains("large")) {
                return 3072;
            } else if (embeddingModel.contains("bge")) {
                return 1024;
            }
        }
        return 1536; // 默认维度
    }
    
    @Override
    public String getModel() {
        return embeddingModel != null ? embeddingModel : getDefaultModel();
    }
    
    /**
     * 异步嵌入
     */
    public CompletableFuture<float[]> embedAsync(String text) {
        return CompletableFuture.supplyAsync(() -> embed(text), executorService);
    }
    
    /**
     * 异步批量嵌入
     */
    public CompletableFuture<List<float[]>> embedBatchAsync(List<String> texts) {
        return CompletableFuture.supplyAsync(() -> embedBatch(texts), executorService);
    }
    
    /**
     * 关闭服务
     */
    public void shutdown() {
        executorService.shutdown();
        log.info("LlmEmbeddingServiceAdapter shutdown");
    }
}
