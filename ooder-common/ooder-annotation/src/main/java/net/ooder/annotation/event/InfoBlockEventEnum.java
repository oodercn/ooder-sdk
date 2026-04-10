package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * InfoBlock组件事件枚举
 * 对应JS中的ood.UI.InfoBlock.EventHandlers定义
 */
public enum InfoBlockEventEnum implements EventKey {
    // List组件事件
    onClick("onClick", "点击时", "profile", "item", "e", "src"),
    onCmd("onCmd", "命令执行", "profile", "item", "cmdkey", "e", "src"),
    onChange("onChange", "变更时", "profile", "oldValue", "newValue", "force", "tag"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    beforeClick("beforeClick", "点击前", "profile", "item", "e", "src"),
    afterClick("afterClick", "点击后", "profile", "item", "e", "src"),
    onDblclick("onDblclick", "双击时", "profile", "item", "e", "src"),
    onShowOptions("onShowOptions", "显示选项", "profile", "item", "e", "src"),
    onItemSelected("onItemSelected", "项目选中", "profile", "item", "e", "src", "type"),
    onLabelClick("onLabelClick", "标签点击", "profile", "e", "src"),
    onLabelDblClick("onLabelDblClick", "标签双击", "profile", "e", "src"),
    onLabelActive("onLabelActive", "标签激活", "profile", "e", "src"),
    
    // InfoBlock特有事件
    onFlagClick("onFlagClick", "标记点击", "profile", "item", "e", "src"),
    // 拖拽事件
    onDrop("onDrop", "拖放", "profile", "e", "src"),
    onGetDragData("onGetDragData", "获取拖拽数据", "profile", "e", "src"),
    onStartDrag("onStartDrag", "开始拖拽", "profile", "e", "src"),
    onDragEnter("onDragEnter", "拖拽进入", "profile", "e", "src"),
    onDragLeave("onDragLeave", "拖拽离开", "profile", "e", "src"),
    onDragStop("onDragStop", "拖拽停止", "profile", "e", "src"),
    onDropTest("onDropTest", "拖放测试", "profile", "e", "src"),
    beforeDrop("beforeDrop", "拖放前", "profile", "e", "src"),
    onDropMarkShow("onDropMarkShow", "显示拖放标记", "profile", "e", "src"),
    afterDrop("afterDrop", "拖放后", "profile", "e", "src"),
    
    // 触摸事件
    touchstart("touchstart", "触摸开始", "profile", "item", "e", "src"),
    touchmove("touchmove", "触摸移动", "profile", "item", "e", "src"),
    touchend("touchend", "触摸结束", "profile", "item", "e", "src"),
    touchcancel("touchcancel", "触摸取消", "profile", "item", "e", "src"),
    
    // 滑动事件
    swipe("swipe", "滑动", "profile", "item", "e", "src"),
    swipeleft("swipeleft", "向左滑动", "profile", "item", "e", "src"),
    swiperight("swiperight", "向右滑动", "profile", "item", "e", "src"),
    swipeup("swipeup", "向上滑动", "profile", "item", "e", "src"),
    swipedown("swipedown", "向下滑动", "profile", "item", "e", "src"),
    
    // 按压事件
    press("press", "按压", "profile", "item", "e", "src"),
    pressup("pressup", "按压释放", "profile", "item", "e", "src"),
    
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
    onShowTips("onShowTips", "显示提示", "profile", "node", "pos");

    private String event;
    private String[] params;
    private String name;

    InfoBlockEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    InfoBlockEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
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