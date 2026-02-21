package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Panel组件事件枚举
 * 对应JS中的ood.UI.Panel.EventHandlers定义
 */
public enum PanelEventEnum implements EventKey {
    // 初始化面板视图事件
    onIniPanelView("onIniPanelView", "初始化面板视图", "profile"),
    
    // 折叠前事件
    beforeFold("beforeFold", "折叠前", "profile"),
    
    // 展开前事件
    beforeExpand("beforeExpand", "展开前", "profile"),
    
    // 折叠后事件
    afterFold("afterFold", "折叠后", "profile"),
    
    // 展开后事件
    afterExpand("afterExpand", "展开后", "profile"),
    
    // 点击栏事件
    onClickBar("onClickBar", "点击栏", "profile", "src"),
    
    // 点击面板事件
    onClickPanel("onClickPanel", "点击面板", "profile", "e", "src"),
    
    // 弹出前事件
    beforePop("beforePop", "弹出前", "profile", "options", "e", "src"),
    
    // 关闭前事件
    beforeClose("beforeClose", "关闭前", "profile"),
    
    // 显示信息事件
    onShowInfo("onShowInfo", "显示信息", "profile", "e", "src"),
    
    // 显示选项事件
    onShowOptions("onShowOptions", "显示选项", "profile", "e", "src"),
    
    // 刷新事件
    onRefresh("onRefresh", "刷新时", "profile"),
    
    // 命令事件
    onCmd("onCmd", "命令执行", "profile", "cmdkey", "e", "src"),
    
    // 父类事件
    beforeRender("beforeRender", "渲染前", "profile"),
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
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos");

    private String event;
    private String[] params;
    private String name;

    PanelEventEnum(String event, String name, String... args) {
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
}