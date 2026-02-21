/**
 * $RCSfile: FormulaInstParams.java,v $
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
import net.ooder.annotation.FormulaParams;
import net.ooder.common.JDSException;
import net.ooder.esb.config.manager.ExpressionParameter;

import java.util.ArrayList;
import java.util.List;

public class FormulaInstParams {

    private String value;
    private String parameterDesc;
    private String parameterCode;
    private String parameterName;
    private String formulaInstId;
    private String conditionInstId;
    private Boolean single;
    FormulaParams paramsType;
    private List<ParamValues> parameterValues = new ArrayList();

    public FormulaInstParams() {

    }

    public FormulaInstParams(ExpressionParameter params, String formulaInstId) {
        this.formulaInstId = formulaInstId;
        initParams(params);
    }

    public FormulaInstParams(ExpressionParameter params, String formulaInstId, String conditionInstId) {
        this.formulaInstId = formulaInstId;
        this.conditionInstId = conditionInstId;
        initParams(params);
    }

    void initParams(ExpressionParameter params) {
        this.single = params.getSingle();
        this.parameterCode = params.getParameterCode();
        this.paramsType = params.getParameterType();
        this.parameterDesc = params.getParameterDesc();
        this.value = params.getValue();
        this.paramsType = params.getParameterType();
    }

    public void putValue(String key, String value) {
        ParamValues oldParam = this.getParamValue(key);
        if (oldParam == null) {
            oldParam = new ParamValues(this, key, key, value);
            parameterValues.add(oldParam);
        } else {
            oldParam.setValue(key);
        }
    }

    public void updateParam(ParamValues paramValue) {
        ParamValues oldParam = this.getParamValue(paramValue.getParameterName());
        if (oldParam != null) {
            parameterValues.remove(oldParam);
        }
        parameterValues.add(paramValue);
    }

    public void removeValue(String name) throws JDSException {
        ParamValues oldParam = this.getParamValue(name);
        if (oldParam != null) {
            parameterValues.remove(oldParam);
        }
    }

    @JSONField(serialize = false)
    public ParamValues getParamValue(String name) {
        ParamValues param = null;
        for (ParamValues oparam : this.getParameterValues()) {
            if (oparam.getParameterName().equals(name)) {
                param = oparam;
            }
        }
        return param;
    }

    public String getConditionInstId() {
        return conditionInstId;
    }

    public void setConditionInstId(String conditionInstId) {
        this.conditionInstId = conditionInstId;
    }

    public Boolean getSingle() {
        return single;
    }

    public void setSingle(Boolean single) {
        this.single = single;
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


    public String getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
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

    public List<ParamValues> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(List<ParamValues> parameterValues) {
        this.parameterValues = parameterValues;
    }
}
