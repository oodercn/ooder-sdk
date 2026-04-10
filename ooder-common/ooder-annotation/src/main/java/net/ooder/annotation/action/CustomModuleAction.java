package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;

import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.*;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;


public enum CustomModuleAction implements ActionType, CustomAction, Enumstype {

    CHECKVALID("检查必填项", CustomTarget.DYNCURRTOPCOMPONENTNAME.getName(), CustomModuleMethod.checkValid, new String[]{}, "true", false),
    ITEMSETDATA("填充数据", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.setData, new String[]{"{args[1]}"}, "true", true),
    INITDATA("填充数据", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.initData, new String[]{"{page.initData()}"}, "true", true),
    SETDATA("填充数据", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.setData, new String[]{"{page.getData()}"}, "true", true);

    private String desc;
    @JSONField(name = "type")
    private ActionTypeEnum actionType = ActionTypeEnum.module;
    private String expression;
    private String target;
    private CustomModuleMethod method;
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

    CustomModuleAction(String script, String[] params) {
        this.script=script;
        this.params=params;
    }


    CustomModuleAction(String desc, String target, CustomModuleMethod method, String[] args, String expression, boolean _return) {
        this.desc = desc;
        this.method = method;
        this.target = target;
        this.expression = expression;
        this.args = args;
        this._return = _return;

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
    public CustomModuleMethod getMethod() {
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
    public String script() {
        return script;
    }

    @Override
    public String[] params() {
        return params;
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
