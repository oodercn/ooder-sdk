/**
 * $RCSfile: ConditionBean.java,v $
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
package net.ooder.ai.bean;

import net.ooder.common.ConditionOperator;

import java.io.Serializable;

/**
 * Condition注解对应的Bean类
 */
public class ConditionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String expression;
    private ConditionOperator operator;
    private String targetStep;
    private String elseTargetStep;
    private String errorStep;
    private boolean continueOnError;

    // 构造函数
    public ConditionBean() {
        this.operator = ConditionOperator.EQUALS;
        this.continueOnError = false;
    }

    // Getter和Setter方法
    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public ConditionOperator getOperator() { return operator; }
    public void setOperator(ConditionOperator operator) { this.operator = operator; }

    public String getTargetStep() { return targetStep; }
    public void setTargetStep(String targetStep) { this.targetStep = targetStep; }

    public String getElseTargetStep() { return elseTargetStep; }
    public void setElseTargetStep(String elseTargetStep) { this.elseTargetStep = elseTargetStep; }

    public String getErrorStep() { return errorStep; }
    public void setErrorStep(String errorStep) { this.errorStep = errorStep; }

    public boolean isContinueOnError() { return continueOnError; }
    public void setContinueOnError(boolean continueOnError) { this.continueOnError = continueOnError; }
}