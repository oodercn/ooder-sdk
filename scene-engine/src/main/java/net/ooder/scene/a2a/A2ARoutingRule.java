package net.ooder.scene.a2a;

/**
 * A2A 路由规则
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class A2ARoutingRule {
    
    private String ruleId;
    private String name;
    private String pattern;
    private String targetAgentId;
    private int priority;
    private boolean enabled = true;
    
    public A2ARoutingRule() {
    }
    
    public A2ARoutingRule(String ruleId, String pattern, String targetAgentId) {
        this.ruleId = ruleId;
        this.pattern = pattern;
        this.targetAgentId = targetAgentId;
    }
    
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public String getTargetAgentId() {
        return targetAgentId;
    }
    
    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean matches(A2AMessage message) {
        if (!enabled || pattern == null) {
            return false;
        }
        
        if (pattern.equals("*")) {
            return true;
        }
        
        if (message.getMessageType() != null) {
            return pattern.equalsIgnoreCase(message.getMessageType().getCode());
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return "A2ARoutingRule{" +
                "ruleId='" + ruleId + '\'' +
                ", pattern='" + pattern + '\'' +
                ", targetAgentId='" + targetAgentId + '\'' +
                ", priority=" + priority +
                '}';
    }
}
