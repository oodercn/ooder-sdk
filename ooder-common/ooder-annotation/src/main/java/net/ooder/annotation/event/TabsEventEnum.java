package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Tabs组件事件枚举定义
 * 对应JS文件: src/main/js/UI/Tabs.js
 */
public enum TabsEventEnum implements EventKey {
    // Tabs特有事件
    onCmd("onCmd", "命令执行", "profile", "item", "cmdkey", "e", "src"),
    onIniPanelView("onIniPanelView", "初始化面板视图", "profile", "item"),
    beforePagePop("beforePagePop", "页面弹出前", "profile", "item", "options", "e", "src"),
    beforePageClose("beforePageClose", "页面关闭前", "profile", "item", "src"),
    afterPageClose("afterPageClose", "页面关闭后", "profile", "item"),
    onShowOptions("onShowOptions", "显示选项", "profile", "item", "e", "src"),
    onItemSelected("onItemSelected", "项目选中", "profile", "item", "e", "src", "type"),
    onCaptionActive("onCaptionActive", "标题激活", "profile", "item", "e", "src"),
    onClickPanel("onClickPanel", "点击面板", "profile", "item", "e", "src"),
    
    // 从ood.absList继承的事件
    beforePrepareItem("beforePrepareItem", "准备项目前", "profile", "item", "pid"),
    beforeIniEditor("beforeIniEditor", "初始化编辑器前", "profile", "item", "captionNode"),
    onBeginEdit("onBeginEdit", "开始编辑", "profile", "item", "editor"),
    beforeEditApply("beforeEditApply", "应用编辑前", "profile", "item", "caption", "editor", "tag"),
    onEndEdit("onEndEdit", "结束编辑", "profile", "item", "editor"),
    
    // 从ood.absValue继承的事件
    beforeValueSet("beforeValueSet", "值设置前", "profile", "oldValue", "newValue", "force", "tag"),
    afterValueSet("afterValueSet", "值设置后", "profile", "oldValue", "newValue", "force", "tag"),
    onValueChange("onValueChange", "值变更时", "profile", "oldValue", "newValue", "force", "tag"),
    beforeUIValueSet("beforeUIValueSet", "UI值设置前", "profile", "oldValue", "newValue", "force", "tag"),
    afterUIValueSet("afterUIValueSet", "UI值设置后", "profile", "oldValue", "newValue", "force", "tag"),
    onChange("onChange", "变更时", "profile", "oldValue", "newValue", "force", "tag"),
    _onChange("_onChange", "内部变更", "profile", "oldValue", "newValue", "force", "tag"),
    beforeDirtyMark("beforeDirtyMark", "脏标记前", "profile", "dirty"),
    
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

    TabsEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    @Override
    public String getEvent() {
        return event;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return event;
    }
}