package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.*;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;

public enum CustomDynModuleAction implements ActionType, CustomAction, Enumstype {

    DYNRELOAD("重新装载", "true",  true),

    LOADMENU("装载菜单", "true",  false),

    DUMP("镜像", "true",  false),

    SAVE("保存", "true",  true);


    private String desc;
    @JSONField(name = "type")
    private ActionTypeEnum actionType = ActionTypeEnum.control;
    private String expression;
    private String target;
    private CustomAPIMethod method = CustomAPIMethod.invoke;
    @JSONField(name = "return")
    private Boolean _return=true;
    private String redirection = "other:callback:call";
    private CustomCondition[] conditions;
    private String[] args;

    String className;
    String childName;
    String okFlag;
    String koFlag;
    private String script;

    private String[] params;

    CustomDynModuleAction(String script, String[] params) {
        this.script=script;
        this.params=params;
    }


    CustomDynModuleAction(String desc, String expression, boolean _return) {
        this.desc = desc;
        this.expression = expression;
        this.target = this.name();
        this.args = new String[]{"{page." + name() + ".invoke()}"};
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



    @Override
    public String getType() {
        return this.name();
    }

    @Override
    public String getName() {
        return desc;
    }

    void updateTagter(String target) {
        this.target = target;
        args[0] = "{page." + target + ".invoke()}";
    }


    public Boolean get_return() {
        return _return;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
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
    public CustomAPIMethod getMethod() {
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

    public void setDesc(String desc) {
        this.desc = desc;
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
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setMethod(CustomAPIMethod method) {
        this.method = method;
    }

    public void set_return(Boolean _return) {
        this._return = _return;
    }

    public void setRedirection(String redirection) {
        this.redirection = redirection;
    }

    public void setConditions(CustomCondition[] conditions) {
        this.conditions = conditions;
    }

    public void setArgs(String[] args) {
        this.args = args;
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
