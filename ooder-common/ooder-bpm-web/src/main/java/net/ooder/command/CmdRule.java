package net.ooder.command;

import net.ooder.annotation.Enums;

public class CmdRule implements Enums {
    private String ruleName;
    private String ruleExpression;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }
}
