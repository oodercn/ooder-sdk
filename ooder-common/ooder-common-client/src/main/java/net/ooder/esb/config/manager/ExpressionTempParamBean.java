/**
 * $RCSfile: ExpressionTempParamBean.java,v $
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
package net.ooder.esb.config.manager;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.FormulaParams;
import net.ooder.annotation.MethodChinaName;

/**
 * time 06-01-01
 *
 * @author wenzhang
 */
public class ExpressionTempParamBean implements ExpressionParameter {


    private String parameterId;


    @JSONField(name = "participantSelectId")
    private String participantSelectId;


    private String parameterenName;

    @MethodChinaName(cname = "参数代码")
    private String parameterCode;

    @MethodChinaName(cname = "参数名称")
    private String parameterName;

    @MethodChinaName(cname = "参数类型")
    private FormulaParams parameterType;

    @MethodChinaName(cname = "参数值")
    private String value;

    @MethodChinaName(cname = "参数描述")
    private String parameterDesc;

    @MethodChinaName(cname = "单个")
    private Boolean single = false;

    @MethodChinaName(cname = "参数值")
    private String parameterValue;


    @Override
    public String getParameterId() {
        return parameterId;
    }

    @Override
    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    @Override
    public String getParticipantSelectId() {
        return participantSelectId;
    }

    @Override
    public void setParticipantSelectId(String participantSelectId) {
        this.participantSelectId = participantSelectId;
    }

    @Override
    public String getParameterenName() {
        return parameterenName;
    }

    @Override
    public void setParameterenName(String parameterenName) {
        this.parameterenName = parameterenName;
    }

    @Override
    public String getParameterCode() {
        return parameterCode;
    }

    @Override
    public void setParameterCode(String parameterCode) {
        this.parameterCode = parameterCode;
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

    @Override
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public FormulaParams getParameterType() {
        return parameterType;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getParameterDesc() {
        return parameterDesc;
    }

    @Override
    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }


    @Override
    public String getParameterValue() {
        return parameterValue;
    }

    @Override
    public void setParameterValue(String parameterValue) {

        this.parameterValue = parameterValue;
    }

    @Override
    public void setParameterType(FormulaParams parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public Boolean getSingle() {
        return single;
    }

    @Override
    public void setSingle(Boolean single) {
        this.single = single;
    }
}
