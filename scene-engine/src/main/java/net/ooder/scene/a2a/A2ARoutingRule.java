package net.ooder.scene.a2a;

import java.util.regex.Pattern;

/**
 * A2A 路由规则
 *
 * <p>定义A2A消息的路由规则，支持多种匹配条件</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class A2ARoutingRule {
    
    private String ruleId;
    private String name;
    private String description;
    
    private String messageTypePattern;
    private String contentPattern;
    private String fromAgentPattern;
    private String sceneGroupPattern;
    
    private String targetAgentId;
    private String targetCapability;
    private String targetRole;
    
    private int priority;
    private boolean enabled = true;
    private long createdAt;
    private long updatedAt;
    
    private transient Pattern compiledMessageTypePattern;
    private transient Pattern compiledContentPattern;
    private transient Pattern compiledFromAgentPattern;
    private transient Pattern compiledSceneGroupPattern;
    
    public A2ARoutingRule() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
    
    public A2ARoutingRule(String ruleId, String pattern, String targetAgentId) {
        this();
        this.ruleId = ruleId;
        this.messageTypePattern = pattern;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMessageTypePattern() {
        return messageTypePattern;
    }
    
    public void setMessageTypePattern(String messageTypePattern) {
        this.messageTypePattern = messageTypePattern;
        this.compiledMessageTypePattern = null;
    }
    
    public String getContentPattern() {
        return contentPattern;
    }
    
    public void setContentPattern(String contentPattern) {
        this.contentPattern = contentPattern;
        this.compiledContentPattern = null;
    }
    
    public String getFromAgentPattern() {
        return fromAgentPattern;
    }
    
    public void setFromAgentPattern(String fromAgentPattern) {
        this.fromAgentPattern = fromAgentPattern;
        this.compiledFromAgentPattern = null;
    }
    
    public String getSceneGroupPattern() {
        return sceneGroupPattern;
    }
    
    public void setSceneGroupPattern(String sceneGroupPattern) {
        this.sceneGroupPattern = sceneGroupPattern;
        this.compiledSceneGroupPattern = null;
    }
    
    public String getTargetAgentId() {
        return targetAgentId;
    }
    
    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }
    
    public String getTargetCapability() {
        return targetCapability;
    }
    
    public void setTargetCapability(String targetCapability) {
        this.targetCapability = targetCapability;
    }
    
    public String getTargetRole() {
        return targetRole;
    }
    
    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
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
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Deprecated
    public String getPattern() {
        return messageTypePattern;
    }
    
    @Deprecated
    public void setPattern(String pattern) {
        this.messageTypePattern = pattern;
        this.compiledMessageTypePattern = null;
    }
    
    public boolean matches(A2AMessage message) {
        if (!enabled || message == null) {
            return false;
        }
        
        if (!matchMessageType(message)) {
            return false;
        }
        
        if (!matchFromAgent(message)) {
            return false;
        }
        
        if (!matchSceneGroup(message)) {
            return false;
        }
        
        if (!matchContent(message)) {
            return false;
        }
        
        return true;
    }
    
    private boolean matchMessageType(A2AMessage message) {
        if (messageTypePattern == null || messageTypePattern.isEmpty()) {
            return true;
        }
        
        if ("*".equals(messageTypePattern)) {
            return true;
        }
        
        if (message.getMessageType() == null) {
            return false;
        }
        
        if (messageTypePattern.equals(message.getMessageType().getCode())) {
            return true;
        }
        
        if (messageTypePattern.contains("*") || messageTypePattern.contains("?")) {
            if (compiledMessageTypePattern == null) {
                String regex = messageTypePattern.replace(".", "\\.")
                        .replace("*", ".*")
                        .replace("?", ".");
                compiledMessageTypePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
            return compiledMessageTypePattern.matcher(message.getMessageType().getCode()).matches();
        }
        
        return false;
    }
    
    private boolean matchFromAgent(A2AMessage message) {
        if (fromAgentPattern == null || fromAgentPattern.isEmpty()) {
            return true;
        }
        
        if ("*".equals(fromAgentPattern)) {
            return true;
        }
        
        String fromAgentId = message.getFromAgentId();
        if (fromAgentId == null) {
            return false;
        }
        
        if (fromAgentPattern.equals(fromAgentId)) {
            return true;
        }
        
        if (fromAgentPattern.contains("*") || fromAgentPattern.contains("?")) {
            if (compiledFromAgentPattern == null) {
                String regex = fromAgentPattern.replace(".", "\\.")
                        .replace("*", ".*")
                        .replace("?", ".");
                compiledFromAgentPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
            return compiledFromAgentPattern.matcher(fromAgentId).matches();
        }
        
        return false;
    }
    
    private boolean matchSceneGroup(A2AMessage message) {
        if (sceneGroupPattern == null || sceneGroupPattern.isEmpty()) {
            return true;
        }
        
        if ("*".equals(sceneGroupPattern)) {
            return true;
        }
        
        String sceneGroupId = message.getSceneGroupId();
        if (sceneGroupId == null) {
            return false;
        }
        
        if (sceneGroupPattern.equals(sceneGroupId)) {
            return true;
        }
        
        if (sceneGroupPattern.contains("*") || sceneGroupPattern.contains("?")) {
            if (compiledSceneGroupPattern == null) {
                String regex = sceneGroupPattern.replace(".", "\\.")
                        .replace("*", ".*")
                        .replace("?", ".");
                compiledSceneGroupPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
            return compiledSceneGroupPattern.matcher(sceneGroupId).matches();
        }
        
        return false;
    }
    
    private boolean matchContent(A2AMessage message) {
        if (contentPattern == null || contentPattern.isEmpty()) {
            return true;
        }
        
        Object payload = message.getPayload();
        if (payload == null) {
            return false;
        }
        
        String content = payload.toString();
        
        if ("*".equals(contentPattern)) {
            return true;
        }
        
        if (compiledContentPattern == null) {
            compiledContentPattern = Pattern.compile(contentPattern, Pattern.CASE_INSENSITIVE);
        }
        
        return compiledContentPattern.matcher(content).find();
    }
    
    public boolean hasTargetCapability() {
        return targetCapability != null && !targetCapability.isEmpty();
    }
    
    public boolean hasTargetRole() {
        return targetRole != null && !targetRole.isEmpty();
    }
    
    public boolean hasTargetAgentId() {
        return targetAgentId != null && !targetAgentId.isEmpty();
    }
    
    public void touch() {
        this.updatedAt = System.currentTimeMillis();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final A2ARoutingRule rule = new A2ARoutingRule();
        
        public Builder ruleId(String ruleId) {
            rule.setRuleId(ruleId);
            return this;
        }
        
        public Builder name(String name) {
            rule.setName(name);
            return this;
        }
        
        public Builder description(String description) {
            rule.setDescription(description);
            return this;
        }
        
        public Builder messageTypePattern(String messageTypePattern) {
            rule.setMessageTypePattern(messageTypePattern);
            return this;
        }
        
        public Builder contentPattern(String contentPattern) {
            rule.setContentPattern(contentPattern);
            return this;
        }
        
        public Builder fromAgentPattern(String fromAgentPattern) {
            rule.setFromAgentPattern(fromAgentPattern);
            return this;
        }
        
        public Builder sceneGroupPattern(String sceneGroupPattern) {
            rule.setSceneGroupPattern(sceneGroupPattern);
            return this;
        }
        
        public Builder targetAgentId(String targetAgentId) {
            rule.setTargetAgentId(targetAgentId);
            return this;
        }
        
        public Builder targetCapability(String targetCapability) {
            rule.setTargetCapability(targetCapability);
            return this;
        }
        
        public Builder targetRole(String targetRole) {
            rule.setTargetRole(targetRole);
            return this;
        }
        
        public Builder priority(int priority) {
            rule.setPriority(priority);
            return this;
        }
        
        public Builder enabled(boolean enabled) {
            rule.setEnabled(enabled);
            return this;
        }
        
        public A2ARoutingRule build() {
            return rule;
        }
    }
    
    @Override
    public String toString() {
        return "A2ARoutingRule{" +
                "ruleId='" + ruleId + '\'' +
                ", name='" + name + '\'' +
                ", messageTypePattern='" + messageTypePattern + '\'' +
                ", targetAgentId='" + targetAgentId + '\'' +
                ", targetCapability='" + targetCapability + '\'' +
                ", targetRole='" + targetRole + '\'' +
                ", priority=" + priority +
                ", enabled=" + enabled +
                '}';
    }
}
