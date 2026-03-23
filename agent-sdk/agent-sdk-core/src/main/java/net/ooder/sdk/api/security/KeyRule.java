package net.ooder.sdk.api.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KeyRule implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String ruleId;
    private String ruleName;
    private String description;
    
    private String sceneGroupId;
    private long defaultExpiresInSeconds;
    private int defaultMaxUseCount;
    private int defaultUsageLimit;
    private List<String> allowedScenes;
    private List<String> allowedOperations;
    private boolean approvalRequired;
    
    private long createdAt;
    private long updatedAt;
    
    public KeyRule() {
        this.allowedScenes = new ArrayList<String>();
        this.allowedOperations = new ArrayList<String>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
    
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public long getDefaultExpiresInSeconds() { return defaultExpiresInSeconds; }
    public void setDefaultExpiresInSeconds(long defaultExpiresInSeconds) { this.defaultExpiresInSeconds = defaultExpiresInSeconds; }
    
    public int getDefaultMaxUseCount() { return defaultMaxUseCount; }
    public void setDefaultMaxUseCount(int defaultMaxUseCount) { this.defaultMaxUseCount = defaultMaxUseCount; }
    
    public int getDefaultUsageLimit() { return defaultUsageLimit; }
    public void setDefaultUsageLimit(int defaultUsageLimit) { this.defaultUsageLimit = defaultUsageLimit; }
    
    public List<String> getAllowedScenes() { return allowedScenes; }
    public void setAllowedScenes(List<String> allowedScenes) { this.allowedScenes = allowedScenes; }
    
    public List<String> getAllowedOperations() { return allowedOperations; }
    public void setAllowedOperations(List<String> allowedOperations) { this.allowedOperations = allowedOperations; }
    
    public boolean isApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(boolean approvalRequired) { this.approvalRequired = approvalRequired; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    public static KeyRule createDefaultRule(String sceneGroupId) {
        KeyRule rule = new KeyRule();
        rule.setRuleId("rule-" + UUID.randomUUID().toString());
        rule.setRuleName("Default Rule");
        rule.setSceneGroupId(sceneGroupId);
        rule.setDefaultExpiresInSeconds(86400);
        rule.setDefaultMaxUseCount(1000);
        rule.setDefaultUsageLimit(1000);
        rule.setCreatedAt(System.currentTimeMillis());
        return rule;
    }
}
