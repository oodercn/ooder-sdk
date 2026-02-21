package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * TreeBar组件事件枚举定义
 * 对应JS文件: src/main/js/UI/TreeBar.js
 */
public enum TreeBarEventEnum implements EventKey {
    onClick("onClick", "点击时", "profile", "item", "e", "src"),
    onCmd("onCmd", "命令执行", "profile", "item", "cmdkey", "e", "src"),
    onChange("onChange", "变更时", "profile", "oldValue", "newValue", "force", "tag"),
    onShowOptions("onShowOptions", "显示选项", "profile", "item", "e", "src"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    beforeClick("beforeClick", "点击前", "profile", "item", "e", "src"),
    afterClick("afterClick", "点击后", "profile", "item", "e", "src"),
    onDblclick("onDblclick", "双击时", "profile", "item", "e", "src"),
    onGetContent("onGetContent", "获取内容", "profile", "item", "callback"),
    onItemSelected("onItemSelected", "项目选中", "profile", "item", "e", "src", "type"),
    beforeFold("beforeFold", "折叠前", "profile", "item"),
    beforeExpand("beforeExpand", "展开前", "profile", "item"),
    afterFold("afterFold", "折叠后", "profile", "item"),
    afterExpand("afterExpand", "展开后", "profile", "item"),
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
    afterDrop("afterDrop", "拖放后", "profile", "e", "src");

    private String event;
    private String[] params;
    private String name;

    TreeBarEventEnum(String event, String name, String... args) {
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