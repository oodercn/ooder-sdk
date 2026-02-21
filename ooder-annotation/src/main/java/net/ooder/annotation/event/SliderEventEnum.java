package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Slider组件事件枚举定义
 * 对应JS文件: src/main/js/UI/Slider.js
 */
public enum SliderEventEnum implements EventKey {
    onLabelClick("onLabelClick", "标签点击", "profile", "e", "src"),
    onLabelDblClick("onLabelDblClick", "标签双击", "profile", "e", "src"),
    onLabelActive("onLabelActive", "标签激活", "profile", "e", "src");

    private String event;
    private String[] params;
    private String name;

    SliderEventEnum(String event, String name, String... args) {
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