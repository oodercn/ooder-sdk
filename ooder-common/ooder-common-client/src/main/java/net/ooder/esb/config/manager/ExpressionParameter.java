/**
 * $RCSfile: ExpressionParameter.java,v $
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

import net.ooder.annotation.FormulaParams;

import java.io.Serializable;

public interface ExpressionParameter extends Serializable {

    public String getParameterId();


    public void setParameterId(String parameterId);


    public String getParticipantSelectId();

    public void setParticipantSelectId(String participantId);


    public String getParameterName();

    public void setParameterName(String parameterName);


    public String getParameterCode();

    public void setParameterCode(String parameterCode);


    public String getParameterenName();

    public void setParameterenName(String parameterenName);

    public FormulaParams getParameterType();

    public String getValue();

    public void setValue(String value);

    public String getParameterDesc();

    public void setParameterDesc(String parameterDesc);

    public void setParameterType(FormulaParams parameterType);

    public String getParameterValue();

    public void setParameterValue(String parameterValue);

    public Boolean getSingle();

    public void setSingle(Boolean single);


}
