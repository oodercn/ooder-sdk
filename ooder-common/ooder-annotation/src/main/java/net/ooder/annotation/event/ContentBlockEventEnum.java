package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * ContentBlock组件事件枚举定义
 * 对应JS文件: src/main/js/UI/ContentBlock.js
 */
public enum ContentBlockEventEnum implements EventKey {
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
    
    // ContentBlock特有事件
    onMoreClick("onMoreClick", "更多点击", "profile", "item", "e", "src"),
    onTitleClick("onTitleClick", "标题点击", "profile", "item", "e", "src"),
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
    touchstart("touchstart", "触摸开始", "profile", "item", "e", "src"),
    touchmove("touchmove", "触摸移动", "profile", "item", "e", "src"),
    touchend("touchend", "触摸结束", "profile", "item", "e", "src"),
    touchcancel("touchcancel", "触摸取消", "profile", "item", "e", "src"),
    swipe("swipe", "滑动", "profile", "item", "e", "src"),
    swipeleft("swipeleft", "向左滑动", "profile", "item", "e", "src"),
    swiperight("swiperight", "向右滑动", "profile", "item", "e", "src"),
    swipeup("swipeup", "向上滑动", "profile", "item", "e", "src"),
    swipedown("swipedown", "向下滑动", "profile", "item", "e", "src"),
    press("press", "按下", "profile", "item", "e", "src"),
    pressup("pressup", "抬起", "profile", "item", "e", "src");

    private String event;
    private String[] params;
    private String name;

    ContentBlockEventEnum(String event, String name, String... args) {
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