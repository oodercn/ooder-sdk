package net.ooder.sdk.llm.scene;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识库上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeContext {

    /**
     * 知识库ID
     */
    private String knowledgeBaseId;

    /**
     * 知识库名称
     */
    private String knowledgeBaseName;

    /**
     * 关联的知识库ID列表
     */
    @Builder.Default
    private List<String> linkedKnowledgeBaseIds = new ArrayList<>();

    /**
     * 检索配置
     */
    private RetrievalConfig retrievalConfig;

    /**
     * 最近检索结果
     */
    @Builder.Default
    private List<RetrievalResult> recentRetrievals = new ArrayList<>();

    /**
     * 知识库元数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 检索配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetrievalConfig {
        private int topK = 5;
        private double similarityThreshold = 0.7;
        private int maxTokens = 2000;
        private boolean enableReranking = false;
    }

    /**
     * 检索结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetrievalResult {
        private String documentId;
        private String chunkId;
        private String content;
        private double score;
        private Map<String, Object> metadata;
    }
}
