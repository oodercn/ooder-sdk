package net.ooder.sdk.will;

import net.ooder.sdk.drivers.llm.LlmDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WillParserImpl implements WillParser {
    
    private static final Logger log = LoggerFactory.getLogger(WillParserImpl.class);
    
    private final LlmDriver llmDriver;
    private boolean useLlmForComplexParsing = true;
    private double llmParsingThreshold = 0.7;
    
    // 正则表达式模式
    private static final Pattern WILL_PATTERN = Pattern.compile(
        "(我想|我要|我需要|我期望|我希望|请|帮我|为我)?\\s*" +
        "(优化|改进|提升|增强|实现|完成|创建|删除|修改|调整|分析|评估|监控)?\\s*" +
        "(.+?)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern PRIORITY_PATTERN = Pattern.compile(
        "(高优先级|紧急|重要|优先|立即|马上|尽快|第一|首要|critical|urgent|high priority)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern TYPE_PATTERN = Pattern.compile(
        "(战略目标|战术目标|操作目标|技术目标|业务目标|产品目标|团队目标|个人目标|strategic|tactical|operational)",
        Pattern.CASE_INSENSITIVE
    );
    
    public WillParserImpl() {
        this.llmDriver = null;
    }
    
    public WillParserImpl(LlmDriver llmDriver) {
        this.llmDriver = llmDriver;
    }
    
    /**
     * 设置是否使用 LLM 进行复杂解析
     * @param useLlm 是否使用
     */
    public void setUseLlmForComplexParsing(boolean useLlm) {
        this.useLlmForComplexParsing = useLlm;
    }
    
    /**
     * 设置 LLM 解析阈值
     * @param threshold 阈值 (0.0 - 1.0)
     */
    public void setLlmParsingThreshold(double threshold) {
        this.llmParsingThreshold = Math.max(0.0, Math.min(1.0, threshold));
    }
    
    @Override
    public WillParseResult parse(String naturalLanguage) {
        if (naturalLanguage == null || naturalLanguage.trim().isEmpty()) {
            return WillParseResult.failure("Empty input");
        }
        
        log.debug("Parsing natural language: {}", naturalLanguage);
        
        // 首先尝试正则解析
        WillParseResult regexResult = parseWithRegex(naturalLanguage);
        
        // 如果正则解析置信度足够高，直接返回
        if (regexResult.getConfidence() >= llmParsingThreshold) {
            log.debug("Using regex parsing result with confidence: {}", regexResult.getConfidence());
            return regexResult;
        }
        
        // 如果启用了 LLM 且有 LLM 驱动，尝试 LLM 解析
        if (useLlmForComplexParsing && llmDriver != null) {
            try {
                WillParseResult llmResult = parseWithLlm(naturalLanguage);
                if (llmResult.getConfidence() > regexResult.getConfidence()) {
                    log.debug("Using LLM parsing result with confidence: {}", llmResult.getConfidence());
                    return llmResult;
                }
            } catch (Exception e) {
                log.warn("LLM parsing failed, falling back to regex: {}", e.getMessage());
            }
        }
        
        return regexResult;
    }
    
    @Override
    public CompletableFuture<WillParseResult> parseAsync(String naturalLanguage) {
        return CompletableFuture.supplyAsync(() -> parse(naturalLanguage));
    }
    
    /**
     * 使用正则表达式解析
     */
    private WillParseResult parseWithRegex(String naturalLanguage) {
        WillExpression will = new WillExpressionImpl();
        will.setNaturalLanguage(naturalLanguage);
        will.setWillId("will-" + UUID.randomUUID().toString().substring(0, 8));
        will.setCreatedAt(System.currentTimeMillis());
        
        Matcher willMatcher = WILL_PATTERN.matcher(naturalLanguage);
        if (willMatcher.find()) {
            String action = willMatcher.group(2);
            String target = willMatcher.group(3);
            
            if (action != null && target != null) {
                will.setAction(action.trim());
                will.setTarget(target.trim());
                will.setDescription(action.trim() + " " + target.trim());
            }
        }
        
        // 提取优先级
        Matcher priorityMatcher = PRIORITY_PATTERN.matcher(naturalLanguage);
        if (priorityMatcher.find()) {
            will.setPriority(WillExpression.Priority.HIGH.getValue());
            will.setPriorityLevel(WillExpression.Priority.HIGH);
        } else {
            will.setPriority(WillExpression.Priority.MEDIUM.getValue());
            will.setPriorityLevel(WillExpression.Priority.MEDIUM);
        }
        
        // 提取类型
        Matcher typeMatcher = TYPE_PATTERN.matcher(naturalLanguage);
        if (typeMatcher.find()) {
            String typeStr = typeMatcher.group(1).toLowerCase();
            if (typeStr.contains("战略") || typeStr.contains("strategic")) {
                will.setType(WillExpression.WillType.STRATEGIC);
            } else if (typeStr.contains("战术") || typeStr.contains("tactical")) {
                will.setType(WillExpression.WillType.TACTICAL);
            } else if (typeStr.contains("操作") || typeStr.contains("operational")) {
                will.setType(WillExpression.WillType.OPERATIONAL);
            } else {
                will.setType(WillExpression.WillType.TECHNICAL);
            }
        } else {
            will.setType(WillExpression.WillType.TECHNICAL);
        }
        
        // 提取约束条件
        List<String> constraints = extractConstraints(naturalLanguage);
        will.setConstraints(constraints);
        
        // 提取资源需求
        List<String> resources = extractResources(naturalLanguage);
        will.setResources(resources);
        
        // 提取时间线
        WillExpression.Timeline timeline = extractTimeline(naturalLanguage);
        will.setTimelineObj(timeline);
        
        // 提取成功标准
        List<String> successCriteria = extractSuccessCriteria(naturalLanguage);
        will.setSuccessCriteria(successCriteria);
        
        will.setStatus(WillExpression.WillStatus.PENDING);
        
        double confidence = calculateConfidence(will);
        
        return WillParseResult.success(will, confidence);
    }
    
    /**
     * 使用 LLM 进行智能解析
     */
    private WillParseResult parseWithLlm(String naturalLanguage) {
        if (llmDriver == null) {
            throw new IllegalStateException("LLM driver not available");
        }
        
        String prompt = buildParsingPrompt(naturalLanguage);
        
        try {
            // 调用 LLM 进行解析
            LlmDriver.CompletionRequest request = new LlmDriver.CompletionRequest();
            request.setPrompt(prompt);
            request.setMaxTokens(1000);
            
            LlmDriver.CompletionResponse response = llmDriver.complete(request).get();
            String llmResponse = response.getChoices().get(0).getText();
            
            // 解析 LLM 返回的结果
            WillExpression will = parseLlmResponse(llmResponse);
            will.setNaturalLanguage(naturalLanguage);
            will.setWillId("will-llm-" + UUID.randomUUID().toString().substring(0, 8));
            will.setCreatedAt(System.currentTimeMillis());
            will.setStatus(WillExpression.WillStatus.PENDING);
            
            double confidence = 0.9; // LLM 解析通常有较高置信度
            
            return WillParseResult.success(will, confidence);
            
        } catch (Exception e) {
            log.error("LLM parsing failed", e);
            throw new RuntimeException("LLM parsing failed", e);
        }
    }
    
    /**
     * 构建 LLM 解析提示词
     */
    private String buildParsingPrompt(String naturalLanguage) {
        return "请分析以下自然语言表达的意图，并提取结构化信息：\n\n" +
               "输入：\"" + naturalLanguage + "\"\n\n" +
               "请以 JSON 格式返回以下字段：\n" +
               "{\n" +
               "  \"action\": \"动作类型\",\n" +
               "  \"target\": \"目标对象\",\n" +
               "  \"description\": \"详细描述\",\n" +
               "  \"type\": \"STRATEGIC/TACTICAL/OPERATIONAL/TECHNICAL\",\n" +
               "  \"priority\": \"HIGH/MEDIUM/LOW\",\n" +
               "  \"constraints\": [\"约束条件1\", \"约束条件2\"],\n" +
               "  \"resources\": [\"资源需求1\", \"资源需求2\"],\n" +
               "  \"timeline\": {\n" +
               "    \"startTime\": \"开始时间\",\n" +
               "    \"endTime\": \"结束时间\",\n" +
               "    \"milestones\": [\"里程碑1\", \"里程碑2\"]\n" +
               "  },\n" +
               "  \"successCriteria\": [\"成功标准1\", \"成功标准2\"]\n" +
               "}";
    }
    
    /**
     * 解析 LLM 返回的结果
     */
    private WillExpression parseLlmResponse(String llmResponse) {
        WillExpressionImpl will = new WillExpressionImpl();
        
        // 简单的 JSON 解析（实际项目中建议使用 JSON 库）
        try {
            // 提取 action
            will.setAction(extractJsonField(llmResponse, "action"));
            
            // 提取 target
            will.setTarget(extractJsonField(llmResponse, "target"));
            
            // 提取 description
            will.setDescription(extractJsonField(llmResponse, "description"));
            
            // 提取 type
            String typeStr = extractJsonField(llmResponse, "type");
            if (typeStr != null) {
                will.setType(WillExpression.WillType.valueOf(typeStr.toUpperCase()));
            }
            
            // 提取 priority
            String priorityStr = extractJsonField(llmResponse, "priority");
            if (priorityStr != null) {
                WillExpression.Priority priority = WillExpression.Priority.valueOf(priorityStr.toUpperCase());
                will.setPriority(priority.getValue());
                will.setPriorityLevel(priority);
            }
            
        } catch (Exception e) {
            log.warn("Failed to parse LLM response as JSON, using fallback", e);
            // 如果 JSON 解析失败，使用简单的文本解析
            will.setDescription(llmResponse);
            will.setAction("unknown");
            will.setTarget(llmResponse);
        }
        
        return will;
    }
    
    /**
     * 从 JSON 字符串中提取字段
     */
    private String extractJsonField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private List<String> extractConstraints(String naturalLanguage) {
        List<String> constraints = new ArrayList<>();
        
        // 提取时间约束
        if (naturalLanguage.contains("在") && naturalLanguage.contains("之前")) {
            constraints.add("时间约束");
        }
        
        // 提取预算约束
        if (naturalLanguage.contains("预算") || naturalLanguage.contains("成本") || naturalLanguage.contains("费用")) {
            constraints.add("预算约束");
        }
        
        // 提取资源约束
        if (naturalLanguage.contains("资源") || naturalLanguage.contains("人力") || naturalLanguage.contains("团队")) {
            constraints.add("资源约束");
        }
        
        return constraints;
    }
    
    private List<String> extractResources(String naturalLanguage) {
        List<String> resources = new ArrayList<>();
        
        if (naturalLanguage.contains("开发") || naturalLanguage.contains("工程师")) {
            resources.add("开发人员");
        }
        
        if (naturalLanguage.contains("测试") || naturalLanguage.contains("QA")) {
            resources.add("测试人员");
        }
        
        if (naturalLanguage.contains("设计") || naturalLanguage.contains("UI")) {
            resources.add("设计人员");
        }
        
        if (naturalLanguage.contains("服务器") || naturalLanguage.contains("云")) {
            resources.add("服务器资源");
        }
        
        return resources;
    }
    
    private WillExpression.Timeline extractTimeline(String naturalLanguage) {
        WillExpression.Timeline timeline = new WillExpression.Timeline();
        timeline.setStartTime(System.currentTimeMillis());
        
        // 简单的截止时间提取
        if (naturalLanguage.contains("本周") || naturalLanguage.contains("这周")) {
            timeline.setEndTime(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
        } else if (naturalLanguage.contains("本月") || naturalLanguage.contains("这个月")) {
            timeline.setEndTime(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        } else if (naturalLanguage.contains("本季度")) {
            timeline.setEndTime(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000);
        } else {
            timeline.setEndTime(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        }
        
        timeline.setMilestones(new ArrayList<>());
        
        return timeline;
    }
    
    private List<String> extractSuccessCriteria(String naturalLanguage) {
        List<String> criteria = new ArrayList<>();
        
        if (naturalLanguage.contains("性能") || naturalLanguage.contains("速度")) {
            criteria.add("性能指标达标");
        }
        
        if (naturalLanguage.contains("质量") || naturalLanguage.contains("bug")) {
            criteria.add("质量指标达标");
        }
        
        if (naturalLanguage.contains("用户") || naturalLanguage.contains("体验")) {
            criteria.add("用户满意度达标");
        }
        
        if (criteria.isEmpty()) {
            criteria.add("目标达成");
        }
        
        return criteria;
    }
    
    private double calculateConfidence(WillExpression will) {
        double confidence = 0.5;
        
        if (will.getAction() != null && !will.getAction().isEmpty()) {
            confidence += 0.2;
        }
        
        if (will.getTarget() != null && !will.getTarget().isEmpty()) {
            confidence += 0.2;
        }
        
        if (will.getDescription() != null && !will.getDescription().isEmpty()) {
            confidence += 0.1;
        }
        
        return Math.min(confidence, 0.95);
    }
    
    @Override
    public List<WillExpression> parseBatch(List<String> naturalLanguages) {
        List<WillExpression> wills = new ArrayList<>();
        
        for (String nl : naturalLanguages) {
            WillParseResult result = parse(nl);
            if (result.isSuccess()) {
                wills.add(result.getWill());
            }
        }
        
        return wills;
    }
    
    @Override
    public WillParseResult refine(WillExpression will, String feedback) {
        if (will == null || feedback == null) {
            return WillParseResult.failure("Will and feedback cannot be null");
        }
        
        log.debug("Refining will: {} with feedback: {}", will.getWillId(), feedback);
        
        // 根据反馈调整意志表达
        String combined = will.getNaturalLanguage() + " " + feedback;
        WillParseResult refined = parse(combined);
        
        if (refined.isSuccess()) {
            refined.getWill().setWillId(will.getWillId());
            refined.getWill().setCreatedAt(will.getCreatedAt().toEpochMilli());
        }
        
        return refined;
    }
    
    @Override
    public ValidationResult validate(WillExpression will) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        result.setErrors(new ArrayList<>());
        result.setWarnings(new ArrayList<>());
        
        if (will == null) {
            result.setValid(false);
            result.getErrors().add("Will expression is null");
            return result;
        }
        
        if (will.getAction() == null || will.getAction().isEmpty()) {
            result.setValid(false);
            result.getErrors().add("Action is required");
        }
        
        if (will.getTarget() == null || will.getTarget().isEmpty()) {
            result.setValid(false);
            result.getErrors().add("Target is required");
        }
        
        if (will.getDescription() == null || will.getDescription().isEmpty()) {
            result.getWarnings().add("Description is recommended");
        }
        
        if (will.getPriorityLevel() == null) {
            result.getWarnings().add("Priority not specified, using default");
        }
        
        return result;
    }
}
