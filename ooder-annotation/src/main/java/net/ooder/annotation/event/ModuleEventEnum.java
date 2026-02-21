package net.ooder.annotation.event;

import net.ooder.annotation.Enumstype;
import net.ooder.common.EventKey;

public enum ModuleEventEnum implements EventKey, Enumstype {

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
    beforeDestroy("beforeDestroy", "开始销毁"),
    onDestroy("onDestroy", "销毁", "module");

    private String event;
    private String[] params;
    private String name;
    private String type;

    ModuleEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
        this.type = name();
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    @Override
    public String toString() {
        return event;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}