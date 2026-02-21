/**
 * $RCSfile: FormulaInst.java,v $
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

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.FormulaType;
import net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.List;

public class FormulaInst {

    String formulaInstId;

    String expression;

    String projectName;

    String name;

    String selectDesc;

    String participantSelectId;

    FormulaType formulaType;

    List<ConditionInst> conditions = new ArrayList<>();

    List<FormulaInstParams> params = new ArrayList<>();

    public FormulaInst() {

    }


    public void updateCondition(ConditionInst conditionInst) throws JDSException {
        ConditionInst oconditionInst = this.getCondition(conditionInst.getConditionInstId());
        if (oconditionInst != null) {
            conditions.remove(oconditionInst);
        }
        conditions.add(oconditionInst);
    }

    public void removeCondition(String conditionInstId) throws JDSException {
        ConditionInst conditionInst = this.getCondition(conditionInstId);
        if (conditionInst != null) {
            conditions.remove(conditionInst);
        }
    }

    @JSONField(serialize = false)
    public ConditionInst getCondition(String conditionInstId) {
        for (ConditionInst conditionInst : this.getConditions()) {
            if (conditionInst.getConditionInstId() != null && conditionInst.getConditionInstId().equals(conditionInstId)) {
                return conditionInst;
            }
        }
        return null;
    }


    public void updateParam(FormulaInstParams param) throws JDSException {
        FormulaInstParams oldParam = this.getParam(param.getParameterCode());
        if (oldParam != null) {
            params.remove(oldParam);
        }
        params.add(param);
    }

    public void removeParam(String paramId) throws JDSException {
        FormulaInstParams oldParam = this.getParam(paramId);
        if (oldParam != null) {
            params.remove(oldParam);
        }
    }

    @JSONField(serialize = false)
    public FormulaInstParams getParam(String paramCode) {
        FormulaInstParams param = null;
        for (FormulaInstParams oparam : this.getParams()) {
            if (oparam.getFormulaInstId() != null && oparam.getFormulaInstId().equals(paramCode)) {
                param = oparam;
            } else if (oparam.getParameterCode() != null && oparam.getParameterCode().equals(paramCode)) {
                param = oparam;
            }
        }
        return param;
    }

    public List<ConditionInst> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionInst> conditions) {
        this.conditions = conditions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormulaInstId() {
        return formulaInstId;
    }

    public String getParticipantSelectId() {
        return participantSelectId;
    }

    public void setParticipantSelectId(String participantSelectId) {
        this.participantSelectId = participantSelectId;
    }

    public void setFormulaInstId(String formulaInstId) {
        this.formulaInstId = formulaInstId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public FormulaType getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(FormulaType formulaType) {
        this.formulaType = formulaType;
    }

    public List<FormulaInstParams> getParams() {
        return params;
    }

    public void setParams(List<FormulaInstParams> params) {
        this.params = params;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSelectDesc() {
        return selectDesc;
    }

    public void setSelectDesc(String selectDesc) {
        this.selectDesc = selectDesc;
    }
}
