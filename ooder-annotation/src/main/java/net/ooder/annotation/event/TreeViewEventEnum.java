package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum TreeViewEventEnum implements EventKey {

    //module event
    onFragmentChanged("onFragmentChanged", "框架初始化完成", "module", "fragment", "init", "newAdd"),
    onMessage("onMessage", "收到消息", "module", "msg1", "msg2", "msg3", "msg4", "msg5", "source"),
    beforeCreated("beforeCreated", "开始创建", "module", "threadid"),
    onLoadBaseClass("onLoadBaseClass", "开始构建", "module", "threadid", "uri", "key"),
    onLoadRequiredClass("onLoadRequiredClass", "加载依赖类", "module", "threadid", "uri", "key"),
    onLoadRequiredClassErr("onLoadRequiredClassErr", "加载依赖类错误", "module", "threadid", "uri"),
    onIniResource("onIniResource", "初始化资源", "module", "threadid"),
    beforeIniComponents("beforeIniComponents", "开始初始化组件", "module", "threadid"),
    afterIniComponents("afterIniComponents", "组件初始化完毕", "module", "threadid"),
    afterShow("afterShow", "数据装载完毕", "module", "threadid"),
    onModulePropChange("onModulePropChange", "模块属性改变", "module", "threadid"),
    onReady("onReady", "开始准备", "module", "threadid"),
    onRender("onRender", "开始渲染", "module", "threadid"),
    onDestroy("onDestroy", "销毁时", "module"),

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

    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),

    //tree event
    onClick("onClick", "点击时", "profile", "item", "e", "src"),
    onCmd("onCmd", "命令执行", "profile", "item", "cmdkey", "e", "src"),
    onChange("onChange", "变更时", "profile", "oldValue", "newValue", "force", "tag"),
    onShowOptions("onShowOptions", "显示选项", "profile", "item", "e", "src"),
    beforeClick("beforeClick", "点击前", "profile", "item", "e", "src"),
    afterClick("afterClick", "点击后", "profile", "item", "e", "src"),
    onDblclick("onDblclick", "双击时", "profile", "item", "e", "src"),
    onGetContent("onGetContent", "获取内容", "profile", "item", "callback"),
    onItemSelected("onItemSelected", "项目选中", "profile", "item", "e", "src", "type"),
    beforeFold("beforeFold", "折叠前", "profile", "item"),
    beforeExpand("beforeExpand", "展开前", "profile", "item"),
    afterFold("afterFold", "折叠后", "profile", "item"),
    afterExpand("afterExpand", "展开后", "profile", "item");

    private String event;
    private String[] params;
    private String name;

    TreeViewEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    TreeViewEventEnum(String event, String name, String... args) {
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