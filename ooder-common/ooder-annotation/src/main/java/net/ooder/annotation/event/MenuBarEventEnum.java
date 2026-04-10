package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * MenuBar组件事件枚举
 * 对应JS中的ood.UI.MenuBar.EventHandlers定义
 */
public enum MenuBarEventEnum implements EventKey {
    // 获取弹出菜单事件
    onGetPopMenu("onGetPopMenu", "获取弹出菜单", "profile", "item", "callback"),
    
    // 菜单按钮点击事件
    onMenuBtnClick("onMenuBtnClick", "菜单按钮点击", "profile", "item", "src"),
    
    // 弹出菜单前事件
    beforePopMenu("beforePopMenu", "弹出菜单前", "profile", "item", "src"),
    
    // 显示子菜单事件
    onShowSubMenu("onShowSubMenu", "显示子菜单", "profile", "popProfile", "item", "src"),
    
    // 菜单选择事件
    onMenuSelected("onMenuSelected", "菜单选择", "profile", "popProfile", "item", "src"),
    
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

    MenuBarEventEnum(String event, String name, String... args) {
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