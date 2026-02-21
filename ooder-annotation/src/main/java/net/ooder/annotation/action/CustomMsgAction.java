package net.ooder.annotation.action;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.CustomCondition;
import net.ooder.annotation.event.ActionType;
import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.Annotation;

public enum CustomMsgAction implements ActionType, CustomAction, Enumstype {
    ALERT("警告", CustomMsgMethod.alert, new String[]{"{args[0].title}", "{args[1].message}"}),
    ECHO("调试日志", CustomMsgMethod.echo, new String[]{"{args[1].title}", "{args[1].message}", "{page.projectName}", "{page.getValue()}"}),
    CONFIRM("确认框", CustomMsgMethod.confirm, new String[]{"{args[1].title}", "{args[1].message}"}),
    PROMPT("提示对话框", CustomMsgMethod.prompt, new String[]{"{args[1].title}", "{args[1].message}"}),
    WARN("警告确认", "{args[1].title}", CustomMsgMethod.confirm, new String[]{"确认操作？", "确认操作吗？该操作可能不可恢复！"}),
    MESSAGE("提示框", CustomMsgMethod.message, new String[]{"{args[1].title}"}),
    SUCCESSMSG("成功调用提示", CustomMsgMethod.message, new String[]{"{args[1].title||'操作成功'}"}),
    ERRORMSG("错误信息提示", CustomMsgMethod.alert, new String[]{null, "{args[1].title}", "{args[1].errdes||args[1].message}"}),
    BUSY("遮罩", CustomMsgMethod.busy, new String[]{"{false}", "{args[1].message||'正在处理...'}"}),
    FREE("解除遮罩", CustomMsgMethod.free, new String[]{}),
    MSG("消息", CustomMsgMethod.msg, new String[]{"{args[1].title}", "{args[1].message}", "200", "5000"}),
    LOG("console日志", CustomMsgMethod.log, new String[]{"{args[1].title}", "{args[1].message}"});

    private String desc;

    @JSONField(name = "type")
    private ActionTypeEnum actionType;
    private String target;
    private CustomMsgMethod method;
    private String expression;
    private String redirection;
    @JSONField(name = "return")
    private Boolean _return = true;

    private String dynReturn;

    private CustomCondition[] conditions;
    String className;
    String childName;
    String okFlag;
    String koFlag;
    private String[] args;

    private String script;

    private String[] params;

    CustomMsgAction(String script, String[] params) {
        this.script = script;
        this.params = params;
    }

    CustomMsgAction(String desc, CustomMsgMethod method, String[] args) {
        this.desc = desc;
        this.actionType = ActionTypeEnum.other;
        this.target = "msg";
        this.method = method;
        this.args = args;
    }

    CustomMsgAction(String desc, String dynReturn, CustomMsgMethod method, String[] args) {
        this.desc = desc;
        this.actionType = ActionTypeEnum.other;
        this.target = "msg";
        this.dynReturn = dynReturn;
        this.method = method;
        this.args = args;
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

    @Override
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
    public String getTarget() {
        return target;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public CustomMsgMethod getMethod() {
        return method;
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
    public CustomCondition[] getConditions() {
        return conditions;
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
        return new CustomCondition[0];
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
    public String[] args() {
        return args;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CustomAction.class;
    }
}
