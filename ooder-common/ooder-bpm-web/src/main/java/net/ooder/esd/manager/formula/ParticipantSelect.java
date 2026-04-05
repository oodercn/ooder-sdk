package net.ooder.esd.manager.formula;

import java.util.List;
import net.ooder.esb.config.manager.ExpressionParameter;

public interface ParticipantSelect {
    String getType();
    void setType(String type);
    String getExpression();
    void setExpression(String expression);
    String getParticipantSelectId();
    List<ExpressionParameter> getParameterList();
    String getFormula();
}
