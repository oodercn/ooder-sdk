package net.ooder.scene.skill.rule.impl;

import net.ooder.scene.skill.rule.MvelRuleEngine;
import net.ooder.scene.skill.rule.RuleScript;
import net.ooder.scene.skill.rule.RuleType;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MVEL 规则引擎实现
 * 
 * <p>基于 MVEL 表达式引擎的规则执行器</p>
 * <p>支持规则编译缓存、场景隔离、优先级排序</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class MvelRuleEngineImpl implements MvelRuleEngine {

    private static final Logger log = LoggerFactory.getLogger(MvelRuleEngineImpl.class);

    private final Map<String, RuleScript> rules = new ConcurrentHashMap<>();
    private final Map<String, Serializable> compiledRules = new ConcurrentHashMap<>();
    private final Map<String, List<String>> sceneRuleIndex = new ConcurrentHashMap<>();
    private final ParserContext parserContext;

    public MvelRuleEngineImpl() {
        this.parserContext = new ParserContext();
        this.parserContext.setStrictTypeEnforcement(false);
        this.parserContext.setStrongTyping(false);
    }

    @Override
    public Map<String, Object> execute(String ruleId, Map<String, Object> context) {
        RuleScript rule = rules.get(ruleId);
        if (rule == null) {
            log.warn("Rule not found: {}", ruleId);
            return createErrorResult("Rule not found: " + ruleId);
        }

        if (!rule.isEnabled()) {
            log.debug("Rule is disabled: {}", ruleId);
            return createErrorResult("Rule is disabled: " + ruleId);
        }

        return executeRule(rule, context);
    }

    @Override
    public Object executeScript(String script, Map<String, Object> context) {
        if (script == null || script.trim().isEmpty()) {
            log.warn("Script is null or empty");
            return createErrorResult("Script is null or empty");
        }

        try {
            Serializable compiled = compiledRules.computeIfAbsent(
                "script_" + script.hashCode(),
                k -> MVEL.compileExpression(script, parserContext)
            );
            return MVEL.executeExpression(compiled, context);
        } catch (Exception e) {
            log.error("Failed to execute script: {}", script, e);
            return createErrorResult("Script execution error: " + e.getMessage());
        }
    }

    @Override
    public void registerRule(RuleScript rule) {
        if (rule == null || rule.getRuleId() == null) {
            log.warn("Invalid rule: rule or ruleId is null");
            return;
        }

        rules.put(rule.getRuleId(), rule);
        compileRule(rule);

        if (rule.getSceneId() != null) {
            sceneRuleIndex.computeIfAbsent(rule.getSceneId(), k -> new ArrayList<>())
                .add(rule.getRuleId());
        }

        log.info("Registered rule: {} for scene: {}", rule.getRuleId(), rule.getSceneId());
    }

    @Override
    public void persistRule(RuleScript rule) {
        registerRule(rule);
    }

    @Override
    public RuleScript getRule(String ruleId) {
        return rules.get(ruleId);
    }

    @Override
    public void removeRule(String ruleId) {
        RuleScript rule = rules.remove(ruleId);
        if (rule != null) {
            compiledRules.remove(ruleId);
            if (rule.getSceneId() != null) {
                List<String> sceneRules = sceneRuleIndex.get(rule.getSceneId());
                if (sceneRules != null) {
                    sceneRules.remove(ruleId);
                }
            }
            log.info("Removed rule: {}", ruleId);
        }
    }

    @Override
    public List<RuleScript> getSceneRules(String sceneId) {
        List<String> ruleIds = sceneRuleIndex.get(sceneId);
        if (ruleIds == null || ruleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<RuleScript> result = new ArrayList<>();
        for (String ruleId : ruleIds) {
            RuleScript rule = rules.get(ruleId);
            if (rule != null && rule.isEnabled()) {
                result.add(rule);
            }
        }

        result.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        return result;
    }

    @Override
    public boolean hasRule(String ruleId) {
        return rules.containsKey(ruleId);
    }

    @Override
    public int getRuleCount() {
        return rules.size();
    }

    public Map<String, Object> executeSceneRules(String sceneId, Map<String, Object> context) {
        List<RuleScript> sceneRules = getSceneRules(sceneId);
        if (sceneRules.isEmpty()) {
            log.debug("No rules found for scene: {}", sceneId);
            return createDefaultResult();
        }

        for (RuleScript rule : sceneRules) {
            if (matchesCondition(rule, context)) {
                Map<String, Object> result = executeRule(rule, context);
                if (result != null && !result.containsKey("error")) {
                    return result;
                }
            }
        }

        return createDefaultResult();
    }

    public Map<String, Object> executeRulesByType(String sceneId, RuleType type, Map<String, Object> context) {
        List<RuleScript> sceneRules = getSceneRules(sceneId);
        
        for (RuleScript rule : sceneRules) {
            if (rule.getType() == type && matchesCondition(rule, context)) {
                Map<String, Object> result = executeRule(rule, context);
                if (result != null && !result.containsKey("error")) {
                    return result;
                }
            }
        }

        return createDefaultResult();
    }

    public void clearSceneRules(String sceneId) {
        List<String> ruleIds = sceneRuleIndex.remove(sceneId);
        if (ruleIds != null) {
            for (String ruleId : ruleIds) {
                rules.remove(ruleId);
                compiledRules.remove(ruleId);
            }
            log.info("Cleared {} rules for scene: {}", ruleIds.size(), sceneId);
        }
    }

    public void clearAllRules() {
        rules.clear();
        compiledRules.clear();
        sceneRuleIndex.clear();
        log.info("Cleared all rules");
    }

    public List<String> getSceneIds() {
        return new ArrayList<>(sceneRuleIndex.keySet());
    }

    public int getSceneRuleCount(String sceneId) {
        List<String> ruleIds = sceneRuleIndex.get(sceneId);
        return ruleIds != null ? ruleIds.size() : 0;
    }

    private void compileRule(RuleScript rule) {
        try {
            if (rule.hasCondition()) {
                String key = "condition_" + rule.getRuleId();
                compiledRules.put(key, MVEL.compileExpression(rule.getCondition(), parserContext));
            }
            if (rule.hasAction()) {
                String key = "action_" + rule.getRuleId();
                compiledRules.put(key, MVEL.compileExpression(rule.getAction(), parserContext));
            }
        } catch (Exception e) {
            log.error("Failed to compile rule: {}", rule.getRuleId(), e);
        }
    }

    private boolean matchesCondition(RuleScript rule, Map<String, Object> context) {
        if (!rule.hasCondition()) {
            return true;
        }

        try {
            String key = "condition_" + rule.getRuleId();
            Serializable compiled = compiledRules.get(key);
            if (compiled == null) {
                compiled = MVEL.compileExpression(rule.getCondition(), parserContext);
                compiledRules.put(key, compiled);
            }

            Object result = MVEL.executeExpression(compiled, context);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            return result != null;
        } catch (Exception e) {
            log.error("Failed to evaluate condition for rule: {}", rule.getRuleId(), e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> executeRule(RuleScript rule, Map<String, Object> context) {
        if (!rule.hasAction()) {
            return createDefaultResult();
        }

        try {
            String key = "action_" + rule.getRuleId();
            Serializable compiled = compiledRules.get(key);
            if (compiled == null) {
                compiled = MVEL.compileExpression(rule.getAction(), parserContext);
                compiledRules.put(key, compiled);
            }

            Object result = MVEL.executeExpression(compiled, context);
            
            if (result instanceof Map) {
                return (Map<String, Object>) result;
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", result);
            resultMap.put("ruleId", rule.getRuleId());
            resultMap.put("ruleName", rule.getName());
            return resultMap;
        } catch (Exception e) {
            log.error("Failed to execute action for rule: {}", rule.getRuleId(), e);
            return createErrorResult("Rule execution error: " + e.getMessage());
        }
    }

    private Map<String, Object> createErrorResult(String error) {
        Map<String, Object> result = new HashMap<>();
        result.put("error", error);
        result.put("success", false);
        return result;
    }

    private Map<String, Object> createDefaultResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("matched", false);
        return result;
    }
}
