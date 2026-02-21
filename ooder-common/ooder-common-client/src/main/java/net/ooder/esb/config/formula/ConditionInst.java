/**
 * $RCSfile: ConditionInst.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.esb.config.formula;

public class ConditionInst extends FormulaInst {

    String conditionInstId;

    String currentClassName;

    String expression;


    public ConditionInst() {

    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getCurrentClassName() {
        return currentClassName;
    }

    public void setCurrentClassName(String currentClassName) {
        this.currentClassName = currentClassName;
    }

    public String getConditionInstId() {
        return conditionInstId;
    }

    public void setConditionInstId(String conditionInstId) {
        this.conditionInstId = conditionInstId;
    }
}
