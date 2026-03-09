package net.ooder.sdk.llm.rag;

import net.ooder.sdk.llm.scene.KnowledgeContext;
import net.ooder.sdk.llm.scene.LlmSceneContext;

import java.util.List;

/**
 * RAG 服务接口
 * 支持知识检索和 Prompt 增强
 */
public interface RagService {

    /**
     * 检索相关知识
     *
     * @param context 知识上下文
     * @param query   查询
     * @return 检索结果列表
     */
    List<RetrievalResult> retrieve(KnowledgeContext context, String query);

    /**
     * 构建增强 Prompt
     *
     * @param originalPrompt 原始 Prompt
     * @param results        检索结果
     * @return 增强后的 Prompt
     */
    String buildAugmentedPrompt(String originalPrompt, List<RetrievalResult> results);

    /**
     * RAG 对话
     *
     * @param context 场景上下文
     * @param message 消息
     * @return RAG 响应
     */
    RagResponse chatWithRag(LlmSceneContext context, String message);

    /**
     * 检索结果
     */
    class RetrievalResult {
        private String documentId;
        private String chunkId;
        private String content;
        private double score;
        private java.util.Map<String, Object> metadata;

        // Getters and Setters
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getChunkId() { return chunkId; }
        public void setChunkId(String chunkId) { this.chunkId = chunkId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }

        public java.util.Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(java.util.Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * RAG 响应
     */
    class RagResponse {
        private String content;
        private List<RetrievalResult> sources;
        private long responseTime;

        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public List<RetrievalResult> getSources() { return sources; }
        public void setSources(List<RetrievalResult> sources) { this.sources = sources; }

        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
    }
}
