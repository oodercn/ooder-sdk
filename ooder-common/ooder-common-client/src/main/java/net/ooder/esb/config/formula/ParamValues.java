/**
 * $RCSfile: ParamValues.java,v $
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

import net.ooder.annotation.FormulaParams;

public class ParamValues {

    private String value;
    private String parameterName;
    private String parameterDesc;
    private String formulaInstId;
    private String conditionInstId;
    private String parameterCode;
    FormulaParams paramsType;

    public ParamValues() {

    }


    public ParamValues(FormulaInstParams params, String key, String value, String desc) {
        this.parameterCode = params.getParameterCode();
        this.formulaInstId = params.getFormulaInstId();
        this.value = value;
        this.parameterName = key;
        this.parameterDesc = desc;
        this.conditionInstId = params.getConditionInstId();
        this.paramsType = params.getParamsType();

    }


    public String getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public String getConditionInstId() {
        return conditionInstId;
    }

    public void setConditionInstId(String conditionInstId) {
        this.conditionInstId = conditionInstId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FormulaParams getParamsType() {
        return paramsType;
    }

    public void setParamsType(FormulaParams paramsType) {
        this.paramsType = paramsType;
    }

    public String getParameterCode() {
        return parameterCode;
    }

    public void setParameterCode(String parameterCode) {
        this.parameterCode = parameterCode;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getFormulaInstId() {
        return formulaInstId;
    }

    public void setFormulaInstId(String formulaInstId) {
        this.formulaInstId = formulaInstId;
    }

}
