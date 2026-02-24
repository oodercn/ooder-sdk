package net.ooder.sdk.will;

import net.ooder.sdk.drivers.llm.LlmDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WillParserImpl implements WillParser {
    
    private static final Logger log = LoggerFactory.getLogger(WillParserImpl.class);
    
    private static final Pattern STRATEGIC_PATTERN = Pattern.compile(
        "(我们希望|我们要|我们的目标是|战略目标).{0,20}(实现|达到|完成).{0,50}(在|于).{0,20}(内|之前|前)"
    );
    
    private static final Pattern TACTICAL_PATTERN = Pattern.compile(
        "(请|希望|要求).{0,10}(优化|改进|调整|实施|执行|完成).{0,30}(以实现|以达到|以便)"
    );
    
    private static final Pattern EXECUTION_PATTERN = Pattern.compile(
        "(完成|执行|处理|解决).{0,30}(在|于).{0,20}(前|之前|内)"
    );
    
    private static final Pattern GOAL_PATTERN = Pattern.compile(
        "(实现|达到|完成|目标|希望).{0,50}"
    );
    
    private static final Pattern TIMELINE_PATTERN = Pattern.compile(
        "(在|于).{0,20}(内|之前|前|完成)"
    );
    
    private static final Pattern ACTION_PATTERN = Pattern.compile(
        "(请|希望|要求|需要).{0,10}(优化|改进|调整|实施|执行|完成|处理|解决)"
    );
    
    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile(
        "(约束|限制|条件|要求|必须|不能|不可|需要).{0,30}"
    );
    
    private final LlmDriver llmDriver;
    
    public WillParserImpl() {
        this.llmDriver = null;
    }
    
    public WillParserImpl(LlmDriver llmDriver) {
        this.llmDriver = llmDriver;
    }
    
    @Override
    public WillParseResult parse(String naturalLanguage) {
        if (naturalLanguage == null || naturalLanguage.trim().isEmpty()) {
            return WillParseResult.failure("Input text cannot be empty");
        }
        
        String willId = "will-" + System.currentTimeMillis();
        WillParseResult result = WillParseResult.success(willId);
        result.setOriginalText(naturalLanguage);
        
        try {
            result.setSemantics(understandSemantics(naturalLanguage));
            result.setIntent(inferIntent(naturalLanguage));
            result.setConstraints(identifyConstraints(naturalLanguage));
            result.setPriority(judgePriority(naturalLanguage));
            result.setType(inferWillType(naturalLanguage));
            
            extractGoal(naturalLanguage, result);
            extractAction(naturalLanguage, result);
            extractTimeline(naturalLanguage, result);
            
            log.debug("Parsed will: type={}, priority={}, goal={}", 
                result.getType(), result.getPriority(), result.getGoal());
            
        } catch (Exception e) {
            log.error("Failed to parse will: {}", naturalLanguage, e);
            return WillParseResult.failure("Parse error: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public String understandSemantics(String text) {
        StringBuilder semantics = new StringBuilder();
        
        if (text.contains("希望") || text.contains("目标")) {
            semantics.append("目标导向型意志; ");
        }
        if (text.contains("请") || text.contains("要求")) {
            semantics.append("指令型意志; ");
        }
        if (text.contains("优化") || text.contains("改进")) {
            semantics.append("改进型操作; ");
        }
        if (text.contains("完成") || text.contains("执行")) {
            semantics.append("执行型操作; ");
        }
        
        return semantics.length() > 0 ? semantics.toString() : "一般性意志表达";
    }
    
    @Override
    public String inferIntent(String text) {
        if (text.contains("效率") || text.contains("提高")) {
            return "EFFICIENCY_IMPROVEMENT";
        }
        if (text.contains("成本") || text.contains("降低")) {
            return "COST_REDUCTION";
        }
        if (text.contains("质量") || text.contains("优化")) {
            return "QUALITY_IMPROVEMENT";
        }
        if (text.contains("完成") || text.contains("执行")) {
            return "TASK_EXECUTION";
        }
        if (text.contains("监控") || text.contains("检测")) {
            return "MONITORING";
        }
        return "GENERAL_INTENT";
    }
    
    @Override
    public List<String> identifyConstraints(String text) {
        List<String> constraints = new ArrayList<>();
        
        Matcher matcher = CONSTRAINT_PATTERN.matcher(text);
        while (matcher.find()) {
            constraints.add(matcher.group().trim());
        }
        
        if (text.contains("必须")) {
            constraints.add("强制性要求");
        }
        if (text.contains("不能") || text.contains("不可")) {
            constraints.add("禁止性要求");
        }
        if (text.contains("预算") || text.contains("成本")) {
            constraints.add("成本约束");
        }
        if (text.contains("时间") || text.contains("期限")) {
            constraints.add("时间约束");
        }
        
        return constraints;
    }
    
    @Override
    public int judgePriority(String text) {
        if (text.contains("紧急") || text.contains("立即") || text.contains("马上")) {
            return 10;
        }
        if (text.contains("重要") || text.contains("关键") || text.contains("核心")) {
            return 8;
        }
        if (text.contains("优先") || text.contains("尽快")) {
            return 7;
        }
        if (text.contains("战略") || text.contains("长期")) {
            return 6;
        }
        if (text.contains("一般") || text.contains("常规")) {
            return 4;
        }
        if (text.contains("低优先级") || text.contains("不急")) {
            return 2;
        }
        return 5;
    }
    
    @Override
    public WillExpression.WillType inferWillType(String text) {
        if (STRATEGIC_PATTERN.matcher(text).find() || 
            text.contains("我们希望") || 
            text.contains("战略目标") ||
            text.contains("长期目标")) {
            return WillExpression.WillType.STRATEGIC;
        }
        
        if (TACTICAL_PATTERN.matcher(text).find() || 
            (text.contains("请") && text.contains("以实现")) ||
            text.contains("优化") && text.contains("以")) {
            return WillExpression.WillType.TACTICAL;
        }
        
        return WillExpression.WillType.EXECUTION;
    }
    
    private void extractGoal(String text, WillParseResult result) {
        Matcher matcher = GOAL_PATTERN.matcher(text);
        if (matcher.find()) {
            String goalText = text.substring(matcher.start(), Math.min(matcher.end() + 20, text.length()));
            result.setGoal(goalText.trim());
        }
    }
    
    private void extractAction(String text, WillParseResult result) {
        Matcher matcher = ACTION_PATTERN.matcher(text);
        if (matcher.find()) {
            String actionText = matcher.group().trim();
            result.setAction(actionText);
            
            String[] actionWords = {"优化", "改进", "调整", "实施", "执行", "完成", "处理", "解决"};
            for (String word : actionWords) {
                if (text.contains(word)) {
                    result.setAction(word);
                    break;
                }
            }
        }
        
        int objStart = text.indexOf(result.getAction());
        if (objStart >= 0) {
            String remaining = text.substring(objStart + result.getAction().length()).trim();
            String[] words = remaining.split("[,，。]");
            if (words.length > 0) {
                result.setObject(words[0].trim());
            }
        }
    }
    
    private void extractTimeline(String text, WillParseResult result) {
        Matcher matcher = TIMELINE_PATTERN.matcher(text);
        if (matcher.find()) {
            result.setTimeline(matcher.group().trim());
        }
        
        if (text.contains("本周") || text.contains("这周")) {
            result.setDeadline("本周内");
        } else if (text.contains("下周")) {
            result.setDeadline("下周内");
        } else if (text.contains("本月") || text.contains("这个月")) {
            result.setDeadline("本月内");
        } else if (text.contains("季度") || text.contains("季度内")) {
            result.setTimeline("本季度");
        }
    }
}
