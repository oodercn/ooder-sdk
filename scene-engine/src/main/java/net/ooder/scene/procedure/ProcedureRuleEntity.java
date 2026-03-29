package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.ProcedureRule;
import net.ooder.sdk.api.procedure.ProcedureRuleType;
import net.ooder.sdk.api.procedure.ErrorAction;

import java.util.HashMap;
import java.util.Map;

/**
 * 规范约束规则实体实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class ProcedureRuleEntity implements ProcedureRule {

    private static final long serialVersionUID = 1L;

    private String ruleId;
    private String name;
    private ProcedureRuleType type;
    private String description;
    private String expression;
    private int priority;
    private String errorMessage;
    private ErrorAction errorAction = ErrorAction.WARN;
    private Map<String, Object> extensions = new HashMap<>();

    public ProcedureRuleEntity() {
    }

    public ProcedureRuleEntity(String ruleId, String name, ProcedureRuleType type) {
        this.ruleId = ruleId;
        this.name = name;
        this.type = type;
    }

    @Override
    public String getRuleId() {
        return ruleId;
    }

    @Override
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ProcedureRuleType getType() {
        return type;
    }

    @Override
    public void setType(ProcedureRuleType type) {
        this.type = type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public ErrorAction getErrorAction() {
        return errorAction;
    }

    @Override
    public void setErrorAction(ErrorAction errorAction) {
        this.errorAction = errorAction;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public String toString() {
        return "ProcedureRuleEntity{" +
                "ruleId='" + ruleId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", priority=" + priority +
                '}';
    }
}
