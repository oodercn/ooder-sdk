package net.ooder.scene.skill.vector;

import net.ooder.sdk.llm.embedding.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LLM 嵌入服务适配器
 *
 * <p>适配 LLM SDK 的嵌入能力到向量服务接口。</p>
 *
 * <p>架构说明：</p>
 * <ul>
 *   <li>向量化能力由 LLM-SDK 提供（EmbeddingService）</li>
 *   <li>本类作为适配器，将 LLM-SDK 的嵌入能力适配到 SceneEngine 的 SceneEmbeddingService 接口</li>
 *   <li>遵循分层架构原则，知识层不直接依赖 LLM 实现</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3
 */
public class LlmEmbeddingServiceAdapter implements SceneEmbeddingService {
    
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
            EmbeddingService.EmbeddingRequest request = EmbeddingService.EmbeddingRequest.builder()
                    .requestId(generateRequestId())
                    .model(embeddingModel != null ? embeddingModel : getDefaultModel())
                    .text(text)
                    .inputType("document")
                    .build();
            
            EmbeddingService.EmbeddingResponse response = embeddingService.embed(request);
            
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
            EmbeddingService.BatchEmbeddingRequest request = EmbeddingService.BatchEmbeddingRequest.builder()
                    .requestId(generateRequestId())
                    .model(embeddingModel != null ? embeddingModel : getDefaultModel())
                    .texts(texts)
                    .inputType("document")
                    .build();
            
            EmbeddingService.BatchEmbeddingResponse response = embeddingService.embedBatch(request);
            
            List<float[]> results = new ArrayList<>();
            for (EmbeddingService.EmbeddingResult result : response.getResults()) {
                if (result.getError() != null) {
                    log.warn("Embedding failed for index {}: {}", result.getIndex(), result.getError());
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
    
    private String generateRequestId() {
        return "emb-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
    
    private String getDefaultModel() {
        try {
            List<String> models = embeddingService.getSupportedModels();
            if (models != null && !models.isEmpty()) {
                for (String model : models) {
                    if (model.contains("small") || model.contains("ada")) {
                        return model;
                    }
                }
                return models.get(0);
            }
        } catch (Exception e) {
            log.debug("Failed to get supported models, using default");
        }
        return "text-embedding-3-small";
    }
    
    @Override
    public int getDimension() {
        if (embeddingModel != null) {
            if (embeddingModel.contains("large")) {
                return 3072;
            } else if (embeddingModel.contains("bge")) {
                return 1024;
            }
        }
        return 1536;
    }
    
    @Override
    public String getModel() {
        return embeddingModel != null ? embeddingModel : getDefaultModel();
    }
    
    public CompletableFuture<float[]> embedAsync(String text) {
        return CompletableFuture.supplyAsync(() -> embed(text), executorService);
    }
    
    public CompletableFuture<List<float[]>> embedBatchAsync(List<String> texts) {
        return CompletableFuture.supplyAsync(() -> embedBatch(texts), executorService);
    }
    
    public void shutdown() {
        executorService.shutdown();
        log.info("LlmEmbeddingServiceAdapter shutdown");
    }
}
