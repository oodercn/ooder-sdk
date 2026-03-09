package net.ooder.scene.skill.rule;

/**
 * 规则脚本模型
 * 
 * <p>定义规则的完整结构</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class RuleScript {

    private String ruleId;
    private String sceneId;
    private String name;
    private RuleType type;
    private String condition;
    private String action;
    private int priority;
    private boolean enabled;
    private long createdAt;
    private String createdBy;
    private String description;

    public RuleScript() {
        this.enabled = true;
        this.priority = 0;
        this.createdAt = System.currentTimeMillis();
    }

    public String getRuleId() {
        return ruleId;
    }

    public RuleScript setRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public String getSceneId() {
        return sceneId;
    }

    public RuleScript setSceneId(String sceneId) {
        this.sceneId = sceneId;
        return this;
    }

    public String getName() {
        return name;
    }

    public RuleScript setName(String name) {
        this.name = name;
        return this;
    }

    public RuleType getType() {
        return type;
    }

    public RuleScript setType(RuleType type) {
        this.type = type;
        return this;
    }

    public String getCondition() {
        return condition;
    }

    public RuleScript setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    public String getAction() {
        return action;
    }

    public RuleScript setAction(String action) {
        this.action = action;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public RuleScript setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public RuleScript setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public RuleScript setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public RuleScript setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RuleScript setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean hasCondition() {
        return condition != null && !condition.trim().isEmpty();
    }

    public boolean hasAction() {
        return action != null && !action.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "RuleScript{" +
            "ruleId='" + ruleId + '\'' +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", priority=" + priority +
            ", enabled=" + enabled +
            '}';
    }
}
