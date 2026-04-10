package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * HiddenInput组件事件枚举
 * 对应JS中的ood.UI.HiddenInput.EventHandlers定义
 */
public enum HiddenInputEventEnum implements EventKey {
    // 脏标记前事件
    beforeDirtyMark("beforeDirtyMark", "脏标记前", "profile", "dirty"),
    
    // 上下文菜单事件
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    
    // 停靠事件
    onDock("onDock", "停靠时", "profile", "region"),
    
    // 布局事件
    onLayout("onLayout", "布局时", "profile"),
    
    // 移动事件
    onMove("onMove", "移动时", "profile", "left", "top", "right", "bottom"),
    
    // 渲染事件
    onRender("onRender", "渲染时", "profile"),
    
    // 调整大小事件
    onResize("onResize", "调整大小", "profile", "width", "height"),
    
    // 显示提示事件
    onShowTips("onShowTips", "显示提示", "profile", "node", "pos"),
    
    // 附加前事件
    beforeAppend("beforeAppend", "附加前", "profile", "child"),
    
    // 附加后事件
    afterAppend("afterAppend", "附加后", "profile", "child"),
    
    // 渲染前事件
    beforeRender("beforeRender", "渲染前", "profile"),
    
    // 渲染后事件
    afterRender("afterRender", "渲染后", "profile"),
    
    // 移除前事件
    beforeRemove("beforeRemove", "移除前", "profile", "child", "subId", "bdestroy"),
    
    // 移除后事件
    afterRemove("afterRemove", "移除后", "profile", "child", "subId", "bdestroy"),
    
    // 热键按下事件
    onHotKeydown("onHotKeydown", "热键按下", "profile", "key", "e", "src"),
    
    // 热键按下事件
    onHotKeypress("onHotKeypress", "热键按下时", "profile", "key", "e", "src"),
    
    // 热键释放事件
    onHotKeyup("onHotKeyup", "热键释放", "profile", "key", "e", "src"),
    
    // 父类事件
    beforePropertyChanged("beforePropertyChanged", "属性变更前", "profile", "name", "value", "ovalue"),
    afterPropertyChanged("afterPropertyChanged", "属性变更后", "profile", "name", "value", "ovalue"),
    onDestroy("onDestroy", "销毁时", "profile"),
    beforeDestroy("beforeDestroy", "销毁前", "profile"),
    afterDestroy("afterDestroy", "销毁后", "profile");

    private String event;
    private String[] params;
    private String name;

    HiddenInputEventEnum(String event, String name, String... args) {
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