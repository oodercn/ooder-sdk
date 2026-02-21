package net.ooder.annotation;

import net.ooder.annotation.IconEnumstype;

public enum RouteToType implements IconEnumstype {
    // 将所有"fa"前缀改为"fas"，并更新特定图标名称


    ReSend("重新发送", "ri-send-plane-line", "$currActivityInst.isCanReSend()"),

    RouteBack("退回", "ri-arrow-go-back-line", "$currActivityInst.isCanRouteBack()"),

    TackBack("收回", "bpmfont bpmshouhui", "$currActivityInst.isCanTakeBack()"),


    SignReceive("签收", "bpmfont bpmziyuan1", "$currActivityInst.isCanSignReceive()"),

    EndRead("阅毕", "ri-book-open-line", "$currActivityInst.isCanEndRead()"),
    EndTask("结束办理", "ri-list-check", "$currActivityInst.isCanTakeBack()"),
    RouteTo("发送", "ri-send-plane-line", "$currActivityInst.activityDef.split.type!='AND'"),
    AutoNext("自动推进", "ri-send-plane-line", "true"),
    PerformEnd("结束办理", "ri-send-plane-line", "true"),
    SelectPersons("选择办理人", "ri-send-plane-line", "true"),
    View("查看历史", "ri-history-line", "true"),
    SaveOnly("保存", "ri-check-line", "$currActivityInst.isCanPerform()"),
    MultiSelect("并行选择", "ri-git-branch-line", "$currActivityInst.activityDef.split.type=='AND'"),
    Multirouteto("并行发送", "ri-git-branch-line", "$currActivityInst.activityDef.split.type=='AND'"),
    Changeperformer("更换办理人", "ri-user-line", "true"),
    CanSpecialSend("特送", "ri-rocket-line", "$currActivityInst.isCanSpecialSend()"),
    Print("打印", "ri-printer-line", "true"),
    Reload("刷新", "ri-loader-line", "true"),
    RouteToEnd("结束", "ri-flag-line", "$currActivityInst.isCanCompleteProcessInst()");


    private String type;

    private String name;

    private String imageClass;

    private String expression;


    RouteToType(String name, String imageClass, String expression) {
        this.type = name();
        this.name = name;
        this.imageClass = imageClass;
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }


    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
