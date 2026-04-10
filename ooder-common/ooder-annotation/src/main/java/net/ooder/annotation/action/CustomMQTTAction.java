package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;

import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.*;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;


public enum CustomMQTTAction implements ActionType, CustomAction, Enumstype {

    JMQCONFIG("连接消息服务", "userJMQ", CustomMQTTMethod.setProperties, null, new String[]{"{{}}", "{args[1].data.jmqConfig}"}, "true",  true),
    JMQCONNECT("连接消息服务", "userJMQ", CustomMQTTMethod.connect, "other:callback:call", new String[]{"{page.userJMQ.connect()}"}, "true",  false);

    private String desc;
    @JSONField(name = "type")
    private ActionTypeEnum actionType = ActionTypeEnum.control;
    private String expression;
    private String target;
    private CustomMQTTMethod method;
    @JSONField(name = "return")
    private Boolean _return=true;
    private String redirection;
    private CustomCondition[] conditions;
    private String[] args;
    String className;
    String childName;
    String okFlag;
    String koFlag;
    private String script;

    private String[] params;

    CustomMQTTAction(String script, String[] params) {
        this.script=script;
        this.params=params;
    }


    CustomMQTTAction(String desc, String target, CustomMQTTMethod method, String redirection, String[] args, String expression, Boolean _return) {
        this.desc = desc;
        this.method = method;
        this.target = target;
        this.expression = expression;
        this.redirection = redirection;
        this.args = args;
        this._return = _return;

    }

    @Override
    public String script() {
        return script;
    }

    @Override
    public String[] params() {
        return params;
    }

    public String getScript() {
        return script;
    }

    public String[] getParams() {
        return params;
    }

    @Override
    public String getType() {
        return this.name();
    }

    @Override
    public String getName() {
        return desc;
    }

    public Boolean get_return() {
        return _return;
    }

    @Override
    public String getDesc() {
        return desc;
    }
    @Override
    public String okFlag() {
        return okFlag;
    }

    @Override
    public String koFlag() {
        return koFlag;
    }

    @Override
    public String className() {
        return className;
    }

    @Override
    public String childName() {
        return childName;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public CustomMQTTMethod getMethod() {
        return method;
    }

    @Override
    public CustomCondition[] getConditions() {
        return conditions;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public String getRedirection() {
        return redirection;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setTarget(String target) {
        this.target = target;
    }


    @Override
    public String desc() {
        return desc;
    }

    @Override
    public ActionTypeEnum getActionType() {
        return actionType;
    }

    @Override
    public ActionTypeEnum type() {
        return actionType;
    }

    @Override
    public String expression() {
        return expression;
    }

    @Override
    public String target() {
        return target;
    }

    @Override
    public String method() {
        return method.getType();
    }

    @Override
    public boolean _return() {
        return _return;
    }

    @Override
    public String redirection() {
        return redirection;
    }

    @Override
    public CustomCondition[] conditions() {
        return conditions;
    }

    @Override
    public String[] args() {
        return args;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CustomAction.class;
    }
}
