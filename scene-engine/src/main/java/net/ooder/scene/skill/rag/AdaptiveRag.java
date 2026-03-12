package net.ooder.scene.skill.rag;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 自适应 RAG 检索器
 *
 * <p>根据查询类型自动选择最优检索策略，提升 RAG 效果</p>
 *
 * <h3>查询分类逻辑：</h3>
 * <ul>
 *   <li>事实查询：包含"是什么"、"谁是"、"什么时候"等关键词</li>
 *   <li>摘要查询：包含"总结"、"概括"、"概述"等关键词</li>
 *   <li>比较查询：包含"区别"、"比较"、"对比"等关键词</li>
 *   <li>创意查询：包含"创意"、"想法"、"建议"等关键词</li>
 *   <li>推理查询：包含"为什么"、"原因"、"分析"等关键词</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
@Component
public class AdaptiveRag {

    private static final Logger logger = Logger.getLogger(AdaptiveRag.class.getName());

    private final RagPipeline ragPipeline;

    public AdaptiveRag(RagPipeline ragPipeline) {
        this.ragPipeline = ragPipeline;
    }

    /**
     * 自适应检索
     *
     * <p>根据查询类型自动选择最优检索策略，提升 RAG 效果</p>
     *
     * @param query 查询文本
     * @param baseContext RAG 上下文
     * @return RAG 检索结果
     */
    public RagResult adaptiveRetrieve(String query, RagContext baseContext) {
        logger.info("[AdaptiveRag] Starting adaptive retrieve for query: " + query);

        // 1. 查询分类（基于关键词匹配）
        QueryType queryType = classifyQuery(query);
        logger.fine("[AdaptiveRag] Query classified as: " + queryType);

        // 2. 根据查询类型选择策略
        RetrievalStrategy strategy = selectStrategy(queryType);
        logger.fine("[AdaptiveRag] Selected strategy: " + strategy);

        // 3. 应用策略参数到上下文
        RagContext optimizedContext = applyStrategy(baseContext, strategy);

        // 4. 执行检索
        RagResult result = ragPipeline.retrieve(optimizedContext);

        // 5. 后处理（根据策略进行结果优化）
        result = postProcess(result, strategy);

        return result;
    }

    /**
     * 查询分类
     *
     * @param query 查询文本
     * @return 查询类型
     */
    private QueryType classifyQuery(String query) {
        String lowerQuery = query.toLowerCase();

        // 事实查询
        if (containsAny(lowerQuery, "是什么", "谁是", "什么时候", "在哪里", "多少", "什么是")) {
            return QueryType.FACTUAL;
        }

        // 摘要查询
        if (containsAny(lowerQuery, "总结", "概括", "概述", "摘要", "归纳")) {
            return QueryType.SUMMARY;
        }

        // 比较查询
        if (containsAny(lowerQuery, "区别", "比较", "对比", "差异", "不同")) {
            return QueryType.COMPARISON;
        }

        // 创意查询
        if (containsAny(lowerQuery, "创意", "想法", "建议", "方案", "设计")) {
            return QueryType.CREATIVE;
        }

        // 推理查询
        if (containsAny(lowerQuery, "为什么", "原因", "分析", "如何", "怎么")) {
            return QueryType.REASONING;
        }

        return QueryType.GENERAL;
    }

    /**
     * 选择检索策略
     *
     * @param queryType 查询类型
     * @return 检索策略
     */
    private RetrievalStrategy selectStrategy(QueryType queryType) {
        switch (queryType) {
            case FACTUAL:
                return RetrievalStrategy.HIGH_PRECISION;
            case SUMMARY:
                return RetrievalStrategy.DIVERSE;
            case COMPARISON:
                return RetrievalStrategy.MULTI_SOURCE;
            case CREATIVE:
                return RetrievalStrategy.DIVERSE;
            case REASONING:
                return RetrievalStrategy.DEEP;
            default:
                return RetrievalStrategy.BALANCED;
        }
    }

    /**
     * 应用策略到上下文
     *
     * @param baseContext 基础上下文
     * @param strategy 检索策略
     * @return 优化后的上下文
     */
    private RagContext applyStrategy(RagContext baseContext, RetrievalStrategy strategy) {
        RagContext optimized = new RagContext();
        optimized.setQuery(baseContext.getQuery());
        optimized.setKbId(baseContext.getKbId());
        optimized.setTopK(strategy.getTopK());
        optimized.setThreshold(strategy.getThreshold());
        optimized.setRetrievalType(baseContext.getRetrievalType());
        optimized.setFilters(baseContext.getFilters());

        // 在 params 中存储重排序标志
        Map<String, Object> params = new HashMap<>();
        if (baseContext.getParams() != null) {
            params.putAll(baseContext.getParams());
        }
        params.put("rerankEnabled", strategy.isRerankEnabled());
        optimized.setParams(params);

        return optimized;
    }

    /**
     * 后处理：根据策略优化结果
     *
     * @param result 检索结果
     * @param strategy 检索策略
     * @return 优化后的结果
     */
    private RagResult postProcess(RagResult result, RetrievalStrategy strategy) {
        switch (strategy) {
            case HIGH_PRECISION:
                // 高精确度：严格过滤低分结果（>=0.8）
                result.setChunks(filterByScore(result.getChunks(), 0.8f));
                break;
            case DIVERSE:
                // 多样化：确保来源多样性，避免单一来源垄断
                result.setChunks(ensureDiversity(result.getChunks()));
                break;
            case MULTI_SOURCE:
                // 多源：按来源分组，确保不同来源的结果都有展示
                result.setChunks(groupBySource(result.getChunks()));
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 按分数过滤
     */
    private List<RagResult.RetrievedChunk> filterByScore(List<RagResult.RetrievedChunk> chunks, float minScore) {
        return chunks.stream()
                .filter(chunk -> chunk.getScore() >= minScore)
                .collect(Collectors.toList());
    }

    /**
     * 确保多样性
     */
    private List<RagResult.RetrievedChunk> ensureDiversity(List<RagResult.RetrievedChunk> chunks) {
        // 简单实现：限制同一来源的最大数量
        Map<String, Integer> sourceCount = new HashMap<>();
        List<RagResult.RetrievedChunk> result = new ArrayList<>();

        for (RagResult.RetrievedChunk chunk : chunks) {
            String source = chunk.getDocId();
            int count = sourceCount.getOrDefault(source, 0);
            if (count < 3) {
                sourceCount.put(source, count + 1);
                result.add(chunk);
            }
        }

        return result;
    }

    /**
     * 按来源分组
     */
    private List<RagResult.RetrievedChunk> groupBySource(List<RagResult.RetrievedChunk> chunks) {
        // 按来源分组后重新排序，确保不同来源交错出现
        Map<String, List<RagResult.RetrievedChunk>> grouped = chunks.stream()
                .collect(Collectors.groupingBy(RagResult.RetrievedChunk::getDocId));

        List<RagResult.RetrievedChunk> result = new ArrayList<>();
        List<String> sources = new ArrayList<>(grouped.keySet());
        int maxIndex = grouped.values().stream().mapToInt(List::size).max().orElse(0);

        for (int i = 0; i < maxIndex; i++) {
            for (String source : sources) {
                List<RagResult.RetrievedChunk> group = grouped.get(source);
                if (i < group.size()) {
                    result.add(group.get(i));
                }
            }
        }

        return result;
    }

    /**
     * 检查是否包含任意关键词
     */
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询类型枚举
     */
    public enum QueryType {
        FACTUAL,      // 事实查询
        SUMMARY,      // 摘要查询
        COMPARISON,   // 比较查询
        CREATIVE,     // 创意查询
        REASONING,    // 推理查询
        GENERAL       // 通用查询
    }

    /**
     * 检索策略枚举
     *
     * <p>不同查询类型对应不同策略参数</p>
     */
    public enum RetrievalStrategy {
        HIGH_PRECISION(3, 0.85f, true),   // 高精确度：topK小，阈值高，启用重排序
        BALANCED(5, 0.75f, true),          // 平衡：适中参数
        MULTI_SOURCE(10, 0.7f, true),      // 多源：topK大，收集更多来源
        DIVERSE(8, 0.65f, true),           // 多样化：较低阈值，确保多样性
        DEEP(10, 0.7f, true),              // 深度：大量检索，深度推理
        DEFAULT(5, 0.75f, false);          // 默认：标准参数

        private final int topK;            // 返回结果数量
        private final float threshold;     // 相似度阈值
        private final boolean rerankEnabled; // 是否启用重排序

        RetrievalStrategy(int topK, float threshold, boolean rerankEnabled) {
            this.topK = topK;
            this.threshold = threshold;
            this.rerankEnabled = rerankEnabled;
        }

        public int getTopK() {
            return topK;
        }

        public float getThreshold() {
            return threshold;
        }

        public boolean isRerankEnabled() {
            return rerankEnabled;
        }
    }
}
