package net.ooder.sdk.api.security.impl;

import net.ooder.sdk.api.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KeyRuleServiceImpl implements KeyRuleService {
    
    private static final Logger log = LoggerFactory.getLogger(KeyRuleServiceImpl.class);
    
    private final Map<String, KeyRule> rules;
    private final Map<String, KeyRule> defaultRules;
    private final KeyManagementService keyManagementService;
    
    public KeyRuleServiceImpl(KeyManagementService keyManagementService) {
        this.rules = new ConcurrentHashMap<String, KeyRule>();
        this.defaultRules = new ConcurrentHashMap<String, KeyRule>();
        this.keyManagementService = keyManagementService;
    }
    
    @Override
    public KeyRule createRule(KeyRule rule) {
        if (rule.getRuleId() == null || rule.getRuleId().isEmpty()) {
            rule.setRuleId("rule-" + UUID.randomUUID().toString());
        }
        rule.setCreatedAt(System.currentTimeMillis());
        rule.setUpdatedAt(System.currentTimeMillis());
        
        rules.put(rule.getRuleId(), rule);
        
        log.info("Created key rule: id={}, name={}", rule.getRuleId(), rule.getRuleName());
        return rule;
    }
    
    @Override
    public KeyRule getRule(String ruleId) {
        return rules.get(ruleId);
    }
    
    @Override
    public List<KeyRule> getAllRules() {
        return new ArrayList<KeyRule>(rules.values());
    }
    
    @Override
    public List<KeyRule> getRulesByScene(String sceneGroupId) {
        List<KeyRule> result = new ArrayList<KeyRule>();
        for (KeyRule rule : rules.values()) {
            if (sceneGroupId.equals(rule.getSceneGroupId())) {
                result.add(rule);
            }
        }
        return result;
    }
    
    @Override
    public KeyRule updateRule(KeyRule rule) {
        if (!rules.containsKey(rule.getRuleId())) {
            log.warn("Rule not found: {}", rule.getRuleId());
            return null;
        }
        
        rule.setUpdatedAt(System.currentTimeMillis());
        rules.put(rule.getRuleId(), rule);
        
        log.info("Updated key rule: id={}", rule.getRuleId());
        return rule;
    }
    
    @Override
    public boolean deleteRule(String ruleId) {
        KeyRule removed = rules.remove(ruleId);
        if (removed != null) {
            log.info("Deleted key rule: id={}", ruleId);
            return true;
        }
        return false;
    }
    
    @Override
    public KeyEntity applyRule(String keyId, String ruleId) {
        KeyEntity key = keyManagementService.getKey(keyId);
        if (key == null) {
            log.warn("Key not found: {}", keyId);
            return null;
        }
        
        KeyRule rule = rules.get(ruleId);
        if (rule == null) {
            log.warn("Rule not found: {}", ruleId);
            return null;
        }
        
        if (rule.getDefaultExpiresInSeconds() > 0) {
            key.setExpiresAt(System.currentTimeMillis() + rule.getDefaultExpiresInSeconds() * 1000);
        }
        if (rule.getDefaultMaxUseCount() > 0) {
            key.setMaxUseCount(rule.getDefaultMaxUseCount());
        }
        if (rule.getAllowedScenes() != null) {
            key.setAllowedScenes(new ArrayList<String>(rule.getAllowedScenes()));
        }
        if (rule.getAllowedOperations() != null) {
            key.setAllowedOperations(new ArrayList<String>(rule.getAllowedOperations()));
        }
        key.setApprovalRequired(rule.isApprovalRequired());
        key.setUpdatedAt(System.currentTimeMillis());
        
        log.info("Applied rule to key: keyId={}, ruleId={}", keyId, ruleId);
        return key;
    }
    
    @Override
    public KeyRule getRecommendedRule(RequestType requestType, String sceneGroupId) {
        KeyRule defaultRule = defaultRules.get(sceneGroupId);
        if (defaultRule != null) {
            return defaultRule;
        }
        
        List<KeyRule> sceneRules = getRulesByScene(sceneGroupId);
        if (!sceneRules.isEmpty()) {
            return sceneRules.get(0);
        }
        
        return createDefaultRule(sceneGroupId);
    }
    
    @Override
    public KeyRule getDefaultRule(String sceneGroupId) {
        return defaultRules.get(sceneGroupId);
    }
    
    @Override
    public void setDefaultRule(String sceneGroupId, KeyRule rule) {
        if (rule.getRuleId() == null || rule.getRuleId().isEmpty()) {
            rule.setRuleId("default-rule-" + sceneGroupId);
        }
        rule.setSceneGroupId(sceneGroupId);
        defaultRules.put(sceneGroupId, rule);
        rules.put(rule.getRuleId(), rule);
        
        log.info("Set default rule for scene: scene={}, ruleId={}", sceneGroupId, rule.getRuleId());
    }
    
    private KeyRule createDefaultRule(String sceneGroupId) {
        KeyRule rule = new KeyRule();
        rule.setRuleId("auto-default-" + sceneGroupId);
        rule.setRuleName("Auto Default Rule");
        rule.setDescription("Automatically created default rule");
        rule.setSceneGroupId(sceneGroupId);
        rule.setDefaultExpiresInSeconds(86400);
        rule.setDefaultMaxUseCount(1000);
        rule.setApprovalRequired(false);
        rule.setCreatedAt(System.currentTimeMillis());
        rule.setUpdatedAt(System.currentTimeMillis());
        
        rules.put(rule.getRuleId(), rule);
        
        log.info("Created auto default rule for scene: scene={}", sceneGroupId);
        return rule;
    }
}
