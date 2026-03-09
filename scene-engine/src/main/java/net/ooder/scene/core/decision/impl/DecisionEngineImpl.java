package net.ooder.scene.core.decision.impl;

import net.ooder.scene.core.decision.DecisionContext;
import net.ooder.scene.core.decision.DecisionMode;
import net.ooder.scene.core.decision.DecisionResult;
import net.ooder.scene.core.decision.DecisionEngine;
import net.ooder.scene.skill.llm.LlmProvider;
import net.ooder.scene.skill.rule.MvelRuleEngine;
import net.ooder.scene.skill.rule.impl.MvelRuleEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 决策引擎实现
 * 
 * <p>支持三种决策模式：</p>
 * <ul>
 *   <li>ONLINE_ONLY - 仅在线决策，依赖LLM</li>
 *   <li>OFFLINE_ONLY - 仅离线决策，依赖规则引擎</li>
 *   <li>ONLINE_FIRST - 优先在线，降级离线（默认）</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class DecisionEngineImpl implements DecisionEngine {

    private static final Logger log = LoggerFactory.getLogger(DecisionEngineImpl.class);
    private static final String VERSION = "2.3.1";
    private static final String DEFAULT_MODEL = "default";

    private final LlmProvider llmProvider;
    private final MvelRuleEngine ruleEngine;
    private DecisionMode mode = DecisionMode.ONLINE_FIRST;
    private final DecisionStats stats;
    private final AtomicLong totalLatency = new AtomicLong(0);
    private boolean llmAvailable = false;

    public DecisionEngineImpl(LlmProvider llmProvider, MvelRuleEngine ruleEngine) {
        this.llmProvider = llmProvider;
        this.ruleEngine = ruleEngine;
        this.stats = new DecisionStats();
        this.llmAvailable = llmProvider != null;
    }

    public DecisionEngineImpl() {
        this(null, new MvelRuleEngineImpl());
    }

    public DecisionResult decide(DecisionContext context) {
        if (context == null) {
            return DecisionResult.failure("DecisionContext is null");
        }

        long startTime = System.currentTimeMillis();
        stats.setTotalDecisions(stats.getTotalDecisions() + 1);

        try {
            DecisionResult result = doDecide(context);
            
            if (result.isSuccess()) {
                stats.setSuccessfulDecisions(stats.getSuccessfulDecisions() + 1);
            } else {
                stats.setFailedDecisions(stats.getFailedDecisions() + 1);
            }

            return result;
        } catch (Exception e) {
            log.error("Decision failed for context: {}", context, e);
            stats.setFailedDecisions(stats.getFailedDecisions() + 1);
            return DecisionResult.failure("Decision failed: " + e.getMessage());
        } finally {
            long latency = System.currentTimeMillis() - startTime;
            totalLatency.addAndGet(latency);
            updateAverageLatency();
        }
    }

    private DecisionResult doDecide(DecisionContext context) {
        DecisionMode effectiveMode = context.getMode() != null ? context.getMode() : this.mode;

        switch (effectiveMode) {
            case ONLINE_ONLY:
                return decideOnline(context);
            case OFFLINE_ONLY:
                return decideOffline(context);
            case ONLINE_FIRST:
            default:
                return decideWithFallback(context);
        }
    }

    private DecisionResult decideOnline(DecisionContext context) {
        if (!isLlmAvailable()) {
            return DecisionResult.failure("LLM is not available in ONLINE_ONLY mode");
        }
        return decideWithLlm(context);
    }

    private DecisionResult decideOffline(DecisionContext context) {
        if (ruleEngine == null) {
            return DecisionResult.failure("Rule engine is not available in OFFLINE_ONLY mode");
        }
        return decideWithRules(context);
    }

    private DecisionResult decideWithFallback(DecisionContext context) {
        if (isLlmAvailable()) {
            try {
                DecisionResult result = decideWithLlm(context);
                if (result.isSuccess() && result.isHighConfidence()) {
                    return result;
                }
                log.debug("LLM decision confidence low, trying rule engine");
            } catch (Exception e) {
                log.warn("LLM decision failed, fallback to rule engine: {}", e.getMessage());
            }
        }

        if (ruleEngine != null) {
            return decideWithRules(context);
        }

        return DecisionResult.failure("No decision method available");
    }

    private DecisionResult decideWithLlm(DecisionContext context) {
        if (llmProvider == null) {
            return DecisionResult.failure("LLM provider is not configured");
        }

        try {
            String prompt = buildDecisionPrompt(context);
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.3);
            options.put("max_tokens", 500);
            
            Map<String, Object> response = llmProvider.chat(DEFAULT_MODEL, messages, options);
            
            DecisionResult result = parseLlmResponse(response);
            result.setFromLlm(true);
            result.setSource(DecisionResult.DecisionSource.LLM);
            
            stats.setLlmDecisions(stats.getLlmDecisions() + 1);
            
            return result;
        } catch (Exception e) {
            log.error("LLM decision error", e);
            return DecisionResult.failure("LLM decision error: " + e.getMessage());
        }
    }

    private DecisionResult decideWithRules(DecisionContext context) {
        if (ruleEngine == null) {
            return DecisionResult.failure("Rule engine is not configured");
        }

        try {
            String query = context.getQuery();
            if (query == null || query.trim().isEmpty()) {
                return DecisionResult.failure("Query is empty");
            }

            Map<String, Object> ruleContext = new HashMap<>();
            ruleContext.put("query", query);
            ruleContext.put("userId", context.getUserId());
            ruleContext.put("sceneId", context.getSceneId());
            ruleContext.putAll(context.getParams());
            
            Map<String, Object> ruleResult = ruleEngine.execute("intent_routing", ruleContext);
            
            DecisionResult result = parseRuleResult(ruleResult);
            result.setFromLlm(false);
            result.setSource(DecisionResult.DecisionSource.RULE_ENGINE);
            
            stats.setRuleDecisions(stats.getRuleDecisions() + 1);
            
            return result;
        } catch (Exception e) {
            log.error("Rule engine decision error", e);
            return DecisionResult.failure("Rule engine error: " + e.getMessage());
        }
    }

    private String buildDecisionPrompt(DecisionContext context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a decision engine. Analyze the user query and determine the appropriate action.\n\n");
        prompt.append("Context:\n");
        prompt.append("- Scene ID: ").append(context.getSceneId()).append("\n");
        prompt.append("- User ID: ").append(context.getUserId()).append("\n");
        prompt.append("- Query: ").append(context.getQuery()).append("\n\n");
        prompt.append("Respond in JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"capability\": \"capability_name\",\n");
        prompt.append("  \"capId\": \"capability_id\",\n");
        prompt.append("  \"params\": { ... },\n");
        prompt.append("  \"confidence\": 0.0-1.0\n");
        prompt.append("}");
        return prompt.toString();
    }

    @SuppressWarnings("unchecked")
    private DecisionResult parseLlmResponse(Map<String, Object> response) {
        if (response == null) {
            return DecisionResult.failure("Empty LLM response");
        }

        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                return DecisionResult.failure("No choices in LLM response");
            }
            
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            if (message == null) {
                return DecisionResult.failure("No message in LLM response");
            }
            
            String content = (String) message.get("content");
            if (content == null || content.trim().isEmpty()) {
                return DecisionResult.failure("Empty content in LLM response");
            }
            
            Map<String, Object> parsed = parseJsonContent(content);
            
            String capability = (String) parsed.get("capability");
            String capId = (String) parsed.get("capId");
            Map<String, Object> params = (Map<String, Object>) parsed.get("params");
            Object confidenceObj = parsed.get("confidence");
            float confidence = 0.0f;
            if (confidenceObj instanceof Number) {
                confidence = ((Number) confidenceObj).floatValue();
            }

            DecisionResult result = DecisionResult.success(capability, capId, params);
            result.setConfidence(confidence);
            return result;
        } catch (Exception e) {
            log.error("Failed to parse LLM response", e);
            return DecisionResult.failure("Failed to parse LLM response: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonContent(String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            String json = content.trim();
            if (json.startsWith("```json")) {
                json = json.substring(7);
            }
            if (json.startsWith("```")) {
                json = json.substring(3);
            }
            if (json.endsWith("```")) {
                json = json.substring(0, json.length() - 3);
            }
            json = json.trim();
            
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1);
                String[] pairs = json.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length == 2) {
                        String key = kv[0].trim().replace("\"", "");
                        String value = kv[1].trim();
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            result.put(key, value.substring(1, value.length() - 1));
                        } else if (value.matches("\\d+\\.?\\d*")) {
                            result.put(key, Float.parseFloat(value));
                        } else {
                            result.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse JSON content: {}", content, e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private DecisionResult parseRuleResult(Map<String, Object> ruleResult) {
        if (ruleResult == null) {
            return DecisionResult.failure("Empty rule result");
        }
        
        String capability = (String) ruleResult.get("capability");
        String capId = (String) ruleResult.get("capId");
        Map<String, Object> params = (Map<String, Object>) ruleResult.get("params");
        Object confidenceObj = ruleResult.get("confidence");
        float confidence = 0.7f;
        if (confidenceObj instanceof Number) {
            confidence = ((Number) confidenceObj).floatValue();
        }

        DecisionResult result = DecisionResult.success(capability, capId, params);
        result.setConfidence(confidence);
        return result;
    }

    private void updateAverageLatency() {
        long total = stats.getTotalDecisions();
        if (total > 0) {
            stats.setAverageLatencyMs((double) totalLatency.get() / total);
        }
    }

    @Override
    public boolean isLlmAvailable() {
        return llmAvailable && llmProvider != null;
    }

    public void setLlmAvailable(boolean available) {
        this.llmAvailable = available;
    }

    @Override
    public DecisionMode getMode() {
        return mode;
    }

    @Override
    public void setMode(DecisionMode mode) {
        this.mode = mode != null ? mode : DecisionMode.ONLINE_FIRST;
        log.info("Decision mode set to: {}", this.mode);
    }

    @Override
    public String getName() {
        return "SceneDecisionEngine";
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void reset() {
        stats.setTotalDecisions(0);
        stats.setSuccessfulDecisions(0);
        stats.setFailedDecisions(0);
        stats.setLlmDecisions(0);
        stats.setRuleDecisions(0);
        stats.setCacheHits(0);
        stats.setAverageLatencyMs(0);
        totalLatency.set(0);
        log.info("Decision engine stats reset");
    }

    @Override
    public DecisionStats getStats() {
        return stats;
    }
}
