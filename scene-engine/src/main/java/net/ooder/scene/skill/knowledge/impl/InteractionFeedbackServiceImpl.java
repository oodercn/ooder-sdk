package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.skill.conversation.*;
import net.ooder.scene.skill.knowledge.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 交互数据反馈服务实现
 * <p>将用户实时交互数据反馈到知识库的正向循环</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class InteractionFeedbackServiceImpl implements InteractionFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(InteractionFeedbackServiceImpl.class);

    // 交互记录存储
    private final Map<String, List<InteractionRecord>> interactionRecords = new ConcurrentHashMap<>();
    // 反馈记录存储
    private final Map<String, FeedbackRecord> feedbackRecords = new ConcurrentHashMap<>();
    // 问答对缓存
    private final List<QAPair> qaPairCache = new CopyOnWriteArrayList<>();
    // 扩展查询反馈
    private final List<SearchFeedback> searchFeedbackList = new CopyOnWriteArrayList<>();

    private final KnowledgeBaseService knowledgeBaseService;
    private final TerminologyService terminologyService;
    private final ConversationService conversationService;

    // 质量评估阈值
    private static final double QA_QUALITY_THRESHOLD = 0.7;
    private static final int MAX_QA_CACHE_SIZE = 1000;

    public InteractionFeedbackServiceImpl(KnowledgeBaseService knowledgeBaseService,
                                          TerminologyService terminologyService,
                                          ConversationService conversationService) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.terminologyService = terminologyService;
        this.conversationService = conversationService;
    }

    @Override
    public void recordInteraction(String sessionId, String query, String response, Map<String, Object> context) {
        log.debug("Recording interaction for session: {}", sessionId);

        InteractionRecord record = new InteractionRecord();
        record.setId(UUID.randomUUID().toString());
        record.setSessionId(sessionId);
        record.setQuery(query);
        record.setResponse(response);
        record.setContext(context);
        record.setTimestamp(System.currentTimeMillis());

        // 评估交互质量
        double qualityScore = evaluateInteractionQuality(query, response, context);
        record.setQualityScore(qualityScore);

        // 存储记录
        interactionRecords.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>()).add(record);

        // 如果质量高，提取问答对
        if (qualityScore >= QA_QUALITY_THRESHOLD) {
            QAPair qaPair = extractQAPair(record);
            if (qaPair != null) {
                qaPairCache.add(qaPair);
                log.debug("Extracted high-quality QA pair: {}", qaPair.getQuestion());
            }
        }

        // 术语学习
        learnTerminologyFromInteraction(query, response, context);
    }

    @Override
    public void recordExpandedSearch(String originalQuery, String expandedQuery,
                                     List<Map<String, Object>> searchResults,
                                     List<Map<String, Object>> usedResults) {
        log.debug("Recording expanded search: {} -> {}", originalQuery, expandedQuery);

        SearchFeedback feedback = new SearchFeedback();
        feedback.setId(UUID.randomUUID().toString());
        feedback.setOriginalQuery(originalQuery);
        feedback.setExpandedQuery(expandedQuery);
        feedback.setSearchResults(searchResults);
        feedback.setUsedResults(usedResults);
        feedback.setTimestamp(System.currentTimeMillis());

        // 计算扩展效果
        double expansionEffectiveness = calculateExpansionEffectiveness(searchResults, usedResults);
        feedback.setExpansionEffectiveness(expansionEffectiveness);

        searchFeedbackList.add(feedback);

        // 如果扩展效果好，学习这个扩展模式
        if (expansionEffectiveness > 0.8) {
            learnExpansionPattern(originalQuery, expandedQuery);
        }
    }

    @Override
    public void recordFeedback(String interactionId, FeedbackType feedbackType,
                               String feedbackContent, String userId) {
        log.info("Recording feedback for interaction: {} - type: {}", interactionId, feedbackType);

        FeedbackRecord record = new FeedbackRecord();
        record.setInteractionId(interactionId);
        record.setFeedbackType(feedbackType);
        record.setFeedbackContent(feedbackContent);
        record.setUserId(userId);
        record.setTimestamp(System.currentTimeMillis());

        feedbackRecords.put(interactionId, record);

        // 根据反馈类型处理
        switch (feedbackType) {
            case POSITIVE:
                handlePositiveFeedback(interactionId);
                break;
            case NEGATIVE:
                handleNegativeFeedback(interactionId);
                break;
            case CORRECTION:
                handleCorrectionFeedback(interactionId, feedbackContent);
                break;
            case EXPANSION:
                handleExpansionFeedback(interactionId, feedbackContent);
                break;
        }
    }

    @Override
    public List<QAPair> extractQAPairs(String sessionId) {
        List<InteractionRecord> records = interactionRecords.get(sessionId);
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        return records.stream()
                .filter(r -> r.getQualityScore() >= QA_QUALITY_THRESHOLD)
                .map(this::extractQAPair)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String archiveConversation(String sessionId, String kbId) {
        log.info("Archiving conversation: {} to knowledge base: {}", sessionId, kbId);

        List<InteractionRecord> records = interactionRecords.get(sessionId);
        if (records == null || records.isEmpty()) {
            log.warn("No records found for session: {}", sessionId);
            return null;
        }

        // 构建对话文档
        StringBuilder content = new StringBuilder();
        content.append("# 对话记录\n\n");
        content.append("会话ID: ").append(sessionId).append("\n");
        content.append("时间: ").append(new Date()).append("\n\n");

        for (InteractionRecord record : records) {
            content.append("## 用户: ").append(record.getQuery()).append("\n\n");
            content.append("## 助手: ").append(record.getResponse()).append("\n\n");
        }

        // 提取问答对并添加到文档
        List<QAPair> qaPairs = extractQAPairs(sessionId);
        if (!qaPairs.isEmpty()) {
            content.append("# 提取的问答对\n\n");
            for (QAPair qa : qaPairs) {
                content.append("Q: ").append(qa.getQuestion()).append("\n");
                content.append("A: ").append(qa.getAnswer()).append("\n\n");
            }
        }

        // 创建文档
        DocumentCreateRequest docRequest = new DocumentCreateRequest();
        docRequest.setTitle("对话记录 - " + sessionId);
        docRequest.setContent(content.toString());
        docRequest.setTags(Arrays.asList("conversation", "archived", sessionId));

        try {
            Document doc = knowledgeBaseService.addDocument(kbId, docRequest);
            log.info("Archived conversation to document: {}", doc.getDocId());
            return doc.getDocId();
        } catch (Exception e) {
            log.error("Failed to archive conversation: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void feedbackSearchResults(String query, List<Map<String, Object>> results,
                                      Map<String, Double> relevanceScores) {
        log.debug("Feedback search results for query: {}", query);

        // 更新结果的相关性分数
        for (Map<String, Object> result : results) {
            String resultId = (String) result.get("id");
            if (resultId != null && relevanceScores.containsKey(resultId)) {
                double score = relevanceScores.get(resultId);
                result.put("feedbackRelevanceScore", score);

                // 如果分数高，可以将其作为正例用于模型训练
                if (score > 0.9) {
                    log.debug("High relevance result found: {}", resultId);
                }
            }
        }
    }

    @Override
    public void learnTerminology(String context, String term, String definition) {
        if (terminologyService != null) {
            terminologyService.learnTerm(context, term, definition);
            log.info("Learned terminology: {} = {}", term, definition);
        }
    }

    @Override
    public InteractionStats getStats(int timeRange) {
        long cutoffTime = System.currentTimeMillis() - (timeRange * 3600 * 1000L);

        InteractionStats stats = new InteractionStats();

        // 统计交互数量
        int totalInteractions = 0;
        int positiveFeedback = 0;
        int negativeFeedback = 0;

        for (List<InteractionRecord> records : interactionRecords.values()) {
            for (InteractionRecord record : records) {
                if (record.getTimestamp() >= cutoffTime) {
                    totalInteractions++;

                    // 检查反馈
                    FeedbackRecord feedback = feedbackRecords.get(record.getId());
                    if (feedback != null) {
                        if (feedback.getFeedbackType() == FeedbackType.POSITIVE) {
                            positiveFeedback++;
                        } else if (feedback.getFeedbackType() == FeedbackType.NEGATIVE) {
                            negativeFeedback++;
                        }
                    }
                }
            }
        }

        stats.setTotalInteractions(totalInteractions);
        stats.setPositiveFeedback(positiveFeedback);
        stats.setNegativeFeedback(negativeFeedback);
        stats.setExtractedQAPairs(qaPairCache.size());

        return stats;
    }

    @Override
    public int triggerKnowledgeBaseUpdate(String kbId) {
        log.info("Triggering knowledge base update: {}", kbId);

        int updatedCount = 0;

        // 1. 将缓存的问答对添加到知识库
        List<QAPair> qaPairsToAdd = new ArrayList<>(qaPairCache);
        qaPairCache.clear();

        for (QAPair qa : qaPairsToAdd) {
            try {
                DocumentCreateRequest docRequest = new DocumentCreateRequest();
                docRequest.setTitle("Q: " + qa.getQuestion().substring(0, Math.min(50, qa.getQuestion().length())));
                docRequest.setContent("问题: " + qa.getQuestion() + "\n\n回答: " + qa.getAnswer());
                docRequest.setTags(Arrays.asList("qa-pair", "auto-generated"));

                knowledgeBaseService.addDocument(kbId, docRequest);
                updatedCount++;
            } catch (Exception e) {
                log.error("Failed to add QA pair to knowledge base: {}", e.getMessage());
            }
        }

        log.info("Updated knowledge base with {} documents", updatedCount);
        return updatedCount;
    }

    // ========== 私有方法 ==========

    private double evaluateInteractionQuality(String query, String response, Map<String, Object> context) {
        double score = 0.5; // 基础分

        // 1. 查询长度适中（10-100字符）
        int queryLength = query.length();
        if (queryLength >= 10 && queryLength <= 100) {
            score += 0.1;
        }

        // 2. 响应长度适中（50-1000字符）
        int responseLength = response.length();
        if (responseLength >= 50 && responseLength <= 1000) {
            score += 0.1;
        }

        // 3. 响应包含结构化信息
        if (response.contains("\n") || response.contains("-") || response.contains("1.")) {
            score += 0.1;
        }

        // 4. 有上下文信息
        if (context != null && !context.isEmpty()) {
            score += 0.1;
        }

        // 5. 查询是问句
        if (query.contains("?") || query.contains("？") ||
            query.startsWith("如何") || query.startsWith("什么") ||
            query.startsWith("怎么") || query.startsWith("为什么")) {
            score += 0.1;
        }

        return Math.min(1.0, score);
    }

    private QAPair extractQAPair(InteractionRecord record) {
        if (record.getQuery() == null || record.getResponse() == null) {
            return null;
        }

        QAPair qaPair = new QAPair(record.getQuery(), record.getResponse());
        qaPair.setId(record.getId());
        qaPair.setQualityScore(record.getQualityScore());
        qaPair.setTimestamp(record.getTimestamp());

        // 提取来源信息
        if (record.getContext() != null) {
            @SuppressWarnings("unchecked")
            List<String> sources = (List<String>) record.getContext().get("sources");
            if (sources != null) {
                qaPair.setSources(sources);
            }
        }

        return qaPair;
    }

    private void learnTerminologyFromInteraction(String query, String response, Map<String, Object> context) {
        // 简化实现：从查询中提取可能的术语
        // 实际实现应该使用 NLP 技术识别专业术语

        // 示例：如果查询包含大写字母组合，可能是缩写
        String[] words = query.split("\\s+");
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z]", "");
            if (cleanWord.length() >= 2 && cleanWord.length() <= 10 &&
                cleanWord.equals(cleanWord.toUpperCase())) {
                // 可能是缩写
                log.debug("Potential abbreviation detected: {}", cleanWord);
            }
        }
    }

    private double calculateExpansionEffectiveness(List<Map<String, Object>> searchResults,
                                                    List<Map<String, Object>> usedResults) {
        if (searchResults.isEmpty() || usedResults.isEmpty()) {
            return 0.0;
        }

        // 计算使用率
        return (double) usedResults.size() / searchResults.size();
    }

    private void learnExpansionPattern(String originalQuery, String expandedQuery) {
        log.info("Learning expansion pattern: {} -> {}", originalQuery, expandedQuery);
        // 实际实现应该存储扩展模式用于未来查询优化
    }

    private void handlePositiveFeedback(String interactionId) {
        log.info("Handling positive feedback for: {}", interactionId);
        // 可以标记该问答对为高价值，优先用于训练
    }

    private void handleNegativeFeedback(String interactionId) {
        log.info("Handling negative feedback for: {}", interactionId);
        // 可以标记该问答对需要改进，或从缓存中移除
    }

    private void handleCorrectionFeedback(String interactionId, String correction) {
        log.info("Handling correction feedback for: {} - correction: {}", interactionId, correction);
        // 可以创建修正后的问答对
    }

    private void handleExpansionFeedback(String interactionId, String expansion) {
        log.info("Handling expansion feedback for: {} - expansion: {}", interactionId, expansion);
        // 可以学习新的查询扩展模式
    }

    // ========== 内部类 ==========

    private static class InteractionRecord {
        private String id;
        private String sessionId;
        private String query;
        private String response;
        private Map<String, Object> context;
        private double qualityScore;
        private long timestamp;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }

        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }

        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }

        public double getQualityScore() { return qualityScore; }
        public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    private static class FeedbackRecord {
        private String interactionId;
        private FeedbackType feedbackType;
        private String feedbackContent;
        private String userId;
        private long timestamp;

        // Getters and Setters
        public String getInteractionId() { return interactionId; }
        public void setInteractionId(String interactionId) { this.interactionId = interactionId; }

        public FeedbackType getFeedbackType() { return feedbackType; }
        public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }

        public String getFeedbackContent() { return feedbackContent; }
        public void setFeedbackContent(String feedbackContent) { this.feedbackContent = feedbackContent; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    private static class SearchFeedback {
        private String id;
        private String originalQuery;
        private String expandedQuery;
        private List<Map<String, Object>> searchResults;
        private List<Map<String, Object>> usedResults;
        private double expansionEffectiveness;
        private long timestamp;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getOriginalQuery() { return originalQuery; }
        public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }

        public String getExpandedQuery() { return expandedQuery; }
        public void setExpandedQuery(String expandedQuery) { this.expandedQuery = expandedQuery; }

        public List<Map<String, Object>> getSearchResults() { return searchResults; }
        public void setSearchResults(List<Map<String, Object>> searchResults) { this.searchResults = searchResults; }

        public List<Map<String, Object>> getUsedResults() { return usedResults; }
        public void setUsedResults(List<Map<String, Object>> usedResults) { this.usedResults = usedResults; }

        public double getExpansionEffectiveness() { return expansionEffectiveness; }
        public void setExpansionEffectiveness(double expansionEffectiveness) { this.expansionEffectiveness = expansionEffectiveness; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
