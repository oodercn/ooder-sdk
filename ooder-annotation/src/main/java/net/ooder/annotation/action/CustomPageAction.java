package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;


public enum CustomPageAction implements ActionType, CustomAction, Enumstype {

    CLOSE("关闭页面", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.destroy, new String[]{}, "true", true),

    RELOAD("刷新页面",
            CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.initData, new String[]{}, "true", true),

    EDITOR("编辑",
            CustomTarget.DYNEDITORMODULENAME.getName(), CustomModuleMethod.show2, new String[]{"{page.show2()}", CustomTarget.DYNEDITORMODULETARGET.getName(), null, null, null, null, "{args[1]}", "{page}", "{" + CustomTarget.EDITERMODULEDIO.getName() + "}"}, "true", true),

    MEDITOR("编辑",
            CustomTarget.DYNEDITORMODULENAME.getName(), CustomModuleMethod.show2, new String[]{"{page.show2()}", CustomTarget.DYNEDITORMODULETARGET.getName(), null, null, null, null, "{args[1]}", "{page}"}, "true", true),

    MORE("更多",
            CustomTarget.MOREMODULEDIO.getName(), CustomModuleMethod.show2, new String[]{"{page.show2()}", null, null, null, null, null, "{args[1]}", "{page}", "{" + CustomTarget.MOREMODULEDIO.getName() + "}"}, "true", true),

    ADD("新增",
            CustomTarget.DYNADDMODULENAME.getName(), CustomModuleMethod.show2, new String[]{null, null, null, null, null, null, "{page.getData()}", "{page}", "{" + CustomTarget.EDITERMODULEDIO.getName() + "}"}, "true", true),

    POP("弹出菜单",
            CustomTarget.DYNEDITORMODULENAME.getName(), CustomModuleMethod.show2, new String[]{"{page.show2()}", null, null, null, null, null, "{args[1]}", "{page}"}, "true", true),

    CLOSEPARENT("关闭父级页面", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.destroyParent, new String[]{}, "true", true),

    RELOADPARENT("刷新父级页面", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.reloadParent, new String[]{}, "true", true),

    RELOADMENU("刷新窗体菜单", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.reloadMenu, new String[]{}, "true", true),

    CLOSETOP("关闭当前窗体", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.destroyCurrDio, new String[]{}, "true", true),

    SAVEPARENT("保存父级", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.reloadParent, new String[]{}, "true", true),

    AUTOSAVE("自动保存", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.autoSave, new String[]{}, "true", true),

    RELOADTOP("刷新顶级页面", CustomTarget.TOPMODULE.getName(), CustomModuleMethod.initData, new String[]{}, "true", true),

    RELOADTOPPARENT("刷新顶级页面", CustomTarget.TOPMODULE.getName(), CustomModuleMethod.reloadParent, new String[]{}, "true", true),

    TREENODECLICK("点击", "true", true);


    private String desc;
    @JSONField(name = "type")
    private ActionTypeEnum actionType = ActionTypeEnum.page;
    private String expression;
    private String target;
    private Enumstype method;
    private boolean _return;
    private String redirection = "page::";
    private CustomCondition[] conditions;
    private String[] args;

    private String className;
    private String childName;
    private String okFlag;
    private String koFlag;
    private String script;
    private String[] params;

    CustomPageAction(String script, String[] params) {
        this.script = script;
        this.params = params;
    }

    CustomPageAction(String desc, String expression, boolean _return) {
        this.desc = desc;
        this.expression = expression;
        this._return = _return;

    }

    CustomPageAction(String desc, String target, Enumstype method, String[] args, String expression, boolean _return) {
        this.desc = desc;
        this.method = method;
        this.target = target;
        this.expression = expression;
        this.args = args;
        this._return = _return;
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
    public String script() {
        return script;
    }

    @Override
    public String[] params() {
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

    @Override
    public Enumstype getMethod() {
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
        if (method != null) {
            this.redirection = "page:" + target + ":" + method.getType();
        } else {
            this.redirection = "page::";
        }
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
