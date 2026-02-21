package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * 移动端UI基础组件事件枚举定义
 * 对应JS文件: src/main/js/UI.mobile.js
 */
public enum MobileUIEventEnum implements EventKey {
    onClick("onClick", "点击时", "profile", "e", "src");

    private String event;
    private String[] params;
    private String name;

    MobileUIEventEnum(String event, String name, String... args) {
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