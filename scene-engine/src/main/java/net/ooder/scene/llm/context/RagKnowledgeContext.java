package net.ooder.scene.llm.context;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.KnowledgeSearchRequest;
import net.ooder.scene.skill.knowledge.KnowledgeSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG 增强的知识上下文
 * <p>扩展 KnowledgeContext，添加知识库检索功能</p>
 *
 * <p>功能：</p>
 * <ul>
 *   <li>自动从知识库检索相关内容</li>
 *   <li>支持多知识库检索</li>
 *   <li>支持相似度过滤</li>
 *   <li>支持检索结果缓存</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class RagKnowledgeContext extends KnowledgeContext {

    private static final Logger log = LoggerFactory.getLogger(RagKnowledgeContext.class);

    private transient KnowledgeBaseService knowledgeBaseService;
    private String lastQuery;
    private List<KnowledgeSearchResult> lastSearchResults;

    public RagKnowledgeContext() {
        super();
    }

    public RagKnowledgeContext(String knowledgeBaseId) {
        super(knowledgeBaseId);
    }

    public RagKnowledgeContext(String knowledgeBaseId, KnowledgeBaseService knowledgeBaseService) {
        super(knowledgeBaseId);
        this.knowledgeBaseService = knowledgeBaseService;
    }

    /**
     * 执行 RAG 检索
     *
     * @param query 查询语句
     * @return 检索到的知识块列表
     */
    public List<KnowledgeChunk> retrieve(String query) {
        if (knowledgeBaseService == null) {
            log.warn("KnowledgeBaseService not available, cannot perform RAG retrieval");
            return new ArrayList<>();
        }

        this.lastQuery = query;

        List<KnowledgeChunk> chunks = new ArrayList<>();

        // 从主知识库检索
        String mainKbId = getKnowledgeBaseId();
        if (mainKbId != null && !mainKbId.isEmpty()) {
            chunks.addAll(retrieveFromKnowledgeBase(mainKbId, query));
        }

        // 从可访问的知识库检索
        for (String kbId : getAccessibleKnowledgeBases()) {
            if (!kbId.equals(mainKbId)) {
                chunks.addAll(retrieveFromKnowledgeBase(kbId, query));
            }
        }

        // 按相似度排序
        chunks.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));

        // 限制结果数量
        int maxResults = getMaxResults();
        if (chunks.size() > maxResults) {
            chunks = chunks.subList(0, maxResults);
        }

        // 更新加载的知识块
        setLoadedChunks(chunks);

        log.debug("RAG retrieval completed: {} chunks for query: {}", chunks.size(), query);

        return chunks;
    }

    /**
     * 从指定知识库检索
     *
     * @param kbId 知识库ID
     * @param query 查询语句
     * @return 知识块列表
     */
    private List<KnowledgeChunk> retrieveFromKnowledgeBase(String kbId, String query) {
        List<KnowledgeChunk> chunks = new ArrayList<>();

        try {
            KnowledgeSearchRequest request = new KnowledgeSearchRequest();
            request.setQuery(query);
            request.setTopK(getMaxResults());
            request.setThreshold(getSimilarityThreshold());

            List<KnowledgeSearchResult> results = knowledgeBaseService.search(kbId, request);

            for (KnowledgeSearchResult result : results) {
                KnowledgeChunk chunk = new KnowledgeChunk();
                chunk.setId(result.getDocId());
                chunk.setContent(result.getContent());
                chunk.setSource(result.getTitle());
                chunk.setScore(result.getScore());
                chunk.getMetadata().put("kbId", kbId);
                chunk.getMetadata().put("chunkId", result.getChunkId());

                chunks.add(chunk);
            }

            // 保存最后检索结果
            if (lastSearchResults == null) {
                lastSearchResults = new ArrayList<>();
            }
            lastSearchResults.addAll(results);

        } catch (Exception e) {
            log.error("Failed to retrieve from knowledge base {}: {}", kbId, e.getMessage(), e);
        }

        return chunks;
    }

    /**
     * 构建 RAG 增强的提示词
     *
     * @param query 用户查询
     * @return 包含检索结果的提示词
     */
    public String buildRagPrompt(String query) {
        // 执行检索
        List<KnowledgeChunk> chunks = retrieve(query);

        StringBuilder sb = new StringBuilder();

        // 添加检索结果
        if (!chunks.isEmpty()) {
            sb.append("## 参考资料\n\n");
            for (int i = 0; i < chunks.size(); i++) {
                KnowledgeChunk chunk = chunks.get(i);
                sb.append("[").append(i + 1).append("] ")
                  .append(chunk.getSource())
                  .append(" (相关度: ").append(String.format("%.2f", chunk.getScore())).append(")\n")
                  .append(chunk.getContent())
                  .append("\n\n");
            }
        }

        // 添加用户查询
        sb.append("## 用户问题\n\n");
        sb.append(query).append("\n\n");

        // 添加回答要求
        sb.append("## 回答要求\n\n");
        sb.append("请基于以上参考资料回答用户问题。如果参考资料不足以回答问题，请明确说明。\n");

        return sb.toString();
    }

    /**
     * 获取最后检索的查询
     */
    public String getLastQuery() {
        return lastQuery;
    }

    /**
     * 获取最后检索结果
     */
    public List<KnowledgeSearchResult> getLastSearchResults() {
        return lastSearchResults;
    }

    /**
     * 设置知识库服务
     */
    public void setKnowledgeBaseService(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    /**
     * 获取知识库服务
     */
    public KnowledgeBaseService getKnowledgeBaseService() {
        return knowledgeBaseService;
    }
}
