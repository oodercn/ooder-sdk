package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * 移动端Switch组件事件枚举定义
 * 对应JS文件: src/main/js/mobile/Form/Switch.js
 */
public enum MobileSwitchEventEnum implements EventKey {
    // ood.UI 生命周期事件处理器
    onReady("onReady", "准备完成", "profile"),
    onCreated("onCreated", "创建时", "profile"),
    onDestroy("onDestroy", "销毁时", "profile"),
    
    // ood.absValue 事件处理器
    onChanged("onChanged", "变更时", "profile", "e", "src", "value"),
    onChecked("onChecked", "选中时", "profile", "e", "src", "value"),
    onValueSet("onValueSet", "值设置时", "profile", "oldValue", "newValue"),
    
    // Switch 特定事件处理器
    onChange("onChange", "切换时", "profile", "checked");

    private String event;
    private String[] params;
    private String name;

    MobileSwitchEventEnum(String event, String name, String... args) {
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