package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum DialogEventEnum implements EventKey {

    onIniPanelView("onIniPanelView", "初始化面板视图", "profile"),
    onRender("onRender", "渲染时", "profile"),
    onLayout("onLayout", "布局时", "profile"),
    onResize("onResize", "调整大小", "profile", "width", "height"),

    onMove("onMove", "移动时", "profile", "left", "top", "right", "bottom"),
    onDock("onDock", "停靠时", "profile", "region"),
    beforePropertyChanged("beforePropertyChanged", "属性变更前", "profile", "name", "value", "ovalue"),
    afterPropertyChanged("afterPropertyChanged", "属性变更后", "profile", "name", "value", "ovalue"),

    beforeAppend("beforeAppend", "添加前", "profile", "child"),
    afterAppend("afterAppend", "添加后", "profile", "child"),
    beforeRemove("beforeRemove", "移除前", "profile", "child", "subId", "bdestroy"),
    afterRemove("afterRemove", "移除后", "profile", "child", "subId", "bdestroy"),

    onDestroy("onDestroy", "销毁时", "profile"),
    beforeDestroy("beforeDestroy", "销毁前", "profile"),

    afterDestroy("afterDestroy", "销毁后", "profile"),
    onShowTips("onShowTips", "显示提示", "profile", "node", "pos"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),

    onShow("onShow", "显示时", "profile"),
    onActivated("onActivated", "激活时", "profile"),
    beforePin("beforePin", "固定前", "profile"),

    beforeStatusChanged("beforeStatusChanged", "状态变更前", "profile"),
    afterStatusChanged("afterStatusChanged", "状态变更后", "profile"),
    onClickPanel("onClickPanel", "点击面板", "profile", "e", "src"),
    beforePop("beforePop", "弹出前", "profile", "options", "e", "src"),

    onLand("onLand", "着陆时", "profile"),
    beforeClose("beforeClose", "关闭前", "profile"),
    onShowOptions("onShowOptions", "显示选项", "profile", "e", "src"),
    onRefresh("onRefresh", "刷新时", "profile"),

    onShowInfo("onShowInfo", "显示信息", "profile", "e", "src"),

    onCmd("onCmd", "命令执行", "profile", "cmdkey", "e", "src");

    private String event;
    private String[] params;
    private String name;

    DialogEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    DialogEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return event;
    }
}