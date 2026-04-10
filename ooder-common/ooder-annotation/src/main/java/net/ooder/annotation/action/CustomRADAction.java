package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.*;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;


public enum CustomRADAction implements ActionType, CustomAction, Enumstype {
    RELOADPAGE("刷新页面", CustomGlobalMethod.call, "callback", ActionTypeEnum.callback, new String[]{"{SAP.reloadPage()}", null, null, "{page}"}, CustomGlobalMethod.call.getType(), "true", true),
    OPENCLS("打开文件", CustomGlobalMethod.call, "callback", ActionTypeEnum.callback, new String[]{"{SAP.openCls()}", null, null, "{args[1].tagVar"}, CustomGlobalMethod.call.getType(), "true", true);

    private String desc;
    @JSONField(name = "type")
    private ActionTypeEnum actionType;
    private String expression;
    private String target;
    private CustomGlobalMethod method;
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

    CustomRADAction(String script, String[] params) {
        this.script=script;
        this.params=params;
    }

    CustomRADAction(String desc, CustomGlobalMethod method, String target, ActionTypeEnum type, String[] args, String redirection, String expression, boolean _return) {
        this.desc = desc;
        this.actionType = type;
        this.redirection = redirection;
        this.expression = expression;
        this.target = target;
        this.method = method;
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
    public String getTarget() {
        return target;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public CustomGlobalMethod getMethod() {
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
    public ActionTypeEnum getActionType() {
        return actionType;
    }


    @Override
    public String desc() {
        return desc;
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
