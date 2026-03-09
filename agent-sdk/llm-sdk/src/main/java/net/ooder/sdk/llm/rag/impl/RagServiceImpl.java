package net.ooder.sdk.llm.rag.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.rag.RagService;
import net.ooder.sdk.llm.scene.KnowledgeContext;
import net.ooder.sdk.llm.scene.LlmSceneContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG 服务实现
 */
@Slf4j
public class RagServiceImpl implements RagService {

    @Override
    public List<RetrievalResult> retrieve(KnowledgeContext context, String query) {
        /**
         * FIXME: 伪实现 - 需要集成向量存储和 Embedding 服务
         *
         * 预期实现：
         * 1. 调用 EmbeddingService 将 query 向量化
         * 2. 调用 VectorStore 检索相似文档
         * 3. 根据 similarityThreshold 过滤结果
         * 4. 返回 topK 个结果
         */
        log.warn("[STUB] retrieve() not implemented. Query: {}", query);

        // 返回空列表作为占位
        return new ArrayList<>();
    }

    @Override
    public String buildAugmentedPrompt(String originalPrompt, List<RetrievalResult> results) {
        if (results == null || results.isEmpty()) {
            return originalPrompt;
        }

        StringBuilder augmentedPrompt = new StringBuilder();

        // 添加系统指令
        augmentedPrompt.append("基于以下参考信息回答问题:\n\n");

        // 添加检索结果
        augmentedPrompt.append("=== 参考信息 ===\n");
        for (int i = 0; i < results.size(); i++) {
            RetrievalResult result = results.get(i);
            augmentedPrompt.append("[").append(i + 1).append("] ");
            augmentedPrompt.append(result.getContent());
            augmentedPrompt.append("\n\n");
        }

        // 添加原始问题
        augmentedPrompt.append("=== 问题 ===\n");
        augmentedPrompt.append(originalPrompt);

        return augmentedPrompt.toString();
    }

    @Override
    public LlmResponse chatWithRag(LlmSceneContext context, String message) {
        long startTime = System.currentTimeMillis();

        // 1. 检索相关知识
        KnowledgeContext knowledgeContext = context.getKnowledgeContext();
        List<RetrievalResult> retrievalResults = new ArrayList<>();

        if (knowledgeContext != null) {
            retrievalResults = retrieve(knowledgeContext, message);

            // 保存最近检索结果
            if (knowledgeContext.getRecentRetrievals() != null) {
                knowledgeContext.getRecentRetrievals().clear();
                knowledgeContext.getRecentRetrievals().addAll(
                        retrievalResults.stream()
                                .map(r -> KnowledgeContext.RetrievalResult.builder()
                                        .documentId(r.getDocumentId())
                                        .chunkId(r.getChunkId())
                                        .content(r.getContent())
                                        .score(r.getScore())
                                        .metadata(r.getMetadata())
                                        .build())
                                .collect(Collectors.toList())
                );
            }
        }

        // 2. 构建增强 Prompt
        String augmentedPrompt = buildAugmentedPrompt(message, retrievalResults);

        // 3. 调用 LLM
        /**
         * FIXME: 伪实现 - 需要集成 LLM 服务
         *
         * 预期实现：
         * 1. 调用 LlmService.chat() 或 chatWithTools()
         * 2. 传入增强后的 prompt
         * 3. 获取 LLM 响应
         */
        log.warn("[STUB] chatWithRag() LLM integration not implemented");

        LlmResponse response = new LlmResponse();
        response.setContent("[STUB] RAG response placeholder. Retrieved " + retrievalResults.size() + " documents.");
        response.setSources(retrievalResults);
        response.setResponseTime(System.currentTimeMillis() - startTime);

        return response;
    }
}
