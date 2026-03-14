package net.ooder.scene.skill.knowledge;

import java.util.*;

/**
 * 交互数据反馈服务接口
 * <p>将用户实时交互数据（LLM对话记录、扩展查询结果等）反馈到知识库的正向循环</p>
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li>对话记录自动归档</li>
 *   <li>高质量问答对提取</li>
 *   <li>扩展查询结果反馈</li>
 *   <li>知识库自动更新</li>
 *   <li>用户反馈收集（点赞/点踩）</li>
 *   <li>术语自动学习</li>
 * </ul>
 *
 * <p>反馈循环流程：</p>
 * <pre>
 * 用户交互 → 数据收集 → 质量评估 → 知识提取 → 知识库更新 → 模型优化
 *     ↑                                                              ↓
 *     └──────────────────── 效果验证 ← 用户反馈 ←────────────────────┘
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface InteractionFeedbackService {

    /**
     * 记录对话交互
     *
     * @param sessionId 会话ID
     * @param query 用户查询
     * @param response 系统响应
     * @param context 上下文信息
     */
    void recordInteraction(String sessionId, String query, String response, Map<String, Object> context);

    /**
     * 记录扩展查询结果
     *
     * @param originalQuery 原始查询
     * @param expandedQuery 扩展后的查询
     * @param searchResults 搜索结果
     * @param usedResults 实际使用的结果
     */
    void recordExpandedSearch(String originalQuery, String expandedQuery,
                              List<Map<String, Object>> searchResults,
                              List<Map<String, Object>> usedResults);

    /**
     * 记录用户反馈
     *
     * @param interactionId 交互ID
     * @param feedbackType 反馈类型（positive/negative）
     * @param feedbackContent 反馈内容
     * @param userId 用户ID
     */
    void recordFeedback(String interactionId, FeedbackType feedbackType,
                        String feedbackContent, String userId);

    /**
     * 提取高质量问答对
     *
     * @param sessionId 会话ID
     * @return 问答对列表
     */
    List<QAPair> extractQAPairs(String sessionId);

    /**
     * 自动归档对话记录
     *
     * @param sessionId 会话ID
     * @param kbId 目标知识库ID
     * @return 归档的文档ID
     */
    String archiveConversation(String sessionId, String kbId);

    /**
     * 反馈扩展查询结果到知识库
     *
     * @param query 查询
     * @param results 结果
     * @param relevanceScores 相关性分数
     */
    void feedbackSearchResults(String query, List<Map<String, Object>> results,
                               Map<String, Double> relevanceScores);

    /**
     * 学习新术语
     *
     * @param context 上下文
     * @param term 术语
     * @param definition 定义
     */
    void learnTerminology(String context, String term, String definition);

    /**
     * 获取交互统计
     *
     * @param timeRange 时间范围（小时）
     * @return 统计数据
     */
    InteractionStats getStats(int timeRange);

    /**
     * 触发知识库更新
     *
     * @param kbId 知识库ID
     * @return 更新的文档数量
     */
    int triggerKnowledgeBaseUpdate(String kbId);

    /**
     * 反馈类型
     */
    enum FeedbackType {
        POSITIVE,   // 正面反馈（点赞）
        NEGATIVE,   // 负面反馈（点踩）
        CORRECTION, // 纠错反馈
        EXPANSION   // 扩展反馈
    }

    /**
     * 问答对
     */
    class QAPair {
        private String id;
        private String question;
        private String answer;
        private String context;
        private double qualityScore;
        private List<String> sources;
        private long timestamp;
        private Map<String, Object> metadata;

        public QAPair(String question, String answer) {
            this.question = question;
            this.answer = answer;
            this.sources = new ArrayList<>();
            this.metadata = new HashMap<>();
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }

        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }

        public double getQualityScore() { return qualityScore; }
        public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }

        public List<String> getSources() { return sources; }
        public void setSources(List<String> sources) { this.sources = sources; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 交互统计
     */
    class InteractionStats {
        private int totalInteractions;
        private int positiveFeedback;
        private int negativeFeedback;
        private int archivedConversations;
        private int extractedQAPairs;
        private int learnedTerms;
        private Map<String, Integer> queryTypeDistribution;
        private Map<String, Double> averageResponseQuality;
        private long timestamp;

        public InteractionStats() {
            this.queryTypeDistribution = new HashMap<>();
            this.averageResponseQuality = new HashMap<>();
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and Setters
        public int getTotalInteractions() { return totalInteractions; }
        public void setTotalInteractions(int totalInteractions) { this.totalInteractions = totalInteractions; }

        public int getPositiveFeedback() { return positiveFeedback; }
        public void setPositiveFeedback(int positiveFeedback) { this.positiveFeedback = positiveFeedback; }

        public int getNegativeFeedback() { return negativeFeedback; }
        public void setNegativeFeedback(int negativeFeedback) { this.negativeFeedback = negativeFeedback; }

        public int getArchivedConversations() { return archivedConversations; }
        public void setArchivedConversations(int archivedConversations) { this.archivedConversations = archivedConversations; }

        public int getExtractedQAPairs() { return extractedQAPairs; }
        public void setExtractedQAPairs(int extractedQAPairs) { this.extractedQAPairs = extractedQAPairs; }

        public int getLearnedTerms() { return learnedTerms; }
        public void setLearnedTerms(int learnedTerms) { this.learnedTerms = learnedTerms; }

        public Map<String, Integer> getQueryTypeDistribution() { return queryTypeDistribution; }
        public void setQueryTypeDistribution(Map<String, Integer> queryTypeDistribution) { this.queryTypeDistribution = queryTypeDistribution; }

        public Map<String, Double> getAverageResponseQuality() { return averageResponseQuality; }
        public void setAverageResponseQuality(Map<String, Double> averageResponseQuality) { this.averageResponseQuality = averageResponseQuality; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
