package net.ooder.skills.api.rag;

import java.util.List;
import java.util.Map;

/**
 * 自适应 RAG 接口
 *
 * <p>根据查询类型自动选择检索策略</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface AdaptiveRag {

    /**
     * 自适应检索
     *
     * <p>根据查询类型自动选择检索策略：</p>
     * <ul>
     *   <li>事实查询: 高 topK, 严格阈值</li>
     *   <li>摘要查询: 中 topK, 宽松阈值</li>
     *   <li>创意查询: 低 topK, 混合策略</li>
     * </ul>
     *
     * @param query       查询内容
     * @param baseContext 基础上下文
     * @return 检索结果
     */
    RagResult adaptiveRetrieve(String query, RagContext baseContext);

    /**
     * 分类查询类型
     *
     * @param query 查询内容
     * @return 查询类型
     */
    QueryType classifyQuery(String query);

    /**
     * 使用指定策略检索
     *
     * @param query    查询内容
     * @param context  上下文
     * @param strategy 检索策略
     * @return 检索结果
     */
    RagResult retrieveWithStrategy(String query, RagContext context, RetrievalStrategy strategy);

    /**
     * 获取推荐策略
     *
     * @param queryType 查询类型
     * @return 推荐策略
     */
    RetrievalStrategy getRecommendedStrategy(QueryType queryType);

    /**
     * 查询类型
     */
    enum QueryType {
        FACTUAL("事实查询", "需要准确的事实信息"),
        SUMMARY("摘要查询", "需要总结和概括"),
        CREATIVE("创意查询", "需要创意和灵感"),
        COMPARISON("对比查询", "需要比较分析"),
        PROCEDURAL("流程查询", "需要步骤指导"),
        EXPLORATORY("探索查询", "需要广泛探索"),
        UNKNOWN("未知类型", "无法分类的查询");

        private final String displayName;
        private final String description;

        QueryType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 检索策略
     */
    enum RetrievalStrategy {
        HIGH_PRECISION("高精度", 3, 0.8, true),      // 事实查询
        BALANCED("平衡", 5, 0.7, false),            // 摘要查询
        DIVERSE("多样性", 8, 0.6, true),            // 创意查询
        COMPREHENSIVE("全面", 10, 0.5, false),      // 探索查询
        STEP_BY_STEP("逐步", 5, 0.7, true);         // 流程查询

        private final String displayName;
        private final int topK;
        private final double threshold;
        private final boolean rerankEnabled;

        RetrievalStrategy(String displayName, int topK, double threshold, boolean rerankEnabled) {
            this.displayName = displayName;
            this.topK = topK;
            this.threshold = threshold;
            this.rerankEnabled = rerankEnabled;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getTopK() {
            return topK;
        }

        public double getThreshold() {
            return threshold;
        }

        public boolean isRerankEnabled() {
            return rerankEnabled;
        }
    }

    /**
     * RAG 上下文
     */
    class RagContext {
        private String kbId;
        private String query;
        private int topK = 5;
        private double threshold = 0.7;
        private boolean rerankEnabled = false;
        private Map<String, Object> filters;
        private Map<String, Object> metadata;

        public String getKbId() {
            return kbId;
        }

        public void setKbId(String kbId) {
            this.kbId = kbId;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public int getTopK() {
            return topK;
        }

        public void setTopK(int topK) {
            this.topK = topK;
        }

        public double getThreshold() {
            return threshold;
        }

        public void setThreshold(double threshold) {
            this.threshold = threshold;
        }

        public boolean isRerankEnabled() {
            return rerankEnabled;
        }

        public void setRerankEnabled(boolean rerankEnabled) {
            this.rerankEnabled = rerankEnabled;
        }

        public Map<String, Object> getFilters() {
            return filters;
        }

        public void setFilters(Map<String, Object> filters) {
            this.filters = filters;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }

    /**
     * RAG 结果
     */
    class RagResult {
        private String query;
        private QueryType queryType;
        private RetrievalStrategy strategy;
        private List<RetrievedChunk> chunks;
        private int totalFound;
        private long processingTime;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public QueryType getQueryType() {
            return queryType;
        }

        public void setQueryType(QueryType queryType) {
            this.queryType = queryType;
        }

        public RetrievalStrategy getStrategy() {
            return strategy;
        }

        public void setStrategy(RetrievalStrategy strategy) {
            this.strategy = strategy;
        }

        public List<RetrievedChunk> getChunks() {
            return chunks;
        }

        public void setChunks(List<RetrievedChunk> chunks) {
            this.chunks = chunks;
        }

        public int getTotalFound() {
            return totalFound;
        }

        public void setTotalFound(int totalFound) {
            this.totalFound = totalFound;
        }

        public long getProcessingTime() {
            return processingTime;
        }

        public void setProcessingTime(long processingTime) {
            this.processingTime = processingTime;
        }
    }

    /**
     * 检索到的块
     */
    class RetrievedChunk {
        private String chunkId;
        private String docId;
        private String docTitle;
        private String content;
        private double score;
        private Map<String, Object> metadata;

        public String getChunkId() {
            return chunkId;
        }

        public void setChunkId(String chunkId) {
            this.chunkId = chunkId;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocTitle() {
            return docTitle;
        }

        public void setDocTitle(String docTitle) {
            this.docTitle = docTitle;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }
}
