package net.ooder.scene.skill.knowledge;

import java.util.*;

/**
 * 术语服务接口
 * <p>提供术语解析、缩写扩展、同义词管理等功能</p>
 *
 * <p>功能：</p>
 * <ul>
 *   <li>术语识别与规范化</li>
 *   <li>缩写/简写自动扩展</li>
 *   <li>同义词扩展（用于增强检索）</li>
 *   <li>查询预处理 Pipeline</li>
 *   <li>业务字典管理</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface TerminologyService {

    /**
     * 解析查询中的术语
     *
     * @param query 原始查询
     * @return 术语解析结果
     */
    TerminologyResolution resolve(String query);

    /**
     * 规范化查询（术语标准化）
     *
     * @param query 原始查询
     * @return 规范化后的查询
     */
    String normalize(String query);

    /**
     * 扩展缩写和简写
     *
     * @param query 原始查询
     * @return 扩展后的查询
     */
    String expandAbbreviations(String query);

    /**
     * 获取术语的同义词列表
     *
     * @param term 术语
     * @return 同义词列表
     */
    List<String> getSynonyms(String term);

    /**
     * 生成查询变体（用于多路召回）
     *
     * @param query 原始查询
     * @return 查询变体列表
     */
    List<String> generateQueryVariants(String query);

    /**
     * 预处理查询（完整 Pipeline）
     *
     * @param query 原始查询
     * @return 预处理后的查询
     */
    PreprocessedQuery preprocess(String query);

    /**
     * 添加术语映射
     *
     * @param mapping 术语映射
     */
    void addTerminology(TerminologyMapping mapping);

    /**
     * 批量添加术语映射
     *
     * @param mappings 术语映射列表
     */
    void addTerminologies(List<TerminologyMapping> mappings);

    /**
     * 从知识库加载术语
     *
     * @param kbId 知识库ID
     */
    void loadFromKnowledgeBase(String kbId);

    /**
     * 获取所有术语
     *
     * @return 术语列表
     */
    List<TerminologyMapping> getAllTerminologies();

    /**
     * 根据分类获取术语
     *
     * @param category 分类
     * @return 术语列表
     */
    List<TerminologyMapping> getTerminologiesByCategory(String category);

    /**
     * 学习新术语（从对话中自动学习）
     *
     * @param context 上下文
     * @param term 新术语
     * @param definition 定义
     */
    void learnTerm(String context, String term, String definition);

    /**
     * 术语解析结果
     */
    class TerminologyResolution {
        private String originalQuery;
        private String normalizedQuery;
        private List<TermMatch> matchedTerms;
        private List<String> expandedAbbreviations;
        private Map<String, String> replacements;

        public TerminologyResolution(String originalQuery) {
            this.originalQuery = originalQuery;
            this.matchedTerms = new ArrayList<>();
            this.expandedAbbreviations = new ArrayList<>();
            this.replacements = new HashMap<>();
        }

        // Getters and Setters
        public String getOriginalQuery() { return originalQuery; }
        public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }

        public String getNormalizedQuery() { return normalizedQuery; }
        public void setNormalizedQuery(String normalizedQuery) { this.normalizedQuery = normalizedQuery; }

        public List<TermMatch> getMatchedTerms() { return matchedTerms; }
        public void setMatchedTerms(List<TermMatch> matchedTerms) { this.matchedTerms = matchedTerms; }

        public List<String> getExpandedAbbreviations() { return expandedAbbreviations; }
        public void setExpandedAbbreviations(List<String> expandedAbbreviations) { this.expandedAbbreviations = expandedAbbreviations; }

        public Map<String, String> getReplacements() { return replacements; }
        public void setReplacements(Map<String, String> replacements) { this.replacements = replacements; }

        public void addMatchedTerm(TermMatch match) {
            this.matchedTerms.add(match);
        }

        public void addReplacement(String original, String replacement) {
            this.replacements.put(original, replacement);
        }
    }

    /**
     * 术语匹配
     */
    class TermMatch {
        private String term;
        private String matchedText;
        private int startPosition;
        private int endPosition;
        private String category;
        private double confidence;

        public TermMatch(String term, String matchedText, int startPosition, int endPosition) {
            this.term = term;
            this.matchedText = matchedText;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.confidence = 1.0;
        }

        // Getters and Setters
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }

        public String getMatchedText() { return matchedText; }
        public void setMatchedText(String matchedText) { this.matchedText = matchedText; }

        public int getStartPosition() { return startPosition; }
        public void setStartPosition(int startPosition) { this.startPosition = startPosition; }

        public int getEndPosition() { return endPosition; }
        public void setEndPosition(int endPosition) { this.endPosition = endPosition; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }

    /**
     * 预处理后的查询
     */
    class PreprocessedQuery {
        private String originalQuery;
        private String normalizedQuery;
        private String expandedQuery;
        private List<String> queryVariants;
        private QueryType queryType;
        private List<TermMatch> recognizedTerms;
        private Map<String, Object> metadata;

        public PreprocessedQuery(String originalQuery) {
            this.originalQuery = originalQuery;
            this.normalizedQuery = originalQuery;
            this.expandedQuery = originalQuery;
            this.queryVariants = new ArrayList<>();
            this.queryVariants.add(originalQuery);
            this.recognizedTerms = new ArrayList<>();
            this.metadata = new HashMap<>();
        }

        // Getters and Setters
        public String getOriginalQuery() { return originalQuery; }
        public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }

        public String getNormalizedQuery() { return normalizedQuery; }
        public void setNormalizedQuery(String normalizedQuery) { this.normalizedQuery = normalizedQuery; }

        public String getExpandedQuery() { return expandedQuery; }
        public void setExpandedQuery(String expandedQuery) { this.expandedQuery = expandedQuery; }

        public List<String> getQueryVariants() { return queryVariants; }
        public void setQueryVariants(List<String> queryVariants) { this.queryVariants = queryVariants; }

        public QueryType getQueryType() { return queryType; }
        public void setQueryType(QueryType queryType) { this.queryType = queryType; }

        public List<TermMatch> getRecognizedTerms() { return recognizedTerms; }
        public void setRecognizedTerms(List<TermMatch> recognizedTerms) { this.recognizedTerms = recognizedTerms; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public void addVariant(String variant) {
            if (!this.queryVariants.contains(variant)) {
                this.queryVariants.add(variant);
            }
        }
    }

    /**
     * 查询类型
     */
    enum QueryType {
        FACTUAL,        // 事实查询
        DEFINITION,     // 定义查询
        COMPARISON,     // 比较查询
        PROCEDURE,      // 流程查询
        CREATIVE,       // 创造性查询
        CONVERSATIONAL  // 对话式查询
    }
}
