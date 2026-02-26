package net.ooder.sdk.migration;

import java.util.function.Predicate;

/**
 * 迁移规则
 */
public class MigrationRule {
    private String id;
    private String name;
    private String description;
    private Predicate<MigrationOpportunity> condition;
    private Action action;
    private int priority;
    private boolean enabled;

    public enum Action {
        ALLOW, BLOCK, REQUIRE_APPROVAL, NOTIFY
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Predicate<MigrationOpportunity> getCondition() {
        return condition;
    }

    public void setCondition(Predicate<MigrationOpportunity> condition) {
        this.condition = condition;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
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

    public boolean matches(MigrationOpportunity opportunity) {
        return enabled && condition != null && condition.test(opportunity);
    }
}
