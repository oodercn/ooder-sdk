package net.ooder.annotation.event;


import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomCondition;

public interface ActionType {

    public String getDesc();

    public ActionTypeEnum getActionType();

    public String getTarget();

    public String[] getArgs();

    public Enumstype getMethod();

    public Boolean get_return();

    public CustomCondition[] getConditions();

    String getExpression();

    String getRedirection();
}

