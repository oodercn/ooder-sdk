package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.action.args.ShowModuleArgs;
import net.ooder.annotation.event.*;
import net.ooder.annotation.event.*;

import java.lang.annotation.Annotation;

public enum CustomLoadClassAction implements ActionType, CustomAction, Enumstype {
    tabShow("{args[1].euClassName}", "maincontent", "{args[1].id}"),
    show2("{args[1].euClassName}", "maincontent", "main"),
    IndexShow("{args[1].euClassName}", "maincontent", "main"),
    GalleryShow("{args[1].euClassName}", "maincontent", "main"),
    none("无");

    private String desc = "切换窗口";
    @JSONField(name = "type")
    private ActionTypeEnum actionType = ActionTypeEnum.other;
    private String expression;
    private String target = "url";

    private CustomGlobalMethod method = CustomGlobalMethod.showModule2;
    @JSONField(name = "return")
    private boolean _return;
    private String redirection = "other:callback:call";
    private CustomCondition[] conditions;
    private String[] args;
    private String script;
    private String[] params;

    String className;
    String childName;
    String okFlag;
    String koFlag;


    CustomLoadClassAction(TreeEvent treeEvent) {
        reSet(treeEvent.className(), treeEvent.targetFrame(), treeEvent.childName());
    }

    CustomLoadClassAction(ToolBarEvent treeEvent) {
        reSet(treeEvent.className(), treeEvent.targetFrame(), treeEvent.childName());
    }

    CustomLoadClassAction(String className, String targetFrame, String childName) {
        reSet(className, targetFrame, childName);
    }

    CustomLoadClassAction(String desc) {
        this.desc = desc;
    }

    public void reSet(ToolBarEvent toolBarEvent) {
        reSet(toolBarEvent.className(), toolBarEvent.targetFrame(), toolBarEvent.childName());
    }

    public void reSet(MenuBarEvent menuBarEvent) {
        reSet(menuBarEvent.className(), menuBarEvent.targetFrame(), menuBarEvent.childName());
    }

    public void reSet(TreeEvent treeEvent) {
        reSet(treeEvent.className(), treeEvent.targetFrame(), treeEvent.childName());
    }

    public void reSet(String className, String targetFrame, String childName) {
        this.className = className;
        this.childName = childName;
        ShowModuleArgs showModuleArgs = new ShowModuleArgs(className, targetFrame, childName);
        this.args = showModuleArgs.toArr().toArray(new String[]{});
    }

    public void reSet(String targetFrame, String childName) {
        this.childName = childName;
        ShowModuleArgs showModuleArgs = new ShowModuleArgs(className, targetFrame, childName);
        this.args = showModuleArgs.toArr().toArray(new String[]{});
    }

    @Override
    public String script() {
        return script;
    }

    @Override
    public String[] params() {
        return params;
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

    public void setMethod(CustomGlobalMethod method) {
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

    @Override
    public ActionTypeEnum getActionType() {
        return actionType;
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
