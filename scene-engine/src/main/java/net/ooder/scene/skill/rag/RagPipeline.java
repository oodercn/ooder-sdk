package net.ooder.scene.skill.rag;

import net.ooder.scene.skill.knowledge.*;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.SearchResult;
import net.ooder.scene.skill.vector.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * RAG Pipeline 实现
 *
 * <p>检索增强生成管道</p>
 * <p>架构层次：知识增强层 - RAG Pipeline</p>
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>Query - 用户查询</li>
 *   <li>Embed - 查询向量化</li>
 *   <li>Retrieve - 向量检索</li>
 *   <li>Rerank - 重排序（可选）</li>
 *   <li>Augment - 提示增强</li>
 *   <li>Generate - 生成回答</li>
 * </ol>
 *
 * @author ooder
 * @since 2.3
 */
public class RagPipeline implements RagApi {

    private static final Logger log = LoggerFactory.getLogger(RagPipeline.class);

    private final KnowledgeBaseService kbService;
    private final SceneEmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final LlmGenerator generator;

    // 知识库配置缓存
    private final Map<String, KnowledgeBaseConfig> knowledgeBaseConfigs = new HashMap<>();

    public RagPipeline(KnowledgeBaseService kbService,
                       SceneEmbeddingService embeddingService,
                       VectorStore vectorStore,
                       LlmGenerator generator) {
        this.kbService = kbService;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.generator = generator;
    }

    @Override
    public RagResult retrieve(RagContext context) {
        log.info("RAG retrieve: kbId={}, query={}", context.getKbId(), context.getQuery());

        // 1. 检查知识库是否存在
        if (!kbService.exists(context.getKbId())) {
            throw new IllegalArgumentException("Knowledge base not found: " + context.getKbId());
        }

        // 2. 向量化查询
        float[] queryVector = embeddingService.embed(context.getQuery());

        // 3. 构建过滤条件
        Map<String, Object> filters = new HashMap<>();
        filters.put("kbId", context.getKbId());
        if (context.getFilters() != null) {
            filters.putAll(context.getFilters());
        }

        // 4. 向量检索
        List<SearchResult> vectorResults = vectorStore.search(
            queryVector,
            context.getTopK(),
            filters
        );

        // 5. 构建结果
        List<RagResult.RetrievedChunk> chunks = new ArrayList<>();
        for (SearchResult vr : vectorResults) {
            if (vr.getScore() < context.getThreshold()) {
                continue;
            }

            String docId = (String) vr.getMetadata().get("docId");
            Document doc = kbService.getDocument(context.getKbId(), docId);

            if (doc != null) {
                RagResult.RetrievedChunk chunk = new RagResult.RetrievedChunk();
                chunk.setChunkId((String) vr.getMetadata().get("chunkId"));
                chunk.setDocId(docId);
                chunk.setDocTitle(doc.getTitle());
                chunk.setContent(vr.getContent());
                chunk.setScore(vr.getScore());
                chunk.setMetadata(vr.getMetadata());
                chunks.add(chunk);
            }
        }

        RagResult result = new RagResult();
        result.setQuery(context.getQuery());
        result.setChunks(chunks);
        result.setTotalFound(chunks.size());

        log.info("RAG retrieve completed: found {} chunks", chunks.size());
        return result;
    }

    @Override
    public String augmentPrompt(String query, RagResult result) {
        log.debug("Augmenting prompt with {} chunks", result.getChunks().size());

        StringBuilder prompt = new StringBuilder();

        // 添加系统提示
        prompt.append("请根据以下参考资料回答问题。如果参考资料中没有相关信息，请说明。\n\n");

        // 添加检索到的内容
        prompt.append("参考资料：\n");
        prompt.append("---\n");

        for (int i = 0; i < result.getChunks().size(); i++) {
            RagResult.RetrievedChunk chunk = result.getChunks().get(i);
            prompt.append("[").append(i + 1).append("] ");
            prompt.append("来源：").append(chunk.getDocTitle()).append("\n");
            prompt.append(chunk.getContent()).append("\n");
            prompt.append("---\n");
        }

        // 添加用户问题
        prompt.append("\n问题：").append(query).append("\n");
        prompt.append("\n请回答：");

        return prompt.toString();
    }

    @Override
    public String generate(String query, RagContext context) {
        log.info("RAG generate: kbId={}, query={}", context.getKbId(), query);

        // 1. 检索相关知识
        RagResult result = retrieve(context);

        if (result.getChunks().isEmpty()) {
            return "抱歉，在知识库中没有找到相关信息。";
        }

        // 2. 增强提示
        String augmentedPrompt = augmentPrompt(query, result);

        // 3. 生成回答
        String answer = generator.generate(augmentedPrompt);

        log.info("RAG generate completed");
        return answer;
    }

    @Override
    public RagResult hybridRetrieve(RagContext context, List<String> kbIds) {
        log.info("RAG hybrid retrieve: kbIds={}, query={}", kbIds, context.getQuery());

        List<RagResult.RetrievedChunk> allChunks = new ArrayList<>();

        // 从多个知识库检索
        for (String kbId : kbIds) {
            RagContext singleContext = new RagContext(context.getQuery(), kbId);
            singleContext.setTopK(context.getTopK() / kbIds.size());
            singleContext.setThreshold(context.getThreshold());

            try {
                RagResult result = retrieve(singleContext);
                allChunks.addAll(result.getChunks());
            } catch (Exception e) {
                log.warn("Failed to retrieve from kb: {}", kbId, e);
            }
        }

        // 按分数排序
        allChunks.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));

        // 截取 topK
        if (allChunks.size() > context.getTopK()) {
            allChunks = allChunks.subList(0, context.getTopK());
        }

        RagResult result = new RagResult();
        result.setQuery(context.getQuery());
        result.setChunks(allChunks);
        result.setTotalFound(allChunks.size());

        return result;
    }

    @Override
    public void registerKnowledgeBase(String kbId, KnowledgeBaseConfig config) {
        log.info("Registering knowledge base: {}", kbId);
        knowledgeBaseConfigs.put(kbId, config);
    }

    @Override
    public void unregisterKnowledgeBase(String kbId) {
        log.info("Unregistering knowledge base: {}", kbId);
        knowledgeBaseConfigs.remove(kbId);
    }

    // ========== 配置方法 ==========

    /**
     * 设置检索模板
     */
    public void setPromptTemplate(String template) {
        // 可以自定义提示模板
    }
}
