package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * 移动端TabBar组件事件枚举定义
 * 对应JS文件: src/main/js/mobile/Navigation/TabBar.js
 */
public enum MobileTabBarEventEnum implements EventKey {
    // ood.UI 生命周期事件处理器
    onReady("onReady", "准备完成", "profile"),
    onCreated("onCreated", "创建时", "profile"),
    onDestroy("onDestroy", "销毁时", "profile"),
    
    // ood.absValue 事件处理器
    onChanged("onChanged", "变更时", "profile", "e", "src", "value"),
    onChecked("onChecked", "选中时", "profile", "e", "src", "value"),
    onValueSet("onValueSet", "值设置时", "profile", "oldValue", "newValue"),
    
    // ood.absList 事件处理器
    onItemSelected("onItemSelected", "项目选中", "profile", "item", "e", "src", "type"),
    onItemAdded("onItemAdded", "项目添加", "profile", "items", "index"),
    onItemRemoved("onItemRemoved", "项目移除", "profile", "items", "indices"),
    
    // TabBar 特定事件处理器
    onTabChange("onTabChange", "标签变更", "profile", "index", "tab");

    private String event;
    private String[] params;
    private String name;

    MobileTabBarEventEnum(String event, String name, String... args) {
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