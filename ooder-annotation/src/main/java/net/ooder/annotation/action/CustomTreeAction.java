package net.ooder.annotation.action;


import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;

public enum CustomTreeAction implements ActionType, CustomAction, Enumstype {


    RELOAD("重新装载", "true", true),

    RELOADCHILD("刷新节点", "true", true),

    DELETE("刪除", "true", false),

    SORTUP("向上", "true", false),

    MSG("消息", "true", false),

    LOADMENU("装载菜单", "true", false),

    SAVEROW("保存", "true", true),

    EDITOR("编辑", "true", true),

    CONFIG("配置", "true", true),

    SORTDOWN("向下", "true", false),

    RESET("重置", "true", true),

    SAVE("保存", "true", true);


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


    CustomTreeAction(String script, String[] params) {
        this.script = script;
        this.params = params;
    }

    CustomTreeAction(String desc, String expression, boolean _return) {
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

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
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
