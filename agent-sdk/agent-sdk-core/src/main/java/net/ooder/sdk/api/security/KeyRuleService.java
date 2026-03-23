package net.ooder.sdk.api.security;

import java.util.List;

public interface KeyRuleService {
    
    KeyRule createRule(KeyRule rule);
    
    KeyRule getRule(String ruleId);
    
    List<KeyRule> getAllRules();
    
    List<KeyRule> getRulesByScene(String sceneGroupId);
    
    KeyRule updateRule(KeyRule rule);
    
    boolean deleteRule(String ruleId);
    
    KeyEntity applyRule(String keyId, String ruleId);
    
    KeyRule getRecommendedRule(RequestType requestType, String sceneGroupId);
    
    KeyRule getDefaultRule(String sceneGroupId);
    
    void setDefaultRule(String sceneGroupId, KeyRule rule);
}
