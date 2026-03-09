package net.ooder.scene.skill.vector;

import net.ooder.llm.api.LlmService;
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
 *   <li>向量化能力由 LLM 层提供（LlmService.embed()）</li>
 *   <li>本类作为适配器，将 LLM 的嵌入能力适配到 EmbeddingService 接口</li>
 *   <li>遵循分层架构原则，知识层不直接依赖 LLM 实现</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3
 */
public class LlmEmbeddingServiceAdapter implements EmbeddingService {
    
    private static final Logger log = LoggerFactory.getLogger(LlmEmbeddingServiceAdapter.class);
    
    private final LlmService llmService;
    private final String embeddingModel;
    private final ExecutorService executorService;
    
    public LlmEmbeddingServiceAdapter(LlmService llmService) {
        this(llmService, null);
    }
    
    public LlmEmbeddingServiceAdapter(LlmService llmService, String embeddingModel) {
        this.llmService = llmService;
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
            // 使用 LLM 服务的嵌入能力
            return llmService.embed(text);
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
            // 使用 LLM 服务的批量嵌入能力
            return llmService.embedBatch(texts);
        } catch (Exception e) {
            log.error("Failed to batch embed texts: {}", e.getMessage(), e);
            throw new RuntimeException("Batch embedding failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int getDimension() {
        // 根据模型返回维度
        // 常见模型的维度：
        // - text-embedding-ada-002: 1536
        // - text-embedding-3-small: 1536
        // - text-embedding-3-large: 3072
        // - bge-large-zh: 1024
        
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
        return embeddingModel != null ? embeddingModel : "default";
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
