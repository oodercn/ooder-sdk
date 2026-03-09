package net.ooder.scene.skill.rule.impl;

import net.ooder.scene.skill.llm.LlmProvider;
import net.ooder.scene.skill.rule.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * LLM 规则生成器实现
 * 
 * <p>利用 LLM 动态生成决策规则</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class LlmRuleGeneratorImpl implements LlmRuleGenerator {

    private static final Logger log = LoggerFactory.getLogger(LlmRuleGeneratorImpl.class);
    private static final String VERSION = "2.3.1";
    private static final String DEFAULT_MODEL = "default";

    private final LlmProvider llmProvider;
    private final MvelRuleEngine ruleEngine;

    public LlmRuleGeneratorImpl(LlmProvider llmProvider, MvelRuleEngine ruleEngine) {
        this.llmProvider = llmProvider;
        this.ruleEngine = ruleEngine;
    }

    @Override
    public RuleScript generateRule(String sceneId, String conversation, Map<String, Object> context) {
        if (llmProvider == null) {
            log.warn("LLM provider is not available");
            return null;
        }

        try {
            String prompt = buildRuleGenerationPrompt(sceneId, conversation, context);
            Map<String, Object> response = callLlm(prompt);
            RuleScript rule = parseRuleFromResponse(response, sceneId);
            
            if (rule != null) {
                RuleValidationResult validation = validateRule(rule);
                if (validation.isValid()) {
                    ruleEngine.registerRule(rule);
                    log.info("Generated and registered rule: {} for scene: {}", rule.getRuleId(), sceneId);
                } else {
                    log.warn("Generated rule validation failed: {}", validation.getErrorMessage());
                }
            }
            
            return rule;
        } catch (Exception e) {
            log.error("Failed to generate rule", e);
            return null;
        }
    }

    @Override
    public RuleScript generateRuleFromIntent(String sceneId, String intent, List<String> examples) {
        if (llmProvider == null) {
            log.warn("LLM provider is not available");
            return null;
        }

        try {
            String prompt = buildIntentRulePrompt(sceneId, intent, examples);
            Map<String, Object> response = callLlm(prompt);
            return parseRuleFromResponse(response, sceneId);
        } catch (Exception e) {
            log.error("Failed to generate rule from intent", e);
            return null;
        }
    }

    @Override
    public RuleScript optimizeRule(RuleScript rule, String feedback) {
        if (llmProvider == null || rule == null) {
            return rule;
        }

        try {
            String prompt = buildOptimizationPrompt(rule, feedback);
            Map<String, Object> response = callLlm(prompt);
            RuleScript optimized = parseRuleFromResponse(response, rule.getSceneId());
            
            if (optimized != null) {
                optimized.setRuleId(rule.getRuleId());
                optimized.setCreatedAt(rule.getCreatedAt());
                optimized.setCreatedBy(rule.getCreatedBy());
            }
            
            return optimized;
        } catch (Exception e) {
            log.error("Failed to optimize rule", e);
            return rule;
        }
    }

    @Override
    public RuleValidationResult validateRule(RuleScript rule) {
        if (rule == null) {
            return RuleValidationResult.invalid("Rule is null");
        }

        if (rule.getRuleId() == null || rule.getRuleId().trim().isEmpty()) {
            return RuleValidationResult.invalid("Rule ID is required");
        }

        RuleValidationResult result = RuleValidationResult.valid();

        if (rule.hasCondition()) {
            try {
                org.mvel2.MVEL.compileExpression(rule.getCondition());
            } catch (Exception e) {
                return RuleValidationResult.invalid("Invalid condition expression: " + e.getMessage());
            }
        }

        if (rule.hasAction()) {
            try {
                org.mvel2.MVEL.compileExpression(rule.getAction());
            } catch (Exception e) {
                return RuleValidationResult.invalid("Invalid action expression: " + e.getMessage());
            }
        }

        if (!rule.hasCondition() && !rule.hasAction()) {
            result.addWarning("Rule has neither condition nor action");
        }

        if (rule.getPriority() < 0) {
            result.addWarning("Negative priority may cause unexpected behavior");
        }

        return result;
    }

    @Override
    public RuleTestResult testRule(RuleScript rule, List<Map<String, Object>> testCases) {
        RuleTestResult result = new RuleTestResult();
        result.setTotalCases(testCases.size());

        int passed = 0;
        int failed = 0;

        for (Map<String, Object> testCase : testCases) {
            TestCaseResult caseResult = new TestCaseResult();
            caseResult.setInput(testCase);

            try {
                Map<String, Object> executionResult = ruleEngine.execute(rule.getRuleId(), testCase);
                if (executionResult == null) {
                    executionResult = new HashMap<>();
                    executionResult.put("error", "Rule execution returned null");
                }
                caseResult.setActualOutput(executionResult);

                Object expected = testCase.get("expected");
                caseResult.setExpectedOutput(expected);

                boolean casePassed = compareResults(expected, executionResult);
                caseResult.setPassed(casePassed);

                if (casePassed) {
                    passed++;
                } else {
                    failed++;
                    caseResult.setMessage("Output does not match expected");
                }
            } catch (Exception e) {
                caseResult.setPassed(false);
                caseResult.setMessage("Execution error: " + e.getMessage());
                failed++;
            }

            result.addCaseResult(caseResult);
        }

        result.setPassedCases(passed);
        result.setFailedCases(failed);
        result.setPassed(failed == 0);
        result.setSummary(String.format("Passed: %d/%d (%.1f%%)", 
            passed, testCases.size(), 100.0 * passed / testCases.size()));

        return result;
    }

    @Override
    public String getName() {
        return "LlmRuleGenerator";
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    private String buildRuleGenerationPrompt(String sceneId, String conversation, Map<String, Object> context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a decision rule based on the following conversation.\n\n");
        prompt.append("Scene ID: ").append(sceneId).append("\n");
        prompt.append("Conversation:\n").append(conversation).append("\n\n");
        
        if (context != null && !context.isEmpty()) {
            prompt.append("Context:\n");
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Generate a rule in the following JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"ruleId\": \"unique_rule_id\",\n");
        prompt.append("  \"name\": \"Rule name\",\n");
        prompt.append("  \"type\": \"DECISION|TRANSFORM|VALIDATION|ROUTING|FALLBACK\",\n");
        prompt.append("  \"condition\": \"MVEL condition expression\",\n");
        prompt.append("  \"action\": \"MVEL action expression returning a Map\",\n");
        prompt.append("  \"priority\": 0-100,\n");
        prompt.append("  \"description\": \"Rule description\"\n");
        prompt.append("}\n\n");
        prompt.append("The action should return a Map with 'capability', 'capId', 'params', and 'confidence' fields.\n");
        prompt.append("Use MVEL syntax for expressions. Available variables: query, userId, sceneId, groupId, agentId.\n");

        return prompt.toString();
    }

    private String buildIntentRulePrompt(String sceneId, String intent, List<String> examples) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a decision rule for the following intent.\n\n");
        prompt.append("Scene ID: ").append(sceneId).append("\n");
        prompt.append("Intent: ").append(intent).append("\n\n");
        
        if (examples != null && !examples.isEmpty()) {
            prompt.append("Example queries:\n");
            for (String example : examples) {
                prompt.append("- ").append(example).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Generate a rule in JSON format that matches queries with this intent.\n");
        prompt.append("The condition should detect the intent, and the action should return the appropriate capability.\n");

        return prompt.toString();
    }

    private String buildOptimizationPrompt(RuleScript rule, String feedback) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Optimize the following decision rule based on user feedback.\n\n");
        prompt.append("Current Rule:\n");
        prompt.append("- ID: ").append(rule.getRuleId()).append("\n");
        prompt.append("- Name: ").append(rule.getName()).append("\n");
        prompt.append("- Type: ").append(rule.getType()).append("\n");
        prompt.append("- Condition: ").append(rule.getCondition()).append("\n");
        prompt.append("- Action: ").append(rule.getAction()).append("\n");
        prompt.append("- Priority: ").append(rule.getPriority()).append("\n\n");
        prompt.append("Feedback: ").append(feedback).append("\n\n");
        prompt.append("Generate an optimized rule in JSON format.\n");

        return prompt.toString();
    }

    private Map<String, Object> callLlm(String prompt) {
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.3);
        options.put("max_tokens", 1000);

        return llmProvider.chat(DEFAULT_MODEL, messages, options);
    }

    @SuppressWarnings("unchecked")
    private RuleScript parseRuleFromResponse(Map<String, Object> response, String sceneId) {
        if (response == null) {
            return null;
        }

        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                return null;
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            if (message == null) {
                return null;
            }

            String content = (String) message.get("content");
            if (content == null || content.trim().isEmpty()) {
                return null;
            }

            return parseRuleFromJson(content, sceneId);
        } catch (Exception e) {
            log.error("Failed to parse rule from response", e);
            return null;
        }
    }

    private RuleScript parseRuleFromJson(String content, String sceneId) {
        try {
            String json = extractJson(content);
            Map<String, Object> parsed = parseSimpleJson(json);

            RuleScript rule = new RuleScript();
            rule.setRuleId((String) parsed.getOrDefault("ruleId", generateRuleId()));
            rule.setSceneId(sceneId);
            rule.setName((String) parsed.get("name"));
            rule.setType(RuleType.fromCode((String) parsed.get("type")));
            rule.setCondition((String) parsed.get("condition"));
            rule.setAction((String) parsed.get("action"));
            rule.setDescription((String) parsed.get("description"));

            Object priority = parsed.get("priority");
            if (priority instanceof Number) {
                rule.setPriority(((Number) priority).intValue());
            }

            return rule;
        } catch (Exception e) {
            log.error("Failed to parse rule JSON: {}", content, e);
            return null;
        }
    }

    private String extractJson(String content) {
        String json = content.trim();
        
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        
        if (start >= 0 && end > start) {
            json = json.substring(start, end + 1);
        }
        
        return json;
    }

    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
        
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }

        json = json.substring(1, json.length() - 1).trim();
        
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean inKey = true;
        boolean inString = false;
        boolean inValue = false;
        int braceCount = 0;
        int bracketCount = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
                if (inValue) {
                    value.append(c);
                }
                continue;
            }

            if (!inString) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                if (c == '[') bracketCount++;
                if (c == ']') bracketCount--;

                if (c == ':' && inKey && braceCount == 0 && bracketCount == 0) {
                    inKey = false;
                    inValue = true;
                    continue;
                }

                if (c == ',' && braceCount == 0 && bracketCount == 0) {
                    addKeyValue(result, key.toString().trim(), value.toString().trim());
                    key = new StringBuilder();
                    value = new StringBuilder();
                    inKey = true;
                    inValue = false;
                    continue;
                }
            }

            if (inKey) {
                key.append(c);
            } else if (inValue) {
                value.append(c);
            }
        }

        if (key.length() > 0) {
            addKeyValue(result, key.toString().trim(), value.toString().trim());
        }

        return result;
    }

    private void addKeyValue(Map<String, Object> result, String key, String value) {
        key = key.replace("\"", "");
        
        if (value.startsWith("\"") && value.endsWith("\"")) {
            result.put(key, value.substring(1, value.length() - 1));
        } else if (value.equalsIgnoreCase("true")) {
            result.put(key, true);
        } else if (value.equalsIgnoreCase("false")) {
            result.put(key, false);
        } else if (value.equalsIgnoreCase("null")) {
            result.put(key, null);
        } else {
            try {
                if (value.contains(".")) {
                    result.put(key, Double.parseDouble(value));
                } else {
                    result.put(key, Integer.parseInt(value));
                }
            } catch (NumberFormatException e) {
                result.put(key, value);
            }
        }
    }

    private String generateRuleId() {
        return "rule_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private boolean compareResults(Object expected, Object actual) {
        if (expected == null) {
            return actual != null;
        }

        if (actual instanceof Map && expected instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> expectedMap = (Map<String, Object>) expected;
            @SuppressWarnings("unchecked")
            Map<String, Object> actualMap = (Map<String, Object>) actual;

            for (Map.Entry<String, Object> entry : expectedMap.entrySet()) {
                Object actualValue = actualMap.get(entry.getKey());
                if (!Objects.equals(entry.getValue(), actualValue)) {
                    return false;
                }
            }
            return true;
        }

        return Objects.equals(expected, actual);
    }
}
