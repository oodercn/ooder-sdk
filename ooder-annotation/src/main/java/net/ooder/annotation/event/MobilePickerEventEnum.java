package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * 移动端Picker组件事件枚举定义
 * 对应JS文件: src/main/js/mobile/Form/Picker.js
 */
public enum MobilePickerEventEnum implements EventKey {
    // 触发器点击事件
    onTriggerClick("onTriggerClick", "触发器点击", "profile", "event"),
    
    // 弹窗显示事件
    onPopupShow("onPopupShow", "弹窗显示", "profile"),
    
    // 弹窗隐藏事件
    onPopupHide("onPopupHide", "弹窗隐藏", "profile"),
    
    // 选项点击事件
    onOptionClick("onOptionClick", "选项点击", "profile", "value", "event"),
    
    // 确认事件
    onConfirm("onConfirm", "确认时", "profile", "value", "event"),
    
    // 取消事件
    onCancel("onCancel", "取消时", "profile", "event"),
    
    // 主题变化事件
    onThemeChange("onThemeChange", "主题变化", "profile", "oldTheme", "newTheme"),
    
    // 继承自UI基类的事件处理器
    onClick("onClick", "点击时", "profile", "event", "src"),
    onRender("onRender", "渲染时", "profile"),
    onResize("onResize", "调整大小", "profile", "width", "height"),
    
    // 错误处理
    onError("onError", "错误时", "profile", "error", "context");

    private String event;
    private String[] params;
    private String name;

    MobilePickerEventEnum(String event, String name, String... args) {
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