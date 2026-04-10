package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;

public enum LocalTreeAction implements ActionType, CustomAction, Enumstype {

    OPENTONODE(CustomTreeMethod.openToNode, "{true}", true, "{args[1].ids[0]}"),

    TOGGLENODE(CustomTreeMethod.toggleNode, "{true}", true, "{args[1].ids[0]}"),

    INSERTITEMS(CustomTreeMethod.insertItems, "{true}", true, "{args[1].data}"),

    REMOVEITEMS(CustomTreeMethod.insertItems, "{true}", true, "{args[1].ids}"),

    CLEARITEMS(CustomTreeMethod.clearItems, "{true}", true, "{args[1].ids[0]}"),

    RELOADNODE(CustomTreeMethod.reloadNode, "{true}", true, "{args[1].ids[0]}"),

    FIRETOGGLENODE(CustomTreeMethod.toggleNode, "{true}", true, "{args[1].id}"),

    FIREITEMCLICKEVENT(CustomTreeMethod.fireItemClickEvent, "{true}", true, "{args[1].ids[0]}");


    private String desc;
    @JSONField(name = "type")
    private ActionTypeEnum actionType = ActionTypeEnum.control;
    private String expression;
    private String target = "@{this.currComponent.alias}";
    private CustomTreeMethod method;
    private Boolean _return=true;
    private CustomCondition[] conditions;
    private String redirection;
    private String[] args;
    String className;
    String childName;
    String okFlag;
    String koFlag;
    private String script;

    private String[] params;

    LocalTreeAction(String script, String[] params) {
        this.script = script;
        this.params = params;
    }


    LocalTreeAction(CustomTreeMethod method, String expression, Boolean _return, String... args) {
        this.desc = method.getName();
        this.method = method;
        this.expression = expression;
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

    @Override
    public String getType() {
        return this.name();
    }

    @Override
    public String getName() {
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public CustomTreeMethod getMethod() {
        return method;
    }

    public void setMethod(CustomTreeMethod method) {
        this.method = method;
    }

    public void set_return(Boolean _return) {
        this._return = _return;
    }

    public void setConditions(CustomCondition[] conditions) {
        this.conditions = conditions;
    }

    public void setRedirection(String redirection) {
        this.redirection = redirection;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getOkFlag() {
        return okFlag;
    }

    public void setOkFlag(String okFlag) {
        this.okFlag = okFlag;
    }

    public String getKoFlag() {
        return koFlag;
    }

    public void setKoFlag(String koFlag) {
        this.koFlag = koFlag;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setParams(String[] params) {
        this.params = params;
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
