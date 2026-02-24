package net.ooder.sdk.nlp.impl;

import net.ooder.sdk.nlp.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RuleBasedNlpProcessor implements NlpProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(RuleBasedNlpProcessor.class);
    
    private final Map<String, Pattern> entityPatterns = new HashMap<>();
    private final Map<String, String> intentKeywords = new HashMap<>();
    
    public RuleBasedNlpProcessor() {
        initDefaultPatterns();
    }
    
    private void initDefaultPatterns() {
        entityPatterns.put("DATE", Pattern.compile("\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}|\\d{1,2}月\\d{1,2}[日号]"));
        entityPatterns.put("TIME", Pattern.compile("\\d{1,2}:\\d{2}|\\d{1,2}[点时]\\d{0,2}分?"));
        entityPatterns.put("NUMBER", Pattern.compile("\\d+(\\.\\d+)?"));
        entityPatterns.put("EMAIL", Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"));
        entityPatterns.put("PHONE", Pattern.compile("1[3-9]\\d{9}"));
        entityPatterns.put("URL", Pattern.compile("https?://[^\\s]+"));
        entityPatterns.put("DEVICE", Pattern.compile("[a-zA-Z]+-\\d+|设备\\d+"));
        
        intentKeywords.put("query", "查询,搜索,查找,寻找,看看,显示,列出");
        intentKeywords.put("create", "创建,新建,添加,增加,建立,生成");
        intentKeywords.put("update", "更新,修改,编辑,更改,变更");
        intentKeywords.put("delete", "删除,移除,清除,去掉");
        intentKeywords.put("execute", "执行,运行,启动,开始,触发");
        intentKeywords.put("stop", "停止,暂停,终止,结束,取消");
        intentKeywords.put("help", "帮助,怎么,如何,什么,为什么");
    }
    
    @Override
    public NlpResult process(String text) {
        NlpResult result = new NlpResult();
        result.setText(text);
        result.setLanguage(detectLanguage(text));
        result.setIntent(extractIntent(text));
        result.setEntities(extractEntities(text));
        result.setTokens(tokenize(text));
        result.setKeywords(extractKeywords(text, 5));
        result.setSentiment(analyzeSentiment(text));
        result.setMetadata(new HashMap<>());
        return result;
    }
    
    @Override
    public String extractIntent(String text) {
        if (text == null || text.isEmpty()) {
            return "unknown";
        }
        
        String lowerText = text.toLowerCase();
        Map<String, Integer> scores = new HashMap<>();
        
        for (Map.Entry<String, String> entry : intentKeywords.entrySet()) {
            String intent = entry.getKey();
            String[] keywords = entry.getValue().split(",");
            
            for (String keyword : keywords) {
                if (lowerText.contains(keyword.trim())) {
                    scores.merge(intent, 1, Integer::sum);
                }
            }
        }
        
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("unknown");
    }
    
    @Override
    public List<Entity> extractEntities(String text) {
        List<Entity> entities = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return entities;
        }
        
        for (Map.Entry<String, Pattern> entry : entityPatterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(text);
            while (matcher.find()) {
                Entity entity = new Entity();
                entity.setType(entry.getKey());
                entity.setText(matcher.group());
                entity.setStartIndex(matcher.start());
                entity.setEndIndex(matcher.end());
                entity.setConfidence(0.0);
                entities.add(entity);
            }
        }
        
        entities.sort(Comparator.comparingInt(Entity::getStartIndex));
        return entities;
    }
    
    @Override
    public List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> tokens = new ArrayList<>();
        String[] words = text.split("[\\s,，。！？、；：\"\"''（）【】《》]+");
        for (String word : words) {
            if (!word.isEmpty()) {
                tokens.add(word);
            }
        }
        return tokens;
    }
    
    @Override
    public String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase()
            .replaceAll("[\\s]+", " ")
            .replaceAll("[，。！？、；：]", " ")
            .trim();
    }
    
    @Override
    public String detectLanguage(String text) {
        if (text == null || text.isEmpty()) {
            return "unknown";
        }
        
        int chineseCount = 0;
        int englishCount = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                chineseCount++;
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                englishCount++;
            }
        }
        
        if (chineseCount > englishCount) {
            return "zh";
        } else if (englishCount > 0) {
            return "unknown";
        }
        
        return "unknown";
    }
    
    @Override
    public SentimentResult analyzeSentiment(String text) {
        SentimentResult result = new SentimentResult();
        
        if (text == null || text.isEmpty()) {
            result.setLabel("neutral");
            result.setScore(0.5);
            return result;
        }
        
        String[] positiveWords = {"好", "棒", "优秀", "成功", "满意", "喜欢", "开心", "good", "great", "excellent"};
        String[] negativeWords = {"差", "坏", "失败", "问题", "错误", "不满", "讨厌", "bad", "error", "fail"};
        
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String word : positiveWords) {
            if (text.contains(word)) positiveCount++;
        }
        
        for (String word : negativeWords) {
            if (text.contains(word)) negativeCount++;
        }
        
        double score = 0.5 + (positiveCount - negativeCount) * 0.1;
        score = Math.max(0, Math.min(1, score));
        
        result.setScore(score);
        result.setLabel(score > 0.6 ? "positive" : (score < 0.4 ? "negative" : "neutral"));
        
        Map<String, Double> scores = new HashMap<>();
        scores.put("positive", score);
        scores.put("negative", 1 - score);
        scores.put("neutral", 1 - Math.abs(score - 0.5) * 1);
        result.setScores(scores);
        
        return result;
    }
    
    @Override
    public List<String> extractKeywords(String text, int limit) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> tokens = tokenize(text);
        Map<String, Integer> frequency = new HashMap<>();
        
        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "的", "是", "在", "有", "和", "了", "我", "你", "他", "她", "它",
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being"
        ));
        
        for (String token : tokens) {
            if (!stopWords.contains(token.toLowerCase()) && token.length() > 1) {
                frequency.merge(token, 1, Integer::sum);
            }
        }
        
        return frequency.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public String summarize(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        if (text.length() <= maxLength) {
            return text;
        }
        
        String[] sentences = text.split("[。！？.!?]");
        if (sentences.length == 0) {
            return text.substring(0, maxLength) + "...";
        }
        
        StringBuilder summary = new StringBuilder();
        for (String sentence : sentences) {
            if (summary.length() + sentence.length() <= maxLength - 3) {
                summary.append(sentence).append("。");
            } else {
                break;
            }
        }
        
        if (summary.length() == 0) {
            return text.substring(0, maxLength - 3) + "...";
        }
        
        return summary.toString();
    }
    
    public void addEntityPattern(String entityType, String pattern) {
        entityPatterns.put(entityType, Pattern.compile(pattern));
    }
    
    public void addIntentKeyword(String intent, String keyword) {
        intentKeywords.merge(intent, keyword, (old, newVal) -> old + "," + newVal);
    }
}
