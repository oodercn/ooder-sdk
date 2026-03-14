package net.ooder.scene.skill.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 反馈闭环示例
 * <p>展示完整的用户交互数据反馈到知识库的正向循环</p>
 *
 * <p>闭环流程：</p>
 * <pre>
 * 1. 用户输入查询
 * 2. 术语解析（缩写扩展）
 * 3. 知识库检索
 * 4. LLM生成响应
 * 5. 记录交互数据
 * 6. 用户反馈（可选）
 * 7. 提取高质量问答对
 * 8. 更新知识库
 * 9. 术语学习
 * 10. 模型优化（未来）
 * </pre>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class FeedbackLoopExample {

    private static final Logger log = LoggerFactory.getLogger(FeedbackLoopExample.class);

    private final TerminologyService terminologyService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final InteractionFeedbackService feedbackService;

    public FeedbackLoopExample(TerminologyService terminologyService,
                                KnowledgeBaseService knowledgeBaseService,
                                InteractionFeedbackService feedbackService) {
        this.terminologyService = terminologyService;
        this.knowledgeBaseService = knowledgeBaseService;
        this.feedbackService = feedbackService;
    }

    /**
     * 执行完整的反馈闭环示例
     */
    public void demonstrateFeedbackLoop() {
        log.info("=== 开始反馈闭环示例 ===");

        // 场景：用户询问关于 JD 的问题
        String sessionId = "session-" + UUID.randomUUID().toString().substring(0, 8);
        String userId = "user-001";

        // ========== 步骤 1: 用户输入 ==========
        String userQuery = "JD 的招聘流程是什么？";
        log.info("步骤 1 - 用户输入: {}", userQuery);

        // ========== 步骤 2: 术语解析 ==========
        log.info("步骤 2 - 术语解析");
        TerminologyService.PreprocessedQuery preprocessed = terminologyService.preprocess(userQuery);
        log.info("  原始查询: {}", preprocessed.getOriginalQuery());
        log.info("  规范化查询: {}", preprocessed.getNormalizedQuery());
        log.info("  扩展查询: {}", preprocessed.getExpandedQuery());
        log.info("  识别到的术语: {}", preprocessed.getRecognizedTerms());
        log.info("  查询变体: {}", preprocessed.getQueryVariants());

        // ========== 步骤 3: 知识库检索 ==========
        log.info("步骤 3 - 知识库检索");
        String searchQuery = preprocessed.getExpandedQuery();
        List<Map<String, Object>> searchResults = performSearch(searchQuery);
        log.info("  检索到 {} 条结果", searchResults.size());

        // ========== 步骤 4: LLM生成响应 ==========
        log.info("步骤 4 - LLM生成响应");
        String llmResponse = generateResponse(userQuery, searchResults);
        log.info("  生成响应: {}", llmResponse.substring(0, Math.min(100, llmResponse.length())) + "...");

        // ========== 步骤 5: 记录交互数据 ==========
        log.info("步骤 5 - 记录交互数据");
        Map<String, Object> context = new HashMap<>();
        context.put("userId", userId);
        context.put("queryType", preprocessed.getQueryType());
        context.put("sources", extractSources(searchResults));
        context.put("terminologyRecognized", preprocessed.getRecognizedTerms().size() > 0);

        feedbackService.recordInteraction(sessionId, userQuery, llmResponse, context);
        log.info("  交互已记录");

        // 记录扩展查询结果
        feedbackService.recordExpandedSearch(
            userQuery,
            searchQuery,
            searchResults,
            searchResults.subList(0, Math.min(3, searchResults.size()))
        );
        log.info("  扩展查询结果已记录");

        // ========== 步骤 6: 用户反馈（模拟） ==========
        log.info("步骤 6 - 用户反馈");
        String interactionId = sessionId + "-" + System.currentTimeMillis();
        feedbackService.recordFeedback(
            interactionId,
            InteractionFeedbackService.FeedbackType.POSITIVE,
            "回答很详细，很有帮助",
            userId
        );
        log.info("  用户反馈已记录: POSITIVE");

        // ========== 步骤 7: 提取高质量问答对 ==========
        log.info("步骤 7 - 提取高质量问答对");
        List<InteractionFeedbackService.QAPair> qaPairs = feedbackService.extractQAPairs(sessionId);
        log.info("  提取到 {} 个高质量问答对", qaPairs.size());
        for (InteractionFeedbackService.QAPair qa : qaPairs) {
            log.info("    Q: {}", qa.getQuestion());
            log.info("    A: {}...", qa.getAnswer().substring(0, Math.min(50, qa.getAnswer().length())));
            log.info("    质量分数: {}", qa.getQualityScore());
        }

        // ========== 步骤 8: 更新知识库 ==========
        log.info("步骤 8 - 更新知识库");
        String kbId = "recruitment-kb";
        String archivedDocId = feedbackService.archiveConversation(sessionId, kbId);
        if (archivedDocId != null) {
            log.info("  对话已归档到知识库，文档ID: {}", archivedDocId);
        }

        int updatedCount = feedbackService.triggerKnowledgeBaseUpdate(kbId);
        log.info("  知识库已更新，新增 {} 个文档", updatedCount);

        // ========== 步骤 9: 术语学习 ==========
        log.info("步骤 9 - 术语学习");
        // 从交互中学习新术语
        feedbackService.learnTerminology(
            "招聘流程咨询",
            "JD",
            "Job Description，职位描述，包含岗位职责、任职要求等信息"
        );
        log.info("  新术语已学习: JD");

        // ========== 步骤 10: 获取统计 ==========
        log.info("步骤 10 - 获取交互统计");
        InteractionFeedbackService.InteractionStats stats = feedbackService.getStats(24);
        log.info("  过去24小时统计:");
        log.info("    总交互数: {}", stats.getTotalInteractions());
        log.info("    正面反馈: {}", stats.getPositiveFeedback());
        log.info("    负面反馈: {}", stats.getNegativeFeedback());
        log.info("    提取问答对: {}", stats.getExtractedQAPairs());

        log.info("=== 反馈闭环示例完成 ===");
    }

    /**
     * 模拟知识库检索
     */
    private List<Map<String, Object>> performSearch(String query) {
        List<Map<String, Object>> results = new ArrayList<>();

        // 模拟检索结果
        Map<String, Object> result1 = new HashMap<>();
        result1.put("id", "doc-001");
        result1.put("title", "招聘流程指南");
        result1.put("content", "招聘流程包括：1.需求分析 2.职位发布 3.简历筛选 4.面试安排 5.录用决策");
        result1.put("score", 0.95);
        results.add(result1);

        Map<String, Object> result2 = new HashMap<>();
        result2.put("id", "doc-002");
        result2.put("title", "JD编写规范");
        result2.put("content", "职位描述应包含：岗位职责、任职要求、薪资范围、工作地点");
        result2.put("score", 0.88);
        results.add(result2);

        Map<String, Object> result3 = new HashMap<>();
        result3.put("id", "doc-003");
        result3.put("title", "面试技巧");
        result3.put("content", "面试评估要点：技术能力、沟通能力、团队协作、文化契合度");
        result3.put("score", 0.75);
        results.add(result3);

        return results;
    }

    /**
     * 模拟LLM生成响应
     */
    private String generateResponse(String query, List<Map<String, Object>> searchResults) {
        StringBuilder response = new StringBuilder();
        response.append("根据知识库信息，JD（职位描述）的招聘流程如下：\n\n");
        response.append("1. **需求分析**：明确岗位需求，确定岗位职责和任职要求\n");
        response.append("2. **职位发布**：编写JD并在各渠道发布招聘信息\n");
        response.append("3. **简历筛选**：根据JD要求筛选合适的候选人\n");
        response.append("4. **面试安排**：组织技术面试和HR面试\n");
        response.append("5. **录用决策**：综合评估后做出录用决定\n\n");
        response.append("JD编写要点：\n");
        response.append("- 清晰的岗位职责描述\n");
        response.append("- 明确的任职要求\n");
        response.append("- 合理的薪资范围\n");
        response.append("- 详细的工作地点信息\n\n");
        response.append("希望这些信息对您有帮助！");

        return response.toString();
    }

    /**
     * 提取来源信息
     */
    private List<String> extractSources(List<Map<String, Object>> searchResults) {
        return searchResults.stream()
            .map(r -> (String) r.get("id"))
            .filter(Objects::nonNull)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 展示多个交互的累积效果
     */
    public void demonstrateCumulativeEffect() {
        log.info("=== 累积效果示例 ===");

        String kbId = "recruitment-kb";

        // 模拟多个用户的交互
        String[] queries = {
            "JD怎么写？",
            "招聘流程是什么？",
            "如何筛选简历？",
            "面试应该问什么问题？",
            "OKR怎么制定？"
        };

        for (int i = 0; i < queries.length; i++) {
            String sessionId = "session-" + i;
            String query = queries[i];

            // 术语解析
            TerminologyService.PreprocessedQuery preprocessed = terminologyService.preprocess(query);

            // 检索
            List<Map<String, Object>> results = performSearch(preprocessed.getExpandedQuery());

            // 生成响应
            String response = generateResponse(query, results);

            // 记录交互
            Map<String, Object> context = new HashMap<>();
            context.put("queryType", preprocessed.getQueryType());
            feedbackService.recordInteraction(sessionId, query, response, context);

            log.info("记录交互 {}: {}", i + 1, query);
        }

        // 触发知识库更新
        int updatedCount = feedbackService.triggerKnowledgeBaseUpdate(kbId);
        log.info("累积效果：新增 {} 个高质量问答对到知识库", updatedCount);

        // 获取统计
        InteractionFeedbackService.InteractionStats stats = feedbackService.getStats(24);
        log.info("统计：总交互 {}, 正面反馈 {}, 提取问答对 {}",
            stats.getTotalInteractions(),
            stats.getPositiveFeedback(),
            stats.getExtractedQAPairs()
        );

        log.info("=== 累积效果示例完成 ===");
    }
}
