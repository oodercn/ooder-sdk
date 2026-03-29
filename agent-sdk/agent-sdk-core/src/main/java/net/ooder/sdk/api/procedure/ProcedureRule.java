package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.Map;

/**
 * Procedure Rule Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ProcedureRule extends Serializable {

    String getRuleId();
    void setRuleId(String ruleId);

    String getName();
    void setName(String name);

    ProcedureRuleType getType();
    void setType(ProcedureRuleType type);

    String getDescription();
    void setDescription(String description);

    String getExpression();
    void setExpression(String expression);

    int getPriority();
    void setPriority(int priority);

    String getErrorMessage();
    void setErrorMessage(String errorMessage);

    ErrorAction getErrorAction();
    void setErrorAction(ErrorAction errorAction);

    Map<String, Object> getExtensions();
    void setExtensions(Map<String, Object> extensions);
}
