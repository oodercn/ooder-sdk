package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * ColorPicker组件事件枚举定义
 * 对应JS文件: src/main/js/UI/ColorPicker.js
 */
public enum ColorPickerEventEnum implements EventKey {
    beforeClose("beforeClose", "关闭前", "profile", "src");

    private String event;
    private String[] params;
    private String name;

    ColorPickerEventEnum(String event, String name, String... args) {
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