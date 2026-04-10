package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum BottonEventEnum implements EventKey {

    onClick("onClick", "点击", "profile", "e", "src", "value"),
    onClickDrop("onClickDrop", "点击下拉", "profile", "e", "src", "value"),
    onChecked("onChecked", "选中", "profile", "e", "value"),
    
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
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    
    // absValue事件
    beforeValueSet("beforeValueSet", "值设置前", "profile", "oldValue", "newValue", "force", "tag"),
    afterValueSet("afterValueSet", "值设置后", "profile", "oldValue", "newValue", "force", "tag"),
    onValueChange("onValueChange", "值变更时", "profile", "oldValue", "newValue", "force", "tag"),
    beforeUIValueSet("beforeUIValueSet", "UI值设置前", "profile", "oldValue", "newValue", "force", "tag"),
    afterUIValueSet("afterUIValueSet", "UI值设置后", "profile", "oldValue", "newValue", "force", "tag"),
    onChange("onChange", "变更时", "profile", "oldValue", "newValue", "force", "tag"),
    _onChange("_onChange", "内部变更", "profile", "oldValue", "newValue", "force", "tag"),
    beforeDirtyMark("beforeDirtyMark", "脏标记前", "profile", "dirty");

    private String event;
    private String[] params;
    private String name;

    BottonEventEnum(String event, String name, String... args) {
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