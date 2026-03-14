package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.skill.knowledge.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * 术语服务实现
 * <p>提供术语解析、缩写扩展、同义词管理等功能</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class TerminologyServiceImpl implements TerminologyService {

    private static final Logger log = LoggerFactory.getLogger(TerminologyServiceImpl.class);

    // 术语存储：术语 -> 映射
    private final Map<String, TerminologyMapping> terminologyMap = new HashMap<>();
    // 快速查找：别名/缩写 -> 标准术语
    private final Map<String, String> aliasToTermMap = new HashMap<>();
    // 分类索引
    private final Map<String, List<TerminologyMapping>> categoryIndex = new HashMap<>();

    // 常用缩写词典（内置）
    private static final Map<String, String> COMMON_ABBREVIATIONS = new HashMap<>();
    static {
        COMMON_ABBREVIATIONS.put("JD", "Job Description");
        COMMON_ABBREVIATIONS.put("HR", "Human Resources");
        COMMON_ABBREVIATIONS.put("CV", "Curriculum Vitae");
        COMMON_ABBREVIATIONS.put("OKR", "Objectives and Key Results");
        COMMON_ABBREVIATIONS.put("KPI", "Key Performance Indicator");
        COMMON_ABBREVIATIONS.put("SOP", "Standard Operating Procedure");
        COMMON_ABBREVIATIONS.put("FAQ", "Frequently Asked Questions");
        COMMON_ABBREVIATIONS.put("API", "Application Programming Interface");
        COMMON_ABBREVIATIONS.put("UI", "User Interface");
        COMMON_ABBREVIATIONS.put("UX", "User Experience");
        COMMON_ABBREVIATIONS.put("DB", "Database");
        COMMON_ABBREVIATIONS.put("SQL", "Structured Query Language");
        COMMON_ABBREVIATIONS.put("AI", "Artificial Intelligence");
        COMMON_ABBREVIATIONS.put("ML", "Machine Learning");
        COMMON_ABBREVIATIONS.put("LLM", "Large Language Model");
        COMMON_ABBREVIATIONS.put("RAG", "Retrieval-Augmented Generation");
        COMMON_ABBREVIATIONS.put("NLP", "Natural Language Processing");
        COMMON_ABBREVIATIONS.put("PR", "Pull Request");
        COMMON_ABBREVIATIONS.put("CI", "Continuous Integration");
        COMMON_ABBREVIATIONS.put("CD", "Continuous Deployment");
    }

    public TerminologyServiceImpl() {
        // 加载内置缩写词典
        loadCommonAbbreviations();
    }

    @Override
    public TerminologyResolution resolve(String query) {
        if (query == null || query.isEmpty()) {
            return new TerminologyResolution("");
        }

        TerminologyResolution resolution = new TerminologyResolution(query);
        String normalizedQuery = query;

        // 1. 查找匹配的术语
        List<TermMatch> matches = findTermMatches(query);
        resolution.setMatchedTerms(matches);

        // 2. 替换缩写
        for (TermMatch match : matches) {
            TerminologyMapping mapping = terminologyMap.get(match.getTerm());
            if (mapping != null) {
                resolution.addReplacement(match.getMatchedText(), mapping.getTerm());
                normalizedQuery = normalizedQuery.replace(
                    match.getMatchedText(),
                    mapping.getTerm()
                );
            }
        }

        resolution.setNormalizedQuery(normalizedQuery);
        return resolution;
    }

    @Override
    public String normalize(String query) {
        TerminologyResolution resolution = resolve(query);
        return resolution.getNormalizedQuery();
    }

    @Override
    public String expandAbbreviations(String query) {
        if (query == null || query.isEmpty()) {
            return query;
        }

        String result = query;

        // 按词分割处理
        String[] words = query.split("\\s+");
        StringBuilder expanded = new StringBuilder();

        for (String word : words) {
            // 去除标点
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            String punctuation = word.replaceAll("[a-zA-Z0-9]", "");

            // 查找缩写扩展
            String expandedWord = aliasToTermMap.getOrDefault(cleanWord.toUpperCase(), cleanWord);

            // 如果没有找到，尝试内置词典
            if (expandedWord.equals(cleanWord)) {
                expandedWord = COMMON_ABBREVIATIONS.getOrDefault(cleanWord.toUpperCase(), cleanWord);
            }

            expanded.append(expandedWord).append(punctuation).append(" ");
        }

        return expanded.toString().trim();
    }

    @Override
    public List<String> getSynonyms(String term) {
        TerminologyMapping mapping = terminologyMap.get(term);
        if (mapping != null) {
            return new ArrayList<>(mapping.getSynonyms());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> generateQueryVariants(String query) {
        List<String> variants = new ArrayList<>();
        variants.add(query);

        // 1. 原始查询
        String original = query;

        // 2. 扩展缩写后的查询
        String expanded = expandAbbreviations(query);
        if (!expanded.equals(original)) {
            variants.add(expanded);
        }

        // 3. 规范化后的查询
        String normalized = normalize(query);
        if (!normalized.equals(original) && !normalized.equals(expanded)) {
            variants.add(normalized);
        }

        // 4. 同义词扩展（简单实现：替换第一个找到的术语）
        TerminologyResolution resolution = resolve(query);
        for (TermMatch match : resolution.getMatchedTerms()) {
            TerminologyMapping mapping = terminologyMap.get(match.getTerm());
            if (mapping != null && !mapping.getSynonyms().isEmpty()) {
                for (String synonym : mapping.getSynonyms()) {
                    String variant = query.replace(match.getMatchedText(), synonym);
                    if (!variants.contains(variant)) {
                        variants.add(variant);
                    }
                }
            }
        }

        return variants;
    }

    @Override
    public PreprocessedQuery preprocess(String query) {
        PreprocessedQuery preprocessed = new PreprocessedQuery(query);

        // 1. 术语解析
        TerminologyResolution resolution = resolve(query);
        preprocessed.setRecognizedTerms(resolution.getMatchedTerms());
        preprocessed.setNormalizedQuery(resolution.getNormalizedQuery());

        // 2. 缩写扩展
        String expanded = expandAbbreviations(query);
        preprocessed.setExpandedQuery(expanded);

        // 3. 生成查询变体
        List<String> variants = generateQueryVariants(query);
        preprocessed.setQueryVariants(variants);

        // 4. 查询分类（简单规则）
        QueryType type = classifyQuery(query);
        preprocessed.setQueryType(type);

        // 5. 元数据
        preprocessed.getMetadata().put("termCount", resolution.getMatchedTerms().size());
        preprocessed.getMetadata().put("hasExpansion", !expanded.equals(query));
        preprocessed.getMetadata().put("variantCount", variants.size());

        log.debug("Preprocessed query: {} -> {} variants, type: {}",
            query, variants.size(), type);

        return preprocessed;
    }

    @Override
    public void addTerminology(TerminologyMapping mapping) {
        if (mapping == null || mapping.getTerm() == null) {
            return;
        }

        String term = mapping.getTerm();
        terminologyMap.put(term, mapping);

        // 更新别名映射
        for (String alias : mapping.getAliases()) {
            aliasToTermMap.put(alias.toUpperCase(), term);
        }

        // 更新缩写映射
        for (String abbr : mapping.getAbbreviations()) {
            aliasToTermMap.put(abbr.toUpperCase(), term);
        }

        // 更新分类索引
        String category = mapping.getCategory();
        if (category != null) {
            categoryIndex.computeIfAbsent(category, k -> new ArrayList<>()).add(mapping);
        }

        log.debug("Added terminology: {}", term);
    }

    @Override
    public void addTerminologies(List<TerminologyMapping> mappings) {
        if (mappings == null) {
            return;
        }
        for (TerminologyMapping mapping : mappings) {
            addTerminology(mapping);
        }
    }

    @Override
    public void loadFromKnowledgeBase(String kbId) {
        // 简化实现：从知识库元数据加载术语
        // 实际实现应该从知识库服务读取配置的术语表
        log.info("Loading terminologies from knowledge base: {}", kbId);

        // 示例：加载招聘相关术语
        if (kbId.contains("recruitment") || kbId.contains("hr")) {
            addRecruitmentTerminologies();
        }
    }

    @Override
    public List<TerminologyMapping> getAllTerminologies() {
        return new ArrayList<>(terminologyMap.values());
    }

    @Override
    public List<TerminologyMapping> getTerminologiesByCategory(String category) {
        return categoryIndex.getOrDefault(category, Collections.emptyList());
    }

    @Override
    public void learnTerm(String context, String term, String definition) {
        // 简化实现：自动学习新术语
        log.info("Learning new term: {} = {}", term, definition);

        TerminologyMapping mapping = new TerminologyMapping(term, definition);
        mapping.setSource("learned");
        mapping.setCategory("learned");
        mapping.getMetadata().put("context", context);
        mapping.getMetadata().put("learnedAt", System.currentTimeMillis());

        addTerminology(mapping);
    }

    // ========== 私有方法 ==========

    private void loadCommonAbbreviations() {
        for (Map.Entry<String, String> entry : COMMON_ABBREVIATIONS.entrySet()) {
            TerminologyMapping mapping = new TerminologyMapping(
                entry.getValue(),
                "Common abbreviation: " + entry.getKey()
            );
            mapping.addAbbreviation(entry.getKey());
            mapping.setCategory("abbreviation");
            addTerminology(mapping);
        }
    }

    private List<TermMatch> findTermMatches(String query) {
        List<TermMatch> matches = new ArrayList<>();

        // 按词分割
        String[] words = query.split("\\s+");
        int position = 0;

        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");

            // 检查是否匹配术语
            TerminologyMapping mapping = terminologyMap.get(cleanWord);
            if (mapping == null) {
                mapping = terminologyMap.get(aliasToTermMap.get(cleanWord.toUpperCase()));
            }

            if (mapping != null) {
                TermMatch match = new TermMatch(
                    mapping.getTerm(),
                    cleanWord,
                    position,
                    position + cleanWord.length()
                );
                match.setCategory(mapping.getCategory());
                matches.add(match);
            }

            position += word.length() + 1; // +1 for space
        }

        return matches;
    }

    private QueryType classifyQuery(String query) {
        String lowerQuery = query.toLowerCase();

        // 定义查询
        if (lowerQuery.matches(".*(什么是|什么是|定义|explain|define).*")) {
            return QueryType.DEFINITION;
        }

        // 比较查询
        if (lowerQuery.matches(".*(比较|对比|区别|vs|versus|difference).*")) {
            return QueryType.COMPARISON;
        }

        // 流程查询
        if (lowerQuery.matches(".*(如何|怎么|步骤|流程|how to|procedure).*")) {
            return QueryType.PROCEDURE;
        }

        // 事实查询（默认）
        if (lowerQuery.matches(".*(多少|几|什么|谁|哪里|when|where|who|what).*")) {
            return QueryType.FACTUAL;
        }

        return QueryType.CONVERSATIONAL;
    }

    private void addRecruitmentTerminologies() {
        // 招聘相关术语
        TerminologyMapping jd = new TerminologyMapping(
            "Job Description",
            "职位描述，包含岗位职责、任职要求等信息"
        );
        jd.addAbbreviation("JD");
        jd.addAlias("职位描述");
        jd.addAlias("岗位描述");
        jd.setCategory("recruitment");
        addTerminology(jd);

        TerminologyMapping cv = new TerminologyMapping(
            "Curriculum Vitae",
            "简历，个人教育和工作经历概述"
        );
        cv.addAbbreviation("CV");
        cv.addAlias("简历");
        cv.addAlias("履历");
        cv.setCategory("recruitment");
        addTerminology(cv);

        TerminologyMapping hr = new TerminologyMapping(
            "Human Resources",
            "人力资源，负责招聘、培训、员工关系等"
        );
        hr.addAbbreviation("HR");
        hr.addAlias("人力资源");
        hr.addAlias("人事");
        hr.setCategory("recruitment");
        addTerminology(hr);

        TerminologyMapping onboard = new TerminologyMapping(
            "Onboarding",
            "入职流程，新员工加入公司的过程"
        );
        onboard.addAlias("入职");
        onboard.addAlias("新员工入职");
        onboard.setCategory("recruitment");
        addTerminology(onboard);
    }
}
